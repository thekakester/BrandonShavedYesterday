package game;

import java.awt.Point;
import java.awt.image.ConvolveOp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import entity.Entity;
import entity.EntityType;
import entity.PlayerEntity;

/**This is designed to handle entities by grouping them into chunks
 * It will manage them appropriately
 * It separates them based on dynamic and static entities.
 * Dynamic entities will be updated each time the server thread runs, but static will not
 * @author midavis
 *
 */
public class EntityChunks {
	private HashMap<Integer,Entity> dynamicEntites = new HashMap<Integer,Entity>();	//Updated by game
	private HashMap<Integer,Entity> staticEntites = new HashMap<Integer,Entity>();	//Update() is never called
	private HashSet<Integer> entityIDs = new HashSet<Integer>();					//All used entity ID numbers
	private HashMap<Integer,HashMap<Integer, HashSet<Entity>>> entityChunks = new HashMap<Integer,HashMap<Integer, HashSet<Entity>>>();	//Sets of entities at agiven x,y chunk
	private int lastAddedEntityID = 99;	//Next entity should be id: 100
	private final HashMap<Integer,ClientDelta> clientDeltas;
	private HashMap<Integer,PlayerEntity> players = new HashMap<Integer,PlayerEntity>();	//Shortcut for getting player objects. (used moslty in AI)
	private HashSet<Integer> deadEids = new HashSet<Integer>();	//IDs that need to be cleaned up when we're not in a loop
	
	
	public EntityChunks(HashMap<Integer,ClientDelta> clientDeltas) {
		this.clientDeltas = clientDeltas;
	}

	/**Add any entity, static or dynamic
	 * 
	 * @param e
	 */
	public void addEntity(Entity e) {
		if (!e.definition.saveable || e.definition.isSpawner) {
			this.addDynamicEntity(e);
		} else {
			e.update(null);//Forces it to update its chunk position
			this.addStaticEntity(e);
		}

		//Tell all the clients about this
		updateEntity(e);

		//If it's a player, track it
		if (e.type == EntityType.PLAYER) {
			players.put(e.id, (PlayerEntity)e);
		}
		
		this.entityIDs.add(e.id);
	}

	public Entity getDynamicEntity(int id) {
		return this.dynamicEntites.get(id);
	}

	private void addStaticEntity(Entity e) {
		this.staticEntites.put(e.id,e);
		addEntityToChunk(e);
	}

	private void addDynamicEntity(Entity e) {
		this.dynamicEntites.put(e.id,e);
		addEntityToChunk(e);
	}

	/**Store all entities, indexed by their chunk
	 * 
	 * @param e
	 */
	private void addEntityToChunk(Entity e) {
		if (!entityChunks.containsKey(e.chunkY)) {
			entityChunks.put(e.chunkY, new HashMap<Integer,HashSet<Entity>>());	//Create if it doesn't exist yet
		}

		HashMap<Integer,HashSet<Entity>> rowOfChunks = entityChunks.get(e.chunkY);
		if (!rowOfChunks.containsKey(e.chunkX)){
			rowOfChunks.put(e.chunkX, new HashSet<Entity>());
		}

		HashSet<Entity> entitiesInChunk = rowOfChunks.get(e.chunkX);
		entitiesInChunk.add(e);
	}

	public int getNewEntityId() {
		int id = lastAddedEntityID + 1;
		while (entityIDs.contains(id)) {
			id++;
		}
		entityIDs.add(id);	//Add something so we don't lose this ID to a race condition
		lastAddedEntityID = id;
		return id;
	}


	/**Mark this entity as updated so that clients learn about it
	 * Example usage: move entity e up:
	 * 		e.y--;				//Move it
	 * 		updateEntity(e.id); //Make sure we tell clients that it changed
	 * 
	 * @param eid The entity ID to udate
	 */
	public void updateEntity(int eid) {
		updateEntity(eid,false);
	}

	public void updateEntity(Entity e) {
		updateEntity(e,false);
	}

	/**Mark this entity as updated so that clients learn about it
	 * Example usage: move entity e up:
	 * 		e.y--;				//Move it
	 * 		updateEntity(e.id); //Make sure we tell clients that it changed
	 * This only works on dynamic entities!
	 * 
	 * @param eid The EID that got updated.
	 * @param forcePlayerUpdate Default false.  If TRUE, this will force the player to update their own position.  Default false.
	 */
	public void updateEntity(int eid, boolean forcePlayerUpdate) {
		Entity e = getEntity(eid);
		if (e==null) {return; }
		updateEntity(e,forcePlayerUpdate);
	}

