package entity;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**This class defines entities and relays information to the client
 * 
 * @author midavis
 *
 */
public class EntityManager {
	ArrayList<EntityDefinition> definitions = new ArrayList<EntityDefinition>();

	public EntityManager() {
		int typeID = 0;

		/**These will make definitions more readable
		 * 
		 */
		int normal = 10;	//Default duration
		int fast = 2;
		int slow = 25;
		int width = 32; //Default width
		int height = 32;//Default height
		boolean collide = true;
		boolean nocollide = false;
		boolean save = true;
		boolean nosave = false;

		//ARRAY DECLARATIONS  -- to make math easier
		int[] grid = {0,320,640,960,1280,1600,1920,2240,2560,2880};
		int[] cell = {0,32,64,96,128,160,192,224,256,288};

		//EntityDefinition(id, collidable, savable, imageTag, x, y, width,height,duration,name)
		add(new EntityDefinition(typeID++, nocollide, nosave, "objects", 288, 0, width, height, normal,	"null"));
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 96, 0, width, height, normal,	"player"));
		last().useWalkingAnimation(96,0);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 0, width, height, normal,		"sign"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 0, width, height, normal,		"gravestone"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 0, 32, width, height, 4,		"gem"));
		for (int frame = 1; frame < 8; frame++) {	//Add remaining frames
			last().addFrame(32*frame, 32);
		}
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 0, 64, width, height, normal,	"wall top back left"));

