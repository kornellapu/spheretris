package basic;
import java.awt.Color;

/** Az alakzat ami blokkokb�l �ll, sz�ne van �s poz�c�ja.
 * 	F�k�nt a mozgathat�/zuhan� alakzat le�r�sait tartalmazza
 * 	Emellet az ezken az alakzatokon v�gzett forgat�sokat is ez az oszt�ly v�gzi
 * */
public class Shape {
	
	/** �sszesen h�t f�le alap alakzat tal�lhat�, ezeket a bet�k reprezent�lj�k*/
	public static enum Type {O, L, J, Z, S, T, I}
	
	/** Az alakzat aktu�lis poz�ci�ja, annak is a bal fels� cs�cske */
	private Position pos;
	/** Az alakzat sz�ne, amivel a blokkjai ki lesznek rajzolva */
	Color color;
	/** Az alakzat t�pusa, f�kent az alakj�t mondja meg */
	Type type;
	
	/** A blokkokb�l �ll� m�trix, amiben az alakzat blokkjait le�r� forma tal�lhat� */
	Block[][] blocks;
	/** Az alakzat teljes sz�less�ge, nem felt�tlen�l kit�lt�tt minden esetben */
	private int width;
	/** Az alakzat teljes magass�ga, nem felt�tlen�l kit�lt�tt minden esetben */
	private int height;
	
	/** Konstruktor ami be�ll�tja a poz�ci�t, sz�nt, t�pust �s a blokkok m�trix�t a t�pus szerint 
	 * 	@param type Az alakzat t�pusa*/
	public Shape(Type type){
		setPos(new Position());
		color = null;
		this.type = type;
		initBlocks(type);
	}
	
	/** M�sol� konstruktor, ami a param�terben adott alakzat min�j�ra be�ll�tja a saj�t �rt�keit 
	 * 	
	 * @param shape Ami alapj�n be�ll�tja az �rt�keket
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
	
	/** Az alakzat t�pus�nak gettere 
	 * 	@return Az alakzat t�pusa*/
	public Type getType() {
		return type;
	}
	
	/** Az alakzat poz�ci�j�nak be�ll�t�sa, bal fels� blokkj�nak a poz�ci�ja 
	 * 	@param ppos A poz�ci� amire �ll�tani kell*/
	public void setPosition(Position ppos) {
		getPos().x = ppos.x;
		getPos().y = ppos.y;
	}
	
	/** Az alakzat poz�ci�j�nak gettere, bal fels� blokkj�nak a poz�ci�ja 
	 * 	@return Az alakzat bal fels� sark�nak poz�c�ja*/
	public Position getPosition() {
		return getPos();
	}
	
	/** A blokk m�trix gettere 
	 * 	@return Alakzat blokk m�trixa*/
	public Block[][] getBlocks(){
		return blocks;
	}
	
	/** Visszaadja, hogy a blokk m�trixban az adott x �s y helyen teli vagy �res blokk �ll
	 * 	
	 * 	@param x X koordin�ta az alakzat blokk m�trix�ban
	 * 	@param y Y koordin�ta az alakzat blokk m�trix�ban
	 * 
	 * 	@return Logikai �rt�k, hogy az alakzatnak a blokk m�trix�ban az teli vagy sem */
	public boolean getFullAt(int x, int y) {
		return blocks[x][y].full;
	}
	
	/** Az alakzat blokk m�trix�nak sz�less�ge 
	 * 	@return Alakzat blokkm�trix�nak sz�less�ge*/
	public int getBlocksWidth() {
		return getWidth();
	}
	
	/** Az alakzat blokk m�trix�nak magass�ga
	 * @return Alakzat blokkm�trix�nak magass�ga */
	public int getBlocksHeight() {
		return getHeight();
	}
	
	/** Megn�zi, hogy az adott poz�ci�ban van-e az alakzatnak teli blokkja vagy sem
	 * 	
	 * 	@param ppos Poz�ci� ellen�rz�sre
	 * 
	 * 	@return Ha nincs benne az alakzatban a poz�ci� akkor hamissal t�r vissza. Egy�bk�nt ha tal�l a poz�ci�ban teli blokkot igazzal, ha nem tal�l akkor hamissal */
	public boolean isPresentAt(Position ppos) {
		if(getPos().x <= ppos.x && ppos.x < getPos().x+getWidth() && getPos().y <= ppos.y && ppos.y < getPos().y+getHeight()) {
			//Ide kell hogy azt adja vissza, hogy van-e ott blokk vagy sem ha �res akkor false, ha teli akkor true
			return blocks[ppos.x-getPos().x][ppos.y-getPos().y].full;
		}
		return false;
	}
	
	/** Az egyszer� kiirat�shoz, ki�rja az alakzat t�pus�t ha String� alak�tj�k 
	 * 	@return Az objektum sz�vegg� alak�tva*/
	public String toString() {
		return "" + type.name();
	}
	
	/**	Ez egy le�r�t tartalmaz� f�ggv�ny, ami megmondja, hogy a 8 kezd�helyen az egyes alakzatok pontosan hol jelenjenek meg el�sz�r
	 * 	Minden alakzatra tartalmaz 8 eltol�st x �s y koordin�t�kn�l, ezek alapj�n lesz v�glegesen elhelyzve a p�ly�n az alakzat bal fels� sarka
	 * 
	 * 	@param spawnPoint Egy a nyolc pont k�z�l ahol spawnolni fog
	 * 
	 * 	@return Az alakzatra �s az adott spawn pointra vonatkoz� eltol�s vektort adja vissza
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
	
	/** Az alakzatok forgat�s�nak le�r�sa
	 * 	A blokkm�trix elemeit �gy helyezi �t mintha a forgat�s megt�rt�nt volna
	 * 	Minden egyes t�pusra �s minden sz�ks�ges teli blokkra megt�rt�nik a fogat�s */
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
	
	/** Az alakzatok kezdeti l�trehoz�s�nak le�r�ja 
	 * 	Minden alakzatnak van egy ilyen le�r�sa
	 * 	Minden blokkm�trix NxN-es vagyis n�gyzet alak�, hogy k�s�bb k�nnyeb legyen forgatni az elemeket
	 * 	Vagy az alakzat sz�n�vel t�lti ki vagy �res blokkot hoz l�tre a blokkm�trixban
	 * 
	 * 	@param shape Az alakzat t�pusa amit l�tre akarunk hozni
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
