package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import entity.EntityType;

public class Map {
	private final String mapFilename;
	private HashSet<Integer> unpassableTiles = new HashSet<Integer>();
	private Game game;
	private int defaultTileForNewChunks = 5;//Water
	int spawnRow,spawnCol,chunkRows,chunkCols;

	//Indexed as row, col.  Eg chunks.get(row).get(col)
	private HashMap<Integer,HashMap<Integer,MapChunk>> chunks = new HashMap<Integer,HashMap<Integer,MapChunk>>();

	//Map player-entityID to a delta object
	//Delta represents what's changed that they don't know about


	/**If generateNew is set to true, a new map will be generated and saved to filename
	 * 
	 * @param filename
	 */
	public Map(Game game, String filename) {
		this.game = game;
		mapFilename = filename;
		unpassableTiles.add(5);//water
		unpassableTiles.add(9);//lava
		unpassableTiles.add(15);//Dark Water
		unpassableTiles.add(19);//Maisma
		unpassableTiles.add(20);//Black Tile

		File f = new File(filename);
		load();
	}

	public void load() {
		System.out.println("Loading map from " + this.mapFilename);

		try {
			File file = new File(mapFilename);
			if (!file.exists()) {
				createBasicMapFile(file);
			}
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[4+4+4+4+4];	//ChunkWidth, ChunkHeight, SpawnRow, SpawnCol, NumChunks
			fis.read(buf);

			ByteBuffer buffer = ByteBuffer.wrap(buf);
			chunkRows = buffer.getInt();
			chunkCols = buffer.getInt();
			spawnRow = buffer.getInt();
			spawnCol = buffer.getInt();
			int numChunks = buffer.getInt();

			System.out.printf("Map has %d chunks.  Spawn (row,col): (%d,%d).  Chunk Size(row,col): (%d,%d)%n",numChunks,spawnRow,spawnCol,chunkRows,chunkCols);

			int bytesPerChunk = 4 + 4 + (4*chunkRows*chunkCols);	//chunkRow, chunkCol, data (rows*cols of ints)
			buf = new byte[bytesPerChunk];
			for (int i = 0; i < numChunks; i++) {
				//Load each chunk
				fis.read(buf);
				ByteBuffer bb = ByteBuffer.wrap(buf);
				int rowCoord = bb.getInt();
				int colCoord = bb.getInt();
				MapChunk chunk = new MapChunk(rowCoord,colCoord,chunkRows,chunkCols);
				for (int r = 0; r < chunkRows; r++) {
					for (int c = 0; c < chunkCols; c++) {
						chunk.set(r, c, bb.getInt());
					}
				}

				//Store this in our map
				if (!chunks.containsKey(rowCoord)) {
					chunks.put(rowCoord, new HashMap<Integer,MapChunk>());
				}
				chunks.get(rowCoord).put(colCoord, chunk);
				System.out.println("Loaded chunk " + rowCoord + " " + colCoord);
			}
			System.out.println("Loaded map: " + numChunks + " chunks");
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createBasicMapFile(File file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		//Put the basics.  ChunkRowSize, chunkColsize, spawnrow, spawncol, and numChunks
		ByteBuffer bb = ByteBuffer.allocate(5*4);
		bb.putInt(6);	//ChunkRowSize
		bb.putInt(6);	//ChunkColSize
		bb.putInt(0);	//SpawnRow
		bb.putInt(0);	//SpawnCol
		bb.putInt(0);	//num chunks	
		fos.write(bb.array());
		fos.close();
	}

	public void save() {
		System.out.println("Saving map to " + mapFilename);
		try {
			FileOutputStream fos = new FileOutputStream(new File(mapFilename));

			//ChunkRows,chunkcols, spawnrow, spawncol, numChunks
			ByteBuffer bb = ByteBuffer.allocate(4 * 5);
			bb.putInt(this.chunkRows);
			bb.putInt(this.chunkCols);
			bb.putInt(spawnRow);
			bb.putInt(spawnCol);
			bb.putInt(countChunks());
			fos.write(bb.array());

			//Now add all the chunk data to it
			for (HashMap<Integer,MapChunk> row : chunks.values()) {
				for (MapChunk chunk : row.values()) {
					fos.write(chunk.getBytes());
				}
			}

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**Get a specific chunk, and create it if it doesn't exist
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	MapChunk getChunk(int row, int col) {
		HashMap<Integer,MapChunk> rowOfChunks = chunks.get(row);
		if (rowOfChunks == null) {
			//Create it
			rowOfChunks = new HashMap<Integer,MapChunk>();
			chunks.put(row, rowOfChunks);
		}

		//Get the chunk
		MapChunk chunk = rowOfChunks.get(col);
		if (chunk == null) {
			System.out.println("Creating new chunk " + row + " " + col);
			chunk = new MapChunk(row,col,this.chunkRows,this.chunkCols);
			chunk.init(this.defaultTileForNewChunks);
			rowOfChunks.put(col, chunk);
		}
		return chunk;
	}

	/**Count how many chunks we have in memory and return that value
	 * 
	 * @return
	 */
	public int countChunks() {
		int count = 0;
		for (HashMap<Integer,MapChunk> chunkMap : chunks.values()) {
			count += chunkMap.size();
		}
		return count;
	}

	public byte[] getUnpassableTileIds() {
		int length = unpassableTiles.size();
		length += 1;

		ByteBuffer bb = ByteBuffer.allocate(length * 4);

		//UnpassableTiles
		bb.putInt(unpassableTiles.size());
		for (int tileID : unpassableTiles) {
			bb.putInt(tileID);
		}


		return bb.array();
	}

	/**Warning, this is not super fast.  2 hash map calls
	 * 
	 * @param row
	 * @param col
	 * @param tile
	 */
	public void set(int row, int col, int tile) {
		//Get the chunk
		int cRow = row/this.chunkRows;
		int rowInsideChunk = row - (cRow * this.chunkRows);
		int cCol = col/this.chunkCols;
		int colInsideChunk = col - (cCol * this.chunkCols);

		//Special case for negative numbers
		if (rowInsideChunk < 0) { cRow--; rowInsideChunk+=this.chunkRows; }
		if (colInsideChunk < 0) { cCol--; colInsideChunk+=this.chunkCols; }	

		MapChunk c = getChunk(cRow,cCol);
		c.tiles[rowInsideChunk][colInsideChunk] = tile;
		game.updateTile(row, col, tile);
	}

	/**Warning, this is not super fast.  2 hash map calls
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public int getTileAt(int row, int col) {
		//Get the chunk
		int cRow = row/this.chunkRows;
		row -= cRow * this.chunkRows;
		int cCol = col/this.chunkCols;
		col -= cCol * this.chunkCols;
		//Special case for negative numbers
		if (row < 0) { cRow--; row+=this.chunkRows; }
		if (col < 0) { cCol--; col+=this.chunkCols; }	
		MapChunk c = getChunk(cRow,cCol);
		return c.tiles[row][col];
	}

	public boolean isTilePassable(int row, int col) {
		return unpassableTiles.contains(getTileAt(row,col));
	}
}
