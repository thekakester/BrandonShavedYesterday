package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import engine.GameBase;
import engine.Server;
import entity.Entity;
import entity.EntityType;

public class Game extends GameBase {

	//This method starts the server and the game
	public static void main(String[] args) {new Server (new Game(),args);}

	public final String MAP = "default";

	public Map map;
	private int lastAddedEntityID = 99;	//Next entity should be id: 101
	private HashMap<Integer,Entity> entities = new HashMap<Integer,Entity>();
	private HashMap<Integer,ClientDelta> clientDeltas = new HashMap<Integer,ClientDelta>();

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
	 * @param eid The EID that got updated.
	 */
	public void updateEntity(int eid) {
		if (entities.containsKey(eid)) {
			Entity e = entities.get(eid);
			
			//Make sure this is added to deltas so we tell our clients
			for (ClientDelta delta : clientDeltas.values()) {
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
		}
		entities.remove(eid);
	}

	public byte[] getClientDelta(int pid) {
		if (!clientDeltas.containsKey(pid)) {
			ClientDelta d = new ClientDelta();
			clientDeltas.put(pid, d);

			//They don't know anything yet!  Tell them everything
			for (Entity e : entities.values()) {
				d.addEntity(e);
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
				entities.put(pid, Entity.create(pid,EntityType.PLAYER));

				//Subscribe this player to the deltas!
				map.subscribe(pid);

				return concat(intToBytes(pid),map.serialize());
			}

			if (key.equalsIgnoreCase("entity")) {
				String[] args = value.split("\\|");
				int eid = Integer.parseInt(args[0]);
				int x = Integer.parseInt(args[1]);
				int y = Integer.parseInt(args[2]);
				Entity e = entities.get(eid);
				e.x = x;
				e.y = y;
				updateEntity(eid);
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
				byte[] response = new byte[0];
				response = concat(response,getClientDelta(pid));
				response = concat(response,map.getDeltaAsBytes(pid));
				return response;
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
						if (EntityType.STATIC_ENTITIES.contains(e.type) && e.x == x && e.y == y) {
							deleteEntity(e.id);	//Gets cleaned up by server
						}
					}
					
					return null;
				}

				//Get an ID and create the entity here
				int id = getNewEntityId();
				Entity e = Entity.create(id, Integer.parseInt(args[0]));
				e.x = Integer.parseInt(args[1]);
				e.y = Integer.parseInt(args[2]);
				addEntity(e);
				return null;	//The user will get the change with update
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
		return 300;
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
				entities.remove(eid);
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
			PrintWriter pw = new PrintWriter(MAP + ".entities");
			pw.println("#Autogenerated");
			for (Entity e : entities.values()) {
				//If it is an UNCHANGING entity, save it
				//This includes static objects and non-moving things
				//EG: PLAYER IS NOT STATIC, Nor ar enemies (but spawners are)
				if (EntityType.STATIC_ENTITIES.contains(e.type)) {
					pw.println(e.type + " " + e.y + " " + e.x);
				}
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() {
		//Set second argument to true to ALWAYS generate a new map file
		map = new Map(MAP + ".map",false);

		//Load Entities
		System.out.println("Loading entities");	
		try {
			File entitiesFile = new File(MAP + ".entities");
			if (!entitiesFile.exists()) { entitiesFile.createNewFile(); }
			Scanner scanner = new Scanner(entitiesFile);
			int lineNum = 0;

			while (scanner.hasNextLine()) {
				lineNum++;
				String line = scanner.nextLine().trim();
				String data[] = line.split(" ");
				if (data.length < 3 || data[0].startsWith("#")) { continue; }
				try {
					//Get a new entity ID
					int id = getNewEntityId();
					Entity e = Entity.create(id, Integer.parseInt(data[0]));
					e.y = Integer.parseInt(data[1]);	//ROW
					e.x = Integer.parseInt(data[2]);	//Col
					entities.put(id, e);
					System.out.println("Loaded entity: " + e);
				} catch (Exception e) {
					System.out.println("Failed to load line " + lineNum + " of entites: " + line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
