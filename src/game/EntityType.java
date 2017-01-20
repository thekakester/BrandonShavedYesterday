package game;

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

	
	//STATIC ENTITIES
	//These should be saved/loaded from map files
	//These should never move or be dynamically created by the game
	//If you need something that moves, create a spawner instead
	public static final HashSet<Integer> STATIC_ENTITIES = new HashSet<Integer>();
	
	//SPAWNERS
	//Point the spawner ID to what it spawns.
	//Ex: SPAWNER_MUSHROOM -> MUSHROOM
	public static final HashMap<Integer,Integer> SPAWNERS = new HashMap<Integer,Integer>();
	
	
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
		
		SPAWNERS.put(SPAWNER_MUSHROOM, MUSHROOM);
	}
	

}
