package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import entity.EntityType;

public class Map implements SerializableObject {
	private final String mapFilename;
	private int map[][];
	private HashSet<Integer> unpassableTiles = new HashSet<Integer>();
	private Game game;
	int spawnRow,spawnCol;
	
	//Map player-entityID to a delta object
	//Delta represents what's changed that they don't know about

	/**Default constructor.  Use this if you don't exclusively need the other
	 * 
	 * @param filename
	 */
	public Map(Game game, String filename) {
		this(game,filename,false);
	}

	/**If generateNew is set to true, a new map will be generated and saved to filename
	 * 
	 * @param filename
	 * @param regenerate If set to true, a new map will always be generated.  Otherwise, it will only be generated if it doesn't exist
	 */
	public Map(Game game, String filename, boolean regenerate) {
		this.game = game;
		mapFilename = filename;
		unpassableTiles.add(5);//water
		unpassableTiles.add(9);//lava
		unpassableTiles.add(15);//Dark Water
		unpassableTiles.add(19);//Maisma
		
		File f = new File(filename);
		if (regenerate || !f.exists()) {
			generateNewMap(200,200,false);	//YOU CAN MODIFY THIS!  (change the map type)
		}
		load();
	}

	public void load() {
		System.out.println("Loading map from " + this.mapFilename);
		try {
			File file = new File(mapFilename);
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[(int)file.length()];
			fis.read(buf);

			ByteBuffer buffer = ByteBuffer.wrap(buf);
			int rows = buffer.getInt();
			int cols = buffer.getInt();
			spawnRow = buffer.getInt();
			spawnCol = buffer.getInt();
			System.out.println("Map size: " + rows + " " + cols + "  Spawn (r,c): " + spawnRow + "," + spawnCol);

			map = new int[rows][cols];

			//Read the data
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					map[r][c] = buffer.getInt();
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		System.out.println("Saving map to " + mapFilename);
		try {
			FileOutputStream fos = new FileOutputStream(new File(mapFilename));
			fos.write(serialize());

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**Generate a new empty map file and saves it.
	 * You should only call this if your map file is corrupt or missing.
	 * 
	 * @param rows Rows in the map
	 * @param cols Cols in the map
	 * @param randomOnes Set to true to generate random spots around the map file
	 */
	private void generateNewMap(int rows, int cols, boolean randomOnes) {
		System.out.println("Generating a new map file");
		map = new int[rows][cols];

		

		Random rand = new Random();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				int tile = 5;
				if (randomOnes) { tile = rand.nextInt(10); }
				map[r][c] = tile;
			}
		}
		save();
	}

	/**Convert the map to a string and write it to the printwriter provided.
	 * FORMAT: first two ints are rows/cols.  Remaining data is integers
	 * 
	 * @param pw
	 */
	@Override
	public byte[] serialize() {
		int length = 4;	//Width + height + spawnRow + spawnCol ints
		length += map.length * map[0].length;	//One int for each tile
		length += 1;	//count of unpassable tiles
		length += unpassableTiles.size();

		int bytesNeeded = length * 4;

		ByteBuffer bb = ByteBuffer.allocate(bytesNeeded);
		bb.putInt(map.length);
		bb.putInt(map[0].length);
		bb.putInt(spawnRow);
		bb.putInt(spawnCol);
		for (int[] row : map) {
			for (int val : row) {
				bb.putInt(val);
			};
		}
		return bb.array();
	}

	public int getNumRows() {
		return map.length;
	}

	public int getNumCols() {
		return map[0].length;
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

	public void set(int row, int col, int tile) {
		map[row][col] = tile;
		game.updateTile(row, col, tile);
	}

	public int getTileAt(int row, int col) {
		return map[row][col];
	}

	public boolean isTilePassable(int row, int col) {
		if (row < 0 || row >= map.length || col < 0 || col >= map[row].length) {
			return false;
		}
		return !unpassableTiles.contains(map[row][col]);
	}
}
