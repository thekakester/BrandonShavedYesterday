package game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import entity.Entity;
import entity.EntityType;

public class Pathfinding {

	private static class Point {
		public int x,y;
		public Point from = null;
		public int depth = 0;
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			return x + ((Integer.MAX_VALUE/2)*y);
		}

		@Override
		public boolean equals(Object o) {
			Point p = (Point)o;
			return p.x == this.x && p.y == this.y;
		}
	}

	/**Get a path from a start location to an end location
	 * Returns a byte list where 0/1/2/3 = up/dn/lft/rght respectively
	 * @param g
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param maxDist
	 * @param isAI if true, this will not look for paths over Anti-AI tiles.
	 * @return
	 */
	public static LinkedList<Byte> findPath(Game g, int startX, int startY, int endX, int endY, int maxDist, boolean isAI) {
		//Swapping start and goal makes building the path easier
		Point goal = new Point(startX,startY);
		Point start = new Point(endX,endY);
		
		//Quick way out if goal (start) isnt passable
		if (!isPassable(g, start, isAI)) { return new LinkedList<Byte>(); }
		
		if (start.equals(goal)) { return buildPath(goal); }

		Queue<Point> queue = new LinkedList<Point>();
		HashSet<Point> visited = new HashSet<Point>();

		//Search backward and build our path back
		queue.offer(start);

		while (!queue.isEmpty()) {
			Point p = queue.poll();
			if (visited.contains(p)) { continue; }
			visited.add(p);

			//Visit everything around this
			List<Point> neighbors = new ArrayList<Point>(4);
			for (int i = 0; i < 4; i++) {
				int dX = i==0 ? -1 : i==1 ? 1 : 0;
				int dY = i==2 ? -1 : i==3 ? 1 : 0;

				Point neighbor = new Point(p.x + dX, p.y + dY);
				neighbor.from = p;
				neighbor.depth = p.depth+1;
				neighbors.add(neighbor);
			}
			
			//Shuffle to give random effect
			Collections.shuffle(neighbors);
				
			for (Point neighbor : neighbors) {	
				//If its passable, add it to the queue
				if (neighbor.depth < maxDist && isPassable(g,neighbor,isAI)) {
					if (neighbor.equals(goal)) {
						return buildPath(neighbor);
					}
					queue.offer(neighbor);
				}
			}
		}

		return new LinkedList<Byte>();	//Empty list = no path
	}

	//Build in terms of directions (up/dwn/lft/rt = 0,1,2,3 respectively)
	private static LinkedList<Byte> buildPath(Point neighbor) {
		LinkedList<Byte> directions = new LinkedList<Byte>();
		
		//Neighbor starts out as startX,starY
		
		//from points to where we must go

		Point current = neighbor;
		while (current.from != null) {
			Point from = current.from;
			if (from.y < current.y) {
				directions.add((byte)0);	//Up (to go from current to the next tile)
			} else if (from.y > current.y) {
				directions.add((byte)1);	//Down (to go from current to the next tile)
			} else if (from.x < current.x) {
				directions.add((byte)2);	//Left (to go from current to the next tile)
			}else {
				directions.add((byte)3);//Right (to go from current to the next tile)
			}
			current = current.from;
		}
		return directions;
	}

	private static boolean isPassable(Game g, Point neighbor, boolean isAI) {
		if (!g.map.isTilePassable(neighbor.y,neighbor.x)) {
			return false;
		}

		//Check if an entity is in the way
		for (Entity e : g.getEntitiesAt(neighbor.x,neighbor.y)) {
			if (e.definition.collidable || (isAI && e.type == EntityType.AI_BLOCKER)) {
				return false;
			}
		}
		return true;
	}
}
