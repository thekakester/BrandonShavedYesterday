package game;

import engine.GameInterface;
import engine.Server;

public class SkeletonGame implements GameInterface {

	//Start the server and the game!
	public static void main(String[] args) { new Server(new SkeletonGame(),args); }
	
	@Override
	public byte[] respondToClient(String key, String value) {
		//This code executes when the client talks to us
		//We can make up our own commands (keys)
		//and create our own arguments for them (values)
		
		//Repeat whatever the client sent us
		if (key.equals("echo")) {
			return value.getBytes();
		}
		
		//Greet the client, ignore the value part
		if (key.equals("greet")) {
			return "Hello!  Welcome to my game".getBytes();
		}
		
		//Empty response
		return null;
	}

	//Gets called every 10 seconds while a player is connected
	@Override
	public void save() {
	}
	

}
