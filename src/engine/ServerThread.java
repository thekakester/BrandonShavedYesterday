package engine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Scanner;

import game.Game;

/**This class handles communication with the client.
 * No game logic should be written here.  Serialization code is fine
 * NOTE: This may or may not actually be multithreaded
 * 
 * @author Mitch
 *
 */
public class ServerThread extends Thread {
	private final Socket socket;
	private final Game game;
	private final String address;
	private String response;

	/**Note, this may or may not actually be a separate thread! 
	 * See server.java
	 * @param game
	 * @param socket
	 */
	public ServerThread(Game game, Socket socket) {
		this.socket = socket;
		this.game = game;
		this.address = socket.getInetAddress().toString() + " (" + System.nanoTime() % 1000 + ")";
	}

	@Override
	public void run() {
		//Read the request
		try {
			Scanner scanner = new Scanner(socket.getInputStream());
			if (!scanner.hasNextLine()) {
				return;
			}

			//All we care about is the first line of the header
			//Specifically the second part (after "GET")
			//Example:  GET /index.html?someKey=someValue
			String request = scanner.nextLine().split(" ")[1];

			//Split our request in half to get:
			//   1) The web page being requested  Eg: "index.html"
			//   2) the (optional) arguments      Eg: "someKey=someValue"
			String[] data = request.split("\\?");
			String filename = data[0];
			String arguments = data.length > 1 ? data[1] : "";

			//Debug information
			System.out.println(address + " requesting: " + request);

			//Check if the file they're requesting exists in our www directory
			File f = new File("www" + filename);
			if (f.exists()) {
				//Return the file to the client
				sendFile(f,false);
				return;
			}

			//Ok, so the file doesn't exist.
			//However, we've got a fake virtual file called "www/g"
			//This "fake" file is generated by the game.  We'll forward the arguments to the game
			//and let it handle the response to send back.
			if (filename.equals("/g")) {
				letGameGenerateResponse(arguments);
			}

			
			//Well, this is awkward.  They didn't want a real file or our virtual one.
			//Send them 404 error!  (pass true to force HTML regardless of file extension
			sendFile(new File("www/404.html"),true);


			//Below is annoying stuff.  If there's any error, make sure we close our connection
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**Open a file and send it back to the client
	 * 
	 * @param f The file to send back
	 * @param isHTML If set to TRUE, this will force the browser to render this as an HTML file.  If false, the browser will use its default.  If you don't know, use FALSE
	 */
	private void sendFile(File f, boolean isHTML) {
		try {
			sendResponse(Files.readAllBytes(f.toPath()),isHTML);
		} catch (Exception e) {
			//There was a problem reading the file
			//Don't leave the client hanging though
			e.printStackTrace();
			sendResponse("".getBytes(),isHTML);
		}

	}

	private void sendResponse(byte[] responseBody) {
		sendResponse(responseBody, false);  //Assume they're not sending html
	}
	
	/**Send a response back to the client.
	 * @param responseBody The HTTP response data in bytes
	 */
	private void sendResponse(byte[] responseBody, boolean isHTML) {
		OutputStream out = null;
		try {
			out = socket.getOutputStream();
			out.write(("HTTP/1.x 200 OK\n").getBytes());
			if (isHTML) {
				out.write("Content-Type: text/html\n".getBytes());
			}
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

	/**The request comes in many parts (GET arguments).  This will parse them
	 * and send each part to the Game object for it to generate a response
	 * @param urlData Everything that comes after the "?" in a url
	 * @throws Exception
	 */
	public void letGameGenerateResponse(String urlData) throws Exception {
		//Example url:
		//update=1|4|7&map=|1|1|6&message=hello%20there

		//Step 1, separate out each argument
		String[] args = urlData.split("&");

		//Store the game's response to each part (arg) so we can combine them later
		//Since each response is in bytes, and we need to store all the responses
		//this must be an array of responses, which is an array of byte arrays
		byte[][] responses = new byte[args.length][];

		//Step 2, loop over each argument
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];

			//Step 3: Split the key from the value
			//Example:  update=1|4|7
			//Becomes "update" and "1|4|7"
			String[] keyValue = argument.split("=");
			String key =  keyValue[0];
			String value = keyValue.length > 1 ? keyValue[1] : "";
			
			//The value may be URL escaped (eg: "Hello%20There")
			//Replace the special characters with their correct counterparts
			value = URLDecoder.decode(value, "UTF-8");

			//Any parsing further than this is specific to the method.  We'll pass this along
			responses[i] = game.respondToClient(key,value);
		}

		//Combine all the responses
		int length = 0;	//Total length of all the responses
		for (byte[] response : responses) { length += response.length; }

		//Create a main response object
		byte[] combinedResponse = new byte[length];

		//Copy data, byte by byte, from each response calculated earlier
		int index = 0;
		for (byte[] response : responses) {
			for (byte b : response) {
				combinedResponse[index++] = b;
			}
		}

		sendResponse(combinedResponse);
	}
}