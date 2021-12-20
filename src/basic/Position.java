package basic;

import logic.GameLogic;

/** K�t eg�sz �rt�ket t�rol mint poz�ci� az x �s y koordin�t�ban
 * 	A koordin�t�kon v�gzett m�veletek is itt tal�lhat�ak
 */
public class Position {
	/**	X kkordin�ta
	 * */
	public int x;
	/**	Y koordin�ta
	 * */
	public int y;
	
	/**	Alap Konstruktor null�ra �ll�tja az x-t �s y-t
	 * */
	public Position(){
		this(0,0);
	}
	
	/**	Konstruktor k�t param�terben kapott �rt�kekere �ll�tja be a poz�ci� koordin�t�kat
	 * 	@param px X koordin�ta
	 * 	@param py Y koordin�ta
	 * */
	public Position(int px, int py){
		x = px;
		y = py;
	}
	
	/**	Konstruktor a param�terk�nt kapott pozic� szerint �ll�tja be a saj�t koordin�t�j�t
	 * 	@param pp A lem�soland� poz�ci�
	 * */
	public Position(Position pp) {
		this(pp.x, pp.y);
	}
	
	/**	�sszeadja a poz�ci�t a param�terben kapott poz�ci�val
	 * 	@param ppos A poz�ci� amit hozz�ad �nmag�hoz
	 * 
	 * 	@return Egy teljesen �j poz�ci�t hoz l�tre �s adja vissza
	 * */
	public Position add(Position ppos) {
		return new Position(x + ppos.x, y + ppos.y);
	}
	
	/**	Meg�llap�tja, hogy a param�terben kapott poz�cit�l mennyi koncentrikus n�gyzetnyi t�vols�gra van ez a koordin�ta
	 * 	
	 * 	@param ppos A kapott poz�ci� amit�l a t�vols�got n�zi
	 * 	
	 * 	@return A t�vols�g a m�sik poz�ci�t�l
	 * */
	public int getLoopDistanceFrom(Position ppos) {
		int dx = Math.abs(ppos.x-x);
		int dy = Math.abs(ppos.y-y);
		return ( dx > dy ? dx : dy );
	}
	
	/**	A poz�ci�t normaliz�lja
	 * 	�gy, hogy a a hosszabb komponenst �ll�tja 1 hossz�s�g�ra, a m�sik koordin�t�t pedig 1-n�l kisebb �rt�kre
	 * 	Meg�rzi az el�jelet
	 * 	Majd kerek�ti k�l�n-k�l�n az x �s � koordin�t�kat eg�szre, ami �gy -1, 0 vagy 1 lehet
	 * 	Az �j eredm�nyt ebben a poz�ci�ban t�rolja el
	 * */
	public void eightDirNormalize() {
		//Norm�l vektor k�sz�t�se
		double length = Math.sqrt(x*x + y*y);
		
		//Ha 0 a k�t koordin�ta, akkor nem lehet normaliz�lni �s a (0,0) megfelel�
		if(length == 0) {
			return;
		}
		
		double dx = x/length;
		double dy = y/length;
		
		//A nagyobb oldalhossz�s�got n�velni egy szorz�ssal, hogy 1 legyen
		double ratioToGrow;
		if(Math.abs(dx) > Math.abs(dy)) {
			ratioToGrow = 1/dx;
		}
		else {
			ratioToGrow = 1/dy;
		}
		//Ha negat�v akkor ne ford�tsa meg az ir�nyt
		if(ratioToGrow < 0)
			ratioToGrow *= -1;
		//Majd oldalakat is megszorozni vele
		dx *= ratioToGrow;
		dy *= ratioToGrow;
		//A oldalakat kerek�teni vagy 1-re, -1-re vagy 0-ra
		x = (int) Math.round(dx);
		y = (int) Math.round(dy);
	}
	
	/**	Stringg� alak�t�sa a poz�ci�nak, az egyszer� kiirat�shoz
	 * 
	 * 	@return Az objektum sz�vegk�nt
	 * */
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	/**	Egyenl�s�g ellen�rz�se, k�l�n-k�l�n a koordin�t�kra
	 * 	
	 * 	@param ppos Az m�sik poz�ci� amivel �sszehasonl�tja
	 * 
	 * 	@return Logikai �rt�k, ha x �s y p�ronk�nt egyenl� egyenl�
	 * */
	public boolean equals(Position ppos) {
		return ppos.x==x && ppos.y==y;
	}
	
	/**	Visszaad egy GameLogic oszt�lyban tal�lhat� Dir enum-b�l egy �rt�ket, ami az ir�nynak felel meg amilyen ir�nyba mutat
	 * 	Csak normaliz�lt poz�ci�ra haszn�lhat�!
	 * 
	 * 	@return Egy ir�nyt ad vissza a nyolc k�z�l
	 * */
	public GameLogic.Dir transformToDirection(){
		// csak normaliz�lt position-re
		if ((x == -1 || x == 0 || x == 1) && (y == -1 || y == 0 || y == 1)) {

			switch (x) {
			// Jobbra
			case 1:
				switch (y) {
				// Le
				case 1:
					return GameLogic.Dir.RIGHT_DOWN;
				// Nincs f�gg�leges
				case 0:
					return GameLogic.Dir.RIGHT;
				// Fel
				case -1:
					return GameLogic.Dir.RIGHT_UP;
				}
				// Nincs v�zszintes mozg�s
			case 0:
				switch (y) {
				// Le
				case 1:
					return GameLogic.Dir.DOWN;
				// Nincs f�gg�leges
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
				// Nincs f�gg�leges
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
