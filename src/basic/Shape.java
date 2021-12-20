package basic;
import java.awt.Color;

/** Az alakzat ami blokkokból áll, színe van és pozícója.
 * 	Fõként a mozgatható/zuhanó alakzat leírásait tartalmazza
 * 	Emellet az ezken az alakzatokon végzett forgatásokat is ez az osztály végzi
 * */
public class Shape {
	
	/** Összesen hét féle alap alakzat található, ezeket a betûk reprezentálják*/
	public static enum Type {O, L, J, Z, S, T, I}
	
	/** Az alakzat aktuális pozíciója, annak is a bal felsõ csücske */
	private Position pos;
	/** Az alakzat színe, amivel a blokkjai ki lesznek rajzolva */
	Color color;
	/** Az alakzat típusa, fõkent az alakját mondja meg */
	Type type;
	
	/** A blokkokból álló mátrix, amiben az alakzat blokkjait leíró forma található */
	Block[][] blocks;
	/** Az alakzat teljes szélessége, nem feltétlenül kitöltött minden esetben */
	private int width;
	/** Az alakzat teljes magassága, nem feltétlenül kitöltött minden esetben */
	private int height;
	
	/** Konstruktor ami beállítja a pozíciót, színt, típust és a blokkok mátrixát a típus szerint 
	 * 	@param type Az alakzat típusa*/
	public Shape(Type type){
		setPos(new Position());
		color = null;
		this.type = type;
		initBlocks(type);
	}
	
	/** Másoló konstruktor, ami a paraméterben adott alakzat minájára beállítja a saját értékeit 
	 * 	
	 * @param shape Ami alapján beállítja az értékeket
	 * */
	public Shape(Shape shape){
		setPos(new Position(shape.getPos()));
		color = null;
		type = shape.type;
		setWidth(shape.getWidth());
		setHeight(shape.getHeight());
		blocks = new Block[getWidth()][getHeight()];
		for(int i=0; i < getWidth(); i++) {
			for(int j = 0; j < getHeight(); j++) {
				blocks[i][j] = new Block(false);
				if(shape.blocks[i][j].full)
					blocks[i][j].full = true;
			}
		}
	}
	
	/** Az alakzat típusának gettere 
	 * 	@return Az alakzat típusa*/
	public Type getType() {
		return type;
	}
	
	/** Az alakzat pozíciójának beállítása, bal felsõ blokkjának a pozíciója 
	 * 	@param ppos A pozíció amire állítani kell*/
	public void setPosition(Position ppos) {
		getPos().x = ppos.x;
		getPos().y = ppos.y;
	}
	
	/** Az alakzat pozíciójának gettere, bal felsõ blokkjának a pozíciója 
	 * 	@return Az alakzat bal felsõ sarkának pozícója*/
	public Position getPosition() {
		return getPos();
	}
	
	/** A blokk mátrix gettere 
	 * 	@return Alakzat blokk mátrixa*/
	public Block[][] getBlocks(){
		return blocks;
	}
	
	/** Visszaadja, hogy a blokk mátrixban az adott x és y helyen teli vagy üres blokk áll
	 * 	
	 * 	@param x X koordináta az alakzat blokk mátrixában
	 * 	@param y Y koordináta az alakzat blokk mátrixában
	 * 
	 * 	@return Logikai érték, hogy az alakzatnak a blokk mátrixában az teli vagy sem */
	public boolean getFullAt(int x, int y) {
		return blocks[x][y].full;
	}
	
	/** Az alakzat blokk mátrixának szélessége 
	 * 	@return Alakzat blokkmátrixának szélessége*/
	public int getBlocksWidth() {
		return getWidth();
	}
	
	/** Az alakzat blokk mátrixának magassága
	 * @return Alakzat blokkmátrixának magassága */
	public int getBlocksHeight() {
		return getHeight();
	}
	
	/** Megnézi, hogy az adott pozícióban van-e az alakzatnak teli blokkja vagy sem
	 * 	
	 * 	@param ppos Pozíció ellenõrzésre
	 * 
	 * 	@return Ha nincs benne az alakzatban a pozíció akkor hamissal tér vissza. Egyébként ha talál a pozícióban teli blokkot igazzal, ha nem talál akkor hamissal */
	public boolean isPresentAt(Position ppos) {
		if(getPos().x <= ppos.x && ppos.x < getPos().x+getWidth() && getPos().y <= ppos.y && ppos.y < getPos().y+getHeight()) {
			//Ide kell hogy azt adja vissza, hogy van-e ott blokk vagy sem ha üres akkor false, ha teli akkor true
			return blocks[ppos.x-getPos().x][ppos.y-getPos().y].full;
		}
		return false;
	}
	
	/** Az egyszerû kiiratáshoz, kiírja az alakzat típusát ha Stringé alakítják 
	 * 	@return Az objektum szöveggé alakítva*/
	public String toString() {
		return "" + type.name();
	}
	
