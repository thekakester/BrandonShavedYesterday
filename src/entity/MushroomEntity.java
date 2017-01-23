package entity;

import java.lang.management.GarbageCollectorMXBean;
import java.util.LinkedList;

import game.Game;
import game.Pathfinding;

public class MushroomEntity extends Entity {
	
	public MushroomEntity(int id, int x, int y) {
		super(id, EntityType.MUSHROOM);
		this.x = spawnX = x;
		this.y = spawnY = y;
	}
	
	private long lastTriggered = System.currentTimeMillis();
	private int spawnX, spawnY;
	
	@Override
	public void update(Game g) {
		//If we're away from our spawn and it's been 5 seconds since we've been triggered, go home
		if ((x != spawnX || y != spawnY) && System.currentTimeMillis() - lastTriggered > 5000) {
			 path = Pathfinding.findPath(g, x, y, spawnX,spawnY, 3000);
		}
		
		//Follow player if less than 5 blocks away
		if (!path.isEmpty()) {
			lastTriggered = System.currentTimeMillis();
			
			//Follow it until empty
			byte b = path.removeFirst();
			if (b==0) { y--; }
			else if (b==1) { y++; }
			else if (b==2) { x--; }
			else if (b==3) { x++; }
			g.updateEntity(id);
			return;
		}
		
		
		//Closest player
		PlayerEntity closestPlayer = null;
		double closestDist = Double.MAX_VALUE;
		for (PlayerEntity p : g.getPlayers()) {
			int dx = p.x-x;
			int dy = p.y-y;
			double dist = (dx*dx)+(dy*dy);
			if (dist < closestDist) {
				closestDist = dist;
				closestPlayer = p;
			}
		}
		
		if (closestPlayer == null) {return;}
		
		//if we're closer than 6 tiles, follow them
		if (closestDist > 6*6) { return;}
		
		//Get a path to the closest player
		path = Pathfinding.findPath(g, x, y, closestPlayer.x,closestPlayer.y, 30);
	}

}
