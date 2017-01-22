package entity;

import javax.management.RuntimeErrorException;

import game.Game;

/**A simple entity spawner.  Can be customized as needed
 * 
 * @author midavis
 *
 */
public class EntitySpawner extends Entity{

	protected Entity child = null;
	
	public EntitySpawner(int id, int type) {
		super(id, type);
	}
	
	
	/**Create an entity appropriate for this spawner
	 * 
	 * @return An entity that gets spawned by this spawner
	 */
	private Entity createEntity(Game g) {
		int id = g.getNewEntityId();
		
		if (type == EntityType.SPAWNER_MUSHROOM) {
			return new MushroomEntity(id,this.x,this.y);
		}
		if (type == EntityType.SPAWNER_CHICK) {
			return new ChickEntity(id,this.x,this.y);
		}
		if (type == EntityType.SPAWNER_ROBOT) {
			return new RobotEntity(id,this.x,this.y);
		}
		
		throw new RuntimeException("Error: Cant spawn an entity from this spawner because no rules are defined of what to spawn");
	}
	
	@Override
	public void update(Game g) {
		if (child == null || !child.isAlive) {
			child = createEntity(g);
			System.out.println("Spawned entity");
			//Add this to the game class
			g.addEntity(child);
		}
	}
}
