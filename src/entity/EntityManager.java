package entity;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**This class defines entities and relays information to the client
 * 
 * @author midavis
 *
 */
public class EntityManager {
	ArrayList<EntityDefinition> definitions = new ArrayList<EntityDefinition>();
	
	public EntityManager() {
		int typeID = 0;
		
		/**These will make definitions more readable
		 * 
		 */
		int dur = 10;	//Default duration
		int width = 32; //Default width
		int height = 32;//Default height
		boolean collide = true;
		boolean nocollide = false;
		boolean save = true;
		boolean nosave = false;
		
		
		
		//EntityDefinition(id, collidable, savable, imageTag, x, y, width,height,duration
		add(new EntityDefinition(typeID++, nocollide, nosave, "objects", 288, 0, width, height, dur));		//NULL
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 96, 0, width, height, dur));	//Player
		last().useWalkingAnimation(96,0);
		add(new EntityDefinition(typeID++, collide, save, "everything", 0, 0, width, height, dur));			//Sign
		add(new EntityDefinition(typeID++, collide, save, "everything", 32, 0, width, height, dur));		//Gravestone
		add(new EntityDefinition(typeID++, nocollide, save, "everything", 0, 32, width, height, 4));		//Gem
		for (int frame = 1; frame < 8; frame++) {	//Add remaining frames
			last().addFrame(32*frame, 32);
		}
		add(new EntityDefinition(typeID++, nocollide, save, "everything", 0, 64, width, height, dur));		//Wall top back left
		
		//ETC
		//PS, feel free to re-assign type IDs if it makes loading easier.
		
	}
	
	public byte[] getBytes() {
		int length = 4;	//Number of entities
		
		//Count up the lengths
		for (EntityDefinition d : definitions) {
			length += d.sizeInBytes();
		}
		
		ByteBuffer bb = ByteBuffer.allocate(length);
		bb.putInt(definitions.size());
		
		for (EntityDefinition d : definitions) {
			bb.put(d.getBytes());
		}
		
		return bb.array();
	}

	/**Shorthand for getting last thing added**/
	private EntityDefinition last() {
		return definitions.get(definitions.size()-1);
	}
	
	/**Shorthand for adding something new**/
	private void add(EntityDefinition d) {
		definitions.add(d);
	}
}
