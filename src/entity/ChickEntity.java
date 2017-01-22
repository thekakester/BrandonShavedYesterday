package entity;

import java.lang.management.GarbageCollectorMXBean;
import java.util.LinkedList;

import game.Game;
import game.Pathfinding;

public class ChickEntity extends Entity {
	private LinkedList<Byte> path = new LinkedList<Byte>();
	public ChickEntity(int id, int x, int y) {
		super(id, EntityType.CHICK);
		this.x = x;
		this.y = y;
	}

	@Override
	public void update(Game g) {
		
		if (path.isEmpty()) {
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
			if (closestDist > 3*3) { return; }	//3 squared

			//Try to run the opposite direction of the player
			int goalX = x + (x-closestPlayer.x);
			int goalY = y + (y-closestPlayer.y);
			
			//path to this
			path = Pathfinding.findPath(g, x, y, goalX, goalY, 6);
		}
		
		//Avoid player if less than 3 blocks away
		if (!path.isEmpty()) {
			//Follow it until empty
			byte b = path.removeFirst();
			if (b==0) { y--; }
			else if (b==1) { y++; }
			else if (b==2) { x--; }
			else if (b==3) { x++; }
			g.updateEntity(id);
			return;
		}


	
		

	}

}
