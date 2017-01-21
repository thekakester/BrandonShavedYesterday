package entity;

import game.Game;

public abstract class EntitySpawner extends Entity{

	protected Entity child = null;
	
	public EntitySpawner(int id, int type) {
		super(id, type);
	}
	
	/**Create an entity for this spawner and return it.
	 * You shouldn't have to do anything else, just return it.
	 * No need to check any conditions
	 * @return The entity that gets created by this spawn
	 */
	protected abstract Entity createEntity();
	
	@Override
	public void update(Game g) {
		if (child == null || !child.isAlive) {
			child = createEntity();
			
			//Add this to the game class
			g.addEntity(child);
		}
	}
}
