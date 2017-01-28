package game;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import engine.GameBase;
import engine.Server;
import entity.Entity;
import entity.EntityDefinition;
import entity.EntityManager;
import entity.EntityType;
import entity.MushroomEntity;
import entity.PlayerEntity;

public class Game extends GameBase {

	//This method starts the server and the game
	public static void main(String[] args) {
		if (args.length >= 2){
			Game.MAP = args[1];
		}
		new Server (new Game(),args);
	}

	public static String MAP = "default";

	public Map map;
	public EntityManager entityManager = new EntityManager();
	private int lastAddedEntityID = 99;	//Next entity should be id: 101
	private HashMap<Integer,Entity> entities = new HashMap<Integer,Entity>();
	private HashMap<Integer,PlayerEntity> players = new HashMap<Integer,PlayerEntity>();	//Shortcut for getting player objects. (used in AI)
	private HashMap<Integer,ClientDelta> clientDeltas = new HashMap<Integer,ClientDelta>();
	private HashMap<Integer,Sign> signs = new HashMap<Integer,Sign>();
	private HashMap<Integer,Point> warps = new HashMap<Integer,Point>();

	public Game() {


		load();	//Load the game (from save files)
	}

	/**Get an ID for an entity that hasn't been used yet
	 * WARNING: Not thread safe!
	 *  Threads have been disabled
	 * @return
	 */
	public int getNewEntityId() {
		int id = lastAddedEntityID + 1;
		while (entities.containsKey(id)) {
			id++;
		}
		entities.put(id, null);	//Add something so we don't lose this ID to a race condition
		lastAddedEntityID = id;
		return id;
	}

	/**Add an entity to the game and update all references for subscribers
	 * (Remove entities by setting "isAlive" to false)
	 * @param e The entity to start tracking
	 */
	public void addEntity(Entity e) {
		entities.put(e.id, e);

		//Quicker way to discern between players
		if (e.type == EntityType.PLAYER) {
			players.put(e.id, (PlayerEntity)e);
		}

		//Add this to all the deltas
		for (ClientDelta delta : clientDeltas.values()) {
			delta.addEntity(e);
		}
	}

	/**Mark this entity as updated so that clients learn about it
	 * Example usage: move entity e up:
	 * 		e.y--;				//Move it
	 * 		updateEntity(e.id); //Make sure we tell clients that it changed
	 * 
	 * @param eid The entity ID to udate
	 */
	public void updateEntity(int eid) {
		updateEntity(eid,false);
	}
	
	/**Mark this entity as updated so that clients learn about it
	 * Example usage: move entity e up:
	 * 		e.y--;				//Move it
	 * 		updateEntity(e.id); //Make sure we tell clients that it changed
	 * 
	 * @param eid The EID that got updated.
	 * @param forcePlayerUpdate Default false.  If TRUE, this will force the player to update their own position.  Default false.
	 */
	public void updateEntity(int eid, boolean forcePlayerUpdate) {
		if (entities.containsKey(eid)) {
			Entity e = entities.get(eid);

			//Make sure this is added to deltas so we tell our clients
			for (ClientDelta delta : clientDeltas.values()) {
				if (delta.pid == eid && !forcePlayerUpdate) { continue; }	//Don't tell player about themself
				//System.out.println("Adding [" + eid + ", (" + e.x + "," + e.y + ")] to delta" + delta.pid);
				delta.addEntity(e);
			}
		}
	}

	public void deleteEntity(int eid) {
		Entity e = entities.get(eid);
		if (e != null) {
			e.isAlive = false;				//Make sure the client removes it
			//Update all the client deltas to reflect this
			//Add this to all the deltas
			for (ClientDelta delta : clientDeltas.values()) {
				delta.addEntity(e);
			}
			if (e.type == EntityType.PLAYER) {
				players.remove(e.id);
			}
		}
		entities.remove(eid);
	}
	
