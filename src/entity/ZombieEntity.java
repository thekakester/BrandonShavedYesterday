package entity;

import game.Game;

public class ZombieEntity extends EntityWithBehavior {
	
	public ZombieEntity(int id,int x, int y) {
		super(id, EntityType.ZOMBIE_ENEMY,x,y);
	}
	
	
	@Override
	public void update(Game g) {
		super.update(g);
		if (this.wander(g, spawnX, spawnY, 10, 10000)) { g.updateEntity(id); return; }
	}

}
