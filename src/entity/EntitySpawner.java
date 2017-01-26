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
	
	public EntitySpawner(int id, int type, int x, int y) {
		super(id, type,x,y);
	}
	
	
	/**Create an entity appropriate for this spawner
	 * 
	 * @return An entity that gets spawned by this spawner
	 */
	private Entity createEntity(Game g) {
		int id = g.getNewEntityId();
		
		if (type == EntityType.MUSHROOM_SPAWN) {
			return new MushroomEntity(id,this.x,this.y);
		}
		if (type == EntityType.CHICK_SPAWN) {
			return new ChickEntity(id,this.x,this.y);
		}
		if (type == EntityType.ROBOT_SPAWN) {
			return new RobotEntity(id,this.x,this.y);
		}
		if (type == EntityType.ZOMBIE_SPAWN) {
			return new ZombieEntity(id,this.x,this.y);
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
