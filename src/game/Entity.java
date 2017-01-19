package game;

import java.nio.ByteBuffer;

public class Entity {
	public final int id;
	public final int type;
	public int[] attributes  = new int[0];	//Dependent on the type of entity (eg.  HP)
	public int x,y;
	public Entity(int id, int type) {
		this.id = id;
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("[Entity id:%d type:%d (x:%d,y:%d)]",id,type,x,y);
	}
	
	public byte[] bytes() {
		
		ByteBuffer bb = ByteBuffer.allocate(this.sizeInBytes());
		
		bb.putInt(id);
		bb.putInt(type);
		bb.putInt(x);
		bb.putInt(y);
		bb.putInt(attributes.length);
		
		for (int i = 0; i < attributes.length; i++) {
			bb.putInt(attributes[i]);
		}
		
		return bb.array();
	}

	public int sizeInBytes() {
		int length = 5;	//ID, type,x,y,attributes.length
		length += attributes.length;
		length *= 4;	//int = 4 bytes
		return length;
	}
}
