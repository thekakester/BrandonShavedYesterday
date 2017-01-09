package hackerGame;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class Game {
	public Map map;
	
	private int lastAddedEntityID = 99;
	private HashMap<Integer,Entity> entities = new HashMap<Integer,Entity>();

	//Constructor that calls Server.main() for convenience
	public static void main(String[] args) {Server.main(args); }

	public Game() {
		//Uncomment this line to generate a new map file
		map = new Map("default.map",false);
	}

	/**WARNING: Not thread safe!
	 * 
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
		ByteBuffer bb = ByteBuffer.allocate(((entities.size() * 3) + 1) * 4);
		bb.putInt(entities.size());
		for (Entity e : entities.values()) {
			bb.putInt(e.id);
			bb.putInt(e.x);
			bb.putInt(e.y);
		}
		return bb.array();
	}
}