	/**	Ez egy leírót tartalmazó függvény, ami megmondja, hogy a 8 kezdõhelyen az egyes alakzatok pontosan hol jelenjenek meg elõször
	 * 	Minden alakzatra tartalmaz 8 eltolást x és y koordinátáknál, ezek alapján lesz véglegesen elhelyzve a pályán az alakzat bal felsõ sarka
	 * 
	 * 	@param spawnPoint Egy a nyolc pont közül ahol spawnolni fog
	 * 
	 * 	@return Az alakzatra és az adott spawn pointra vonatkozó eltolás vektort adja vissza
	 * */
	public Position getSpawnOffset(int spawnPoint){
		Position o = new Position();
		switch(type) {
		case O:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -2;
				break;
			case 1:
				o.x = 1;
				o.y = -2;
				break;
			case 2:
				o.x = 1;
				o.y = -1;
				break;
			case 3:
				o.x = -1;
				o.y = -1;
				break;
			case 4:
				o.y = 1;
				break;
			case 5:
				o.x = -2;
				o.y = 1;
				break;
			case 6:
				o.x = -2;
				break;
			case 7:
				o.x = -2;
				o.y = -2;
				break;
			}
			break;
		case Z:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -3;
				break;
			case 1:
				o.y = -2;
				break;
			case 2:
				o.x = 1;
				o.y = -2;
				break;
			case 3:
				o.x = 1;
				break;
			case 4:
				o.x = -1;
				break;
			case 5:
				o.x = -2;
				o.y = -1;
				break;
			case 6:
				o.x = -3;
				o.y = -1;
				break;
			case 7:
				o.x = -3;
				o.y = -3;
				break;
			}
			break;
		case S:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -3;
				break;
			case 1:
				o.x = 1;
				o.y = -3;
				break;
			case 2:
				o.x = 1;
				o.y = -1;
				break;
			case 3:
				o.y = -1;
				break;
			case 4:
				o.x = -1;
				break;
			case 5:
				o.x = -3;
				break;
			case 6:
				o.x = -3;
				o.y = -2;
				break;
			case 7:
				o.x = -2;
				o.y = -2;
				break;
			}
			break;
		case L:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -3;
				break;
			case 1:
				o.y = -3;
				break;
			case 2:
				o.y = -1;
				break;
			case 3:
				o.y = 1;
				break;
			case 4:
				o.x = -1;
				o.y = 1;
				break;
			case 5:
				o.x = -2;
				o.y = -1;
				break;
			case 6:
				o.x = -3;
				o.y = -1;
				break;
			case 7:
				o.x = -3;
				o.y = -3;
				break;
			}
			break;
		case J:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -3;
				break;
			case 1:
				o.x = 1;
				o.y = -3;
				break;
			case 2:
				o.x = 1;
				o.y = -1;
				break;
			case 3:
				o.y = -1;
				break;
			case 4:
				o.x = -1;
				o.y = 1;
				break;
			case 5:
				o.x = -2;
				o.y = 1;
				break;
			case 6:
				o.x = -2;
				o.y = -1;
				break;
			case 7:
				o.x = -2;
				o.y = -3;
				break;
			}
			break;
		case T:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -2;
				break;
			case 1:
				o.x = 1;
				o.y = -2;
				break;
			case 2:
				o.x = 1;
				o.y = -1;
				break;
			case 3:
				break;
			case 4:
				o.x = -1;
				o.y = 1;
				break;
			case 5:
				o.x = -2;
				break;
			case 6:
				o.x = -3;
				o.y = -1;
				break;
			case 7:
				o.x = -3;
				o.y = -2;
				break;
			}
			break;
		case I:
			switch(spawnPoint) {
			case 0:
				o.x = -2;
				o.y = -4;
				break;
			case 1:
				o.x = -1;
				o.y = -4;
				break;
			case 2:
				o.x = -1;
				o.y = -2;
				break;
			case 3:
				o.x = -1;
				o.y = 1;
				break;
			case 4:
				o.x = -2;
				o.y = 1;
				break;
			case 5:
				o.x = -3;
				o.y = 1;
				break;
			case 6:
				o.x = -3;
				o.y = -1;
				break;
			case 7:
				o.x = -3;
				o.y = -4;
				break;
			}
			break;
		}
		
		return o;
	}
	
	/** Az alakzatok forgatásának leírása
	 * 	A blokkmátrix elemeit úgy helyezi át mintha a forgatás megtörtént volna
	 * 	Minden egyes típusra és minden szükséges teli blokkra megtörténik a fogatás */
	public void rotate() {
		Block temp;
		switch(type) {
		case L:/*
			temp = blocks[0][0];
			blocks[0][0] = blocks[2][0];
			blocks[2][0] = blocks[2][2];
			blocks[2][2] = blocks[0][2];
			blocks[0][2] = temp;
			temp = blocks[1][0];
			blocks[1][0] = blocks[2][1];
			blocks[2][1] = blocks[1][2];
			blocks[1][2] = blocks[0][1];
			blocks[0][1] = temp;
			break;
			*/
		case J:
		case T:
			temp = blocks[0][0];
			blocks[0][0] = blocks[0][2];
			blocks[0][2] = blocks[2][2];
			blocks[2][2] = blocks[2][0];
			blocks[2][0] = temp;
			temp = blocks[1][0];
			blocks[1][0] = blocks[0][1];
			blocks[0][1] = blocks[1][2];
			blocks[1][2] = blocks[2][1];
			blocks[2][1] = temp;
			break;
		case S:
			temp = blocks[1][0];
			blocks[1][0] = blocks[1][2];
			blocks[1][2] = temp;
			temp = blocks[2][2];
			blocks[2][2] = blocks[0][2];
			blocks[0][2] = temp;
			break;
		case Z:
			temp = blocks[2][1];
			blocks[2][1] = blocks[0][1];
			blocks[0][1] = temp;
			temp = blocks[2][0];
			blocks[2][0] = blocks[2][2];
			blocks[2][2] = temp;
			break;
		case I:
			temp = blocks[2][0];
			blocks[2][0] = blocks[3][1];
			blocks[3][1] = temp;
			temp = blocks[1][1];
			blocks[1][1] = blocks[2][2];
			blocks[2][2] = temp;
			temp = blocks[0][1];
			blocks[0][1] = blocks[2][3];
			blocks[2][3] = temp;
			break;
		default:
		}
	}
	
	/** Az alakzatok kezdeti létrehozásának leírója 
	 * 	Minden alakzatnak van egy ilyen leírása
	 * 	Minden blokkmátrix NxN-es vagyis négyzet alakú, hogy késõbb könnyeb legyen forgatni az elemeket
	 * 	Vagy az alakzat színével tölti ki vagy üres blokkot hoz létre a blokkmátrixban
	 * 
	 * 	@param shape Az alakzat típusa amit létre akarunk hozni
	 * */
	private void initBlocks(Type shape) {
		switch(shape) {
		case O:
			setWidth(2);
			setHeight(2);
			color = new Color(255, 255, 0);		//yellow
			blocks = new Block[getWidth()][getHeight()];
			blocks[0][0] = new Block(color);
			blocks[0][1] = new Block(color);
			blocks[1][0] = new Block(color);
			blocks[1][1] = new Block(color);
			break;
		case L:
			setWidth(3);
			setHeight(3);
			color = new Color(255, 120, 0);		//orange
			blocks = new Block[getWidth()][getHeight()];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(false);
			blocks[0][2] = new Block(false);
			blocks[1][0] = new Block(color);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(color);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(false);
			blocks[2][2] = new Block(color);
			break;
		case J:
			setWidth(3);
			setHeight(3);
			color = new Color(0, 0, 205);		//blue
			blocks = new Block[getWidth()][getHeight()];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(false);
			blocks[0][2] = new Block(color);
			blocks[1][0] = new Block(color);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(color);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(false);
			blocks[2][2] = new Block(false);
			break;
		case Z:
			setWidth(3);
			setHeight(3);
			color = new Color(255, 0, 0);		//red
			blocks = new Block[getWidth()][getHeight()];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(color);
			blocks[0][2] = new Block(false);
			blocks[1][0] = new Block(false);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(color);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(false);
			blocks[2][2] = new Block(color);
			break;
		case S:
			setWidth(3);
			setHeight(3);
			color = new Color(0, 255, 0);		//green
			blocks = new Block[getWidth()][getHeight()];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(false);
			blocks[0][2] = new Block(color);
			blocks[1][0] = new Block(false);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(color);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(color);
			blocks[2][2] = new Block(false);
			break;
		case T:
			setWidth(3);
			setHeight(3);
			color = new Color(205, 0, 205);		//magenta
			blocks = new Block[getWidth()][getHeight()];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(color);
			blocks[0][2] = new Block(false);
			blocks[1][0] = new Block(color);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(false);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(color);
			blocks[2][2] = new Block(false);
			break;
		case I:
			setWidth(4);
			setHeight(4);
			color = new Color(0, 255, 255);		//cyan
			blocks = new Block[getWidth()][getHeight()];
			for(int i=0; i < 4; i++) {
				for(int j=0; j<4; j++) {
					blocks[i][j] = new Block(false);
				}
			}
			blocks[2][0] = new Block(color);
			blocks[2][1] = new Block(color);
			blocks[2][2] = new Block(color);
			blocks[2][3] = new Block(color);
			break;
		default:
			//TODO ShapeNotFoundException
			break;
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}
}
