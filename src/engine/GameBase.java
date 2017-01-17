package engine;

public abstract class GameBase {
	/**A client will routinely send us arguments.
	 * @param key Often the command that is to be executed.  Eg "setplayerposition"
	 * @param value The argument(s) associated.  These must be parsed manually because their format is not restricted.  Eg "10,10"
	 * @return Return a byte array to send back to the client.  For convenience, use a ByteBuffer for numeric values and String.getBytes() for strings.
	 */
	public abstract byte[] respondToClient(String key, String value);
	
	/**Gets run repeatedly in a separate thread from the server.
	 */
	public abstract void run();
	
	/**How long to wait between consecutive run() calls.
	 * Recommended to keep this value as high as possible to minimize client waiting time
	 * Remember, the client and game cannot run code at the same time!
	 * @return The time in milliseconds to wait before calling run() again.
	 */
	public long delayBetweenRuns() {
		return 100;	//Default delay 100ms
	}
}
