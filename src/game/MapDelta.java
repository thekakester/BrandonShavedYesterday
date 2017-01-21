package game;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class MapDelta {
	
	private LinkedList<Delta> deltas = new LinkedList<Delta>();
	
	//TODO move this all to ClientDelta object
	public byte[] serialize() {
		//Convert this into bytes!
		
		//Each change has row,col,type (3x4bytes)
		int bytesNeeded = deltas.size() * (3*4);
		bytesNeeded += 8;	//2 int needed for the transmission type & length at the beginning
		
		ByteBuffer bb = ByteBuffer.allocate(bytesNeeded);
		bb.putInt(ResponseType.MAP_UPDATE);
		bb.putInt(deltas.size());
		
		
		for (Delta d : deltas) {
			bb.putInt(d.row);
			bb.putInt(d.col);
			bb.putInt(d.type);
		}
		
		return bb.array();
	}
	
	public void add(int row, int col, int type) {
		deltas.add(new Delta(row,col,type));
	}
	
	private class Delta {
		int row, col, type;
		public Delta(int row, int col, int type) {
			this.row = row;
			this.col = col;
			this.type = type;
		}
	}
}
