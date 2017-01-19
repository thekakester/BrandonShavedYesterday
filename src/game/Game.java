package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;

import engine.GameBase;
import engine.Server;

public class Game extends GameBase {

	//This method starts the server and the game
	public static void main(String[] args) {new Server (new Game(),args);}

	public final String MAP = "default";
	
	public Map map;
	private int lastAddedEntityID = 99;	//Next entity should be id: 101
	private HashMap<Integer,Entity> entities = new HashMap<Integer,Entity>();

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

	public void updateEntity(int eid, int x, int y) {
		if (entities.containsKey(eid)) {
			Entity e = entities.get(eid);
			e.x = x;
			e.y = y;
		}
	}

	public byte[] serializeEntities() {

		//Count the size in bytes of our entites
		int size = 0;
		for (Entity e : entities.values()) {
			size += e.sizeInBytes();
		}

		//Add 2 more integers (8 bytes): ResponseType and entites.size();
		size += 8;

		ByteBuffer bb = ByteBuffer.allocate(size);
		bb.putInt(ResponseType.ENTITY_UPDATE);
		bb.putInt(entities.size());

		for (Entity e : entities.values()) {
			for (byte b : e.bytes()) {
				bb.put(b);
			}
		}
		return bb.array();
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
				entities.put(pid, new Entity(pid,EntityType.PLAYER));

				//Subscribe this player to the deltas!
				map.subscribe(pid);

				return concat(intToBytes(pid),map.serialize());
			}

			if (key.equals("entity")) {
				String[] args = value.split("\\|");
				updateEntity(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
				return null;	//Nothing to say back
			}

			if (key.equals("map")) {
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

			if (key.equals("update")) {
				//Return anything that might have changed
				int pid = Integer.parseInt(value);
				byte[] response = new byte[0];
				response = concat(response,serializeEntities());
				response = concat(response,map.getDeltaAsBytes(pid));
				return response;
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

	long lastSave = 0;	
	@Override
	public void run() {

		//Save the game every 10 seconds
		if (System.currentTimeMillis() - 10000 > lastSave) {
			save();
			lastSave = System.currentTimeMillis();
		}
	}

	/**Save the game
	 * 
	 */
	public void save() {
		map.save();	//Save the map
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
					Entity e = new Entity(id, Integer.parseInt(data[0]));
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
