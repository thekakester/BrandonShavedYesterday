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
	
	protected EntityWithBehavior(int id, int type) {
		super(id, type);
	}

	Random random = new Random();
	
	
	long waitStartedTime = 0;
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
			int destX = x + (random.nextInt(distance*2)-distance);
			int destY = y + (random.nextInt(distance*2)-distance);
			//Try to find a path here
			//Dist *= 4 because we can be going from one corner to the other
			path = Pathfinding.findPath(g, this.x, this.y, destX, destY, distance*4);
			if (path.size() > 0) { break; }
		}
		
		//Did we find a path
		if (path.size() > 0) {
			this.setPath(path);
			g.updateEntity(id);
			waitStartedTime = 0;	//Reset wait timer
			return true;	//Tell parent we can stop
		}
		
		//Hmm.. we can't find a path.  We're stuck
		return false;
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
