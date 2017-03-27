/* Cell.java */

package mazegenerator;

public class Cell {
	private boolean visited = false;
	private boolean topWall = true;
	private boolean leftWall = true;
	private boolean start = false;
	private boolean goal = false;
	private boolean path = false;
	private int row;
	private int column;

	public Cell (int row, int column) {
		this.row = row;
		this.column = column;
	}

	public boolean visited() {return visited;}
	public boolean topWall() {return topWall;}
	public boolean leftWall() {return leftWall;}
	public boolean isStart() {return start;}
	public boolean isGoal() {return goal;}
	public boolean isPath() {return path;}
	public int row() {return row;}
	public int column() {return column;}

	public void markVisited() {
		visited = true;
	}
	public void markUnvisited() {
		visited = false;
	}
	public void toggleTop() {
		topWall = false;
	}
	public void toggleLeft() {
		leftWall = false;
	}
	public void makeStart(){
		start = true;
	}
	public void makeGoal(){
		goal = true;
	}
	public void makePath(){
		path = true;
	}

	public byte getByte() {
		byte b = 0b000;
		if (leftWall) {b ^= 0b111;}
		if (start) {b ^= 0b100;}
		if (goal) {b ^= 0b010;}
		if (path) {b ^= 0b001;}
		return b;
	}
}