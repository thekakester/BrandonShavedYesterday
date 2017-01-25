package entity;

import java.lang.management.GarbageCollectorMXBean;
import java.util.LinkedList;

import game.Game;
import game.Pathfinding;

public class MushroomEntity extends EntityWithBehavior {

	public MushroomEntity(int id, int x, int y) {
		super(id, EntityType.MUSHROOM);
		this.x = spawnX = x;
		this.y = spawnY = y;
	}

	private long lastTriggered = System.currentTimeMillis();
	private int spawnX, spawnY;

	@Override
	public void update(Game g) {
		super.update(g);

		//Use a prebuilt behavior
		if (this.wander(g, spawnX, spawnY, 6, 1000)) { return; }
	}

}
