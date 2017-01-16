package game;

import java.nio.ByteBuffer;
import java.util.HashMap;

import engine.GameInterface;
import engine.Server;

public class Game implements GameInterface {

	//This method starts the server and the game
	public static void main(String[] args) {new Server (new Game(),args);}

	public Map map;
	private int lastAddedEntityID = 99;	//Next entity should be id: 101
	private HashMap<Integer,Entity> entities = new HashMap<Integer,Entity>();

	public Game() {
		//Set second argument to true to ALWAYS generate a new map file
		map = new Map("default.map",false);
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
		entities.put(id, new Entity(id));	//Add something so we don't lose this ID to a race condition
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
		ByteBuffer bb = ByteBuffer.allocate(((entities.size() * 3) + 2) * 4);
		bb.putInt(ResponseType.ENTITY_UPDATE);
		bb.putInt(entities.size());
		
		for (Entity e : entities.values()) {
			bb.putInt(e.id);
			bb.putInt(e.x);
			bb.putInt(e.y);
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

	//This gets called every 10 seconds while players are connected
	@Override
	public void save() {
		map.save();
	}

}
