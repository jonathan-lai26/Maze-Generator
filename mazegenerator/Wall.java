/* Wall.java */

package mazegenerator;

public enum Wall {
	TOP_EXISTS ("+---"), TOP_REMOVED ("+   "), 
	LEFT_EXISTS ("|   "), LEFT_REMOVED ("    "),
	LE_START("| S "), LR_START("  S "),
	LE_GOAL("| G "), LR_GOAL("  G "),
	LE_PATH("| * "), LR_PATH("  * ");

	private final String wall;
	
	Wall(String wall) {
		this.wall = wall;
	}

	public String getWall() {return this.wall;}
}