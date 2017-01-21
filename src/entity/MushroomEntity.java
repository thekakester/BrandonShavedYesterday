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
		//Move around
		y--;
		if(y < 0) { this.isAlive = false; }
		g.updateEntity(id);
	}

}