	/**Send a message to a specific player
	 * Use this if some other player's action needs to send a direct message
	 * 
	 * @param eid The player (pid) to get this message
	 * @param notification The message to send
	 */
	public void addNotification(int eid, Sign notification) {
		if (clientDeltas.containsKey(eid)) {
			clientDeltas.get(eid).addNotification(notification);
		}
	}

	/**update player deltas so they know a tile changed
	 * 
	 * @param row
	 * @param col
	 * @param type
	 */
	public void updateTile(int row, int col, int type) {
		for (ClientDelta delta : clientDeltas.values()) {
			delta.updateTile(row,col,type);
		}
	}

	public byte[] getClientDelta(int pid) {
		if (!clientDeltas.containsKey(pid)) {
			ClientDelta d = new ClientDelta(pid);
			clientDeltas.put(pid, d);

			//They don't know anything yet!  Tell them everything
			for (Entity e : entities.values()) {
				d.addEntity(e);
			}

			//Tell them everything about the map
			for (int row = 0; row < map.getNumRows(); row++) {
				for (int col = 0; col < map.getNumCols(); col++) {
					d.updateTile(row, col, map.getTileAt(row,col));
				}
			}

		}
		return clientDeltas.get(pid).getBytes();
	}



	/**A client will routinely send us arguments.
	 * @param key Often the command that is to be executed.  Eg "setplayerposition"
	 * @param value The argument(s) associated.  These must be parsed manually because their format is not restricted.  Eg "10,10"
	 * @return Return a byte array to send back to the client.  For convenience, use a ByteBuffer for numeric values and String.getBytes() for strings.
	 */
	public byte[] respondToClient(String key, String value) {
		try {
			if (key.equalsIgnoreCase("init")) {
				//Return the PID and the original map state
				int pid = getNewEntityId();

				//Add the entity for our player
				Entity player = Entity.create(pid,EntityType.PLAYER,map.spawnCol,map.spawnRow);
				addEntity(player);

				//Response is: pid and mapRows, mapCols
				ByteBuffer bb = ByteBuffer.allocate(4*3);
				bb.putInt(pid);
				bb.putInt(map.getNumRows());
				bb.putInt(map.getNumCols());
				return concat(concat(bb.array(),map.getUnpassableTileIds()),entityManager.getBytes());
			}

			if (key.equalsIgnoreCase("entity")) {
				String[] args = value.split("\\|");
				int eid = Integer.parseInt(args[0]);

				//Parse the directions from the client
				for (int i = 1; i < args.length; i++) {
					int direction = Integer.parseInt(args[i]);
					Entity e = entities.get(eid);
					e.appendToPath((byte)direction);
				}

				if (args.length > 1) {
					updateEntity(eid);
				}
				return null;	//Nothing to say back
			}

			if (key.equalsIgnoreCase("map")) {
				String[] args = value.split("\\|");
				//Loop over the args
				for (int i = 1; i+2 < args.length; i+=3) {
					int row = Integer.parseInt(args[i]);
					int col = Integer.parseInt(args[i+1]);
					int tile = Integer.parseInt(args[i+2]);
					map.set(row,col,tile);
				}
				return null;	//nothing to say back!
			}

			if (key.equalsIgnoreCase("update")) {
				//Return anything that might have changed
				int pid = Integer.parseInt(value);

				((PlayerEntity)entities.get(pid)).refresh();	//Reset our countdown timer until the system erases thisplayer

				return getClientDelta(pid);
			}

			//DEAD
			if (key.equalsIgnoreCase("d")) {
				int eid = Integer.parseInt(value);
				Entity e = entities.get(eid);
				if (e.definition.baseHP > 0) {
					//If its a player, don't ACTUALLY kill them
					if (players.containsKey(e.id)) {
						e.x = map.spawnCol;
						e.y = map.spawnRow;
						updateEntity(e.id,true);//Force update
						//Tell the dead player that they died
						addNotification(e.id,new Sign("You died"));
					} else {
						for (ClientDelta d : clientDeltas.values()){
							d.addDeadEntity(eid);
						}
						e.isAlive = false;//Mark it as dead so our thread cleans it up
					}
					
					
					
				}
			}

			if (key.equalsIgnoreCase("attack")) {
				int eid = Integer.parseInt(value);
				for(ClientDelta d : clientDeltas.values()){
					d.addAttackingEntity(eid);
				}
			}

			//Add an entity, or remove all if ID=0
			if (key.equalsIgnoreCase("createEntity")) {
				//Parse out the params
				String[] args = value.split("\\|"); // type|x|y
				//If type is 0, remove!
				if(args[0].equals("0")) {
					System.out.println("Deleting entities at " + args[1] + "," + args[2]);
					int x = Integer.parseInt(args[1]);
					int y = Integer.parseInt(args[2]);

					//Delete everything in this spot
					for (Entity e : entities.values()) {
						if (e.definition.saveable &&  e.x == x && e.y == y) {
							deleteEntity(e.id);	//Gets cleaned up by server
						}
					}

					return null;
				}

				//Get an ID and create the entity here
				int id = getNewEntityId();
				Entity e = Entity.create(id, Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
				addEntity(e);
				return null;	//The user will get the change with update
			}

			//Chat
			if(key.equalsIgnoreCase("chat")){
				for(ClientDelta d : clientDeltas.values()){
					d.addChat(value);
				}
			}

			if (key.equalsIgnoreCase("sign")) {
				//Only arg is eid of the sign
				Sign sign = signs.get(Integer.parseInt(value));
				if(sign == null) { return new Sign(true).getBytes();}	//Default message
				return sign.getBytes();
			}
			
			if (key.equalsIgnoreCase("warp")) {
				String[] args = value.split("\\|"); // targetEID|warpEID
				Point p = warps.get(Integer.parseInt(args[1]));
				if(p == null) { return null; }	//Do nothing
				Entity e = entities.get(Integer.parseInt(args[0]));
				e.x = p.x;
				e.y = p.y;
				updateEntity(e.id,true);	//Force update
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ("Unknown or invalid command: " + key).getBytes();
	}

	//Method for conveniently converting a single integer
	private byte[] intToBytes(int x) {
		return ByteBuffer.allocate(4).putInt(x).array();
	}

	private byte[] concat(byte[] arrayA, byte[] arrayB) {
		byte[] combined = new byte[arrayA.length+arrayB.length];
		int i = 0;
		for (byte b : arrayA) {combined[i++] = b;}
		for (byte b : arrayB) {combined[i++] = b;}
		return combined;
	}

	@Override
	public long delayBetweenRuns() {
		return 50;
	}

	long lastSave = 0;	
	@Override
	public void run() {

		//Save the game every 10 seconds
		if (System.currentTimeMillis() - 10000 > lastSave) {
			save();
			lastSave = System.currentTimeMillis();
		}

		//Get a set of all entity ids
		//Avoid a concurrent modification exception
		Integer[] eids = new Integer[entities.size()];
		eids = entities.keySet().toArray(eids);

		for (int eid : eids) {
			Entity e = entities.get(eid);

			//Ignore null entities
			if (e==null) { entities.remove(eid); continue;}

			e.update(this);
			if (e.isAlive == false) {
				deleteEntity(e.id);
			}
		}
	}

	/**Save the game
	 * 
	 */
	public void save() {
		map.save();	//Save the map

		//Save entities
		System.out.println("Saving entities");
		try {
			PrintWriter pw = new PrintWriter(Game.MAP + ".entities");
			pw.println("#Autogenerated");
			for (Entity e : entities.values()) {
				//If it is an UNCHANGING entity, save it
				//This includes static objects and non-moving things
				//EG: PLAYER IS NOT STATIC, Nor ar enemies (but spawners are)
				if (e.definition.saveable) {
					pw.println(e.id + " " + e.type + " " + e.y + " " + e.x);
				}
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() {
		//Set second argument to true to ALWAYS generate a new map file
		map = new Map(this,Game.MAP + ".map",false);

		//Load Entities
		String filename = Game.MAP + ".entities";
		System.out.println("Loading entities from " + filename);	
		try {
			File entitiesFile = new File(filename);
			if (!entitiesFile.exists()) { entitiesFile.createNewFile(); }
			Scanner scanner = new Scanner(entitiesFile);
			int lineNum = 0;

			while (scanner.hasNextLine()) {
				lineNum++;
				String line = scanner.nextLine().trim();
				String data[] = line.split(" ");
				if (data.length < 4 || data[0].startsWith("#")) { continue; }
				try {
					//Get a new entity ID
					int eid = Integer.parseInt(data[0]);//getNewEntityId();
					Entity e = Entity.create(eid, Integer.parseInt(data[1]),Integer.parseInt(data[3]),Integer.parseInt(data[2]));
					entities.put(eid, e);
					System.out.println("Loaded entity: " + e);
				} catch (Exception e) {
					System.out.println("Failed to load line " + lineNum + " of entites: " + line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Load signs
		filename = Game.MAP + ".signs";
		System.out.println("Loading signs from " + filename);
		try {
			File signsFile = new File(filename);
			if (!signsFile.exists()) {
				signsFile.createNewFile(); 
				PrintWriter pw = new PrintWriter(signsFile);
				pw.println("#Tab delimited, up to 5 parameters");
				pw.println("#First: eid of sign");
				pw.println("#Second-fifth: Text");
				pw.close();
			}
			Scanner scanner = new Scanner(signsFile);
			int lineNum = 0;

			while (scanner.hasNextLine()) {
				lineNum++;
				String line = scanner.nextLine().trim();
				String data[] = line.split("\t");
				if (data.length < 2 || data[0].startsWith("#")) { continue; }
				try {
					//Get the entity ID this ties to
					int eid = Integer.parseInt(data[0]);

					//Get the remainder of the lines (up to 4)
					Sign sign = new Sign();
					for (int i = 0; i < 4; i++) {
						if (i+1 >= data.length) { break; }
						sign.addLine(data[i+1]);
					}

					//Store this sign
					signs.put(eid, sign);
					System.out.println("Loaded sign for eid: " + eid + " with " + (data.length-1) + " lines");
				} catch (Exception e) {
					System.out.println("Failed to load line " + lineNum + " of signs: " + line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		//Load Warps
		filename = Game.MAP + ".warps";
		System.out.println("Loading warps from " + filename);
		try {
			File warpFile = new File(filename);
			if (!warpFile.exists()) {
				warpFile.createNewFile(); 
				PrintWriter pw = new PrintWriter(warpFile);
				pw.println("#Space Delimited");
				pw.println("#First: eid of warp");
				pw.println("#Second and third: x & y of warp location");
				pw.close();
			}
			Scanner scanner = new Scanner(warpFile);
			int lineNum = 0;

			while (scanner.hasNextLine()) {
				lineNum++;
				String line = scanner.nextLine().trim();
				String data[] = line.split(" ");
				if (data.length < 2 || data[0].startsWith("#")) { continue; }
				try {
					//Get the entity ID this ties to
					int eid = Integer.parseInt(data[0]);

					//Get destination
					int x = Integer.parseInt(data[1]);
					int y = Integer.parseInt(data[2]);

					//Store this sign
					warps.put(eid,new Point(x,y));
					System.out.println("Loaded warp for eid: " + eid + " to (x: " + x + ",y:" + y + ")");
				} catch (Exception e) {
					System.out.println("Failed to load line " + lineNum + " of warps: " + line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Collection<PlayerEntity> getPlayers() {
		return players.values();
	}

	@Override
	public boolean debugShowCommunication() {
		return this.players.size() < 2;	//2 or more players stop debug communication
	}

	//TODO update this for chunks
	public HashSet<Entity> getEntitiesAt(int x, int y) {
		HashSet<Entity> set = new HashSet<Entity>();
		for (Entity e : entities.values()) {
			if (e.x == x && e.y == y) {
				set.add(e);
			}
		}
		return set;
	}

}
