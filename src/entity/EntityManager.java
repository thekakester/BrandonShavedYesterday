package entity;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**This class defines entities and relays information to the client
 * 
 * @author midavis
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *   HEY YOU!
 *   If you modify this file, make sure to run
 *   the main method of this class to re-generate EntityType.java!
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class EntityManager {
	public static void main(String[] args) {
		System.out.println("Generating java definition file");
		EntityManager manager = new EntityManager();
		try {manager.generateEntityTypeJavaFile();}
		catch (Exception e) {e.printStackTrace();}
	}

	public static ArrayList<EntityDefinition> definitions = new ArrayList<EntityDefinition>();

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
		add(new EntityDefinition(typeID++, nocollide, nosave, "everything", 288, 0, width, height, normal,"null"));
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 96, 0, width, height, fast,"player"));
		last().useWalkingAnimation(96,0);
		last().baseHP = 1;
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 0, width, height, normal,"sign"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 32, 0, width, height, normal,"Gravestone"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 0, 64, width, height, normal,"Wall top back left"));
		last().isOverlay = true;
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 32, 64, width, height, normal,"Wall top back right"));
		last().isOverlay = true;
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 96, width, height, normal,"Wall bot back left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 96, width, height, normal,"Wall bot back right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 128, width, height, normal,"Wall left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 128, width, height, normal,"Wall right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 160, width, height, normal,"Wall front top left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 160, width, height, normal,"Wall front top right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 192, width, height, normal,"Wall front bot left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 192, width, height, normal,"Wall front bot right"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 64, 64, width, height, normal,"Wall norm Top"));
		last().isOverlay = true;
		add(new EntityDefinition(typeID++, collide, save,  "everything", 64, 96, width, height, normal,"Wall norm"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 64, 128, width, height, normal,"Wall Pillar top left half"));
		last().isOverlay = true;
		add(new EntityDefinition(typeID++, collide, save,  "everything", 64, 160, width, height, normal,"Wall Pillar bottom left half"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 96, 128, width, height, normal,"Wall Pillar top right half"));
		last().isOverlay = true;
		add(new EntityDefinition(typeID++, collide, save,  "everything", 96, 160, width, height, normal,"Wall Pillar bottom right half"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", 256, 32, width, height, normal,"Egg Yellow"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", 288, 32, width, height, normal,"Egg Blue-Orange"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 0, 288, width, height, normal,"Mushroom Spawn"));
		last().isSpawner = true;
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 0, 256, width, height,fast,"Mushroom Enemy"));
		last().baseHP = 1;
		last().useWalkingAnimation(0,256);
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 32, 288, width, height, normal,"Chick Spawn"));
		last().isSpawner = true;
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 96, 256, width, height,fast,"Chick Entity"));
		last().baseHP = 1;
		last().useWalkingAnimation(96,256);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 96, 64, width, height*2, normal,"Tree"));
		last().setOffset(0, 32);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 128, 64, width, height, normal,"Fence back left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 160, 64, width, height, normal,"Fence back"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 192, 64, width, height, normal,"Fence back right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 128, 96, width, height, normal,"Fence left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 160, 96, width, height, normal,"Fence center"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 192, 96, width, height, normal,"Fence right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 128, 128, width, height, normal,"Fence front left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 160, 128, width, height, normal,"Fence front"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 192, 128, width, height, normal,"Fence front right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 224, 64, width, height, normal,"Fence Post"));
		add(new EntityDefinition(typeID++, nocollide, nosave, "darkerCharacters", 96, 0, width, height,fast,"Zombie Enemy"));
		last().baseHP = 1;
		last().useWalkingAnimation(96,0);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 224, 96, width, height, normal,"Torch Yellow"));
		last().addFrame(224+32, 96);
		last().addFrame(224+64, 96);
		last().addFrame(224+32, 96);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 224, 128, width, height, normal,"Torch Blue"));
		last().addFrame(224+32, 128);
		last().addFrame(224+64, 128);
		last().addFrame(224+32, 128);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 224, 160, width, height, normal,"Torch Red"));
		last().addFrame(224+32, 160);
		last().addFrame(224+64, 160);
		last().addFrame(224+32, 160);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 224, 192, width, height, normal,"Torch Green"));
		last().addFrame(224+32, 192);
		last().addFrame(224+64, 192);
		last().addFrame(224+32, 192);
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 64, 288, width, height, normal,"Robot Spawn"));
		last().isSpawner = true;
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 384, 0, width*2, height*2,fast,"Robot Entity"));
		last().baseHP = 1;
		last().setOffset(16, 32);
		last().useWalkingAnimation(384,0);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[0], width, height, normal,"Teal Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[0], width, height, normal,"Red Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[0], width, height, normal,"Blue Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[0], width, height, normal,"Yellow Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[0], width, height, normal,"Green Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[5], grid[1]+cell[0], width, height, normal,"White Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[6], grid[1]+cell[0], width, height, normal,"Black Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[7], grid[1]+cell[0], width, height, normal,"Purple Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[8], grid[1]+cell[0], width, height, normal,"Cyan Potion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[1], width, height, normal,"Meat"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[1], width, height, normal,"Wheat"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[1], width, height, normal,"Apple"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[1], width, height, normal,"Bread"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[1], width, height, normal,"Potato"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[2], width, height, normal,"White Flower"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[2], width, height, normal,"Onion"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[2], width, height, normal,"Purple Flower"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[2], width, height, normal,"Leeks"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[2], width, height, normal,"Pink Flower"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[5], grid[1]+cell[2], width, height, normal,"Beats"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[6], grid[1]+cell[2], width, height, normal,"Blue Flower"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[7], grid[1]+cell[2], width, height, normal,"Carrot"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[3], width, height, normal,"Sword"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[3], width, height, normal,"Battle Axe"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[3], width, height, normal,"Dagger"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[3], width, height, normal,"Arrow"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[3], width, height, normal,"Cane"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[5], grid[1]+cell[3], width, height, normal,"Pole"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[6], grid[1]+cell[3], width, height, normal,"Bow"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[7], grid[1]+cell[3], width, height, normal,"Spear"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[8], grid[1]+cell[3], width, height, normal,"Pick Axe"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[9], grid[1]+cell[3], width, height, normal,"SledgeHammer"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[4], width, height, normal,"Bludgeon"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[4], width, height, normal,"Spiked Bludgeon"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[4], width, height, normal,"Flail"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[4], width, height, normal,"Sceptre"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[4], width, height, normal,"Axe"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[5], grid[1]+cell[4], width, height, normal,"War Hammer"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[6], grid[1]+cell[4], width, height, normal,"Royal Sceptre"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[5], width, height, normal,"Helmet"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[5], width, height, normal,"Horny Helmet"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[5], width, height, normal,"Mohawk Helmet"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[5], width, height, normal,"Blue Mage Hat"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[5], width, height, normal,"Grey Mage Hat"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[5], grid[1]+cell[5], width, height, normal,"Red Mage Hat"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[6], grid[1]+cell[5], width, height, normal,"Apprentice Hat"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[6], width, height, normal,"ChestPlate"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[6], width, height, normal,"Gilded ChestPlate"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[6], width, height, normal,"Chainmail"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[6], width, height, normal,"Red Armor"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[6], width, height, normal,"Grey Mage Robe"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[5], grid[1]+cell[6], width, height, normal,"Blue Mage Robe"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[6], grid[1]+cell[6], width, height, normal,"Red Mage Robe"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[7], grid[1]+cell[6], width, height, normal,"Leather Body"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[8], grid[1]+cell[6], width, height, normal,"Studded Body"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[7], width, height, normal,"PlateLegs"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[7], width, height, normal,"Gilded PlateLegs"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[7], width, height, normal,"Sexy Legs"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[7], width, height, normal,"Red Boots"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[7], width, height, normal,"Boots"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[8], width, height, normal,"Red Shield"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[8], width, height, normal,"Good Shield"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[8], width, height, normal,"Blood Shield"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[8], width, height, normal,"Gray Shield"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[1]+cell[8], width, height, normal,"Wooden Shield"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[5], grid[1]+cell[8], width, height, normal,"KiteShield"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[6], grid[1]+cell[8], width, height, normal,"Round Shield"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[1]+cell[9], width, height, normal,"Gold Amulet"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[1]+cell[9], width, height, normal,"Amulet"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[1]+cell[9], width, height, normal,"Gold Unstrung Amulet"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[1]+cell[9], width, height, normal,"Unstrung Amulet"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[8], grid[1]+cell[9], width, height, normal,"Red Key"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[9], grid[1]+cell[9], width, height, normal,"Grey Key"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[2], grid[1]+cell[0], width, height, normal,"Urn"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[3], grid[1]+cell[0], width, height, normal,"Vase"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[4], grid[1]+cell[0], width, height, normal,"Mineral Water"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[5], grid[1]+cell[0], width, height, normal,"Water"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[1], grid[1]+cell[1], width, height, normal,"Big Fish"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[2], grid[1]+cell[1], width, height, normal,"Salmon"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[3], grid[1]+cell[1], width, height, normal,"Blue Fish"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[4], grid[1]+cell[1], width, height, normal,"Lobster"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[5], grid[1]+cell[1], width, height, normal,"Dead Salmon"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[0], grid[1]+cell[3], width, height, normal,"Short Sword"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[1], grid[1]+cell[3], width, height, normal,"Short Flail"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[2], grid[1]+cell[3], width, height, normal,"Fishing Pole"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[3], grid[1]+cell[3], width, height, normal,"Short BattleAxe"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[4], grid[1]+cell[3], width, height, normal,"Halberd"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[5], grid[1]+cell[3], width, height, normal,"Scimitar"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[3], grid[1]+cell[8], width, height, normal,"Blue Book"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[4], grid[1]+cell[8], width, height, normal,"Red Book"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[5], grid[1]+cell[8], width, height, normal,"Black Book"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[0], grid[1]+cell[9], width, height, normal,"Gold Key"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[2]+cell[1], width, height, normal,"One Die"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[2]+cell[1], width, height, normal,"Two Die"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[2], grid[2]+cell[1], width, height, normal,"Three Die"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[2]+cell[1], width, height, normal,"Four Die"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[2]+cell[1], width, height, normal,"Five Die"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[5], grid[2]+cell[1], width, height, normal,"Six Die"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[6], grid[2]+cell[1], width, height, normal,"Small Die"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[7], grid[2]+cell[1], width, height, normal,"Blank Die"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[2]+cell[2], width, height, normal,"Unlit Candle"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[1], grid[2]+cell[2], width, height, normal,"Candle Flicker"));
		last().addFrame(grid[0]+cell[2],grid[2]+cell[2]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[2]+cell[2], width, height, normal,"Fireball"));
		last().addFrame(grid[0]+cell[3],grid[2]+cell[3]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[4], grid[2]+cell[2], width, height, normal,"Explosion"));
		last().addFrame(grid[0]+cell[6],grid[2]+cell[2]);
		last().addFrame(grid[0]+cell[8],grid[2]+cell[2]);
		last().addFrame(grid[0]+cell[4],grid[2]+cell[4]);
		last().addFrame(grid[0]+cell[6],grid[2]+cell[4]);
		last().addFrame(grid[0]+cell[8],grid[2]+cell[4]);
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[0], grid[2]+cell[3], width, height, normal,"Unlit Wall Candle"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[1], grid[2]+cell[3], width, height, normal,"Wall Candle"));
		last().addFrame(grid[0]+cell[2],grid[2]+cell[3]);
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[0], grid[2]+cell[4], width, height, normal,"Unlit Wall Torch"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[1], grid[2]+cell[4], width, height, normal,"Wall Torch"));
		last().addFrame(grid[0]+cell[2],grid[2]+cell[4]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[3], grid[2]+cell[4], width, height, normal,"Bubble Ray"));
		last().addFrame(grid[0]+cell[3],grid[2]+cell[5]);
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[0], grid[2]+cell[5], width, height, normal,"Unlit Lantern"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[1], grid[2]+cell[5], width, height, normal,"Lantern"));
		last().addFrame(grid[0]+cell[2],grid[2]+cell[5]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[3]+cell[0], width, height, normal,"Blue Gem"));
		last().addFrame(grid[0]+cell[1],grid[3]+cell[0]);
		last().addFrame(grid[0]+cell[2],grid[3]+cell[0]);
		last().addFrame(grid[0]+cell[3],grid[3]+cell[0]);
		last().addFrame(grid[0]+cell[4],grid[3]+cell[0]);
		last().addFrame(grid[0]+cell[5],grid[3]+cell[0]);
		last().addFrame(grid[0]+cell[6],grid[3]+cell[0]);
		last().addFrame(grid[0]+cell[7],grid[3]+cell[0]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[3]+cell[1], width, height, normal,"Yellow Gem"));
		last().addFrame(grid[0]+cell[1],grid[3]+cell[1]);
		last().addFrame(grid[0]+cell[2],grid[3]+cell[1]);
		last().addFrame(grid[0]+cell[3],grid[3]+cell[1]);
		last().addFrame(grid[0]+cell[4],grid[3]+cell[1]);
		last().addFrame(grid[0]+cell[5],grid[3]+cell[1]);
		last().addFrame(grid[0]+cell[6],grid[3]+cell[1]);
		last().addFrame(grid[0]+cell[7],grid[3]+cell[1]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[3]+cell[2], width, height, normal,"Pink Gem"));
		last().addFrame(grid[0]+cell[1],grid[3]+cell[2]);
		last().addFrame(grid[0]+cell[2],grid[3]+cell[2]);
		last().addFrame(grid[0]+cell[3],grid[3]+cell[2]);
		last().addFrame(grid[0]+cell[4],grid[3]+cell[2]);
		last().addFrame(grid[0]+cell[5],grid[3]+cell[2]);
		last().addFrame(grid[0]+cell[6],grid[3]+cell[2]);
		last().addFrame(grid[0]+cell[7],grid[3]+cell[2]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[3]+cell[3], width, height, normal,"Green Gem"));
		last().addFrame(grid[0]+cell[1],grid[3]+cell[3]);
		last().addFrame(grid[0]+cell[2],grid[3]+cell[3]);
		last().addFrame(grid[0]+cell[3],grid[3]+cell[3]);
		last().addFrame(grid[0]+cell[4],grid[3]+cell[3]);
		last().addFrame(grid[0]+cell[5],grid[3]+cell[3]);
		last().addFrame(grid[0]+cell[6],grid[3]+cell[3]);
		last().addFrame(grid[0]+cell[7],grid[3]+cell[3]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[3]+cell[4], width, height, normal,"Gray Gem"));
		last().addFrame(grid[0]+cell[1],grid[3]+cell[4]);
		last().addFrame(grid[0]+cell[2],grid[3]+cell[4]);
		last().addFrame(grid[0]+cell[3],grid[3]+cell[4]);
		last().addFrame(grid[0]+cell[4],grid[3]+cell[4]);
		last().addFrame(grid[0]+cell[5],grid[3]+cell[4]);
		last().addFrame(grid[0]+cell[6],grid[3]+cell[4]);
		last().addFrame(grid[0]+cell[7],grid[3]+cell[4]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[0], grid[3]+cell[5], width, height, normal,"Orange Gem"));
		last().addFrame(grid[0]+cell[1],grid[3]+cell[5]);
		last().addFrame(grid[0]+cell[2],grid[3]+cell[5]);
		last().addFrame(grid[0]+cell[3],grid[3]+cell[5]);
		last().addFrame(grid[0]+cell[4],grid[3]+cell[5]);
		last().addFrame(grid[0]+cell[5],grid[3]+cell[5]);
		last().addFrame(grid[0]+cell[6],grid[3]+cell[5]);
		last().addFrame(grid[0]+cell[7],grid[3]+cell[5]);
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[8], grid[3]+cell[0], width, height, normal,"Mucus Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[9], grid[3]+cell[1], width, height, normal,"Purple Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[8], grid[3]+cell[0], width, height, normal,"Spotted Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[0]+cell[9], grid[3]+cell[1], width, height, normal,"Mountain Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[0], grid[3]+cell[0], width, height, normal,"Striped Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[1], grid[3]+cell[0], width, height, normal,"Green Yellow Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[2], grid[3]+cell[0], width, height, normal,"Yellow Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[3], grid[3]+cell[0], width, height, normal,"Wavy Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[0], grid[3]+cell[1], width, height, normal,"Magma Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[1], grid[3]+cell[1], width, height, normal,"RedEgg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[2], grid[3]+cell[1], width, height, normal,"Blue Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[3], grid[3]+cell[1], width, height, normal,"Pink Egg"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[5]+cell[1], grid[4]+cell[0], width, height, normal,"Clover"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", grid[5]+cell[2], grid[4]+cell[0], width, height, normal,"Bush"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", grid[5]+cell[3], grid[4]+cell[0], width, height, normal,"Blue Bush"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", grid[5]+cell[4], grid[4]+cell[0], width, height, normal,"Red Bush"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[0], grid[0]+cell[0],width,height,normal,"House Wall"));
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[8]+cell[1], grid[0]+cell[0],width,height,normal,"House Door"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[2], grid[0]+cell[0],width,height,normal,"House Door Shut"));
		last().onTrigger = typeID-2;	//Turns to open door when triggered
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[3], grid[0]+cell[0],width,height,normal,"House Window"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[4], grid[0]+cell[0],width,height,normal,"House Window Shut"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[5], grid[0]+cell[0],width,height,normal,"House Left Strut"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[6], grid[0]+cell[0],width,height,normal,"House Right Strut"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[2], grid[0]+cell[1],width,height,normal,"Billy Bashful the Badass Barrel"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[3], grid[0]+cell[1],width,height,normal,"Chest"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[0], grid[0]+cell[6],width,height*2,normal,"Well"));
		last().setOffset(0, 32);
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 32, 288, width, height, normal,"Zombie Spawn"));												
		last().isSpawner = true;
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[1], grid[0]+cell[6],width,height*2,normal,"Tree2"));
		last().setOffset(0, 32);
		add(new EntityDefinition(typeID++, nocollide, save, "everything", 288-32, 0, width, height, normal,"warp"));
		last().isTrigger = true;
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[8], grid[0]+cell[1],width,height,normal,"Basic Bush"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[9], grid[0]+cell[1],width,height,normal,"Splash"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[0], grid[0]+cell[2],width,height,normal,"House Roof Angled Small Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[1], grid[0]+cell[2],width,height,normal,"House Left Angled Big Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[2], grid[0]+cell[2],width,height,normal,"House Roof Half Small Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[4], grid[0]+cell[2],width,height,normal,"House Roof Half Big Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[5], grid[0]+cell[2],width,height,normal,"House Roof Half Big Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[6], grid[0]+cell[2],width,height,normal,"House Roof Half Small Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[7], grid[0]+cell[2],width,height,normal,"House Roof Piece Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[3], grid[0]+cell[2],width,height,normal,"House Roof Piece Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[8], grid[0]+cell[2],width,height,normal,"House Roof Angled Big Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[9], grid[0]+cell[2],width,height,normal,"House Roof Angled Small Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[1], grid[0]+cell[3],width,height,normal,"Snow House Door"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[2], grid[0]+cell[3],width,height,normal,"Snow House Door Shut"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[3], grid[0]+cell[3],width,height,normal,"Snow House Window"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[4], grid[0]+cell[3],width,height,normal,"Snow House Window Shut"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[2], grid[0]+cell[4],width,height,normal,"Billy Bashfuls Badass Barrel Buddy "));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[3], grid[0]+cell[4],width,height,normal,"Snow Chest"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[2], grid[0]+cell[6],width,height*2,normal,"SnOhWell"));
		last().setOffset(0, 32);
		add(new EntityDefinition(typeID++, nocollide, save, "everything", 288-64, 0, width, height, normal,"AI Blocker"));
		last().isTrigger = true;
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[3], grid[0]+cell[6],width,height*2,normal,"Snow Tree"));
		last().setOffset(0, 32);
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[4], grid[0]+cell[1], width, height, normal,"House Roof Middle"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[8], grid[0]+cell[4],width,height,normal,"Snow Basic Bush"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[9], grid[0]+cell[4],width,height,normal,"Puddle"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[0], grid[0]+cell[5],width,height,normal,"Snow House Left Angled Small Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[1], grid[0]+cell[5],width,height,normal,"Snow House Left Angled Big Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[2], grid[0]+cell[5],width,height,normal,"Snow House Roof Piece Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[3], grid[0]+cell[5],width,height,normal,"Snow House Roof Half Small Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[4], grid[0]+cell[5],width,height,normal,"Snow House Roof Half Big Left"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[5], grid[0]+cell[5],width,height,normal,"Snow House Roof Half Big Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[6], grid[0]+cell[5],width,height,normal,"Snow House Roof Half Small Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[7], grid[0]+cell[5],width,height,normal,"Snow House Roof Piece Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[8], grid[0]+cell[5],width,height,normal,"Snow House Roof Angled Big Right"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[8]+cell[9], grid[0]+cell[5],width,height,normal,"Snow House Roof Angled Small Right"));
		
		//Ground Crystals
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[6]+cell[6], grid[0]+cell[4],width,height,normal,"Purple Crystal Small"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[6]+cell[7], grid[0]+cell[4],width,height,normal,"Purple Crystal Big"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[6]+cell[8], grid[0]+cell[4],width,height,normal,"Blue Crystal Small"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[6]+cell[9], grid[0]+cell[4],width,height,normal,"Blue Crystal Big"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[6]+cell[6], grid[0]+cell[5],width,height,normal,"Orange Crystal Small"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[6]+cell[7], grid[0]+cell[5],width,height,normal,"Orange Crystal Big"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[6]+cell[8], grid[0]+cell[5],width,height,normal,"Green Crystal Small"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[6]+cell[9], grid[0]+cell[5],width,height,normal,"Green Crystal Big"));
		
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[1]+cell[0],width,height,normal,"No Fire"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[1]+cell[0],width,height,fast,"Lit Fire"));
		last().addFrame(grid[2]+cell[1],grid[1]+cell[1]);
		last().addFrame(grid[2]+cell[1],grid[2]+cell[1]);
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[1]+cell[0],width,height,normal,"Dead Fire"));
		last().addFrame(grid[2]+cell[2],grid[1]+cell[1]);
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[0],width,height,normal,"Fur Top"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[4], grid[1]+cell[0],width,height,normal,"Pelt Top"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[5], grid[1]+cell[0],width,height,normal,"Blood Light 1"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[6], grid[1]+cell[0],width,height,normal,"Blood Light 2"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[1],width,height,normal,"Fur Bottom"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[4], grid[1]+cell[1],width,height,normal,"Pelt Bottom"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[5], grid[1]+cell[1],width,height,normal,"Blood Moderate 1"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[6], grid[1]+cell[1],width,height,normal,"Blood Moderate 2"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[2],width,height,normal,"Chair"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[2],width,height,normal,"Table"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[2],width,height,normal,"Blood Excessive 1"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[2],width,height,normal,"Blood Excessive 2"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[1]+cell[3],width,height,normal,"Barrel"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[1]+cell[3],width,height,normal,"Barrel Open"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[1]+cell[3],width,height,normal,"Bone 1"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[3],width,height,normal,"Bone 2"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[4], grid[1]+cell[3],width,height,normal,"Bone 3"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[5], grid[1]+cell[3],width,height,normal,"Blood Medium 1"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[6], grid[1]+cell[3],width,height,normal,"Blood Medium 2"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[1]+cell[4],width,height,normal,"Debris 1"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[1]+cell[4],width,height,normal,"Debris 2"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[1]+cell[4],width,height,normal,"Bone 4"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[4],width,height,normal,"Skull"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[4], grid[1]+cell[4],width,height,normal,"Bone 5"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[5], grid[1]+cell[4],width,height,normal,"Blood Profuse 1"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[6], grid[1]+cell[4],width,height,normal,"Blood Profuse 2"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[1]+cell[5],width,height,normal,"Basic Chest Top"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[1]+cell[5],width,height,normal,"Crate"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[1]+cell[6],width,height,normal,"Basic Chest"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[1]+cell[7],width,height,normal,"Basic Chest Bottom"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[2]+cell[0],width,height,normal,"Rock Small"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[2]+cell[0],width,height,normal,"Rock Med"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[2]+cell[0],width,height,normal,"Rock Small Snow"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[2]+cell[0],width,height,normal,"Rock Med Snow"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[2]+cell[1],width,height,normal,"Rock XSmall"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[2]+cell[1],width,height,normal,"Rock Large"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[2]+cell[1],width,height,normal,"Rock XSmall Snow"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[2]+cell[1],width,height,normal,"Rock Large Snow"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[2]+cell[2],width,height,normal,"Gold Rock Small"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[2]+cell[2],width,height,normal,"Gold Rock Med"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[2]+cell[2],width,height,normal,"Gem Rock Small"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[2]+cell[2],width,height,normal,"Gem Rock Med"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[2]+cell[3],width,height,normal,"Gold Rock XSmall"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[2]+cell[3],width,height,normal,"Gold Rock Large"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[2]+cell[3],width,height,normal,"Gem Rock XSmall"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[2]+cell[3],width,height,normal,"Gem Rock Large"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[2]+cell[4],width,height,normal,"One Gold Coin"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[2]+cell[4],width,height,normal,"Two Gold Coins"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[2]+cell[4],width,height,normal,"Four Gold Coins"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[2]+cell[4],width,height,normal,"Nine Gold Coins"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[2]+cell[5],width,height,normal,"Approximately Fourteen Gold Coins"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[2]+cell[5],width,height,normal,"Gold Chalice"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[2], grid[2]+cell[5],width,height,normal,"Gold Bars"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[3], grid[2]+cell[5],width,height,normal,"Gold Box"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[0], grid[2]+cell[6],width,height,normal,"Gold Crown"));
		add(new EntityDefinition(typeID++, collide, save, "everything", grid[2]+cell[1], grid[2]+cell[6],width,height,normal,"Gold Loot"));
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[0], grid[0]+cell[0],width,height,normal,"Path Inner 1"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[1], grid[0]+cell[0],width,height,normal,"Path Inner 2"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[2], grid[0]+cell[0],width,height,normal,"Path Inner 3"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[0], grid[0]+cell[1],width,height,normal,"Path Inner 4"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[2], grid[0]+cell[1],width,height,normal,"Path Inner 5"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[0], grid[0]+cell[2],width,height,normal,"Path Inner 6"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[1], grid[0]+cell[2],width,height,normal,"Path Inner 7"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[2], grid[0]+cell[2],width,height,normal,"Path Inner 8"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[0], grid[0]+cell[3],width,height,normal,"Path Outer 1"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[1], grid[0]+cell[3],width,height,normal,"Path Outer 2"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[2], grid[0]+cell[3],width,height,normal,"Path Outer 3"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[0], grid[0]+cell[4],width,height,normal,"Path Outer 4"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[1], grid[0]+cell[4],width,height,normal,"Path"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[2], grid[0]+cell[4],width,height,normal,"Path Outer 5"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[0], grid[0]+cell[5],width,height,normal,"Path Outer 6"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[1], grid[0]+cell[5],width,height,normal,"Path Outer 7"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, nocollide, save, "everything", grid[3]+cell[2], grid[0]+cell[5],width,height,normal,"Path Outer 8"));
		last().isUnderlay = true;
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 32, width, height, normal,"Wall top back left collision"));
		last().isOverlay = true;
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 32, width, height, normal,"Wall top back right collision"));
		last().isOverlay = true;
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[2], grid[0] + cell[1], width, height, normal,"Wall Norm Overlay"));
		last().isOverlay = true;
		add(new EntityDefinition(typeID++, collide, save,  "everything", grid[0]+cell[8], grid[0] + cell[2], width, height, normal,"Gate Closed"));
		last().onTrigger=typeID;	//Turns into the next entity when triggered
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[9], grid[0] + cell[2], width, height, normal,"Gate Open"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", grid[9]+cell[6], grid[1] + cell[1], width, height, normal,"Castle Door Locked"));
		last().onTrigger=typeID;	//Turn into the next entitiy
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[9]+cell[6], grid[1] + cell[5], width, height, normal,"Castle Door"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[4], grid[0] + cell[5], width, height, normal,"Ladder Hole"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[0]+cell[5], grid[0] + cell[5], width, height, normal,"Ladder"));
		//add(new EntityDefinition(typeID++, collide, save,  "everything", grid[4]+cell[i], grid[4]+cell[j], width, height, normal,"Blue Building"));


	}
	
	//-------------------------------DO NOT MODIFY BELOW------------------------------
	
	/**Generate EntityType.java!
	 * Run this whenever you change something above
	 * @throws Exception
	 */
	private void generateEntityTypeJavaFile() throws Exception {
		//Generates EntityManager.java based on the names
		PrintWriter pw = new PrintWriter("src/entity/EntityType.java");
		pw.println("/*This class is automatically generated by EntityManager.java");
		pw.println("* Do not modify manually! */");
		pw.println("package entity;");
		pw.println("import java.util.HashSet;");
		pw.println("public class EntityType {");
		for (EntityDefinition d : definitions) {
			String name = d.name.replace(" ", "_").replace("-","").toUpperCase();
			pw.printf("\tpublic static final int %s = %d;\n",name,d.type);
			System.out.println("Generated: " + name + " = " + d.type);
		}
		
		//Spawners
		pw.println("\tpublic static HashSet<Integer> SPAWNERS = new HashSet<Integer>();");
		pw.println("\tstatic {");
		for (EntityDefinition d : definitions) {
			if (d.isSpawner) {
				System.out.println("Added " + d.name + " to spawners");
				pw.printf("\tSPAWNERS.add(%d);//Autogenerated: %s\n",d.type,d.name);
			}
		}
		
		pw.println("\t}\n}");
		pw.close();
		System.out.println("Done!  Remember to clean your eclipse project!");
		
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
