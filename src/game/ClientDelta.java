package game;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;

import entity.Entity;

public class ClientDelta {

	public ClientDelta(int pid) {
		this.pid = pid;
	}

	/**Call this whenever an entity changes.  Added, modified, or removed
	 * 
	 */
	public void addEntity(Entity e) {
		//System.out.println(pid + " Added delta: (" + e.id + ") " + e.x + "," + e.y);
		this.changedEntities.add(e);
	}

	private ArrayList<String> undeliveredChatMessages = new ArrayList<String>();
	private HashSet<Entity> changedEntities = new HashSet<Entity>();
	private HashSet<MapDelta> changedMapTiles = new HashSet<MapDelta>();
	private HashSet<Integer> attackingEntities = new HashSet<Integer>();
	private HashSet<Integer> deadEntities = new HashSet<Integer>();
	private HashSet<Sign> notifications = new HashSet<Sign>();
	private int DEBUG_MAX_RESPONSESIZE = 500;
	final int pid;

	//Convert all our entity stuff 
	public byte[] getBytes() {
		//Count the size in bytes of our entites
		int size = 0;

		/**************
		 * STEP 1: Count bytes to prep buffer
		 ****************/
		//Add 2 more integers (8 bytes): ResponseType and entites.size();
		size += 8;
		for (Entity e : changedEntities) {
			size += e.sizeInBytes();
		}

		//Count size of our map deltas
		size += 8;		//Add 2 integers (8bytes): ResponseType and tiles.size()
		size += changedMapTiles.size() * (3*4);	//Each delta is 3 ints (row,col,type)

		//This is chat
		size += 8; //add 2 integers (8bytes): ResponseType and messages.length
		size += undeliveredChatMessages.size()*4; //Add Length Int for every message
		for(String s : undeliveredChatMessages ){
			size+= s.length()*2;
		}

		//Add entities that are attacking
		size += 8;	//Add 2 integers(8 bytes) ResponseType and attackingEntities.length
		size += 4 * attackingEntities.size();

		//Add entities that are dead
		size += 8;	//Add 2 integers(8 bytes) ResponseType and attackingEntities.length
		size += 4 * deadEntities.size();
		
		//Add notifications (each sign is self contained.  Contains its own header
		for (Sign s : notifications) {
			size += s.getSizeInBytes();
		}

		ByteBuffer bb = ByteBuffer.allocate(size);

		/**************
		 * STEP 2: Fill our buffer with what we need
		 ****************/ 
		bb.putInt(ResponseType.ENTITY_UPDATE);
		bb.putInt(changedEntities.size());

		for (Entity e : changedEntities) {
			for (byte b : e.bytes()) {
				bb.put(b);
			}
		}

		//Clear our cache
		changedEntities.clear();

		//PART 2--------------------
		//map deltas
		bb.putInt(ResponseType.MAP_UPDATE);

		//Debug stuff.  Limit the number of tile updates we can send at a time
		//int tilesToSend = DEBUG_MAX_RESPONSESIZE;
		//if (changedMapTiles.size() < tilesToSend) {
		//	tilesToSend = changedMapTiles.size();
		//}

		//Number of tiles we're sending
		bb.putInt(changedMapTiles.size());


		for (MapDelta d : changedMapTiles) {
			bb.putInt(d.row);
			bb.putInt(d.col);
			bb.putInt(d.type);
		}

		changedMapTiles.clear();
		//Clear what we sent
		//for (int i = 0; i < tilesToSend; i++) {
		//	changedMapTiles.remove(0);
		//}

		bb.putInt(ResponseType.CHAT);
		bb.putInt(undeliveredChatMessages.size());
		for(String s : undeliveredChatMessages){
			bb.putInt(s.length()); 
			for(char c : s.toCharArray()){
				bb.putChar(c);
			}
		}

		undeliveredChatMessages.clear();

		//ADD ATTACKING ENTITIES
		bb.putInt(ResponseType.ATTACKING_ENTITIES);
		bb.putInt(attackingEntities.size());
		for (int eid : attackingEntities) {
			bb.putInt(eid);
		}
		attackingEntities.clear();

		//ADD ATTACKING ENTITIES
		bb.putInt(ResponseType.DEAD_ENTITIES);
		bb.putInt(deadEntities.size());
		for (int eid : deadEntities) {
			bb.putInt(eid);
		}
		deadEntities.clear();
		
		//ADD NOTIFICATIONS
		//Each sign is self contained.  Contains responsetype and length
		for (Sign s : notifications) {
			bb.put(s.getBytes());
		}
		notifications.clear();

		return bb.array();

	}

	public void updateTile(int row, int col, int type) {
		changedMapTiles.add(new MapDelta(row, col, type));
	}

	public void addChat(String value) {
		// TODO Auto-generated method stub
		undeliveredChatMessages.add(value);
	}

	public void addAttackingEntity(int eid) {
		if (eid == this.pid) { return; }	//Dont add for ourself
		attackingEntities.add(eid);
	}

	public void addDeadEntity(int eid) {
		deadEntities.add(eid);
	}

	public void addNotification(Sign notification) {
		notifications.add(notification);
		
	}
}
