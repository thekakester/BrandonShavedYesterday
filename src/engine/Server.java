package engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.Random;
import java.util.Scanner;

import game.Game;

public class Server {

	private GameInterface game;
	
	/**Create a server for the selected game
	 * 
	 * @param game
	 */
	public Server(GameInterface game, String[] args) {
		
		//Start a simple web server
		int port = Settings.PORT;	//Shouldn't change
		
		//If the user specify a different port as an argument?
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
				System.out.println("Using user specified port (" + port + ")");
			} catch (Exception e){System.out.println("ERROR PARSING PORT.  Not a number: " + args[0]);}
		}
		
		try {
			System.out.println("Starting web server on port " + port);
			ServerSocket socket = new ServerSocket(port);
			System.out.println("Awaiting connections");
			while (true) {
				try {
					//Thread.sleep(500);
					Socket client = socket.accept();
					ServerThread thread = new ServerThread(game, client);
					thread.run();	//CHANGE TO .start() to be multithreaded.  WARNING, code must be threadsafe then!
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