/*
		add(typeID++,"objects",32,32);	"NULL
		"288,0,10);

		//createWalkingAnimSprites(typeID++,"characters",32,32,96,0);//Sprite(s): Player (up/dn/lf/rt, walking and idle)

		game.uniqueEntityIDs+=2;
		// add(typeID++,"everything",32,32);	"Sign
		//"0,0,10);

		add(typeID++,"everything",32,32);	"Gravestone
		"32,0,10);

		add(typeID++,"everything",32,32);	"Gem
		var xTmp = 0;
		for (var i = 0; i < 8; i++) {	//Load the 8 frames, all to the right of eachother
			"(xTmp++)*32,32,4);
		}

		add(typeID++,"everything",32,32);	"Wall top back left
		"0,64,10);

		add(typeID++,"everything",32,32);	"Wall top back right
		"32,64,10);

		add(typeID++,"everything",32,32);	"Wall bot back left
		"0,96,10);

		add(typeID++,"everything",32,32);	"Wall bot back right
		"32,96,10);

		add(typeID++,"everything",32,32);	"Wall left
		"0,128,10);

		add(typeID++,"everything",32,32);	"Wall right
		"32,128,10);

		add(typeID++,"everything",32,32);	"Wall front top left
		"0,160,10);

		add(typeID++,"everything",32,32);	"Wall front top right
		"32,160,10);

		add(typeID++,"everything",32,32);	"Wall front bot left
		"0,192,10);

		add(typeID++,"everything",32,32);	"Wall front bot right
		"32,192,10);

		add(typeID++,"everything",32,32);	"Wall norm Top
		"64,64,10);

		add(typeID++,"everything",32,32);	"Wall norm
		"64,96,10);

		add(typeID++,"everything",32,32);	"Wall Pillar top left half
		"64,128,10);

		add(typeID++,"everything",32,32);	"Wall Pillar bottom left half
		"64,160,10);

		add(typeID++,"everything",32,32);	"Wall Pillar top right half
		"96,128,10);

		add(typeID++,"everything",32,32);	"Wall Pillar bottom right half
		"96,160,10);

		add(typeID++,"everything",32,32);	"Egg Yellow
		"256,32,10);

		add(typeID++,"everything",32,32);	"Egg Blue-Orange
		"288,32,10);*/

		add(new EntityDefinition(typeID++,collide,save,"everything",0,288,32,32,normal,"Mushroom Spawn"));

		createWalkingAnimSprites(typeID++,"characters",32,32,0,256);//Sprite(s): Mushroom Entity (up/dn/lf/rt, walking and idle)

		/*add(typeID++,"everything",32,32);	"Chick Spawn
		"32,288,10);

		createWalkingAnimSprites(typeID++,"characters",32,32,96,256);//Sprite(s): Chick Entity (up/dn/lf/rt, walking and idle)

		add(typeID++,"everything",32,64,0,32);	"Tree (2blocks high)
		"96,64,10);

		add(typeID++,"everything",32,32);	"Fence back left
		"128,64,10);

		add(typeID++,"everything",32,32);	"Fence back
		"160,64,10);

		add(typeID++,"everything",32,32);	"Fence back right
		"192,64,10);

		add(typeID++,"everything",32,32);	"Fence left
		"128,96,10);

		add(typeID++,"everything",32,32);	"Fence center
		"160,96,10);

		add(typeID++,"everything",32,32);	"Fence right
		"192,96,10);

		add(typeID++,"everything",32,32);	"Fence front left
		"128,128,10);

		add(typeID++,"everything",32,32);	"Fence front
		"160,128,10);

		add(typeID++,"everything",32,32);	"Fence front right
		"192,128,10);

		add(typeID++,"everything",32,32);	"Fence Post
		"224,64,10);

		createWalkingAnimSprites(typeID++,"darkerCharacters",32,32,96,0);//Sprite(s): Zombie Entity (up/dn/lf/rt, walking and idle)

		add(typeID++,"everything",32,32);	"Torch Yellow
		for (var i = 0; i < 4; i++) {
			var y = i < 3 ? i : 1;	//Frames 0-1-2-1
			"224+(y*32),96,3);
		}

		add(typeID++,"everything",32,32);	"Torch Blue
		for (var i = 0; i < 4; i++) {
			var y = i < 3 ? i : 1;	//Frames 0-1-2-1
			"224+(y*32),128,3);
		}

		add(typeID++,"everything",32,32);	"Torch Red
		for (var i = 0; i < 4; i++) {
			var y = i < 3 ? i : 1;	//Frames 0-1-2-1
			"224+(y*32),160,3);
		}

		add(typeID++,"everything",32,32);	"Torch Green
		for (var i = 0; i < 4; i++) {
			var y = i < 3 ? i : 1;	//Frames 0-1-2-1
			"224+(y*32),192,3);
		}

		add(typeID++,"everything",32,32);	"Robot Spawn
		"64,288,10);

		createWalkingAnimSprites(typeID++,"characters",64,64,384,0,16,32);//Sprite(s): Robot Entity (up/dn/lf/rt, walking and idle)




		//ITEM SPRITES 
		//Block A 2
		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Teal Potion
		"grid[0] +cell[0],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Potion
		"grid[0] +cell[1],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blue Potion
		"grid[0] +cell[2],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Yellow Potion
		"grid[0] +cell[3],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Green Potion
		"grid[0] +cell[4],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "White Potion
		"grid[0] +cell[5],grid[1]+cell[0],10);


		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Black Potion
		"grid[0] +cell[6],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Purple Potion
		"grid[0] +cell[7],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Cyan Potion
		"grid[0] +cell[8],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Meat
		"grid[0] +cell[0],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Wheat
		"grid[0] +cell[1],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Apple
		"grid[0] +cell[2],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Bread
		"grid[0] +cell[3],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Potato
		"grid[0] +cell[4],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "White Flower
		"grid[0] +cell[0],grid[1]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Onion
		"grid[0] +cell[1],grid[1]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Purple Flower
		"grid[0] +cell[2],grid[1]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Leeks
		"grid[0] +cell[3],grid[1]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Pink Flower
		"grid[0] +cell[4],grid[1]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Beats
		"grid[0] +cell[5],grid[1]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blue Flower
		"grid[0] +cell[6],grid[1]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Carrot
		"grid[0] +cell[7],grid[1]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Sword
		"grid[0] +cell[0],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Battle Axe
		"grid[0] +cell[1],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Dagger
		"grid[0] +cell[2],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Arrow
		"grid[0] +cell[3],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Cane
		"grid[0] +cell[4],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Pole
		"grid[0] +cell[5],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Bow
		"grid[0] +cell[6],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Spear
		"grid[0] +cell[7],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Pick Axe
		"grid[0] +cell[8],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "SledgeHammer
		"grid[0] +cell[9],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Bludgeon
		"grid[0] +cell[0],grid[1]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Spiked Bludgeon
		"grid[0] +cell[1],grid[1]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Flail
		"grid[0] +cell[2],grid[1]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Sceptre
		"grid[0] +cell[3],grid[1]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Axe
		"grid[0] +cell[4],grid[1]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "War Hammer
		"grid[0] +cell[5],grid[1]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Royal Sceptre
		"grid[0] +cell[6],grid[1]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Helmet
		"grid[0] +cell[0],grid[1]+cell[5],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Horny Helmet
		"grid[0] +cell[1],grid[1]+cell[5],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Mohawk Helmet
		"grid[0] +cell[2],grid[1]+cell[5],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blue Mage Hat
		"grid[0] +cell[3],grid[1]+cell[5],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Grey Mage Hat
		"grid[0] +cell[4],grid[1]+cell[5],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Mage Hat
		"grid[0] +cell[5],grid[1]+cell[5],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Apprentice Hat
		"grid[0] +cell[6],grid[1]+cell[5],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "ChestPlate
		"grid[0] +cell[0],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Gilded ChestPlate
		"grid[0] +cell[1],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Chainmail
		"grid[0] +cell[2],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Armor
		"grid[0] +cell[3],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Grey Mage Robe
		"grid[0] +cell[4],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blue Mage Robe
		"grid[0] +cell[5],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Mage Robe
		"grid[0] +cell[6],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Leather Body
		"grid[0] +cell[7],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Studded Body
		"grid[0] +cell[8],grid[1]+cell[6],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "PlateLegs
		"grid[0] +cell[0],grid[1]+cell[7],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Gilded PlateLegs
		"grid[0] +cell[1],grid[1]+cell[7],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Sexy Legs
		"grid[0] +cell[2],grid[1]+cell[7],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Boots
		"grid[0] +cell[3],grid[1]+cell[7],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Boots
		"grid[0] +cell[4],grid[1]+cell[7],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Shield
		"grid[0] +cell[0],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Good Shield
		"grid[0] +cell[1],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blood Shield
		"grid[0] +cell[2],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Gray Shield
		"grid[0] +cell[3],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Wooden Shield
		"grid[0] +cell[4],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "KiteShield
		"grid[0] +cell[5],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Round Shield
		"grid[0] +cell[6],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Gold Amulet
		"grid[0] +cell[0],grid[1]+cell[9],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Amulet
		"grid[0] +cell[1],grid[1]+cell[9],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Gold Unstrung Amulet
		"grid[0] +cell[2],grid[1]+cell[9],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Unstrung Amulet
		"grid[0] +cell[3],grid[1]+cell[9],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Key
		"grid[0] +cell[8],grid[1]+cell[9],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Grey Key
		"grid[0] +cell[9],grid[1]+cell[9],10);

		//Block B 2
		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Urn
		"grid[1] +cell[2],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Vase
		"grid[1] +cell[3],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Mineral Water
		"grid[1] +cell[4],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Water
		"grid[1] +cell[5],grid[1]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Big Fish
		"grid[1] +cell[1],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Salmon
		"grid[1] +cell[2],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blue Fish
		"grid[1] +cell[3],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Lobster
		"grid[1] +cell[4],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Dead Salmon
		"grid[1] +cell[5],grid[1]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Short Sword
		"grid[1] +cell[0],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Short Flail
		"grid[1] +cell[1],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Fishing Pole
		"grid[1] +cell[2],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Short BattleAxe
		"grid[1] +cell[3],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Halberd
		"grid[1] +cell[4],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Scimitar
		"grid[1] +cell[5],grid[1]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blue Book
		"grid[1] +cell[3],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Book
		"grid[1] +cell[4],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Black Book
		"grid[1] +cell[5],grid[1]+cell[8],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Gold Key
		"grid[1] +cell[0],grid[1]+cell[9],10);


		//BLOCK A3
		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "One Die
		"grid[0] +cell[0],grid[2]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Two Die
		"grid[0] +cell[1],grid[2]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Three Die
		"grid[0] +cell[2],grid[2]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Four Die
		"grid[0] +cell[3],grid[2]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Five Die
		"grid[0] +cell[4],grid[2]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Six Die
		"grid[0] +cell[5],grid[2]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Small Die
		"grid[0] +cell[6],grid[2]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blank Die
		"grid[0] +cell[7],grid[2]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Unlit Candle
		"grid[0] +cell[0],grid[2]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Candle Flicker
		"grid[0] +cell[1],grid[2]+cell[2],10);
		"grid[0] +cell[2],grid[2]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Fireball
		"grid[0] +cell[3],grid[2]+cell[2],10);
		"grid[0] +cell[3],grid[2]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",64,64); "Explosion
		"grid[0] +cell[4],grid[2]+cell[2],10);
		"grid[0] +cell[6],grid[2]+cell[2],10);
		"grid[0] +cell[8],grid[2]+cell[2],10);
		"grid[0] +cell[4],grid[2]+cell[4],10);
		"grid[0] +cell[6],grid[2]+cell[4],10);
		"grid[0] +cell[8],grid[2]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Unlit Wall Candle
		"grid[0] +cell[0],grid[2]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Wall Candle
		"grid[0] +cell[1],grid[2]+cell[3],10);
		"grid[0] +cell[2],grid[2]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Unlit Wall Torch
		"grid[0] +cell[0],grid[2]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Wall Torch
		"grid[0] +cell[1],grid[2]+cell[4],10);
		"grid[0] +cell[2],grid[2]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Bubble Ray
		"grid[0] +cell[3],grid[2]+cell[4],10);
		"grid[0] +cell[3],grid[2]+cell[5],10);


		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Unlit Lantern
		"grid[0] +cell[0],grid[2]+cell[5],10);


		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Lantern
		"grid[0] +cell[1],grid[2]+cell[5],10);
		"grid[0] +cell[2],grid[2]+cell[5],10);

		//BLOCK A4 - All Crystals Plus Eggs
		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Yellow Gem
		"grid[0] +cell[0],grid[3]+cell[1],10);
		"grid[0] +cell[1],grid[3]+cell[1],10);
		"grid[0] +cell[2],grid[3]+cell[1],10);
		"grid[0] +cell[3],grid[3]+cell[1],10);
		"grid[0] +cell[4],grid[3]+cell[1],10);
		"grid[0] +cell[5],grid[3]+cell[1],10);
		"grid[0] +cell[6],grid[3]+cell[1],10);
		"grid[0] +cell[7],grid[3]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Pink Gem
		"grid[0] +cell[0],grid[3]+cell[2],10);
		"grid[0] +cell[1],grid[3]+cell[2],10);
		"grid[0] +cell[2],grid[3]+cell[2],10);
		"grid[0] +cell[3],grid[3]+cell[2],10);
		"grid[0] +cell[4],grid[3]+cell[2],10);
		"grid[0] +cell[5],grid[3]+cell[2],10);
		"grid[0] +cell[6],grid[3]+cell[2],10);
		"grid[0] +cell[7],grid[3]+cell[2],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Green Gem
		"grid[0] +cell[0],grid[3]+cell[3],10);
		"grid[0] +cell[1],grid[3]+cell[3],10);
		"grid[0] +cell[2],grid[3]+cell[3],10);
		"grid[0] +cell[3],grid[3]+cell[3],10);
		"grid[0] +cell[4],grid[3]+cell[3],10);
		"grid[0] +cell[5],grid[3]+cell[3],10);
		"grid[0] +cell[6],grid[3]+cell[3],10);
		"grid[0] +cell[7],grid[3]+cell[3],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Gray Gem
		"grid[0] +cell[0],grid[3]+cell[4],10);
		"grid[0] +cell[1],grid[3]+cell[4],10);
		"grid[0] +cell[2],grid[3]+cell[4],10);
		"grid[0] +cell[3],grid[3]+cell[4],10);
		"grid[0] +cell[4],grid[3]+cell[4],10);
		"grid[0] +cell[5],grid[3]+cell[4],10);
		"grid[0] +cell[6],grid[3]+cell[4],10);
		"grid[0] +cell[7],grid[3]+cell[4],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Orange Gem
		"grid[0] +cell[0],grid[3]+cell[5],10);
		"grid[0] +cell[1],grid[3]+cell[5],10);
		"grid[0] +cell[2],grid[3]+cell[5],10);
		"grid[0] +cell[3],grid[3]+cell[5],10);
		"grid[0] +cell[4],grid[3]+cell[5],10);
		"grid[0] +cell[5],grid[3]+cell[5],10);
		"grid[0] +cell[6],grid[3]+cell[5],10);
		"grid[0] +cell[7],grid[3]+cell[5],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Mucus Egg
		"grid[0] +cell[8],grid[3]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Purple Egg
		"grid[0] +cell[9],grid[3]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Spotted Egg"grid[0] +cell[8],grid[3]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Mountain Egg"grid[0] +cell[9],grid[3]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Striped Egg"grid[1] +cell[0],grid[3]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Green Yellow Egg"grid[1] +cell[1],grid[3]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Yellw Egg"grid[1] +cell[2],grid[3]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Wavy Egg"grid[1] +cell[3],grid[3]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Magma Egg"grid[1] +cell[0],grid[3]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "RedEgg"grid[1] +cell[1],grid[3]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blue Egg"grid[1] +cell[2],grid[3]+cell[1],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Pink Egg"grid[1] +cell[3],grid[3]+cell[1],10);

		//VILLAGE - E5, F5
		for(var i = 0;  i < 6;  i ++){  "Blue Bldg
			for(var j = 0;  j < 6; j++){
				if(j == 5 && (i != 2 && i != 3))continue;
				add("entity"+ game.uniqueEntityIDs++,"everything",32,32); 
				"grid[4] +cell[i],grid[4]+cell[j],10);
			}
		}

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Clover"grid[5] +cell[1],grid[4]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Weeds"grid[5] +cell[2],grid[4]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Blue Bush"grid[5] +cell[3],grid[4]+cell[0],10);

		add("entity"+ game.uniqueEntityIDs++,"everything",32,32); "Red Bush"grid[5] +cell[4],grid[4]+cell[0],10);*/

		//ETC
		//PS, feel free to re-assign type IDs if it makes loading easier.

	}

	private void createWalkingAnimSprites(int i, String string, int j, int k, int l, int m) {
		// TODO Auto-generated method stub
		
	}

	public byte[] getBytes() {
		int length = 4;	//Number of entities

		//Count up the lengths
		for (EntityDefinition d : definitions) {
			length += d.sizeInBytes();
		}

		ByteBuffer bb = ByteBuffer.allocate(length);
		bb.putInt(definitions.size());

		for (EntityDefinition d : definitions) {
			bb.put(d.getBytes());
		}

		return bb.array();
	}

	/**Shorthand for getting last thing added**/
	private EntityDefinition last() {
		return definitions.get(definitions.size()-1);
	}

	/**Shorthand for adding something new**/
	private void add(EntityDefinition d) {
		definitions.add(d);
	}
}