	public void updateEntity(Entity e, boolean forcePlayerUpdate) {
		//Create a point for this entity to check its chunk
		Point chunk = new Point(e.chunkX,e.chunkY);

		//Make sure this is added to deltas so we tell our clients
		for (ClientDelta delta : clientDeltas.values()) {
			if (delta.pid == e.id && !forcePlayerUpdate) { continue; }	//Don't tell player about themself (unless forced)
			//Skip if in different chunks
			if (delta.player.getCachedChunks().contains(chunk) || (forcePlayerUpdate&&delta.pid == e.id)) {
				delta.addEntity(e);
			}
		}
	}

	/**Marks an entity as deleted.  This will tell the clients that the entitiy is dead
	 * It will be actually deleted the next time the gameThread runs.
	 * @param eid
	 */
	public void deleteEntity(int eid) {
		Entity e = getEntity(eid);

		//Tell the client this is kill
		e.isAlive = false;
		updateEntity(eid);
		
		//Mark this for deletion
		this.deadEids.add(eid);

		//If the player died, remove them
		if (e.type == EntityType.PLAYER) {
			players.remove(e.id);
		}
	}

	/**Get an entity by its eid
	 * 
	 * @param eid
	 * @return NULL if no entity by this id
	 */
	public Entity getEntity(int eid) {
		if (!entityIDs.contains(eid)) { return null; }
		Entity e = dynamicEntites.get(eid);
		if (e == null) {
			e = staticEntites.get(eid);
		}
		
		return e;
	}

	public boolean containsPlayer(int id) {
		return players.containsKey(id);
	}

	/**Get a set of entities at a given point
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public HashSet<Entity> getEntitiesAt(int x, int y) {
		Point[] coords = EntityChunks.convertToChunkCoords(x, y);
		x = coords[1].x;
		y = coords[1].y;

		HashSet<Entity> entities = getEntitiesInChunk(coords[0].x, coords[0].y);

		//Get the entities that are on the position we are looking for
		HashSet<Entity> onPoint = new HashSet<>();
		for (Entity e : entities) {
			if (e.x == x && e.y==y) {
				onPoint.add(e);
			}
		}
		return onPoint;
	}

	/**Get a set of entities in a given chunk
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public HashSet<Entity> getEntitiesInChunk(int x, int y) {

		HashMap<Integer,HashSet<Entity>> rowOfChunks = entityChunks.get(y);
		if (rowOfChunks == null) { return new HashSet<Entity>(); }	//Nothing

		HashSet<Entity> entities = rowOfChunks.get(x);
		if (entities == null) { return new HashSet<Entity>(); }	//Nothing

		return entities;
	}

	/**Returns a 2D array of points.
	 * The first point represents the chunk coords, the second represents the coordinates within that chunk
	 * @param x
	 * @param y
	 * @return
	 */
	public static Point[] convertToChunkCoords(int x, int y) {
		int chunkX = x / Map.chunkCols;
		int chunkY = y / Map.chunkRows;
		if (x < 0) {chunkX--; x+= Map.chunkCols;}
		if (y < 0) {chunkY--; y+= Map.chunkRows;}
		return new Point[] {new Point(chunkX, chunkY), new Point(x,y)};
	}

	/**Delete anything from memory that is considered dead
	 * 
	 */
	public void removeDeadEntities() {
		HashSet<Entity> deadEntities = new HashSet<Entity>();
		
		//Clear them out!
		for (int eid : deadEids) {
			//One of the below will add null.  Beware of this!
			deadEntities.add(staticEntites.remove(eid));
			deadEntities.add(dynamicEntites.remove(eid));
			entityIDs.remove(eid);
		}
		
		//Now remove these entities from our chunk index
		for (Entity e : deadEntities) {
			Point[] coords = convertToChunkCoords(e.x, e.y);
			getEntitiesInChunk(coords[0].x,coords[0].y).remove(e);
		}
	}

	public HashSet<Integer> getEntityIds() {
		return entityIDs;
	}

	public Collection<PlayerEntity> getPlayers() {
		return this.players.values();
	}

	public Collection<Entity> getDynamicEntitiesClone() {
		HashSet<Entity> cloned = new HashSet<Entity>();
		cloned.addAll(dynamicEntites.values());
		return cloned;
	}

}
