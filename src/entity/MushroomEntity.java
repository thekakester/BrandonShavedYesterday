package entity;

import java.lang.management.GarbageCollectorMXBean;

import game.Game;

public class MushroomEntity extends Entity {
	
	public MushroomEntity(int id, int x, int y) {
		super(id, EntityType.MUSHROOM);
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void update(Game g) {
		//Follow player if less than 5 blocks away
		
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
		if (closestDist < 1) { return; }
		if (closestDist > 6*6) { return; }	//6 squared
		
		//Move toward them
		int dX = closestPlayer.x - x;
		int dY = closestPlayer.y - y;
		
		//Move with whatever is the furthest
		if (Math.abs(dY) > Math.abs(dX)) {
			//Move with y
			y += dY / Math.abs(dY);	//Snaps value to -1 or 1.  Yay math
		} else {
			//Move with x value
			x += dX / Math.abs(dX);	//Snaps value to -1 or 1.  Yay math
		}
		
		g.updateEntity(id);
	}

}
