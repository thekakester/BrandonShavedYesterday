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
		add(new EntityDefinition(typeID++, nocollide, nosave, "objects", 288, 0, width, height, normal,"null"));
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 96, 0, width, height, normal,"player"));
		last().useWalkingAnimation(96,0);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 0, width, height, normal,"sign"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 32, 0, width, height, normal,"Gravestone"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 64, width, height, normal,"Wall top back left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 64, width, height, normal,"Wall top back right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 96, width, height, normal,"Wall bot back left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 96, width, height, normal,"Wall bot back right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 128, width, height, normal,"Wall left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 128, width, height, normal,"Wall right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 160, width, height, normal,"Wall front top left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 160, width, height, normal,"Wall front top right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 0, 192, width, height, normal,"Wall front bot left"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 32, 192, width, height, normal,"Wall front bot right"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 64, 64, width, height, normal,"Wall norm Top"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 64, 96, width, height, normal,"Wall norm"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 64, 128, width, height, normal,"Wall Pillar top left half"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 64, 160, width, height, normal,"Wall Pillar bottom left half"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 96, 128, width, height, normal,"Wall Pillar top right half"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", 96, 160, width, height, normal,"Wall Pillar bottom right half"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", 256, 32, width, height, normal,"Egg Yellow"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", 288, 32, width, height, normal,"Egg Blue-Orange"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 0, 288, width, height, normal,"Mushroom Spawn"));
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 0, 256, width, height, normal,"Mushroom Enemy"));
		last().useWalkingAnimation(0,256);
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", 32, 288, width, height, normal,"Chick Spawn"));
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 96, 256, width, height, normal,"Chick Entity"));
		last().useWalkingAnimation(96,256);
		add(new EntityDefinition(typeID++, collide, save,  "everything", 96, 64, width, height, normal,"Tree"));
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
		add(new EntityDefinition(typeID++, nocollide, nosave, "darkerCharacters", 96, 0, width, height, normal,"Zombie Enemy"));
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
		add(new EntityDefinition(typeID++, nocollide, nosave, "characters", 384, 0, width, height, normal,"Robot Entity"));
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
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[2], grid[3]+cell[0], width, height, normal,"Yellw Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[3], grid[3]+cell[0], width, height, normal,"Wavy Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[0], grid[3]+cell[1], width, height, normal,"Magma Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[1], grid[3]+cell[1], width, height, normal,"RedEgg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[2], grid[3]+cell[1], width, height, normal,"Blue Egg"));
		add(new EntityDefinition(typeID++, nocollide, nosave,  "everything", grid[1]+cell[3], grid[3]+cell[1], width, height, normal,"Pink Egg"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[5]+cell[1], grid[4]+cell[0], width, height, normal,"Clover"));
		add(new EntityDefinition(typeID++, nocollide, save,  "everything", grid[5]+cell[2], grid[4]+cell[0], width, height, normal,"Weeds"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", grid[5]+cell[3], grid[4]+cell[0], width, height, normal,"Blue Bush"));
		add(new EntityDefinition(typeID++, collide, save,  "everything", grid[5]+cell[4], grid[4]+cell[0], width, height, normal,"Red Bush"));
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
