package entity;

import java.lang.management.GarbageCollectorMXBean;
import java.util.LinkedList;

import game.Game;
import game.Pathfinding;

public class ZombieEntity extends Entity {
	
	public ZombieEntity(int id) {
		super(id, EntityType.ZOMBIE_ENEMY);
	}
	
	private long lastTriggered = System.currentTimeMillis();
	
	@Override
	public void update(Game g) {
		super.update(g);

		//Follow player if less than 5 blocks away
		if (!getPath().isEmpty()) {
			lastTriggered = System.currentTimeMillis();
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
		if (closestDist > 20*20) { return;}

		//Get a path to the closest player
		LinkedList<Byte> path = Pathfinding.findPath(g, x, y, closestPlayer.x,closestPlayer.y, 60);
		if (path.size() > 0) {path.removeLast();}
		setPath(path);

		if (path.size() > 0) {
			g.updateEntity(id);
		}
	}

}
