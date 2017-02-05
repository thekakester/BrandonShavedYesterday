package entity;

import game.Game;

public class GhostEntity extends EntityWithBehavior {

	public GhostEntity(int id, int x, int y) {
		super(id, EntityType.GHOST_ENTITY,x,y);
		this.isHostile = true;
	}

	@Override
	public void update(Game g) {
		super.update(g);

		//Use a prebuilt behavior
		if (this.wander(g, spawnX, spawnY, 2, 1000)) { return; }
	}

}
