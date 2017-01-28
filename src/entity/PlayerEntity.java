package entity;

import java.awt.Point;
import java.util.HashSet;

import game.Game;

public class PlayerEntity extends Entity{

	private long lastUpdate = System.currentTimeMillis();	//If a player is inactive for 5 seconds, kill em
	private HashSet<Point> cachedChunks = new HashSet<Point>();
	private HashSet<Point> newChunks = new HashSet<Point>();	//Chunks that need to be sent to the client
	private HashSet<Point> staleChunks = new HashSet<Point>();	//Chunks that need to be deleted on the client
	private int chunkRow, chunkCol;
	
	protected PlayerEntity(int id, int type,int x, int y) {
		super(id, type,x,y);
	}
	
	@Override
	public void update(Game g) {
		super.update(g);
		
		//Kill if idle for 5 seconds
		if (System.currentTimeMillis() - lastUpdate > 5000) {
			System.out.println("Player " + id + " disconnected.");
			g.deleteEntity(id);
			
			//Add a zombie in its place
			Entity zombie = Entity.create(g.getNewEntityId(), EntityType.ZOMBIE_ENEMY,x,y);
			g.addEntity(zombie);
		}
	}

	public void refresh() {
		lastUpdate = System.currentTimeMillis();
	}
	
	/**Recalculate chunkRow and chunkCol and send back a set of chunks we need but don't have
	 * 
	 * @return
	 */
	public void recalculateChunks(int chunkRowSize, int chunkColSize) {
		int newChunkRow = y / chunkRowSize;
		int newChunkCol = x / chunkColSize;
		if (y < 0) { newChunkRow--; }
		if (x < 0) { newChunkCol--; }
		
		
		//Did we change?
		if (newChunkRow == chunkRow && newChunkCol == chunkCol && !cachedChunks.isEmpty()) {
			return;	//Nothing changed
		}
		
		//What's this?  We changed?
		//Add all the chunks we need to see
		HashSet<Point> newCachedChunks = new HashSet<Point>();
		for (int row = newChunkRow - 1; row <= newChunkRow+1; row++) {
			for (int col = newChunkCol-1; col <= newChunkCol+1; col++) {
				newCachedChunks.add(new Point(col,row));
			}
		}
		
		//Create a hash set of chunks that need to be added and those that need to be removed
		this.newChunks.clear();
		this.staleChunks.clear();
		for (Point p : newCachedChunks) {
			if (!cachedChunks.contains(p)) {
				newChunks.add(p);
			}
		}
		
		//What ones do we need to get rid of?
		for (Point p : cachedChunks) {
			if (!newCachedChunks.contains(p)) {
				staleChunks.add(p);
			}
		}
		
		//Set our new cached chunks
		this.cachedChunks = newCachedChunks;
		this.chunkRow = newChunkRow;
		this.chunkCol = newChunkCol;
	}
	
	public HashSet<Point> getStaleChunks() {
		return staleChunks;
	}
	public HashSet<Point> getNewChunks() {
		return newChunks;
	}
}
