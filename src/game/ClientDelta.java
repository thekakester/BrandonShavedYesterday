package game;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import entity.Entity;

public class ClientDelta {

	/**Call this whenever an entity changes.  Added, modified, or removed
	 * 
	 */
	public void addEntity(Entity e) {
		this.changedEntities.add(e);
	}

	private ArrayList<Entity> changedEntities = new ArrayList<Entity>();


	//Convert all our entity stuff 
	public byte[] getBytes() {
		//Count the size in bytes of our entites
		int size = 0;
		for (Entity e : changedEntities) {
			size += e.sizeInBytes();
		}

		//Add 2 more integers (8 bytes): ResponseType and entites.size();
		size += 8;

		ByteBuffer bb = ByteBuffer.allocate(size);
		bb.putInt(ResponseType.ENTITY_UPDATE);
		bb.putInt(changedEntities.size());

		for (Entity e : changedEntities) {
			for (byte b : e.bytes()) {
				bb.put(b);
			}
		}
		
		//Clear our cache
		changedEntities.clear();
		
		return bb.array();
	}
}
