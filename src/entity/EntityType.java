package entity;

import java.util.HashMap;
import java.util.HashSet;

public class EntityType {
	public static final int PLAYER 						= 1;
	public static final int SIGN 						= 2;
	public static final int GRAVESTONE 					= 3;
	public static final int GEM 						= 4;
	public static final int WALL_BACK_TOP_LEFT 			= 5;
	public static final int WALL_BACK_TOP_RIGHT 		= 6;
	public static final int WALL_BACK_BOT_LEFT 			= 7;
	public static final int WALL_BACK_BOT_RIGHT 		= 8;
	public static final int WALL_SIDE_LEFT 				= 9;
	public static final int WALL_SIDE_RIGHT 			= 10;
	public static final int WALL_FRONT_TOP_LEFT 		= 11;
	public static final int WALL_FRONT_TOP_RIGHT 		= 12;
	public static final int WALL_FRONT_BOT_LEFT 		= 13;
	public static final int WALL_FRONT_BOT_RIGHT 		= 14;
	public static final int WALL_NORM_TOP 				= 15;
	public static final int WALL_NORM 					= 16;
	public static final int WALL_PILLAR_TOP_LEFTHALF 	= 17;
	public static final int WALL_PILLAR_TOP_RIGHTHALF 	= 18;
	public static final int WALL_PILLAR_BOT_LEFTHALF 	= 19;
	public static final int WALL_PILLAR_BOT_RIGHTHALF 	= 20;
	public static final int EGG_YELLOW					= 21;
	public static final int EGG_BLUEORANGE				= 22;
	public static final int SPAWNER_MUSHROOM			= 23;
	public static final int MUSHROOM					= 24;
	public static final int SPAWNER_CHICK				= 25;
	public static final int CHICK						= 26;
	public static final int TREE						= 27;
	public static final int FENCE_BACK_LEFT				= 28;
	public static final int FENCE_BACK					= 29;
	public static final int FENCE_BACK_RIGHT			= 30;
	public static final int FENCE_LEFT					= 31;
	public static final int FENCE_CENTER				= 32;
	public static final int FENCE_RIGHT					= 33;
	public static final int FENCE_FRONT_LEFT			= 34;
	public static final int FENCE_FRONT					= 35;
	public static final int FENCE_FRONT_RIGHT			= 36;
	public static final int FENCE_POST					= 37;

	
	//STATIC ENTITIES
	//These should be saved/loaded from map files
	//These should never move or be dynamically created by the game
	//If you need something that moves, create a spawner instead
	public static final HashSet<Integer> STATIC_ENTITIES = new HashSet<Integer>();
	
	//SPAWNER ENTITIES
	//Entities that spawn others!
	public static final HashSet<Integer> SPAWNERS = new HashSet<Integer>();
	
	//Collidable objects
	//Entities that can't be walked over.
	public static final HashSet<Integer> COLLIDABLE_ENTITIES = new HashSet<Integer>();
	
