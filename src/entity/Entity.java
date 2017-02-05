package entity;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import game.Game;
import game.Map;

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
	public int x,y;
	public int chunkX, chunkY;
	public int oldChunkX, oldChunkY;		//Used for reindexing.  Remove from old, add to new
	private long pathStartTime = 0;			//When was the path created
	private LinkedList<Byte> path = new LinkedList<Byte>();
	public final EntityDefinition definition;
	public boolean isHostile = false;	//If true, will damage player when they walk in front
	private boolean calculatedChunks = false;	//This is required for static items since they never run update();
	public int triggersEid = -1;			//When set, this will be the object that this object triggers.

	/**Create and return an entity based on its type
	 * 
	 * @param id The ID to assign this entity
	 * @param type The type of the entity
	 * @return The entity that was created
	 */
	public static Entity create(int id, int type, int x, int y) {
		if (EntityType.SPAWNERS.contains(type)) {
			return new EntitySpawner(id,type,x,y);
		}

		if (type == EntityType.PLAYER) {
			return new PlayerEntity(id, type,x,y);
		}

		if (type == EntityType.ZOMBIE_ENEMY) {
			return new ZombieEntity(id,x,y);
		}

		//It didn't fit one of the special cases?
		//Return a standard (static) entity
		return new Entity(id, type, x, y);
	}

	protected Entity(int id, int type, int x, int y) {
		this.id = id;
		this.type = type;
		this.definition = EntityManager.definitions.get(type);
		this.x = x; this.y = y;
	}

	@Override
	public String toString() {
		return String.format("[Entity id:%d type:%d (x:%d,y:%d)]",id,type,x,y);
	}

	public byte[] bytes() {
		//if not alive, set x and y to -1 (lets client know)
		if (!isAlive) { x = y = -1; }


		ByteBuffer bb = ByteBuffer.allocate(this.sizeInBytes());

		bb.putInt(id);
		bb.putInt(type);
		bb.putInt(x);
		bb.putInt(y);

		//Add flags.  Contains isHostile
		byte flags = 0;
		if (isHostile) { flags |= 0x1; }
		bb.put(flags);


		bb.putInt(path.size());
		for (Byte b : path) {
			bb.put(b);
		}

		//Elapsed time since path created
		bb.putInt(getPathElapsedTime());

		return bb.array();
	}

	private int getPathElapsedTime() {
		int timeElapsed = (int)(System.currentTimeMillis()-pathStartTime);
		if (timeElapsed < 0) { return 0; }
		return timeElapsed;
	}

	public int sizeInBytes() {
		int length = 6;	//ID, type,x,y,path.length,elapsedTimeSincePathCreated
		length *= 4;	//int = 4 bytes
		length += 1;	//Flags (1 byte)

		//Path is in bytes, not int so we add it after multiplying by 4
		length += path.size();

		return length;
	}

	public void update(Game g) {
		if (!calculatedChunks) {
			if (calculateChunks() && g!=null){	//G is null when creating static objects
				g.reindex(this);
			}
		}	//Everythign must do this at least once
		if (path.isEmpty()) { return; }

		//Update x and y based on our path
		long timeElapsed = System.currentTimeMillis() - pathStartTime;


		//WARNING: THis must be exactly the same as the client!
		int tilesPerSecond = 6;
		float millisecondsPerTile = 1000f/tilesPerSecond;
		//How many tiles have we completely gone over?
		//Note that path[-1] is where we currently are.  We're not starting at path[0]
		float tilesPast = timeElapsed / millisecondsPerTile;

		if (tilesPast > path.size()) { tilesPast = path.size(); }


		int mostRecentTile = (int)tilesPast;
		float tween = tilesPast-mostRecentTile;


		//Update our position up to our last fully met tile
		for (int i = 0; i < mostRecentTile; i++) {
			int direction = path.removeFirst();
			if (direction == 0) { this.y--; }
			if (direction == 1) { this.y++; }
			if (direction == 2) { this.x--;}
			if (direction == 3) { this.x++; }
		}
		
		//Are we standing on a despawner?
		for (Entity e : g.getEntitiesAt(x, y)) {
			if (e.type == EntityType.AI_DESPAWNER) {
				if (this.isHostile) {
					g.deleteEntity(this.id);
					break;
				}
			}
		}

		//If our chunk changed, update us in the entityChunker
		if (calculateChunks()) {
			//We need to re-index this entity
			g.reindex(this);
		}
		

		//Update our start time to
		pathStartTime += (int)(mostRecentTile * millisecondsPerTile);
	}

	private boolean calculateChunks() {
		calculatedChunks = true;	//So we don't run this if we don't ahve to
		
		oldChunkX = chunkX;
		oldChunkY = chunkY;
		//Get our chunk
		chunkX = x / Map.chunkCols;
		if (x < 0) { chunkX--; }
		chunkY = y / Map.chunkCols;
		if (y < 0) { chunkY--; }
		
		return oldChunkX != chunkX || oldChunkY != chunkY;
	}

	protected void setPath(LinkedList<Byte> path) {
		this.path = path;
		this.pathStartTime = System.currentTimeMillis();
	}

	protected LinkedList<Byte> getPath() {
		return path;
	}

	/**Append this to the end of the path if it exists
	 * Create a new one otherwise
	 */
	public void appendToPath(byte direction) {
		if(!getPath().isEmpty()) {
			getPath().addLast(direction);
		} else {
			LinkedList<Byte> p = new LinkedList<Byte>();
			p.add(direction);
			setPath(p);
		}

		//If the path is larger than 5, we're too far behind.
		//Just teleport
		if (getPath().size() > 5) {
			while (!path.isEmpty()) {
				byte b = path.removeFirst();
				if (b == 0) { this.y--; }
				if (b == 1) { this.y++; }
				if (b == 2) { this.x--; }
				if (b == 3) { this.x++; }
			}
		}
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Entity) {
			return ((Entity)o).id == this.id;
		}
		return false;
	}

	public void clearPath() {
		this.path.clear();
	}
}
