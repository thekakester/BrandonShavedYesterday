package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Map implements SerializableObject {
	private final String mapFilename;
	private int map[][];
	private int[] unpassableTiles = {5,9};	//Water, lava
	//Map player-entityID to a delta object
	//Delta represents what's changed that they don't know about
	private HashMap<Integer,MapDelta> deltas = new HashMap<Integer,MapDelta>();
	
	/**Default constructor.  Use this if you don't exclusively need the other
	 * 
	 * @param filename
	 */
	public Map(String filename) {
		this(filename,false);
	}
	
	/**If generateNew is set to true, a new map will be generated and saved to filename
	 * 
	 * @param filename
	 * @param regenerate If set to true, a new map will always be generated.  Otherwise, it will only be generated if it doesn't exist
	 */
	public Map(String filename, boolean regenerate) {
		mapFilename = filename;
		File f = new File(filename);
		if (regenerate || !f.exists()) {
			generateNewMap(100,100,true);	//YOU CAN MODIFY THIS!  (change the map type)
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
			System.out.println("Map size: " + rows + " " + cols);

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
		int length = 2;	//Width + height ints
		length += map.length * map[0].length;	//One int for each tile
		length += 1;	//count of unpassable tiles
		length += unpassableTiles.length;
		
		int bytesNeeded = length * 4;
		
		ByteBuffer bb = ByteBuffer.allocate(bytesNeeded);
		bb.putInt(map.length);
		bb.putInt(map[0].length);
		for (int[] row : map) {
			for (int val : row) {
				bb.putInt(val);
			};
		}
		
		//UnpassableTiles
		bb.putInt(unpassableTiles.length);
		for (int tileID : unpassableTiles) {
			bb.putInt(tileID);
		}
		
		return bb.array();
	}

	public void set(int row, int col, int tile) {
		map[row][col] = tile;
		
		//Tell our deltas what happened
		for (MapDelta d : deltas.values()) {
			d.add(row, col, tile);
		}
	}
	
	public byte[] getDeltaAsBytes(int pid) {
		return deltas.get(pid).serialize();	//Will throw an exception if they player is not subscribed for deltas
	}

	public void subscribe(int pid) {
		this.deltas.put(pid,new MapDelta());
	}
}
