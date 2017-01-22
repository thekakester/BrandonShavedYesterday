package game;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class MapDelta {
	int row, col, type;
	public MapDelta(int row, int col, int type) {
		this.row = row;
		this.col = col;
		this.type = type;
	}

}
