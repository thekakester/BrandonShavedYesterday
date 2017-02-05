package game;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;

public class Sign {
	private String[][] lines = new String[0][4];	//First index is pages, next index is lines itself
	public int triggerEid = -1;	//If set, this will get triggered when this sign is read

	public Sign() {

	}

	public Sign(boolean nothing) {
		if (nothing) {
			lines = new String[1][4];
			lines[0][0] = "Hmm... I can't quite make out";
			lines[0][1] = "what this is trying to say.";
			lines[0][2] = "";
			lines[0][3] = "Must not be important";
		}
	}

	public Sign(String string) {
		//Split by newlines
		String[] data = string.split("\n");
		lines = new String[1][4];
		for (int i = 0; i < 4; i++) {
			if (i < data.length) {
				lines[0][i] = data[i];
			} else {
				lines[0][i] = "";
			}
		}
	}

	public void addPage(String[] page) {
		addPage();	//Adds an extra slot at the end.  Copies arrays
		int pg = lines.length-1;
		
		//Replace null with empty string
		for (int i =0; i < page.length; i++) {
			if (page[i] == null) { page[i] = ""; }
		}
		
		for (int line = 0; line < 4; line++)  {
			String text = "";
			if (line < page.length) { text = page[line]; }
			try {
				lines[pg][line] = URLEncoder.encode(text, "UTF-8").replace("+", "%20");
			} catch (UnsupportedEncodingException e) {e.printStackTrace();}
		} 
	}

	private void addPage() {
		String[][] newLines = new String[lines.length+1][4];
		for (int page = 0; page < lines.length; page++) {
			for (int line = 0; line < lines[page].length; line++) {
				newLines[page][line] = lines[page][line];
			}
		}
		lines = newLines;
	}

	public byte[] getBytes() {
		//Build the response
		ByteBuffer bb = ByteBuffer.allocate(getSizeInBytes());

		bb.putInt(ResponseType.NOTIFICATION);
		bb.putInt(lines.length);	//Number of pages

		for (int page = 0; page < lines.length; page++) {
			for (int i = 0; i < lines[page].length; i++) {
				bb.putInt(lines[page][i].length());
				for (char c : lines[page][i].toCharArray()) {
					bb.putChar(c);
				}
			}
		}
		return bb.array();
	}

	public int getSizeInBytes() {
		//Calculate the length to return
		int size = 8;	//First 2 ints (8bytes): ResponseType + numPages

		for (int page = 0; page < lines.length; page++) {
			//For each line, list the length of the string (as an int)
			size += 4*4;	//4 lines, 1 int length for each
			for (int i = 0; i < lines[page].length; i++) {
				size += lines[page][i].length() * 2;	//2 bytes per char
			}
		}
		return size;
	}
}
