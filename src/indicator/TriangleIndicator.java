package indicator;
import java.awt.Color;

import basic.Position;
import logic.GameLogic;

/**	A gravit�ci�s indik�tor, ami megmutatja milyen ir�nyba fog esni az �ppen es� alakzat
 * 	Egy adott poz�ci�ban helyezkedik el, blokk hely�n lehet kirajzolni, �s emellett a cs�csnak ir�nya is van
 * 	Tartalmaz egy sz�nt is amilyen sz�n� lesz az indik�tor
 * */
public class TriangleIndicator {
	/** Indik�tor poz�ci�ja, ahov� ki lesz rajzolva */
	private Position pos;
	/** A gravit�ci� ir�nya */
	private GameLogic.Dir dir;
	/** A sz�ne az indik�tornak */
	Color color;
	
	/**	Konstruktor, ami be�ll�tja a poz�ci�t, ir�nyt ismeretlenre �s a sz�nt sz�rk�re
	 * */
	public TriangleIndicator() {
		setPos(new Position());
		setDir(GameLogic.Dir.UNKNOWN);
		color = Color.gray;
	}
	
	/** Friss�ti (be�ll�tja) az indik�tor poz�ci�j�t �s ir�ny�t 
	 * @param x X koordin�ta
	 * @param y Y koordin�ta
	 * @param d Gravit�ci� ir�nya*/
	public void set( int x, int y, GameLogic.Dir d ) {
		getPos().x = x;
		getPos().y = y;
		setDir(d);
	}
	
	/** Be�ll�tja a sz�n�t 
	 * 	@param c A be�ll�tani k�v�nt sz�n*/
	void setColor(Color c) {
		color = c;
	}
	
	/** Visszaadja a sz�n�t 
	 * 	@return Az indik�tor sz�ne*/
	public Color getColor() {
		return color;
	}

	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}

	public GameLogic.Dir getDir() {
		return dir;
	}

	public void setDir(GameLogic.Dir dir) {
		this.dir = dir;
	}
}
