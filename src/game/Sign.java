package game;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;

public class Sign {
	private int lineCount = 0;
	private String[] lines = new String[4];
	
	public Sign() {
		
	}
	
	public void addLine(String s) {
		if (lineCount >= lines.length) { return; }
		try {
			lines[lineCount++] = URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
	}

	public byte[] getBytes() {
		//Calculate the length to return
		int size = 8;	//First 2 ints (8bytes): ResponseType + numLines
		
		//For each line, list the length of the string (as an int)
		size += 4 * lineCount;
		for (int i = 0; i < lineCount; i++) {
			size += lines[i].length() * 2;	//2 bytes per char
		}
		
		//Build the response
		ByteBuffer bb = ByteBuffer.allocate(size);
		
		bb.putInt(ResponseType.SIGN);
		bb.putInt(lineCount);
		
		for (int i = 0; i < lineCount; i++) {
			bb.putInt(lines[i].length());
			for (char c : lines[i].toCharArray()) {
				bb.putChar(c);
			}
		}
		return bb.array();
	}
}
