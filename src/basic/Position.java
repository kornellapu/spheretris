package basic;

import logic.GameLogic;

/** Két egész értéket tárol mint pozíció az x és y koordinátában
 * 	A koordinátákon végzett mûveletek is itt találhatóak
 */
public class Position {
	/**	X kkordináta
	 * */
	public int x;
	/**	Y koordináta
	 * */
	public int y;
	
	/**	Alap Konstruktor nullára állítja az x-t és y-t
	 * */
	public Position(){
		this(0,0);
	}
	
	/**	Konstruktor két paraméterben kapott értékekere állítja be a pozíció koordinátákat
	 * 	@param px X koordináta
	 * 	@param py Y koordináta
	 * */
	public Position(int px, int py){
		x = px;
		y = py;
	}
	
	/**	Konstruktor a paraméterként kapott pozicó szerint állítja be a saját koordinátáját
	 * 	@param pp A lemásolandó pozíció
	 * */
	public Position(Position pp) {
		this(pp.x, pp.y);
	}
	
	/**	Összeadja a pozíciót a paraméterben kapott pozícióval
	 * 	@param ppos A pozíció amit hozzáad önmagához
	 * 
	 * 	@return Egy teljesen új pozíciót hoz létre és adja vissza
	 * */
	public Position add(Position ppos) {
		return new Position(x + ppos.x, y + ppos.y);
	}
	
	/**	Megállapítja, hogy a paraméterben kapott pozícitól mennyi koncentrikus négyzetnyi távolságra van ez a koordináta
	 * 	
	 * 	@param ppos A kapott pozíció amitõl a távolságot nézi
	 * 	
	 * 	@return A távolság a másik pozíciótól
	 * */
	public int getLoopDistanceFrom(Position ppos) {
		int dx = Math.abs(ppos.x-x);
		int dy = Math.abs(ppos.y-y);
		return ( dx > dy ? dx : dy );
	}
	
	/**	A pozíciót normalizálja
	 * 	Úgy, hogy a a hosszabb komponenst állítja 1 hosszúságúra, a másik koordinátát pedig 1-nél kisebb értékre
	 * 	Megõrzi az elõjelet
	 * 	Majd kerekíti külön-külön az x és í koordinátákat egészre, ami így -1, 0 vagy 1 lehet
	 * 	Az új eredményt ebben a pozícióban tárolja el
	 * */
	public void eightDirNormalize() {
		//Normál vektor készítése
		double length = Math.sqrt(x*x + y*y);
		
		//Ha 0 a két koordináta, akkor nem lehet normalizálni és a (0,0) megfelelõ
		if(length == 0) {
			return;
		}
		
		double dx = x/length;
		double dy = y/length;
		
		//A nagyobb oldalhosszúságot növelni egy szorzással, hogy 1 legyen
		double ratioToGrow;
		if(Math.abs(dx) > Math.abs(dy)) {
			ratioToGrow = 1/dx;
		}
		else {
			ratioToGrow = 1/dy;
		}
		//Ha negatív akkor ne fordítsa meg az irányt
		if(ratioToGrow < 0)
			ratioToGrow *= -1;
		//Majd oldalakat is megszorozni vele
		dx *= ratioToGrow;
		dy *= ratioToGrow;
		//A oldalakat kerekíteni vagy 1-re, -1-re vagy 0-ra
		x = (int) Math.round(dx);
		y = (int) Math.round(dy);
	}
	
	/**	Stringgé alakítása a pozíciónak, az egyszerû kiiratáshoz
	 * 
	 * 	@return Az objektum szövegként
	 * */
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	/**	Egyenlõség ellenõrzése, külön-külön a koordinátákra
	 * 	
	 * 	@param ppos Az másik pozíció amivel összehasonlítja
	 * 
	 * 	@return Logikai érték, ha x és y páronként egyenlõ egyenló
	 * */
	public boolean equals(Position ppos) {
		return ppos.x==x && ppos.y==y;
	}
	
	/**	Visszaad egy GameLogic osztályban található Dir enum-ból egy értéket, ami az iránynak felel meg amilyen irányba mutat
	 * 	Csak normalizált pozícióra használható!
	 * 
	 * 	@return Egy irányt ad vissza a nyolc közül
	 * */
	public GameLogic.Dir transformToDirection(){
		// csak normalizált position-re
		if ((x == -1 || x == 0 || x == 1) && (y == -1 || y == 0 || y == 1)) {

			switch (x) {
			// Jobbra
			case 1:
				switch (y) {
				// Le
				case 1:
					return GameLogic.Dir.RIGHT_DOWN;
				// Nincs függõleges
				case 0:
					return GameLogic.Dir.RIGHT;
				// Fel
				case -1:
					return GameLogic.Dir.RIGHT_UP;
				}
				// Nincs vízszintes mozgás
			case 0:
				switch (y) {
				// Le
				case 1:
					return GameLogic.Dir.DOWN;
				// Nincs függõleges
				case 0:
					return GameLogic.Dir.NO_DIR;
				// Fel
				case -1:
					return GameLogic.Dir.UP;
				}
				// Balra
			case -1:
				switch (y) {
				// Le
				case 1:
					return GameLogic.Dir.LEFT_DOWN;
				// Nincs függõleges
				case 0:
					return GameLogic.Dir.LEFT;
				// Fel
				case -1:
					return GameLogic.Dir.LEFT_UP;
				}
			}
		}
		
		return GameLogic.Dir.UNKNOWN;
	}
	
}
