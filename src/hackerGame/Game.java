package hackerGame;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Game {
	public Map map;
	
	private int lastAddedEntityID = 99;
	private HashMap<Integer,Entity> entities = new HashMap<Integer,Entity>();

	//Constructor that calls Server.main() for convenience
	public static void main(String[] args) {Server.main(args); }

	public Game() {
		//Uncomment this line to generate a new map file
		map = new Map("default.map",true);
	}

	/**WARNING: Not thread safe!
	 * 
	 * @return
	 */
	public int getNewEntityId() {
		int id = lastAddedEntityID + 1;
		while (entities.containsKey(id)) {
			id++;
		}
		entities.put(id, null);	//Add something so we don't lose this ID to a race condition
		lastAddedEntityID = id;
		return id;
	}
}
