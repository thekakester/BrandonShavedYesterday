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
	private long cooldownStart = 0;

	public EntitySpawner(int id, int type, int x, int y) {
		super(id, type,x,y);
	}


	/**Create an entity appropriate for this spawner
	 * 
	 * @return An entity that gets spawned by this spawner
	 */
	private Entity createEntity(Game g) {
		int id = g.getNewEntityId();
		Entity e = null;
		
		if (type == EntityType.MUSHROOM_SPAWN) {
			e = new MushroomEntity(id,this.x,this.y);
		} else if (type == EntityType.CHICK_SPAWN) {
			e = new ChickEntity(id,this.x,this.y);
		} else if (type == EntityType.ROBOT_SPAWN) {
			e = new RobotEntity(id,this.x,this.y);
		} else if (type == EntityType.ZOMBIE_SPAWN) {
			e = new ZombieEntity(id,this.x,this.y);
		} else if (type == EntityType.BAT_SPAWN) {
			e = new BatEntity(id,this.x,this.y);
		} else if (type == EntityType.GHOST_SPAWN) {
			e = new GhostEntity(id,this.x,this.y);
		} else if (type == EntityType.SKELETON_SPAWN) {
			e = new SkeletonEntity(id,this.x,this.y);
		} else if (type == EntityType.SPIDER_SPAWN) {
			e = new SpiderEntity(id,this.x,this.y);
		} else if (type == EntityType.SLIME_SPAWN) {
			e = new SlimeEntity(id,this.x,this.y);
		}
		
		if (e == null) {
			throw new RuntimeException("Error: Cant spawn an entity from this spawner because no rules are defined of what to spawn");
		}
		
		e.triggersEid = this.triggersEid;	//Apply the on same death trigger
		
		return e;
	}

	@Override
	public void update(Game g) {
		super.update(g);
		if (child == null || !child.isAlive) {
			if (cooldownStart == 0) {
				cooldownStart = System.currentTimeMillis();
			}
			
			if (System.currentTimeMillis() - cooldownStart > 5000) {
				cooldownStart = 0;
				child = createEntity(g);
				if  (child != null) {
					System.out.println("Spawner " + id + " spawned entity " + child.id + " (type: " + child.type + ")");
					//Add this to the game class
					g.addEntity(child);
				}
			}
		}
	}
}
