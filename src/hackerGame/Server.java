package hackerGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.Random;
import java.util.Scanner;

public class Server {
	public static void main(String[] args) { new Server(); }

	Game game = new Game();
	
	public Server() {
		
		//Start a simple web server
		int port = 80;	//Shouldn't change
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
