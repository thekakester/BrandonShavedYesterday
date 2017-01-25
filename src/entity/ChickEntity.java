package entity;

import game.Game;

public class ChickEntity extends EntityWithBehavior {
	public ChickEntity(int id, int x, int y) {
		super(id, EntityType.CHICK_ENTITY,x,y);
	}

	@Override
	public void update(Game g) {
		super.update(g);

		if (this.wander(g, spawnX, spawnY, 3, 2500)) { g.updateEntity(id); return; }
	}

}
