package entity;

import game.Game;

public class SkeletonEntity extends EntityWithBehavior {

	public SkeletonEntity(int id, int x, int y) {
		super(id, EntityType.SKELETON_ENTITY,x,y);
		this.isHostile = true;
	}

	@Override
	public void update(Game g) {
		super.update(g);
		if (this.followNearestPlayer(g, 10)) { return; }
		if (this.wander(g, spawnX, spawnY, 10, 10000)) { return; }
	}

}
