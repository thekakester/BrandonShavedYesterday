package game;

public class Message {
	public final String message;
	public final int pid;
	public Message(int pid, String message) {
		this.pid = pid;
		this.message = message;
	}
}
