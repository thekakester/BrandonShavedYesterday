package entity;

import java.util.LinkedList;
import java.util.Random;

import game.Game;
import game.Pathfinding;

/**A collection of reusable entity behaviors.
 * Extend this class instead of entity to get access to these functions
 * 
 * @author midavis
 *
 */
public class EntityWithBehavior extends Entity {
	protected int spawnX,spawnY;
	protected EntityWithBehavior(int id, int type, int x, int y) {
		super(id, type,x,y);
		spawnX = x;
		spawnY = y;
	}

	private Random random = new Random();
	private long waitStartedTime = 0;
	
	/**Wander around a point randomly, then wait before wandering again
	 * 
	 * @param x Center of wandering
	 * @param y Center of wandering
	 * @param distance Furthest distance to wander away from center
	 * @param wait How long to wait between wanders
	 * @return true if a path was set.  Parent code should halt when true is returned.  Response of false means no action was taken
	 */
	public boolean wander(Game g, int x, int y, int distance, long wait) {
		//If there's a path running, let it go
		if (!getPath().isEmpty()) { return true; }
		
		//There's no path.  Are we waiting?
		if (waitStartedTime == 0) {
			//We haven't started a timer yet!
			//Start it now
			waitStartedTime = System.currentTimeMillis();
		}
		
		//Wait the correct amount of time
		if (System.currentTimeMillis()-waitStartedTime < wait) { 
			return true;
		}
		
		LinkedList<Byte> path = new LinkedList<Byte>();
				
		//Do 10 attempts at finding a valid place, then give up
		for (int attempt = 0; attempt < 10; attempt++) {
			int destX = x + (random.nextInt((distance*2)+1)-distance);
			int destY = y + (random.nextInt((distance*2)+1)-distance);
			//Try to find a path here
			//Dist *= 4 because we can be going from one corner to the other
			path = Pathfinding.findPath(g, this.x, this.y, destX, destY, distance*4,true);	//Pass true to specify we can't pass AntiAI tiles
			if (path.size() > 0) { break; }
		}
		
		//Did we find a path
		if (path.size() > 0) {
			this.setPath(path);
			g.updateEntity(this);
			waitStartedTime = 0;	//Reset wait timer
			return true;	//Tell parent we can stop
		}
		
		//Hmm.. we can't find a path.  We're stuck
		return false;
	}
	
	/**Attempt to attack the entity specified
	 * 
	 * @param g Game object
	 * @return
	 */
	public boolean followNearestPlayer(Game g, int maxDistance) {
		if (!this.getPath().isEmpty()) { return true; }
		maxDistance *= maxDistance;	//We use distSquared.  Its faster than using sqrt()
		
		//Find the nearest player
		PlayerEntity p = getNearestPlayer(g, x, y, maxDistance);
		if (p!= null) {
			//Get a path to them
			this.setPath(Pathfinding.findPath(g, x, y, p.x, p.y, maxDistance, true));
			if (!getPath().isEmpty()) { g.updateEntity(this); }
			return (!this.getPath().isEmpty());
		}
		
		return false;
	}
	
	/**Get the nearest player to a specific point
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @return
	 */
	private PlayerEntity getNearestPlayer(Game g, int x, int y, int maxDistance) {
		int closestDistSquared = Integer.MAX_VALUE;
		PlayerEntity closest = null;
		for (PlayerEntity p : g.getPlayers()) {
			int distSquared = ((p.x-x)*(p.x-x))+((p.y-y)*(p.y-y));
			if (distSquared <= maxDistance && distSquared < closestDistSquared) {
				closestDistSquared = distSquared;
				closest = p;
			}
		}
		
		return closest;
	}
	
	public String pathToText(LinkedList<Byte> path) {
		String s = "";
		for (Byte b : path) {
			if (b == 0) { s += "u ";}
			if (b == 1) { s += "d ";}
			if (b == 2) { s += "l ";}
			if (b == 3) { s += "r ";}
		}
		
		return s;
	}
}
