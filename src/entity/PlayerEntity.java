package entity;

import game.Game;

public class PlayerEntity extends Entity{

	private long lastUpdate = System.currentTimeMillis();	//If a player is inactive for 5 seconds, kill em
	
	protected PlayerEntity(int id, int type,int x, int y) {
		super(id, type,x,y);
	}
	
	@Override
	public void update(Game g) {
		//Kill if idle for 5 seconds
		if (System.currentTimeMillis() - lastUpdate > 5000) {
			System.out.println("Player " + id + " disconnected.");
			g.deleteEntity(id);
			
			//Add a zombie in its place
			Entity zombie = Entity.create(g.getNewEntityId(), EntityType.ZOMBIE_ENEMY,x,y);
			g.addEntity(zombie);
		}
	}

	public void refresh() {
		lastUpdate = System.currentTimeMillis();
	}
	
}
