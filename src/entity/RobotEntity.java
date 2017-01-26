package entity;

import java.util.LinkedList;

import game.Game;
import game.Pathfinding;

public class RobotEntity extends EntityWithBehavior {
	
	public RobotEntity(int id, int x, int y) {
		super(id, EntityType.ROBOT_ENTITY,x,y);
	}
	
	@Override
	public void update(Game g) {
		super.update(g);

		if (this.wander(g, spawnX, spawnY, 1, 2000)) { return; }
	}

}
