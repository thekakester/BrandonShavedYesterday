package engine;

public interface GameInterface {
	/**A client will routinely send us arguments.
	 * @param key Often the command that is to be executed.  Eg "setplayerposition"
	 * @param value The argument(s) associated.  These must be parsed manually because their format is not restricted.  Eg "10,10"
	 * @return Return a byte array to send back to the client.  For convenience, use a ByteBuffer for numeric values and String.getBytes() for strings.
	 */
	public byte[] respondToClient(String key, String value);

	/**Called evert 10 seconds
	 * 
	 */
	public void save();
}