	static {
		STATIC_ENTITIES.add(SIGN);
		STATIC_ENTITIES.add(GRAVESTONE);
		STATIC_ENTITIES.add(GEM);
		STATIC_ENTITIES.add(WALL_BACK_TOP_LEFT);
		STATIC_ENTITIES.add(WALL_BACK_TOP_RIGHT);
		STATIC_ENTITIES.add(WALL_BACK_BOT_LEFT);
		STATIC_ENTITIES.add(WALL_BACK_BOT_RIGHT);
		STATIC_ENTITIES.add(WALL_SIDE_LEFT);
		STATIC_ENTITIES.add(WALL_SIDE_RIGHT);
		STATIC_ENTITIES.add(WALL_FRONT_TOP_LEFT);
		STATIC_ENTITIES.add(WALL_FRONT_TOP_RIGHT);
		STATIC_ENTITIES.add(WALL_FRONT_BOT_LEFT);
		STATIC_ENTITIES.add(WALL_FRONT_BOT_RIGHT);
		STATIC_ENTITIES.add(WALL_NORM_TOP);
		STATIC_ENTITIES.add(WALL_NORM);
		STATIC_ENTITIES.add(WALL_PILLAR_TOP_LEFTHALF);
		STATIC_ENTITIES.add(WALL_PILLAR_TOP_RIGHTHALF);
		STATIC_ENTITIES.add(WALL_PILLAR_BOT_LEFTHALF);
		STATIC_ENTITIES.add(WALL_PILLAR_BOT_RIGHTHALF);
		STATIC_ENTITIES.add(EGG_YELLOW);
		STATIC_ENTITIES.add(EGG_BLUEORANGE);
		STATIC_ENTITIES.add(SPAWNER_MUSHROOM);
		STATIC_ENTITIES.add(SPAWNER_CHICK);
		STATIC_ENTITIES.add(TREE);
		STATIC_ENTITIES.add(FENCE_BACK_LEFT);
		STATIC_ENTITIES.add(FENCE_BACK);
		STATIC_ENTITIES.add(FENCE_BACK_RIGHT);
		STATIC_ENTITIES.add(FENCE_LEFT);
		STATIC_ENTITIES.add(FENCE_CENTER);
		STATIC_ENTITIES.add(FENCE_RIGHT);
		STATIC_ENTITIES.add(FENCE_FRONT_LEFT);
		STATIC_ENTITIES.add(FENCE_FRONT);
		STATIC_ENTITIES.add(FENCE_FRONT_RIGHT);
		STATIC_ENTITIES.add(FENCE_POST);
		
		
		SPAWNERS.add(SPAWNER_MUSHROOM);
		SPAWNERS.add(SPAWNER_CHICK);
		
		COLLIDABLE_ENTITIES.add(SIGN);
		COLLIDABLE_ENTITIES.add(WALL_BACK_TOP_LEFT);
		COLLIDABLE_ENTITIES.add(WALL_BACK_TOP_RIGHT);
		COLLIDABLE_ENTITIES.add(WALL_BACK_BOT_LEFT);
		COLLIDABLE_ENTITIES.add(WALL_BACK_BOT_RIGHT);
		COLLIDABLE_ENTITIES.add(WALL_SIDE_LEFT);
		COLLIDABLE_ENTITIES.add(WALL_SIDE_RIGHT);
		COLLIDABLE_ENTITIES.add(WALL_FRONT_TOP_LEFT);
		COLLIDABLE_ENTITIES.add(WALL_FRONT_TOP_RIGHT);
		COLLIDABLE_ENTITIES.add(WALL_FRONT_BOT_LEFT);
		COLLIDABLE_ENTITIES.add(WALL_FRONT_BOT_RIGHT);
		COLLIDABLE_ENTITIES.add(WALL_NORM_TOP);
		COLLIDABLE_ENTITIES.add(WALL_NORM);
		COLLIDABLE_ENTITIES.add(WALL_PILLAR_TOP_LEFTHALF);
		COLLIDABLE_ENTITIES.add(WALL_PILLAR_TOP_RIGHTHALF);
		COLLIDABLE_ENTITIES.add(WALL_PILLAR_BOT_LEFTHALF);
		COLLIDABLE_ENTITIES.add(WALL_PILLAR_BOT_RIGHTHALF);
		COLLIDABLE_ENTITIES.add(TREE);
		COLLIDABLE_ENTITIES.add(FENCE_BACK_LEFT);
		COLLIDABLE_ENTITIES.add(FENCE_BACK);
		COLLIDABLE_ENTITIES.add(FENCE_BACK_RIGHT);
		COLLIDABLE_ENTITIES.add(FENCE_LEFT);
		COLLIDABLE_ENTITIES.add(FENCE_CENTER);
		COLLIDABLE_ENTITIES.add(FENCE_RIGHT);
		COLLIDABLE_ENTITIES.add(FENCE_FRONT_LEFT);
		COLLIDABLE_ENTITIES.add(FENCE_FRONT);
		COLLIDABLE_ENTITIES.add(FENCE_FRONT_RIGHT);
		COLLIDABLE_ENTITIES.add(FENCE_POST);
		
	}
	

}
