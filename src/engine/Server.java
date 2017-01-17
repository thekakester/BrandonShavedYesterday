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
import java.util.concurrent.Semaphore;

import game.Game;

public class Server {

	private GameBase game;
	private long lastSaveTime = 0;
	private static Semaphore gameLock = new Semaphore(1);
	
	/**Create a server for the selected game
	 * 
	 * @param game
	 */
	public Server(GameBase game, String[] args) {
		
		//Start a simple web server
		int port = Settings.PORT;	//Shouldn't change
		
		//If the user specify a different port as an argument?
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
				System.out.println("Using user specified port (" + port + ")");
			} catch (Exception e){System.out.println("ERROR PARSING PORT.  Not a number: " + args[0]);}
		}
		
		//Start a thread to handle our game (default updates every 100ms, acts like another client)
		GameThread gt = new GameThread(game);
		gt.start();
		
		try {
			System.out.println("Starting web server on port " + port);
			ServerSocket socket = new ServerSocket(port);
			System.out.println("Awaiting connections");
			while (true) {
				try {
					//Thread.sleep(500);
					Socket client = socket.accept();
					ServerThread thread = new ServerThread(game, client);
					thread.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getLock() {
		try {
			gameLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void releaseLock() {
		gameLock.release();
	}
}
