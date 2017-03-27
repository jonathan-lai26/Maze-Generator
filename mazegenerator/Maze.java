/* Maze.java */

package mazegenerator;

import java.util.*;
import mazegenerator.*;

/**
 *  The Maze class represents a perfect maze in a rectangular grid so 
 *  there is exactly one path between any two points.
 **/
public class Maze {
	//row and column lengths of the maze
	private int rows;
	private int columns;
	//maze represented an a 2D array where maze[rows][columns]
	private Cell[][] mazeState;
	//randomonly generated start and goal cells in the maze
	private Cell start;
	private Cell goal;
	//hashmap used for wall lookup when displaying maze
	private Map<Byte, String> map = new HashMap<Byte, String>();

	
	/**
   	 *  Maze() creates a rectangular grid and initializes each cell 
     **/
	public Maze (int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		mazeState = new Cell[rows][columns];
		
		//new cell initializiation
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				mazeState[i][j] = new Cell(i, j);
		}

		//adding byte keys for wall hashmap
		map.put(Byte.valueOf((byte)0b111), Wall.LEFT_EXISTS.getWall());
		map.put(Byte.valueOf((byte)0b000), Wall.LEFT_REMOVED.getWall());
		map.put(Byte.valueOf((byte)0b011), Wall.LE_START.getWall());
		map.put(Byte.valueOf((byte)0b100), Wall.LR_START.getWall());
		map.put(Byte.valueOf((byte)0b101), Wall.LE_GOAL.getWall());
		map.put(Byte.valueOf((byte)0b010), Wall.LR_GOAL.getWall());
		map.put(Byte.valueOf((byte)0b110), Wall.LE_PATH.getWall());
		map.put(Byte.valueOf((byte)0b001), Wall.LR_PATH.getWall());

	}

	/**
   	 *  generateMaze() removes walls in the rectangular grit to create
   	 *	a maze. Starts at the top-leftmost cell and utilizes a
   	 *	depth-first search (DFS) algorithm to visit every cell.
     **/
	public void generateMaze() {
		Stack<Cell> stack = new Stack<Cell>();
		Cell start = mazeState[0][0];
		
		start.markVisited();
		stack.push(start);
		while (!stack.empty()) {
			Cell current = stack.peek();
			Cell adj = adjacentUnvisited(current);
			if (adj != null) {
				adj.markVisited();
				removeWall(current, adj);
				stack.push(adj);
			} else {
				stack.pop();
			}
		}
	}

	/**
   	 *  adjacentUnvisited() returns a randomly chosen adjacent 
   	 *	cell that has yet to be marked as visited. Returns null
   	 *	if no cells are found.
     **/
	public Cell adjacentUnvisited(Cell current) {
		Direction[] directions = Direction.values();
		Collections.shuffle(Arrays.asList(directions));

		for (Direction d : directions) {
			if (inBounds(d, current.row(), current.column())) {
				Cell next = mazeState[d.row() + current.row()][d.column() +current.column()];
				Boolean visited = next.visited();
				if (!visited) {
					return next;
				}
			}
		}
		return null;
	}

	/**
   	 *  inBounds() checks whether a possible cell exists at 
   	 *  the position directly north, south, east, or west of
   	 *	the input cell.
     **/
	public boolean inBounds(Direction d, int row, int column) {
		int r = d.row() + row;
		int c = d.column() + column;
		return (r > -1 && c > -1 && r < rows && c < columns);
	}

	/**
   	 *  removeWall() removes the grid wall separating a current 
   	 *	cell from a next cell.
     **/
	public void removeWall(Cell current, Cell next) {
		if (current.row() - next.row() == 1) {			//next cell is north
			current.toggleTop();
		} else if (current.row() - next.row() == -1) {	//next cell is south
			next.toggleTop();
		} else if (current.column() - next.column() == 1) {	//next cell is west
			current.toggleLeft();
		} else {										//next cell is east
			next.toggleLeft();
		}
	}

	/**
   	 *  generateGoal() marks a chosen cell in the maze as the
   	 *	start cell and another chosen cell as the goal cell.
   	 *
     **/
	public void generateGoal(int startRow, int startCol, int goalRow, int goalCol) {
		start = mazeState[startRow][startCol];
		start.makeStart();
		goal = mazeState[goalRow][goalCol];
		goal.makeGoal();
	}

	/**
   	 *  generateGoal() randomly chooses start and goal cells
     **/
	public void generateGoal() {
		Random r = new Random();
		int startRow = r.nextInt(rows);
		int startCol = r.nextInt(columns);
		// start = mazeState[startRow][startCol];
		// start.makeStart();

		int goalRow = r.nextInt(rows);
		int goalCol = r.nextInt(columns);
		Boolean different = false;
		//checks for case that goal == start
		while (!different) {
			if ((goalRow != startRow) || (goalCol != startCol)) {
				different = true;
			} else {
				goalRow = r.nextInt(rows);
				goalCol = r.nextInt(columns);
			}
		}

		// goal = mazeState[goalRow][goalCol];
		// goal.makeGoal();
		generateGoal(startRow, startCol, goalRow, goalCol);
	}

	/**
   	 *  generateSolution() finds the unique path that separates the
   	 *	start and goal cells using a breadth-first search (BFS) algorithm
   	 *	then marks the cells along this path.
     **/
	public void generateSolution() {
		markAllUnvisited();
		Queue<ArrayList<Cell>> queue = new LinkedList<ArrayList<Cell>>();
		ArrayList<Cell> path = new ArrayList<Cell>();
		Cell start = this.start;
		
		start.markVisited();
		path.add(start);
		queue.add(path);
		boolean foundSolution = false;
		//finds path and stores into the variable path
		while (!(foundSolution || queue.isEmpty())) {
			ArrayList<Cell> current = queue.remove();
			Cell lastCell = current.get(current.size()-1);
			if (lastCell.isGoal()) {
				path = current;
				foundSolution = true;
			} else {
				ArrayList<Cell> adj = allAdjacentUnvisited(lastCell);
				for (Cell cell : adj) {
					ArrayList<Cell> previousPath = new ArrayList<Cell>(current);
					cell.markVisited();
					previousPath.add(cell);
					queue.add(previousPath);
				}
			}
		}
		//marks cells along path in maze
		for (Cell cell : path) {
			if (!(cell.isStart() || cell.isGoal())) {
				cell.makePath();
			}
		}
			
	}

	/**
   	 *  noWall() checks if a wall exists between the current cell and 
   	 *	the cell to either its north, south, east, or west.
     **/
	public boolean noWall(Direction d, int row, int column) {		
		if (d.row() == -1) {							//next cell is north
			return (!mazeState[row][column].topWall());
		} else if (d.row() == 1) {						//next cell is south
			return (!mazeState[row + d.row()][column].topWall());
		} else if (d.column() == -1) {					//next cell is west
			return (!mazeState[row][column].leftWall());
		} else {										//next cell is east
			return (!mazeState[row][column + d.column()].leftWall());
		}
	}

	/**
   	 *  allAdjacentUnvisited() returns a list of cells that are directly 
   	 *	adjacent to the cell that are unvisited and not separated
   	 *	by a wall.
     **/
	public ArrayList<Cell> allAdjacentUnvisited(Cell current) {
		Direction[] directions = Direction.values();
		Collections.shuffle(Arrays.asList(directions));
		ArrayList<Cell> adj = new ArrayList<Cell>();

		for (Direction d : directions) {
			if (inBounds(d, current.row(), current.column())) {
				if (noWall(d, current.row(), current.column())) {
					Cell next = mazeState[d.row() + current.row()][d.column() +current.column()];
					Boolean visited = next.visited();
					if (!visited) {
						adj.add(next);
					}
				}
			}
		}
		return adj;
	}

	/**
   	 *  markAllUnvisited() runs through maze and marks all cells
   	 *	as unvisited.
     **/
	public void markAllUnvisited() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				mazeState[i][j].markUnvisited();
		}
	}

	/**
   	 *  display() represents maze state as one string
     **/
	public String display() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < rows; i++) {
			//build top walls of maze
			for (int j = 0; j < columns; j++) {
				if (mazeState[i][j].topWall()) {
					sb.append(Wall.TOP_EXISTS.getWall());
				} else {
					sb.append(Wall.TOP_REMOVED.getWall());
				}
			}
			sb.append("+\n");
			//build east/west walls of maze as well as start, goal, or path indicator
			for (int j = 0; j < columns; j++) {
				Byte key = new Byte(mazeState[i][j].getByte());
				sb.append(map.get(key));
			}
			//build east-most walls of maze
			sb.append("|\n");
		}

		//build bottom row of walls
		for (int k = 0; k < columns; k++) {
			sb.append(Wall.TOP_EXISTS.getWall());
		}
		sb.append("+\n");

		return sb.toString();
	}

	public static void main(String[] args) {
		
		//default row and column values
		int rows = 10;
		int columns = 10;

		int startRow = 0;
    	int startCol = 0;
   		int goalRow = 0;
   		int goalCol = 0;
		boolean customGoal = false;

		//user input for row and columns
		if (args.length == 2) {
		    try {
		    	rows = Integer.parseInt(args[0]);
		    	columns = Integer.parseInt(args[1]);
		    	if ((rows <= 1) || (columns <= 1)) {
		    		System.out.println("Row and column must be greater than 1");
		    		System.exit(0);
		    	} 
		    } catch (NumberFormatException e) {
		    	System.out.println("Row and column must both be numbers.");
		    	System.exit(0);
		    }
    	
    	//user input for row, columns, start, and goal cell
    	} else if (args.length == 4) {
		    try {
		    	rows = Integer.parseInt(args[0]);
		    	columns = Integer.parseInt(args[1]);
		    	if ((rows <= 1) || (columns <= 1)) {
		    		System.out.println("Row and column must be greater than 1");
		    		System.exit(0);
		    	} 
		    } catch (NumberFormatException e) {
		    	System.out.println("Row and column must both be numbers.");
		    	System.exit(0);
		    }
    		try {
    			String[] start = args[2].split(",");
    			String[] goal = args[3].split(",");

    			startRow = Integer.parseInt(start[0]);
    			startCol = Integer.parseInt(start[1]);
    			goalRow = Integer.parseInt(goal[0]);
    			goalCol = Integer.parseInt(goal[1]);

				if ((goalRow == startRow) && (goalCol == startCol)) {
					System.out.println("Start and goal cells must be different.");
					System.exit(0);
				} else if ((startRow >= rows) || (startCol >= columns) || (goalRow >= rows) || (goalCol >= columns)) {
					System.out.println("Start and goal cells cannot exceed row or column size. \nThey must be within the range: [0, row-1],[0, column-1]");
					System.exit(0);
				} else if ((startRow < 0) || (startCol < 0) || (goalRow < 0) || (goalCol < 0)) {
					System.out.println("Start and goal cells cannot be negative.\nThey must be within the range: [0, row-1],[0, column-1]");
					System.exit(0);
				}

    			customGoal = true;
    		} catch (NumberFormatException|ArrayIndexOutOfBoundsException e) {
        		System.out.println("Incorrect start and goal cell format. \nMust be: start_row_#,start_column_# goal_row_#,goal_column_#");
        		System.exit(0);
      		}
      	//incorrect user input
    	} else if (args.length > 0) {
    		System.out.println("Incorrect number of arguments.");
    		System.exit(0);
    	}

		Maze maze = new Maze(rows, columns);
		System.out.println("\n\n~~~ORIGINAL ROW x COLUMN GRID~~~");
		System.out.println(maze.display());
		maze.generateMaze();
		System.out.println("\n\n~~~GRID CONVERTED TO MAZE~~~");
		System.out.println(maze.display());
		if (customGoal) {
			maze.generateGoal(startRow, startCol, goalRow, goalCol);
		} else {
			maze.generateGoal();
		}
		System.out.println("\n\n~~~START AND GOAL CELLS IDENTIFIED~~~");
		System.out.println(maze.display());
		maze.generateSolution();
		System.out.println("\n\n~~~SOLUTION PATH~~~");
		System.out.println(maze.display());

	}
}