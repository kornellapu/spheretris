import java.awt.Color;

/**	A gravitációs indikátor, ami megmutatja milyen irányba fog esni az éppen esõ alakzat
 * */
public class TriangleIndicator {
	Position pos;
	GameLogic.Dir dir;
	Color color;
	
	public TriangleIndicator() {
		pos = new Position();
		dir = GameLogic.Dir.UNKNOWN;
		color = Color.lightGray;
	}
	
	void set( int x, int y, GameLogic.Dir d ) {
		pos.x = x;
		pos.y = y;
		dir = d;
	}
	
	void setColor(Color c) {
		color = c;
	}
	
	Color getColor() {
		return color;
	}
}
