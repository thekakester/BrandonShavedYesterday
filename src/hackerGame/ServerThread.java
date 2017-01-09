package hackerGame;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Scanner;

/**This class handles communication with the client.
 * No game logic should be written here.  Serialization code is fine
 * 
 * @author Mitch
 *
 */
public class ServerThread extends Thread {
	private final Socket socket;
	private final Game game;
	private final String address;
	private String response;

	public ServerThread(Game game, Socket socket) {
		this.socket = socket;
		this.game = game;
		this.address = socket.getLocalAddress().toString() + " (" + System.nanoTime() % 1000 + ")";
		System.out.println("New connection from " + address);
	}

	@Override
	public void run() {
		//Read the request
		try {
			Scanner scanner = new Scanner(socket.getInputStream());
			if (!scanner.hasNextLine()) {
				return;
			}
			String request = scanner.nextLine().split(" ")[1];

			//Separate the args
			String[] data = request.split("\\?");
			System.out.println(address + " requesting: " + request);
			if (data[0].equals("/g")) {
				//Do the game logic
				handleQuery(data[1].split("&"));
			} else {
				//Find the file they requested
				File f = new File("www" + data[0]);
				if (f.exists()) {
					String responseType = "application/octet-stream";
					if (f.getName().endsWith(".html")) {
						responseType = "text/html";
					}
					sendResponse(Files.readAllBytes(f.toPath()),responseType);
				} else {
					System.out.println(address + " " + f.getPath() + " doesn't exist");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(address + " Connection closed");
	}

	private void sendResponse(byte[] responseBody) {
		sendResponse(responseBody, "application/octet-stream");
	}

	private void sendResponse(byte[] responseBody, String contentType) {
		OutputStream out = null;
		try {
			out = socket.getOutputStream();
			out.write(("HTTP/1.x 200 OK\n").getBytes());
			//out.write(("Content-Type:" + contentType + "\n").getBytes());
			out.write(("Content-Length:" + responseBody.length + "\n").getBytes());
			out.write("\n".getBytes());
			out.write(responseBody);
		} catch (IOException e) {e.printStackTrace();}
		finally {
			try {
				out.flush();
			} catch (Exception e) {e.printStackTrace();}
		}
	}

	private byte[] intToBytes(int x) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(x);
		return bb.array();
	}

	public void handleQuery(String[] args) throws Exception {
		String query = args[0];

		if (query.equals("init")) {
			init();
		} else if (query.equals("getpid")) {
			getPid();
		} else if (query.equals("update")) {
			update(args);
		}
	}

	/**This gets called by the client while loading and should send all data
	 * 
	 */
	private void init() {
		sendResponse(game.map.serialize());
	}

	private void getPid() {
		sendResponse(intToBytes(game.getNewEntityId()));
	}
	
	private void update(String[] args) {
		game.updateEntity(Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]));
		sendResponse(game.serializeEntities());
	}
}
