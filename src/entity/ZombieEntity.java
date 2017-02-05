package entity;

import game.Game;

public class ZombieEntity extends EntityWithBehavior {
	public long deathTime = -1;				//If positive, this is when the zombie will die
	
	public ZombieEntity(int id,int x, int y) {
		super(id, EntityType.ZOMBIE_ENEMY,x,y);
		this.isHostile = true;
	}
	
	
	@Override
	public void update(Game g) {
		super.update(g);
		
		if (deathTime > 0 && System.currentTimeMillis() > deathTime) { 
			g.deleteEntity(id);
			return;
		}
		
		if (this.followNearestPlayer(g, 10)) { return; }
		if (this.wander(g, spawnX, spawnY, 10, 10000)) { return; }
	}

}
