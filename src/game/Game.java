package game;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import engine.GameBase;
import engine.Server;
import entity.Entity;
import entity.EntityManager;
import entity.EntityType;
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
	private HashMap<Integer,ClientDelta> clientDeltas = new HashMap<Integer,ClientDelta>();
	private HashMap<Integer,Sign> signs = new HashMap<Integer,Sign>();
	private HashMap<Integer,Point> warps = new HashMap<Integer,Point>();
	private final EntityChunks entityChunks;
	private ArrayList<String> serverLog = new ArrayList<String>();

	public Game() {
		log("Server started");
		entityChunks = new EntityChunks(clientDeltas);
		load();	//Load the game (from save files)
	}

	/**Get an ID for an entity that hasn't been used yet
	 * WARNING: Not thread safe!
	 *  Threads have been disabled
	 * @return
	 */
	public int getNewEntityId() {
		return entityChunks.getNewEntityId();
	}

	/**Add an entity to the game and update all references for subscribers
	 * (Remove entities by setting "isAlive" to false)
	 * @param e The entity to start tracking
	 */
	public void addEntity(Entity e) {
		entityChunks.addEntity(e);
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
			return null;
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
				log("Player " + pid + " joined");

				//Add the entity for our player
				PlayerEntity player = (PlayerEntity)Entity.create(pid,EntityType.PLAYER,map.spawnCol,map.spawnRow);
				addEntity(player);

				//Create a delta for this player
				ClientDelta delta = new ClientDelta(player);
				clientDeltas.put(player.id, delta);

				//Add the map stuff
				updatePlayerChunk(player);
				entityChunks.updateEntity(player,true);//Cause us to actually spawn

				//Response is: pid
				ByteBuffer bb = ByteBuffer.allocate(4);
				bb.putInt(pid);
				return concat(concat(bb.array(),map.getUnpassableTileIds()),entityManager.getBytes());
			}

			if (key.equalsIgnoreCase("entity")) {
				String[] args = value.split("\\|");
				int eid = Integer.parseInt(args[0]);
				Entity e = entityChunks.getEntity(eid);
				//Parse the directions from the client
				for (int i = 1; i < args.length; i++) {
					int direction = Integer.parseInt(args[i]);
					e.appendToPath((byte)direction);
				}

				//Update the chunk they're in
				updatePlayerChunk((PlayerEntity)e);

				if (args.length > 1) {
					entityChunks.updateEntity(e);
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

				((PlayerEntity)entityChunks.getEntity(pid)).refresh();	//Reset our countdown timer until the system erases thisplayer

				return getClientDelta(pid);
			}

			//DEAD
			if (key.equalsIgnoreCase("d")) {
				String[] data = value.split("\\|");
				int pid = Integer.parseInt(data[0]);
				int eid = Integer.parseInt(data[1]);
				Entity e = entityChunks.getEntity(eid);
				if (e.definition.baseHP > 0) {
					//If its a player, don't ACTUALLY kill them
					if (entityChunks.containsPlayer(e.id)) {
						e.x = map.spawnCol;
						e.y = map.spawnRow;
						entityChunks.updateEntity(e,true);//Force update
						//Tell the dead player that they died
						addNotification(e.id,new Sign("You died"));
					} else {
						for (ClientDelta d : clientDeltas.values()){
							d.addDeadEntity(eid);
						}
						e.isAlive = false;//Mark it as dead so our thread cleans it up
						triggerEntity(pid,e.triggersEid);	//Trigger if there is something
						
					}
				}
				return null;	//This was missing
			}

			if (key.equalsIgnoreCase("attack")) {
				int eid = Integer.parseInt(value);
				for(ClientDelta d : clientDeltas.values()){
					d.addAttackingEntity(eid);
				}
				return null;
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
					for (Entity e : entityChunks.getEntitiesAt(x,y)) {
						if (e.definition.saveable) {
							entityChunks.deleteEntity(e.id);	//Tells clients then gets cleaned by server
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
				String[] data = value.split("\\|");
				int pid = Integer.parseInt(data[0]);
				log("Chat: " + value);
				for(ClientDelta d : clientDeltas.values()){
					d.addChat(pid,data[1]);
				}
			}

			if (key.equalsIgnoreCase("sign")) {
				//Arg 1 is player, arg 2 is the sign id.
				//Player is needed for triggers
				String[] data = value.split("\\|");
				int pid = Integer.parseInt(data[0]);
				int eid = Integer.parseInt(data[1]);
				Sign sign = signs.get(eid);
				if(sign == null) { return new Sign(true).getBytes();}	//Default message
				triggerEntity(pid,sign.triggerEid);	//Trigger if there is something
				return sign.getBytes();
			}

			if (key.equalsIgnoreCase("warp")) {
				String[] args = value.split("\\|"); // targetEID|warpEID
				Point p = warps.get(Integer.parseInt(args[1]));
				if(p == null) { System.out.println("No warp here, sorry"); return null; }	//Do nothing
				Entity e = entityChunks.getEntity(Integer.parseInt(args[0]));
				e.x = p.x;
				e.y = p.y;
				entityChunks.updateEntity(e,true);	//Force update
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.err.println("Invalid command: " + key);
		return ("Unknown or invalid command: " + key).getBytes();
	}

	/**Trigger an entity if there is something to trigger
	 * 
	 * @param pid The player this applies to
	 * @param triggerEid
	 */
	private void triggerEntity(int pid, int triggerEid) {
		if (triggerEid < 0 ) { return; }
		Entity target = entityChunks.getEntity(triggerEid);
		if (target == null) { return; }
		ClientDelta delta = clientDeltas.get(pid);
		if (delta == null) { return; }
		//What does this turn into?
		int type = EntityManager.definitions.get(target.type).onTrigger;
		if (type < 0) { return; }
		Entity e = Entity.create(triggerEid, type, target.x, target.y);
		delta.addOverrideEntity(e);	
	}

	/**Give a player object, check what chunk theyre in and send clientDeltas if necessary
	 * 
	 * @param p A reference to the player entity
	 */
	private void updatePlayerChunk(PlayerEntity p) {
		ClientDelta delta = clientDeltas.get(p.id);
		if (delta == null) { return; }	//This should never happen.  TO get here, player has to move.  To move, they must have delta

		p.recalculateChunks(map.chunkRows, map.chunkCols);

		//Add new chunks to the player's delta
		for (Point point : p.getNewChunks()) {
			//Add these tiles to the client delta
			MapChunk chunk = map.getChunk(point.y,point.x);
			for (int row = 0; row < chunk.tiles.length; row++) {
				for (int col = 0; col < chunk.tiles[row].length; col++) {
					delta.updateTile(row + (chunk.row*map.chunkRows), col+(chunk.col*map.chunkCols), chunk.tiles[row][col]);
				}
			}
		}



		//Add stale chunks to the player's delta
		for (Point point : p.getStaleChunks()) {
			int startRow = point.y * map.chunkRows;
			int startCol = point.x * map.chunkCols;
			delta.addStaleMapZone(startRow, startCol, startRow + map.chunkRows, startCol + map.chunkCols);
		}

		//Add entities that are in the new chunks and delete entities in the stale ones
		for (Point chunk : p.getNewChunks()) {
			for (Entity e : entityChunks.getEntitiesInChunk(chunk.x,chunk.y)) {
					delta.addEntity(e);
			}
		}
		
		for (Point chunk : p.getStaleChunks()) {
			for (Entity e : entityChunks.getEntitiesInChunk(chunk.x,chunk.y)) {
				if (e.id != p.id) {
					delta.addDeadEntity(e.id);
				}
			}
		}

		//Clear the player's chunks so we don't keep sending them
		p.getNewChunks().clear();
		p.getStaleChunks().clear();
		
		//TODO when our game gets bigger and we have lots of entities, we can't just loop through each one every time a player loads
		//a new chunk.  Instead, modify entity.update() and cause it to place itself in a 2D hash map for its chunk.  Then we just have to loop over
		//these "chunks" and add the entities.
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
		return 150;
	}

	long lastSave = 0;	
	@Override
	public void run() {

		//Save the game every 10 seconds
		if (System.currentTimeMillis() - 50000 > lastSave) {
			save();
			lastSave = System.currentTimeMillis();
			log(entityChunks.getPlayers().size() + " players online");
		}

		entityChunks.removeDeadEntities();
		
		//Update all of our dynamic entities.  Note, spawners add to our list, so wee need a copy first
		for (Entity e : entityChunks.getDynamicEntitiesClone()) {
			e.update(this);
		}
	}

	public void log(String s) {
		String logString = new SimpleDateFormat("[yyyy.MM.dd.HH.mm.ss]").format(new Date()) + " " + s;
		serverLog.add(logString);
		System.out.println(logString);
	}
	
	/**Save the game
	 * 
	 */
	public void save() {
		log("Saving game");
		map.save();	//Save the map

		//Save entities
		System.out.println("Saving entities");
		try {
			PrintWriter pw = new PrintWriter(Game.MAP + ".entities");
			pw.println("#Autogenerated");
			for (int eid : entityChunks.getEntityIds()) {
				Entity e = entityChunks.getEntity(eid);
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
		
		try {
			FileWriter logWriter = new FileWriter(Game.MAP + ".serverlog");
			for (String s : this.serverLog) {
				logWriter.append(s + "\n");
			}
			this.serverLog.clear();
			logWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void load() {
		//Set second argument to true to ALWAYS generate a new map file
		map = new Map(this,Game.MAP + ".map");

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
					entityChunks.addEntity(e);
					//System.out.println("Loaded entity: " + e);
				} catch (Exception e) {
					System.out.println("Failed to load line " + lineNum + " of entites: " + line);
					e.printStackTrace();
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

					//Get the sign if it exists
					Sign sign = signs.get(eid);
					if (sign == null) {
						sign = new Sign();
					}
					String[] page = new String[4];
					for (int i = 0; i+1 < data.length; i++) {
						page[i] = data[i+1];
					}
					sign.addPage(page);

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
				if (data.length < 3 || data[0].startsWith("#")) { continue; }
				try {
					//Get the entity ID this ties to
					int eid = Integer.parseInt(data[0]);

					//Get destination
					int x = Integer.parseInt(data[1]);
					int y = Integer.parseInt(data[2]);

					//Store this warp
					warps.put(eid,new Point(x,y));
					System.out.println("Loaded warp for eid: " + eid + " to (x: " + x + ",y:" + y + ")");
				} catch (Exception e) {
					System.out.println("Failed to load line " + lineNum + " of warps: " + line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Load triggers
		filename = Game.MAP + ".triggers";
		System.out.println("Loading triggers from " + filename);
		try {
			File triggerFile = new File(filename);
			if (!triggerFile.exists()) {
				triggerFile.createNewFile(); 
				PrintWriter pw = new PrintWriter(triggerFile);
				pw.println("#Space Delimited");
				pw.println("#First: eid of the triggerable object");
				pw.println("#Second: eid of the object to trigger");
				pw.println("#(An entity is triggered when their AI on the server says so.  This includes signs, onDeath(for spawners), and onWalk (for triggers)");
				pw.close();
			}
			Scanner scanner = new Scanner(triggerFile);
			int lineNum = 0;

			while (scanner.hasNextLine()) {
				lineNum++;
				String line = scanner.nextLine().trim();
				String data[] = line.split(" ");
				if (data.length < 2 || data[0].startsWith("#")) { continue; }
				try {
					//Get the entity ID this ties to
					int eid = Integer.parseInt(data[0]);
					int target = Integer.parseInt(data[1]);

					//Store this sign
					entityChunks.getEntity(eid).triggersEid = target;
					if (signs.containsKey(eid)) {signs.get(eid).triggerEid = target;}
					System.out.println("Loaded trigger for eid: " + eid + " triggers->" + target);
				} catch (Exception e) {
					System.out.println("Failed to load line " + lineNum + " of triggers: " + line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

	public Collection<PlayerEntity> getPlayers() {
		return entityChunks.getPlayers();
	}

	@Override
	public boolean debugShowCommunication() {
		return getPlayers().size() < 2;	//2 or more players stop debug communication
	}

	public void updateEntity(Entity e) {
		entityChunks.updateEntity(e);
	}

	public void deleteEntity(int eid) {
		entityChunks.deleteEntity(eid);
	}

	public HashSet<Entity> getEntitiesAt(int x, int y) {
		return entityChunks.getEntitiesAt(x, y);
	}

	public void reindex(Entity entity) {
		entityChunks.reindex(entity);
	}
}
