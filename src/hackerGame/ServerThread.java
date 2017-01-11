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
		this.address = socket.getInetAddress().toString() + " (" + System.nanoTime() % 1000 + ")";
		//System.out.println("New connection from " + address);
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

			//Separate the args from the page
			String[] data = request.split("\\?");
			System.out.println(address + " requesting: " + request);
			if (data[0].equals("/g")) {
				if (data.length < 2) {
					//They didn't ask anything, so we won't respond
					sendResponse("".getBytes());
					return;
				}
				//Do the game logic
				handleQuery(data[1]);
			} else {
				//Find the file they requested
				if (data[0].length()==1) { data[0] = "/index.html"; }
				File f = new File("www" + data[0]);
				if (f.exists()) {
					String responseType = "application/octet-stream";
					if (f.getName().endsWith(".html")) {
						responseType = "text/html";
					}
					sendResponse(Files.readAllBytes(f.toPath()),responseType);
				} else {
					System.out.println(address + " " + f.getPath() + " doesn't exist");
					sendResponse("".getBytes());
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
		//System.out.println(address + " Connection closed");
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

	public void handleQuery(String urlData) throws Exception {
		//Example url:
		//update=1|4|7&map=|1|1|6&message=hello%20there
		
		//Step 1, separate out each argument
		String[] args = urlData.split("&");
		
		
		byte[][] responses = new byte[args.length][];
		int index = 0;	//Which response to fill next
		
		//Step 2, loop over each argument
		for (String argument : args) {
			//Step 3: Split the key from the value
			//Example:  update=1|4|7
			//Becomes "update" and "1|4|7"
			String[] keyValue = argument.split("=");
			if (keyValue.length < 2) {
				responses[index++] = new byte[0];
				//Something is wrong
				System.out.println("Can't understant argument, not enough arguments: " + argument);
			} else {
				//Any parsing further than this is specific to the method.  We'll pass this along
				responses[index++] = game.execute(keyValue[0],keyValue[1]);
			}
		}
		
		
		//Combine all the responses
		int length = 0;
		for (byte[] response : responses) { length += response.length; }
		
		//Create a main response object
		byte[] combinedResponse = new byte[length];
		
		//Copy data over
		index = 0;
		for (byte[] response : responses) {
			for (byte b : response) {
				combinedResponse[index++] = b;
			}
		}
		
		sendResponse(combinedResponse);
//		
//		//return this response
//		
//		if (query.equals("init")) {
//			init();
//		} else if (query.equals("getpid")) {
//			getPid();
//		} else if (query.equals("update")) {
//			update(args);
//		} else {
//			sendResponse(("Unhandled server command: " + query).getBytes());
//		}
//		
	}

	/**This gets called by the client while loading and should send all data
	 * 
	 */
//	private void init() {
//		sendResponse(game.map.serialize());
//	}
//
//	private void getPid() {
//		sendResponse(intToBytes(game.getNewEntityId()));
//	}
//	
//	private void update(String[] args) {
//		game.updateEntity(Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]));
//		sendResponse(game.serializeEntities());
//	}
}
