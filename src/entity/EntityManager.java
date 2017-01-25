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
		
		add(new EntityDefinition(typeID++, false, false, "objects", 288, 0, 32, 32, 10));	//NULL
		add(new EntityDefinition(typeID++, false, false, "characters", 96, 0, 32, 32, 10));	//Player
		last().useWalkingAnimation(96,0);
		add(new EntityDefinition(typeID++, true, true, "everything", 0, 0, 32, 32, 10));	//Sign
		
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
