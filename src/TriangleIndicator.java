import java.awt.Color;

/**	A gravit�ci�s indik�tor, ami megmutatja milyen ir�nyba fog esni az �ppen es� alakzat
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
