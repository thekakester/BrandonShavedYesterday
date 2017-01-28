package game;

import java.nio.ByteBuffer;

public class MapChunk {
	final int[][] tiles;
	final int row,col;
	public MapChunk(int rowCoord, int colCoord, int rows, int cols) {
		tiles = new int[rows][cols];
		this.row = rowCoord;
		this.col = colCoord;
	}
	
	public void set(int row, int col, int type) {
		this.tiles[row][col] = type;
	}

	public byte[] getBytes() {
		ByteBuffer bb = ByteBuffer.allocate(8 + (4*tiles.length*tiles[0].length));
		
		//First 2 ints are the chunkRow and chunkCol (coordinates of this chunk)
		bb.putInt(row);
		bb.putInt(col);
		
		for (int[] row : tiles) {
			for (int tile : row) {
				bb.putInt(tile);
			}
		}
		return bb.array();
	}

	
	/**Set all tiles to this type
	 * 
	 * @param tileType
	 */
	public void init(int tileType) {
		for (int row = 0; row < tiles.length; row++) {
			for (int col = 0; col < tiles[row].length; col++) {
				tiles[row][col] = tileType;
			}
		}
	}
}
