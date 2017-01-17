package game;

public class Entity {
	public final int id;
	public int x,y;
	public Entity(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Entity " + id + ": (" + x + "," + y + ")";
	}
}
