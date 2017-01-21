package entity;

import java.nio.ByteBuffer;

import game.Game;

/**This is a BASE class.  If an entity is static, use this.
 * if it needs special params, override this class.
 * 
 * @author midavis
 *
 */
public class Entity {
	public boolean isAlive = true;			//If set to false, this should be untracked
	public final int id;
	public final int type;
	public int[] attributes  = new int[0];	//Dependent on the type of entity (eg.  HP)
	public int x,y;
	
	/**Create and return an entity based on its type
	 * 
	 * @param id The ID to assign this entity
	 * @param type The type of the entity
	 * @return The entity that was created
	 */
	public static Entity create(int id, int type) {
		if (EntityType.SPAWNERS.containsKey(type)) {
			return new EntitySpawner(id,type);
		}
		
		//It didn't fit one of the special cases?
		//Return a standard (static) entity
		return new Entity(id, type);
	}
	
	protected Entity(int id, int type) {
		this.id = id;
		this.type = type;
		
		//A very bad way at using spawners.  This should probably be re-imagined
		if (EntityType.SPAWNERS.containsKey(type)) {
			this.attributes = new int[] {EntityType.SPAWNERS.get(type),-1};	//SpawnType,id of spawned entity
		}
	}

	@Override
	public String toString() {
		return String.format("[Entity id:%d type:%d (x:%d,y:%d)]",id,type,x,y);
	}
	
	public byte[] bytes() {
		
		ByteBuffer bb = ByteBuffer.allocate(this.sizeInBytes());
		
		bb.putInt(id);
		bb.putInt(type);
		bb.putInt(x);
		bb.putInt(y);
		bb.putInt(attributes.length);
		
		for (int i = 0; i < attributes.length; i++) {
			bb.putInt(attributes[i]);
		}
		
		return bb.array();
	}

	public int sizeInBytes() {
		int length = 5;	//ID, type,x,y,attributes.length
		length += attributes.length;
		length *= 4;	//int = 4 bytes
		return length;
	}
	
	public void update(Game g) {
		//Static objects do nothing
	}
}
