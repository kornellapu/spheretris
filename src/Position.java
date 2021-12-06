/** K�t eg�sz �rt�ket t�rol mint poz�ci� az x �s y koordin�t�ban	
 */
public class Position {
	public int x;
	public int y;
	
	public Position(){
		this(0,0);
	}
	
	public Position(int px, int py){
		x = px;
		y = py;
	}
	
	public Position(Position pp) {
		this(pp.x, pp.y);
	}
	
	public Position add(Position ppos) {
		return new Position(x + ppos.x, y + ppos.y);
	}
	
	int getLoopDistanceFrom(Position ppos) {
		int dx = Math.abs(ppos.x-x);
		int dy = Math.abs(ppos.y-y);
		return ( dx > dy ? dx : dy );
	}
	
	public void eightDirNormalize() {
		//Norm�l vektor k�sz�t�se
		double length = Math.sqrt(x*x + y*y);
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
		//A oldalakat kerek�teni vagy 1-re vagy 0-ra
		x = (int) Math.round(dx);
		y = (int) Math.round(dy);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	boolean equals(Position ppos) {
		return ppos.x==x && ppos.y==y;
	}
	
	GameLogic.Dir transformToDirection(){
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
