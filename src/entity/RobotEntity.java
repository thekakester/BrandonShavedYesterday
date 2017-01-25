package entity;

import java.lang.management.GarbageCollectorMXBean;
import java.util.LinkedList;

import game.Game;
import game.Pathfinding;

public class RobotEntity extends Entity {
	
	public RobotEntity(int id, int x, int y) {
		super(id, EntityType.ROBOT_ENTITY);
		this.x = spawnX = x;
		this.y = spawnY = y;
	}
	
	private long lastTriggered = System.currentTimeMillis();
	private int spawnX, spawnY;
	
	@Override
	public void update(Game g) {
		super.update(g);

		//Follow player if less than 5 blocks away
		if (!getPath().isEmpty()) {
			lastTriggered = System.currentTimeMillis();
			return;
		}

		//If we're away from our spawn and it's been 5 seconds since we've been triggered, go home
		if ((x != spawnX || y != spawnY) && System.currentTimeMillis() - lastTriggered > 5000) {
			this.setPath(Pathfinding.findPath(g, x, y, spawnX,spawnY, 3000));
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
		LinkedList<Byte> path = Pathfinding.findPath(g, x, y, closestPlayer.x,closestPlayer.y, 30);
		if (path.size() > 0) {path.removeLast();}
		setPath(path);

		if (path.size() > 0) {
			g.updateEntity(id);
		}
	}

}
