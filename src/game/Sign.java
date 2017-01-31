package game;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;

public class Sign {
	private int lineCount = 0;
	private String[] lines = new String[4];
	public int triggerEid = -1;	//If set, this will get triggered when this sign is read

	public Sign() {

	}

	public Sign(boolean nothing) {
		if (nothing) {
			lineCount = 4;
			lines[0] = "Hmm... I can't quite make out";
			lines[1] = "what this is trying to say.";
			lines[2] = "";
			lines[3] = "Must not be important";
		}
	}

	public Sign(String string) {
		//Split by newlines
		String[] data = string.split("\n");
		lineCount = Math.min(4, data.length);
		for (int i = 0; i < lineCount; i++) {
			lines[i] = data[i];
		}
	}

	public void addLine(String s) {
		if (lineCount >= lines.length) { return; }
		try {
			lines[lineCount++] = URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
	}

	public byte[] getBytes() {
		//Build the response
		ByteBuffer bb = ByteBuffer.allocate(getSizeInBytes());

		bb.putInt(ResponseType.NOTIFICATION);
		bb.putInt(lineCount);

		for (int i = 0; i < lineCount; i++) {
			bb.putInt(lines[i].length());
			for (char c : lines[i].toCharArray()) {
				bb.putChar(c);
			}
		}
		return bb.array();
	}

	public int getSizeInBytes() {
		//Calculate the length to return
		int size = 8;	//First 2 ints (8bytes): ResponseType + numLines

		//For each line, list the length of the string (as an int)
		size += 4 * lineCount;
		for (int i = 0; i < lineCount; i++) {
			size += lines[i].length() * 2;	//2 bytes per char
		}
		return size;
	}
}
