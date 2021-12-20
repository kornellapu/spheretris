package indicator;
import java.awt.Color;

import basic.Position;
import logic.GameLogic;

/**	A gravitációs indikátor, ami megmutatja milyen irányba fog esni az éppen esõ alakzat
 * 	Egy adott pozícióban helyezkedik el, blokk helyén lehet kirajzolni, és emellett a csúcsnak iránya is van
 * 	Tartalmaz egy színt is amilyen színû lesz az indikátor
 * */
public class TriangleIndicator {
	/** Indikátor pozíciója, ahová ki lesz rajzolva */
	private Position pos;
	/** A gravitáció iránya */
	private GameLogic.Dir dir;
	/** A színe az indikátornak */
	Color color;
	
	/**	Konstruktor, ami beállítja a pozíciót, irányt ismeretlenre és a színt szürkére
	 * */
	public TriangleIndicator() {
		setPos(new Position());
		setDir(GameLogic.Dir.UNKNOWN);
		color = Color.gray;
	}
	
	/** Frissíti (beállítja) az indikátor pozícióját és irányát 
	 * @param x X koordináta
	 * @param y Y koordináta
	 * @param d Gravitáció iránya*/
	public void set( int x, int y, GameLogic.Dir d ) {
		getPos().x = x;
		getPos().y = y;
		setDir(d);
	}
	
	/** Beállítja a színét 
	 * 	@param c A beállítani kívánt szín*/
	void setColor(Color c) {
		color = c;
	}
	
	/** Visszaadja a színét 
	 * 	@return Az indikátor színe*/
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
