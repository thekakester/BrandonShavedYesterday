package entity;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class EntityDefinition {
	final int type,width,height,animationDuration;
	final boolean collidable, saveable;
	final String srcImageTag;
	ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	
	/**Default constructor.
	 * 
	 * @param typeID - A unique representation for this entitiy
	 * @param collidable - If true, the player cannot walk on this tile
	 * @param saveable - If true, this will be saved as part of the game.  DO NOT enable this for things that move or spawn.
	 * 					 Example: Fence.  BAD-EXAMPLE: zombie
	 * @param srcImageTag - The tag of the image (on the cient) to use.  Example "characters" (assuming a tag "characters" was preloaded)
	 * @param firstFrameX - X Coordinate of the first frame in the spritesheet
	 * @param firstFrameY - Y Coordinate of the first frame in the spritesheet
	 * @param width - Width of the sprite
	 * @param height - Height of the sprite
	 * @param animationDuration
	 */
	public EntityDefinition(int typeID, boolean collidable, boolean saveable, String srcImageTag, int firstFrameX, int firstFrameY, int width, int height, int animationDuration) {
		this.type = typeID;
		this.collidable = collidable;
		this.saveable = saveable;
		this.srcImageTag = srcImageTag;
		this.width = width;
		this.height = height;
		this.animationDuration = animationDuration;
		
		Sprite defaultSprite = new Sprite("entity" + typeID,animationDuration,width,height);
		defaultSprite.addFrame(firstFrameX, firstFrameY);
		this.sprites.add(defaultSprite);
		
	}
	
	/**Add a frame to the most recently created sprite
	 * 
	 * @param x
	 * @param y
	 */
	public void addFrame(int x, int y) {
		sprites.get(sprites.size()-1).addFrame(x, y);
	}

	/**Add a set of sprites to this entity for walking animations
	 * THE ANIMATION SPRITESHEET MUST BE:
	 *  4 rows (downFace,leftFace,rightFace,upFace in that order)
	 *  3 columns (stepLeft, between,  step right in that order)
	 * 
	 * @param x x coordinate of the top left corner of the walking animation spritesheet
	 * @param y y coordinate of the top left corner of the walking animation spritesheet
	 */
	public void useWalkingAnimation(int x, int y) {
		int optionalXOffset = 0;
		int optionalYoffset = 0;
		
		int duration = 3;	//3 time units per animation
		
		for (int row = 0; row < 4; row++) {
			int direction = (row + 1) % 4;	//Converts from spritesheet direction (down,left,right,up) to engine direction (up,down,left,right)
			int yOff = row * height;
			
			//Walking (eg entity2_0_w means entity 2 walking animation up)
			Sprite s = new Sprite("entity" + type + "_" + direction + "_w",duration,width,height);
			sprites.add(s);
			for (int col = 0; col < 4; col++) {
				int xOff = col;
				if (xOff == 3) { xOff = 1; }
				xOff *= width;
				s.addFrame(x + xOff, y + yOff);
			}
			
			//Idle (eg entity2_1 means entity 2 idle look down)
			s = new Sprite("entity" + type + "_" + direction,duration,width,height);
			s.addFrame(x+width, y+yOff);
			sprites.add(s);
		}
	}

	public byte[] getBytes() {
		ByteBuffer bb = ByteBuffer.allocate(this.sizeInBytes());
		bb.put(collidable?(byte)1:(byte)0);
		bb.putInt(srcImageTag.length());
		for (char c : srcImageTag.toCharArray()) {
			bb.putChar(c);
		}
		
		bb.putInt(sprites.size());
		for (Sprite s : sprites) {
			bb.put(s.getBytes());
		}
		
		return bb.array();
	}

	public int sizeInBytes() {
		int len = 1 + 4 + (srcImageTag.length()*2);	//Collidable, imageSource name
		//Add the sprites
		len += 4;	//num sprites
		for (Sprite s : sprites) {
			len += s.getSizeInBytes();
		}
		return len;
	}
}

//Just a collection of frames
class Sprite {
	final int animationDuration;
	final String tag;
	final int width,height;
	ArrayList<Frame> frames = new ArrayList<Frame>();
	public Sprite(String tag, int animationDuration, int width, int height) {
		this.animationDuration = animationDuration;
		this.tag = tag;
		this.width = width;
		this.height = height;
	}
	
	public void addFrame(int x, int y) {
		frames.add(new Frame(x,y));
	}
	
	public int getSizeInBytes() {
		//width, height, tag length, tag, animationduration, frame length, frames
		return 4 + 4 + 4 + (tag.length()*2) + 4 + 4 + (8*frames.size());
	}
	
	/**See getSizeInBytes for order of data
	 * 
	 * @return
	 */
	public byte[] getBytes() {
		ByteBuffer bb = ByteBuffer.allocate(getSizeInBytes());
		bb.putInt(width);
		bb.putInt(height);
		
		bb.putInt(tag.length());
		for (char c : tag.toCharArray()) {
			bb.putChar(c);
		}
		
		bb.putInt(animationDuration);
		bb.putInt(frames.size());
		for (Frame f : frames) {
			bb.putInt(f.x);
			bb.putInt(f.y);
		}
		return bb.array();
	}
}

class Frame {
	int x,y;
	public Frame(int x, int y) { this.x = x; this.y = y;}
}
