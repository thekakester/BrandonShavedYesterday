package game;

public class StaleMapDelta {
	public final int startRow,startCol,endRow,endCol;
	public StaleMapDelta(int startRow, int startCol, int endRow, int endCol) {
		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;
	}
}
