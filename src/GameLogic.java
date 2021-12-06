import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.awt.Color;

/**	Tartalmazza a j�t�k logik�j�t. A j�t�kmez�vel kapcsolatos minden adat itt t�rol�dik �s a hozz�juk tartoz� f�ggv�nyek
 * 	Kezeli a lees� blokkokat �s mozgatja. Ellen�rzi a j�t�k v�g�t �s kezeli �s pontozza a teljesen kit�lt�tt teljes gy�r�ket a k�z�ppontt�l.
 * */
public class GameLogic {
	/** Az es�si ir�nyok meghat�roz�s�ra, �s hogy melyik ir�nyba lehet mozgatni az alakzatot.
	 */
	public static enum Dir {LEFT, RIGHT, UP, DOWN, LEFT_UP, RIGHT_UP, LEFT_DOWN, RIGHT_DOWN, NO_DIR, UNKNOWN }
	
	/**	Az alap alakzatokat tartalmaz� lista, amib�l tud v�letleszer�en sorsolni
	 * */
	ArrayList<Shape> basicShapes;
	/**	A teljes j�t�kmez�, ami tartalmazza a l�that� �s nem l�that� mez�ket is
	 * @see Block
	 * */
	Block[][] gameField;
	/**	A l�that� j�t�kt�r sz�less�ge
	 * */
	public final int gameWidth  = 41;
	/**	A l�that� j�t�kt�r magass�ga
	 * */
	public final int gameHeight = 41;
	/**	A nem l�that� mez�k a j�t�kt�r k�r�l, amik a j�t�k v�ge ellen�rz�s�t szolg�lj�k. Az �j alakzatok ide ker�lnek ebbe a r�szbe. A magass�ga aut�matikusan meghat�rozott
	 * */
	static int gameOffScreenBorder;
	/**	A gravit�ci�s pont, koordin�ta amihez k�zeledik az zuhan� alakzat
	 * @see Position
	 * */
	Position gravityPoint;
	/**	A zuhan� alakzat	
	 * */
	Shape fallingShape;
	/**	Az �j keletkez�s helye, 0 k�z�pen fent, 1 jobbra fent, stb... �sszesen 8 ir�nyb�l [0-7]
	 * */
	int shapeSpawnedAt;
	/**	A j�t�k v�g�t jelz� �rt�k	
	 * */
	boolean gameOver;
	/**	A debuggol�sn�l az id�z�t� miatt ker�lt bele, hogy ne l�pjen tov�bb ha a program a breakpointban �ll	
	 * */
	boolean stepReady;
	/**	A j�t�k sor�n el�rt pont
	 * */
	int score;
	/**	A j�t�k v�g�n el�rt pont, ez a j�t�k v�g�n friss�l csak
	 * */
	int gameOverScore;
	/**	A kezdeti l�ptet�s �rt�ke
	 * */
	int initialMSBetweenSteps = 1000;
	/**	Az egyes es�sek k�z�tti miliszekundumok sz�ma
	 * */
	int millisBetweenSteps;
	/**	A lerakott alakzatot veszi k�r�l, hogy l�that� legyen
	 * */
	PlacementIndicator shapeIndicator;
	/**	A gravit�ci�s ir�ny mutat�sa
	 * */
	TriangleIndicator gravityIndicator;
	
	/** A l�ptet�sek k�sleltet�s�nek gettere
	 * */
	int getStepTime() {
		return millisBetweenSteps;
	}
	
	/**	Az alakzat jel�l�nek a gettere
	 * */
	PlacementIndicator getShapeIndicator() {
		return shapeIndicator;
	}
	
	/**	Konstruktor
	 *  Be�ll�tja �s inicializ�lja �j j�t�kra
	 * 	A sz�ks�ges be�ll�t�sok egy k�l�n initNewGame f�ggv�nyben, hogy k�s�bb is h�vhat� legyen
	 * */
	public GameLogic(){
		initNewGame();
	}
	
	/**	A j�t�k inicializ�l�sa
	 * 	
	 * 	Feladatok amiket elv�gez:
	 * 	<p>
	 * 		Elk�sz�ti az alakzatokat
	 * 		Be�ll�tja a j�t�kt�r nem l�that� sz�l�nek a nagys�g�t
	 * 		A grevit�ci�s k�z�ppontot a p�lya k�zep�re �ll�tja
	 * 		Felt�lti a j�t�kt�r t�bbi blokkj�t �res mez�kkel (inicializ�lja azokat)
	 * 		Feh�rre �ll�tja a k�z�ppontot
	 * 		A lees� alakzatot null-ra �ll�tja
	 * 		A L�ptet�s sebess�g�t vissza�ll�tja a kezdeti �rt�kre
	 * 		Az indik�torokat inicializ�lja
	 * 		A pontot kinull�zza
	 * 		A k�vetkez� "spawn point"-ot be�ll�tja, a fenti pontra
	 * 		A j�t�k v�g�t kit�rli
	 * </p>
	 * */
	synchronized public void initNewGame() {
		createBasicShapes();
		gameOffScreenBorder = getMaxOfShapes();
		gameField = new Block[gameWidth+2*gameOffScreenBorder][gameHeight+2*gameOffScreenBorder];
		gravityPoint = new Position( gameWidth/2+gameOffScreenBorder, gameHeight/2+gameOffScreenBorder );
		
		for(int xi = 0; xi < gameWidth+2*gameOffScreenBorder; xi++) {
			for(int yi = 0; yi < gameHeight+2*gameOffScreenBorder; yi++) {
				gameField[xi][yi] = new Block(false);
			}
		}
		gameField[gravityPoint.x][gravityPoint.y].setColor(Color.white);
		gameField[gravityPoint.x][gravityPoint.y].setFull(true);
		
		fallingShape = null;
		millisBetweenSteps = initialMSBetweenSteps;
		shapeIndicator = new PlacementIndicator(this);
		gravityIndicator = new TriangleIndicator();
		
		score = 0;
		shapeSpawnedAt = 0;
		gameOver = false;
		stepReady = true;
	}
	
	/**	A j�t�mez�re teszi (mozgatja) az alakzatban szerepl� blokkokat (sz�n �s lefedetts�g)
	 * 	Az alakzat hely�t is be�ll�tja
	 * 	A j�t�kmez�t is be�ll�tja
	 * 	Emellett a gravit�ci�s  indik�tort is be�ll�tja
	 * */
	void moveShapeTo(Shape shape, Position pos) {
		if(shape == null)
			return;
		
		//Alakzat be�ll�t�sa
		shape.setPosition(pos);
		//J�t�kmez� be�ll�t�sa
		for(int wi = 0; wi < shape.width; wi++) {
			for(int hi = 0; hi < shape.height; hi++) {
				if(shape.getFullAt(wi, hi)) {
					gameField[pos.x + wi][pos.y + hi].setTo(shape.blocks[wi][hi]);
				}
			}
		}
		
		//System.out.println("Alakzat mozgatva: " + pos);
		
		//Gravit�ci�s indik�tor be�ll�t�sa
		setFallingGravityIndicator(gravityIndicator);
	}
	
	/**	A j�t�kt�r le�r�ja, ami visszaad egy poz�ci�t, ahol az alakzat l�trej�het �gy, hogy k�vetkez� l�p�sre essen be a j�t�kt�rbe.
	 * Figyelembe veszi az alakzat alakj�t is, �gy, hogy az alakzatot a megfelel� offsettel eltolja
	 * Az �sszes "spawn point" le�r�j�t is tartalmazza
	 * @param dir Megadja, hogy a 8 ir�ny k�z�l melyiket szeretn�nk kiv�lasztani (0: fent k�z�pen, 1: jobb fel�l, stb...)
	 * */
	Position getSpawnPosFromEightDir(Shape shape, int dir) {
		Position newPos = new Position();
		
		int LEFT_X = gameOffScreenBorder;
		int MID_X = gameWidth/2 + gameOffScreenBorder;
		int RIGHT_X = gameWidth + gameOffScreenBorder -1;
		
		int TOP_Y = gameOffScreenBorder;
		int MID_Y = gameHeight/2 + gameOffScreenBorder;
		int BOT_Y = gameHeight + gameOffScreenBorder -1;
		
		int spawnPoint = dir%8; 
		//DEBUG POINT - Spawn point fix�l�sa
		//spawnPoint = 4;
		
		//�sszes spawn point le�r�
		switch(spawnPoint){
		default:
		case 0:
			newPos.x = MID_X;
			newPos.y = TOP_Y;
			break;
		case 1:
			newPos.x = RIGHT_X;
			newPos.y = TOP_Y; 
			break;
		case 2:
			newPos.x = RIGHT_X;
			newPos.y = MID_Y;
			break;
		case 3:
			newPos.x = RIGHT_X;
			newPos.y = BOT_Y;
			break;
		case 4:
			newPos.x = MID_X;
			newPos.y = BOT_Y;
			break;
		case 5:
			newPos.x = LEFT_X;
			newPos.y = BOT_Y;
			break;
		case 6:
			newPos.x = LEFT_X;
			newPos.y = MID_Y;
			break;
		case 7:
			newPos.x = LEFT_X;
			newPos.y = TOP_Y;
			break;
		}
		//Az alakzatnak az offsetj�vel eltolja
		newPos.x += shape.getSpawnOffset(spawnPoint).x;
		newPos.y += shape.getSpawnOffset(spawnPoint).y;
		
		return newPos;
	}
	
	/**	�j v�letlenszer� alakzat l�treho�sa �s elhelyez�se a nem l�that� j�t�kt�ren
	 * 	L�trehozza az �j alakzatot v�letlenszer�en az �sszes alap form�b�l
	 * 	A posici�j�t a "spawn point"-ra helyezi �s �gy �ll�tja, be hogy a k�vetkez� l�p�sre m�r l�that� legyen
	 * 	Az alakzatot a megfelel� helyre mozgatja
	 * */
	synchronized void spawnNewFallingShape() {		
		Random rnd = new Random(System.currentTimeMillis());
		Shape.Type randomType = basicShapes.get(rnd.nextInt(basicShapes.size())).getType();
		//DEBUG POINT - Alakzat fix�l�sa
		// randomType = Shape.Type.I;
		//Alakzat k�sz�t�se
		fallingShape = new Shape(randomType);
		Position spawnPoint = getSpawnPosFromEightDir(fallingShape, shapeSpawnedAt);
		// spawnPoint = getPosFromEightDir(fallingShape, 0);
		//K�vetkez� spawn pontra �ll�t�s
		shapeSpawnedAt++;
		//Alakzat mozgat�sa a spawnpointra
		moveShapeTo(fallingShape, new Position(spawnPoint));
	}
	
	/**	Ellen�rz� f�ggv�ny, ami megvizsg�lja, hogy a k�z�ppontt�l adott t�vols�gban (n�gyzetes gy�r�k sz�ma) minden block teli-e?
	 * 	K�r alakban, a bal fels� sarokb�l v�gign�zi, hogy minden blokk teli-e?
	 * 	Ha nem azonnal visszat�r hamis �rt�kkel
	 * 	Ha mindegyik teli, akkor minden blokkott v�gign�zett
	 * */
	boolean isLoopFullFromMiddle(int distanceFromMiddle) {
		if(distanceFromMiddle < 1 || distanceFromMiddle > gameWidth/2 || distanceFromMiddle > gameHeight/2)
			return false;
		
		//K�zep�nek a meghat�roz�sa
		int MID_X = gameOffScreenBorder + gameWidth/2;
		int MID_Y = gameOffScreenBorder + gameHeight/2;
		//Iter�l�s a megfelel� m�ret� n�gyzetes gy�r�n
		int edgeLength = distanceFromMiddle*2 +1;
		//Bal fels� sarokb�l ellen�rizni a jobb fels� sarokig
		for(int i=0; i < edgeLength; i++) {
			if(gameField[MID_X-distanceFromMiddle + i][MID_Y-distanceFromMiddle].isEmpty()){
				//Ha tal�l �reset visszat�r
				return false;
			}
		}
		//Jobb fels�b�l a job als�ba
		for(int i=0; i < edgeLength; i++) {
			if(gameField[MID_X+distanceFromMiddle][MID_Y-distanceFromMiddle + i].isEmpty()) {
				return false;
			}
		}
		//Jobb als�b�l a bal als�ba
		for(int i=0; i < edgeLength; i++) {
			if(gameField[MID_X+distanceFromMiddle - i][MID_Y+distanceFromMiddle].isEmpty()) {
				return false;
			}
		}
		//Bal als�b�l a bal fels�be
		for (int i = 0; i < edgeLength; i++) {
			if (gameField[MID_X - distanceFromMiddle][MID_Y+distanceFromMiddle - i].isEmpty()) {
				return false;
			}
		}
		
		//Minden �le a blokkoknak foglalt volt
		return true;
	}
	
	/**	Egy adott t�vols�gban az �sszes blokkot �tsz�nezi
	 * 	A k�z�ppont meghat�roz�sa ut�n iter�l a a megfelel� t�vols�gba l�v� ny�gyzetes gy�r�n
	 * 	Minden oldalon megy v�gig egyszerre, teh�t csak egy ciklust tartalmaz, amiben sz�nez		
	 * 
	 * @param distanceFromMiddle Az adott t�vols�g
	 * */
	void changeColorInLoop(int distanceFromMiddle, Color color) {
		// K�zep�nek a meghat�roz�sa
		int MID_X = gameOffScreenBorder + gameWidth / 2;
		int MID_Y = gameOffScreenBorder + gameHeight / 2;
		// Iter�l�s a megfelel� m�ret� n�gyzetes gy�r�n
		int length = distanceFromMiddle * 2 + 1;
		//Sarkokmeghat�roz�sa
		int TOP_Y   = MID_Y - distanceFromMiddle;
		int LEFT_X  = MID_X - distanceFromMiddle;
		int RIGHT_X = MID_X + distanceFromMiddle;
		int BOT_Y   = MID_Y + distanceFromMiddle;
		
		//V�gigmegy�nk minden oldalon egyszerre
		//�tsz�nezz�k
		for(int i = 0; i < length; i++) {
			gameField[LEFT_X  +i][TOP_Y   ].color = color;
			gameField[RIGHT_X   ][TOP_Y +i].color = color;
			gameField[RIGHT_X -i][BOT_Y   ].color = color;
			gameField[LEFT_X    ][BOT_Y -i].color = color;
		}
	}
	
	/**	Visszaad egy kollekci�t amik az �sszes lehets�ges t�vols�got tartalmazza, ami a pont �s az alakzat �sszes teli blokkja k�z�tt lehet
	 * 	Az alakzat blokkjain v�gigiter�l �s ha az alakzatban az adott blokk teli, akkor a blokk gy�r� t�vols�g�t kisz�molja �s ha nincs benne a v�geredm�nybe hozz�adja
	 * 	@return Az �sszes olyan gy�r� t�vols�g egy ArrayList-ben amiben az alakzat blokkjai megtal�lhat�an a k�z�ppontt�l.
	 * */
	ArrayList<Integer> getAllBlockDistanceIn(Shape shape, Position pos){
		ArrayList<Integer> distances = new ArrayList<>();
		//Alakzat blokkjain iter�l�s
		for(int sxi=0; sxi<shape.getBlocksWidth(); sxi++) {
			for(int syi=0; syi<shape.getBlocksHeight(); syi++) {
				if( shape.getFullAt(sxi, syi) ) {
					//Ha az adott blokk teli csak akkor sz�molja
					//Az alakzat orig�j�hoz (jobb fels� sarok)
					Position actBlockPos = new Position(shape.getPosition());
					//Az iter�lt �rt�kek hozz�ad�sa
					actBlockPos.x += sxi;
					actBlockPos.y += syi;
					//Ennek a t�vols�ga koncentrikus n�gyzetekkel a pos-t�l
					int actDist = actBlockPos.getLoopDistanceFrom(pos);
					if( !distances.contains(actDist) ) {
						//Ha nincs benne ez a t�vols�g
						distances.add(actDist);
					}
				}
			}
		}
		
		return distances;
	}
	
	/**	�gymond cella aut�mata, ami a k�z�ppont fel� viszi a lerakott blokkokat egy l�ptet�ssel
	 * 	A k�z�ppont fel� esnek eggyel beljebb azok, amik felette, alatta vagy mellette vannak.
	 * 	A sarkokb�l megn�zi a kifel� tal�lhat� 3 cell�t �s ha azok k�z�tt valamelyik teli akkor az lesz
	 * 	A benti gy�r� sarkai, az eggyel t�volabb tal�lhat� 3 L-alak� sarokelemb�l �ll �ssze, ha ott b�rmelyik teli a bels� gy�r�ben is teli lesz
	 * 	Csak akkor marad �res ha mind h�rom �res volt el�tte
	 * */
	void moveOuterBlocksCloserToMiddle(int distanceFromMiddle) {
		//K�zep�nek a meghat�roz�sa
		int MID_X = gameOffScreenBorder + gameWidth/2;
		int MID_Y = gameOffScreenBorder + gameHeight/2;
		
		// Iter�l�s az �sszes gy�r�n az adott m�rett�l kezdve
		// +1, hogy az utols� t�vols�gban a k�perny�n k�v�lr�l is mozgassa az �res blokkokat befel�
		for (int dist = distanceFromMiddle; dist < gameWidth / 2 +1; dist++) {
			//System.out.println("" + dist + " tavolsagban beljebb mozgat�s");

			int edgeLength = dist * 2 - 1;
			int offs = (int) Math.floor(edgeLength/2);

			// Bal fels� sarokb�l a jobb fels� sarokig �trakni a felette l�v� sorb�l, amik
			// nem sarok elemek
			for (int i = 0; i < edgeLength; i++) {
				//System.out.println("Offs:" + offs);
				gameField[MID_X - offs + i][MID_Y - dist] = new Block(gameField[MID_X - offs + i][MID_Y - dist - 1]);
			}
			// Jobb fels�b�l a job als�ba
			for (int i = 0; i < edgeLength; i++) {
				gameField[MID_X + dist][MID_Y - offs + i] = new Block(gameField[MID_X + dist + 1][MID_Y - offs + i]);
			}
			// Jobb als�b�l a bal als�ba
			for (int i = 0; i < edgeLength; i++) {
				gameField[MID_X + offs - i][MID_Y + dist] = new Block(gameField[MID_X + offs - i][MID_Y + dist + 1]);
			}
			// Bal als�b�l a bal fels�be
			for (int i = 0; i < edgeLength; i++) {
				gameField[MID_X - dist][MID_Y + offs - i] = new Block(gameField[MID_X - dist - 1][MID_Y + offs - i]);
			}

			// Sarkok ellen�rz�se
			// Bal fels� sarok mind a h�rom ir�nyb�l
			if (gameField[MID_X - dist - 1][MID_Y - dist - 1].isFull()) {
				// El�sz�r az �tl�t n�zz�k meg
				gameField[MID_X - dist][MID_Y - dist] = new Block(gameField[MID_X - dist - 1][MID_Y - dist - 1]);
			} else if (gameField[MID_X - dist][MID_Y - dist - 1].isFull()) {
				// Majd a felette l�v�t
				gameField[MID_X - dist][MID_Y - dist] = new Block(gameField[MID_X - dist][MID_Y - dist - 1]);
			} else if (gameField[MID_X - dist - 1][MID_Y - dist].isFull()) {
				// Majd a mellette l�v�t balra
				gameField[MID_X - dist][MID_Y - dist] = new Block(gameField[MID_X - dist - 1][MID_Y - dist].isFull());
			} else {
				gameField[MID_X - dist][MID_Y - dist] = new Block(false);
			}

			// Jobb fels� sarok
			if (gameField[MID_X + dist + 1][MID_Y - dist - 1].isFull()) {
				// �tl�
				gameField[MID_X + dist][MID_Y - dist] = new Block(gameField[MID_X + dist + 1][MID_Y - dist - 1]);
			} else if (gameField[MID_X + dist + 1][MID_Y - dist].isFull()) {
				// Mellette jobbra
				gameField[MID_X + dist][MID_Y - dist] = new Block(gameField[MID_X + dist + 1][MID_Y - dist]);
			} else if (gameField[MID_X + dist][MID_Y - dist - 1].isFull()) {
				// Felette
				gameField[MID_X + dist][MID_Y - dist] = new Block(gameField[MID_X + dist][MID_Y - dist - 1]);
			} else {
				gameField[MID_X + dist][MID_Y - dist] = new Block(false);
			}

			// Jobb als� sarok
			if (gameField[MID_X + dist + 1][MID_Y + dist + 1].isFull()) {
				// �tl�
				gameField[MID_X + dist][MID_Y + dist] = new Block(gameField[MID_X + dist + 1][MID_Y + dist + 1]);
			} else if (gameField[MID_X + dist][MID_Y + dist + 1].isFull()) {
				// Alatta
				gameField[MID_X + dist][MID_Y + dist] = new Block(gameField[MID_X + dist][MID_Y + dist + 1]);
			} else if (gameField[MID_X + dist + 1][MID_Y + dist].isFull()) {
				// Mellette jobbra
				gameField[MID_X + dist][MID_Y + dist] = new Block(gameField[MID_X + dist + 1][MID_Y + dist]);
			} else {
				gameField[MID_X + dist][MID_Y + dist] = new Block(false);
			}

			// Bal als� sarok
			if (gameField[MID_X - dist - 1][MID_Y + dist + 1].isFull()) {
				// �tl�
				gameField[MID_X - dist][MID_Y + dist] = new Block(gameField[MID_X - dist - 1][MID_Y + dist + 1]);
			} else if (gameField[MID_X - dist - 1][MID_Y + dist].isFull()) {
				// Mellette balra
				gameField[MID_X - dist][MID_Y + dist] = new Block(gameField[MID_X - dist - 1][MID_Y + dist]);
			} else if (gameField[MID_X - dist][MID_Y + dist + 1].isFull()) {
				// Alatta
				gameField[MID_X - dist][MID_Y + dist] = new Block(gameField[MID_X - dist][MID_Y + dist + 1]);
			} else {
				gameField[MID_X - dist][MID_Y + dist] = new Block(false);
			}
		}
	}
	
	/**	Blokkok kit�rl�se adott t�vols�gban a k�z�ppontt�l
	 * 	A k�z�ppont meghat�roz�sa ut�n, v�gigmegy a megfelel� t�vols�gban l�v� gy�r�n a bal fels� sarokb�l az �ramutat� j�r�savl megfelel�en
	 * 	Az oldalakon egyes�vel halad v�gig �s mindenhol a blokkok kit�lt�tts�g�t hamis-ra �ll�tja
	 * */
	void removeBlocksInLoop(int distanceFromMiddle){
		// A blokkok t�rl�se a teljes loop-ban
		// K�zep�nek a meghat�roz�sa
		int MID_X = gameOffScreenBorder + gameWidth / 2;
		int MID_Y = gameOffScreenBorder + gameHeight / 2;
		// Iter�l�s a megfelel� m�ret� n�gyzetes gy�r�n
		int edgeLength = distanceFromMiddle * 2 + 1;
		// Bal fels� sarokb�l ellen�rizni a jobb fels� sarokig
		for (int i = 0; i < edgeLength; i++) {
			gameField[MID_X - distanceFromMiddle + i][MID_Y - distanceFromMiddle] = new Block(false);
		}
		// Jobb fels�b�l a job als�ba
		for (int i = 0; i < edgeLength; i++) {
			gameField[MID_X + distanceFromMiddle][MID_Y - distanceFromMiddle + i] = new Block(false);
		}
		// Jobb als�b�l a bal als�ba
		for (int i = 0; i < edgeLength; i++) {
			gameField[MID_X + distanceFromMiddle - i][MID_Y + distanceFromMiddle] = new Block(false);
		}
		// Bal als�b�l a bel fels�be
		for (int i = 0; i < edgeLength; i++) {
			gameField[MID_X - distanceFromMiddle][MID_Y + distanceFromMiddle - i] = new Block(false);
		}
	}
	
	/** Az adott alakzatot reprezent�l� r�szben leellen�rzi, hogy van-e teljes gy�r�, ha van pontot hozz�adja, kit�rli a blokkokat �s mozgatja a t�bbit befel�
	 * 	Csak azokat a gy�r�ket ellen�rzi ahol az alakzat lehet, minden ami az alakzatban szerepel, ezek k�z�l lehets�ges, hogy nemm indegyik megfelel�
	 * 	Miut�n meg�llap�totta az �sszes lehets�ges t�vols�got, leellen�rzi, hogy ezekben a t�vols�gokban van-e teljes gy�r�
	 * 	Ha teljes a gy�r� pontokat hozz��rja az eddigi ponthoz �s a gy�r�t �resre �ll�tja (kit�rli a blokkokat)
	 * 	A pont alapj�n gyors�tja a j�t�kot
	 * 	Ezeket a teljes t�vols�gokat �sszegy�jti �s k�v�lr�l befel� haladva az �sszes blokkot befel� mozgatja a k�z�ppont fel�, �gy a m�r �res helyekre beleeshetnek a k�ls� blokkok.
	 * 	Az�rt iter�l k�v�lr�l befel�, hogy a legk�ls� blokk ak�r t�bbsz�r is leeshessen addig am�g tud
	 * 	Emellett ha ezek ut�n is van m�g teli gy�r� az csak az�rt lehet, mert egy k�ls� nem teli gy�r� beljebb esve teli lett.
	 * 	Ezt a j�t�k �tsz�nezi feh�rre, jelezve, hogy ez a gy�r� nem t�r�lhet� innent�l, ezzel is nehez�tve a j�t�kot
	 * */
	void scoreAllLoopIn(Shape shape) {
		//Csak azokat a gy�r�ket ellen�rzi ahol az alakzat lehet, minden ami az alakzatban szerepel
		//Ezek k�z�l lehets�ges, hogy nemm indegyik megfelel�
		ArrayList<Integer> distances = getAllBlockDistanceIn(shape, new Position(gameOffScreenBorder+gameWidth/2, gameOffScreenBorder+gameHeight/2));
		//A val�ban teljes gy�r�k t�vols�g�nak elt�rol�s�ra
		ArrayList<Integer> fullDistances = new ArrayList<>();
		
		//Rendez�s n�vekv� sorrendbe
		Collections.sort(distances);
		
		for(Integer dist : distances) {
			if( isLoopFullFromMiddle(dist) ) {
				//Pontoz�s
				//A n�gysz�g gy�r� m�rete: oldalhossz�s�g *4
				//Az oldalhossz�s�g: t�vols�g*2
				//Szorozva a t�vols�ggal m�g egyszer
				score += dist*2 *4 * dist;
				
				//Be�ll�tja az id� l�ptet�st
				//Egyre gyorsul
				millisBetweenSteps = initialMSBetweenSteps - (score / 10) * 25;
				if(millisBetweenSteps < 150) {
					millisBetweenSteps = 150;
				}
				
				//Kit�rl�s
				removeBlocksInLoop(dist);
				//A list�hoz ad�s ahonnan k�v�l mozgatni kell majd
				fullDistances.add(dist);
			}
		}
		
		//K�v�lr�l befel� l�ptetve, hogy elker�lje a hib�s eseteket (egym�st k�vet� k�t sor stb.)
		Collections.sort(fullDistances);
		Collections.reverse(fullDistances);
		
		for(Integer dist : fullDistances) {
			//System.out.println("A " + dist + " t�vols�gn�l nagyobbak l�ptet�se beljebb");
			//A kit�r�ltek hely�re bel�pteti a k�v�l es� blokkokat
			moveOuterBlocksCloserToMiddle(dist);
		}
		
		//Itt el�fordulhat, hogy vannak teljes loopok a cella aut�mata miatt
		//Ha m�g mind�g vannak azokat feh�rre v�ltoztatja, hiszen azokat nem a j�t�kos hozta l�tre
		//Ez�ltal is nehez�tve a j�t�kot, magasabb lesz a p�lya �s nem �r pontot
		for(int dist = 1; dist < gameWidth/2; dist++) {
			if(isLoopFullFromMiddle(dist)) {
				changeColorInLoop(dist, Color.white);
			}
		}
	}
	
	/** Az aktu�lis pont gettere
	 * 	@return Az aktu�lis pontot
	 */
	int getScore() {
		return score;
	}
	
	/**
	 * J�t�k v�gi pont gettere
	 * @return A v�gleges pont
	 */
	int getFinalScore() {
		return gameOverScore;
	}
	
	/**	A f�ggv�ny ami a l�ptet�st v�gzi, minden id�egys�g alatt egyszer fut le
	 * 	Az id�z�t� ezt h�vja meg, hogy "mozogjon az alakzat"
	 * 
	 * 	A feladatok amiket elv�gez:
	 * 	<p>
	 * 		A j�t�k v�gi jelz� figyelembev�tele, ha v�ge van ne csin�jon semmit
	 * 		Ha nincs �ppen zuhan� alakzat l�trehoz �s elhelyez egy �jat a k�vetkez� "spawn point"-on
	 * 		Ha van �ppen zuhan� alakzat, akkor megn�zi, hogy k�vetkez� l�p�sre �tk�zni, fog-e
	 * 			Ha �tk�zik akkor kirajzolja az indik�tort, hogy jelezze a pontos t�vols�gokat, pontoz ha sz�ks�ges majd be�ll�tja a j�t�k v�g�t ha kell. A zuhan� alakzatnak l�trehoz egy �jat.
	 * 		Ha nem fog �tk�zni, akkor l�pteti az �ppen zuhan� alakzatot eggyel beljebb a k�z�ppont fel�	
	 * 	<p>
	 * */
	void step() {		
		//J�t�k v�ge ellen�rz�se �s 
		//Debug miatt beker�lt egy plusz felt�tel, hogy a Timer ne h�vja meg ha m�g fut egy.
		if (!gameOver && stepReady) {
			
			stepReady = false;
			if (fallingShape == null) {
				//�ppen zuhan� alakzat mozgat�sa
				spawnNewFallingShape();
			} else {
				// Megn�zni, hogy �tk�zni fog-e a lees� blokk
				if (willFallingCollide()) {
					//Indik�tor kirajzol�sa
					shapeIndicator.setVisible(fallingShape);
					
					//Ellen�rizni van-e teljes k�r
					scoreAllLoopIn(fallingShape);
					// Ellen�rizni, hogy a nem l�that� borderben van-e blokk
					if ( checkGameOver() ) {
						return;
					}
					//Hozzon l�tre egy �j alakzatot
					spawnNewFallingShape();
				} else {
					// Ha nem fog �tk�zni m�s blokkokkal akkor l�pteti eggyel
					moveFallingShapeByGravity();
					// moveShapeTo(fallingShape, getNextPos(fallingShape, gravityPoint));
				}
			}
		}
		
		stepReady = true;
	}
	
	/**	Leellen�rzi, hogy a poz�ci� a l�that� j�t�kt�rben van-e
	 * 	Ha bek�k esik igazat ad vissza
	 * 	Ha k�v�l esik a nem l�that� r�szre hamisat ad vissza
	 * 	@return Bel�l helyezkedik-e el
	 * */
	boolean isInsideGameField(Position testPos){
		if(testPos.x >= gameOffScreenBorder && testPos.x < gameOffScreenBorder+gameWidth  &&
		   testPos.y >= gameOffScreenBorder && testPos.y < gameOffScreenBorder+gameHeight ) {
			return true;
		}
		
		return false;
	}
	
	/**	A j�t�k v�g�nek ellen�rz�se. Az �ppen zuhan� alakzatra megn�zi, hogy hol helyezkedik el
	 * 	Csak akkor ellen�rzi ha van az alakzatnak olyan blokkja ami k�v�l eshet a j�t�kt�ren
	 * 	V�gigiter�l az alakzat blokkjain, ha tal�l telit akkor leellen�rzi, hogy a j�t�kt�ren van-e vagy sem
	 * 	Ha nincs a j�t�kt�ren teli blokkja az alakzatnak, akkor a j�t�k v�get �r
	 * 	Be�ll�tja a v�gs� pontot
	 * */
	boolean checkGameOver() {
		//Csak akkor ellen�rizze a j�t�k v�g�t ha k�v�l esik a hat�rokon
		if(	fallingShape.getPosition().x < gameOffScreenBorder || 
			fallingShape.getPosition().y < gameOffScreenBorder ||
			fallingShape.getPosition().x+fallingShape.getBlocksWidth()  > gameWidth+gameOffScreenBorder ||
			fallingShape.getPosition().y+fallingShape.getBlocksHeight() > gameHeight+gameOffScreenBorder)
		{
			Position testBlockPos = new Position();
			//Alakzat blokkjain val� iter�l�s
			for(int sxi=0; sxi < fallingShape.getBlocksWidth(); sxi++){
				for(int syi=0; syi < fallingShape.getBlocksHeight(); syi++) {
					testBlockPos.x = fallingShape.getPosition().x+sxi;
					testBlockPos.y = fallingShape.getPosition().y+syi;
					if( fallingShape.isPresentAt(testBlockPos) ){
						if( !isInsideGameField(testBlockPos)) {
							gameOver = true;
							setFinalScore();
							System.out.println("GAME OVER");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**	A v�gs� pont be�ll�t�sa
	 * */
	void setFinalScore() {
		gameOverScore = score;
	}
	
	/**	Megn�zi, hogy a k�vetkez� l�p�sre a �tk�zik-e az �ppen es� alakzat alakzat b�rmivel
	 * 	Meg�llap�tja az aktu�lis alakzat k�vetkez� hely�t
	 * 	Ha el�ri a k�zep�t �s nem tud mozogni, akkor is �lljon meg a zuhan�s
	 * 	Leellen�rzi, hogy a k�vetkez� poz�ci�ban �rintkezik-e m�s blokkokkal
	 * 
	 * 	@return Lesz-e �rintkez�s ha mozgat�s t�rt�nik
	 * */
	boolean willFallingCollide() {
		boolean colliding = false;
		
		if(fallingShape != null) {
			Position futurePos = getNextPosByGravity(fallingShape, gravityPoint);
			
			//Ha el�ri a k�zep�t �s nem tud mozogni akkor is �lljon meg a zuhan�s
			if(futurePos.equals(fallingShape.getPosition())) {
				return true;
			}
			//�rintkez�s ellen�rz�s egy adott pontban
			colliding = willShapeCollideAt(fallingShape, futurePos);
		}
		
		return colliding;
	}
	
	/**	V�gign�zi az alakzat minden blokkj�ra, hogy �rinntkezni, fog-e m�s blokkal a megadott helyen
	 * 	Itt m�g nem t�rt�nik semmilyen mozgat�s csak ellen�rz�s
	 * 	Egyes�vel megn�zni, hogy az alakzatnak van-e kit�lt�tt blokkja ami �rintkezne m�ssal
	 * 	Ha az es�s el�tti form�nak nincsen ott blokkja �s az es�s ut�n van kit�lt�tt blokkja csak akkor t�rt�nik meg az ellen�rz�s, hogy abban a poz�ci�ban van-e teli blokk a j�t�kt�ren
	 * 
	 * 	@param pos A megadott hely, ahol az alakzatra ellen�rizni kell
	 * */
	boolean willShapeCollideAt(Shape shape, Position pos) {
		boolean colliding = false;
		//Egyes�vel megn�zni, hogy az alakzatnak van-e olyan blokkja ami �rintkezne m�ssal
		for(int sxi = 0; !colliding && sxi < shape.getBlocksWidth(); sxi++) {
			for(int syi = 0; !colliding && syi < shape.getBlocksHeight(); syi++){
				Position oneBlockPos = new Position(pos.x+sxi, pos.y+syi);
				//Ha az es�s el�tti form�nak nincsenek ott blokkjai �s a form�nak van kit�lt�tt blokkja
				if( !shape.isPresentAt(oneBlockPos) && shape.getFullAt(sxi, syi) ){
					colliding = gameField[oneBlockPos.x][oneBlockPos.y].full;
				}
			}
		}
		return colliding;
	}
	
	/**	Zuhan� alakzat mozgat�sa
	 * 	Kit�li az eredeti hely�n l�v�t, majd elhelyezi az �j helyre, ami k�zelebb van a gravit�ci�s ponthoz
	 * */
	void moveFallingShapeByGravity() {
		clearInShape(gameField, fallingShape);
		moveShapeTo(fallingShape, getNextPosByGravity(fallingShape, gravityPoint));
	}
	
	/**	A felhaszn�l� �ltal mozgathat� ir�nyok valamelyik�be mozgat�s
	 * 	Fontos, hogy csak a gravit�ci�ra mer�legesen mozgathat�
	 * */
	void moveFallingShapeByDir(Dir dir) {
		clearInShape(gameField, fallingShape);
		moveShapeTo(fallingShape, getNextMovePos(fallingShape, dir));
	}
	
	/**	Kit�rli az alakzat blokkjait a j�t�kmez�r�l
	 * 	V�gigiter�l a blokkokon �s be�ll�tja a mez�ket a j�t�kt�ren, hogy ne legyenek kit�lt�ttek, ott ahol az alakzatnak vannak kit�lt�tt blokkjai
	 * */
	void clearInShape(Block[][] field, Shape shape) {
		for(int sxi = 0; sxi < shape.getBlocksWidth(); sxi++) {
			for(int syi = 0; syi < shape.getBlocksHeight(); syi++) {
				if(shape.getFullAt(sxi, syi)) {
					field[shape.getPosition().x+sxi][shape.getPosition().y+syi].setFull(false);;
				}
			}
		}
	}
	
	/**	Zuhan� alakzat forgat�sa
	 * 	Leellen�rzi, hogy okozna-e a forgat�s �tk�z�st
	 * 	Ha nem okoz �tk�z�st kit�rli az aktu�lis alakzatot a j�t�kt�rr�l �s mozgatja az �j elforgatott alakzatot az eredeti hely�re
	 * 
	 * 	@return T�rt�nt-e forgat�s
	 * */
	boolean rotateFallingShape(){
		if(fallingShape == null)
			return false;
		
		//Ellen�rizni, hogy nem okoz-e �rintkez�st
		Shape futureShape = new Shape(fallingShape);
		futureShape.rotate();
		for(int sxi = 0; sxi < futureShape.width; sxi++) {
			for(int syi = 0; syi < futureShape.height; syi++) {
				Position actPos = new Position( futureShape.getPosition().x+sxi, futureShape.getPosition().y+syi);
				if( !fallingShape.isPresentAt(actPos) && futureShape.getFullAt(sxi, syi) && gameField[actPos.x][actPos.y].isFull() ) {
					return false;
				}
			}
		}
		
		clearInShape(gameField, fallingShape);
		fallingShape.rotate();
		moveShapeTo(fallingShape, fallingShape.getPosition());
		
		return true;
	}
	
	/**	A felhaszn�l� �ltali mozgat�s ellen�rz�se, a gravit�ci�ra mer�legesen mozgathat�
	 * 	El�sz�r ellen�rzi, hogy a gravit�ci� ir�ny�ra mer�legesen vagy annak ir�ny�ba akarjuk mozgatni
	 * 	Ha nem abba az ir�nyba akarjuk mozgatni akkor visszat�r hamissal �s nem mozgatja
	 * 	Ha megfelel� a mozgat�s ir�nya, akkor leellen�rzi, hogy �tk�zne-e az alakzat
	 * 	Ha nincs �tk�z�s �tmozgatja az alakzatot �s igazzal t�r vissza
	 * 
	 * 	@return Megt�rt�nt-e a mozgat�s
	 * */
	boolean moveFallingShape(Dir dir) {
		if(fallingShape == null)
			return false;
		
		//Ellen�rizni, hogy az ir�nyvektorra mer�legesen mozgatja-e
		Position direction = new Position ( gravityPoint.x - (fallingShape.getPosition().x + fallingShape.width/2), gravityPoint.y - (fallingShape.getPosition().y + fallingShape.height/2) );
		direction.eightDirNormalize();
		Dir gravityDir = direction.transformToDirection();
		//Filterez�s a gravit�ci� ir�ny�nak megfelel�en, maximum k�t ir�nyba lehet mozgatni
		switch(gravityDir) {
		case NO_DIR:
		case UNKNOWN:
			return false;
		case LEFT:
			if(dir != Dir.UP && dir != Dir.DOWN && dir != Dir.LEFT)
				return false;
			break;
		case RIGHT:
			if(dir != Dir.UP && dir != Dir.DOWN && dir != Dir.RIGHT)
				return false;
			break;
		case UP:
			if(dir != Dir.LEFT && dir != Dir.RIGHT && dir != Dir.UP)
				return false;
			break;
		case DOWN:
			if(dir != Dir.LEFT && dir != Dir.RIGHT && dir != Dir.DOWN)
				return false;
			break;
		case LEFT_UP:
			if(dir != Dir.LEFT && dir != Dir.UP)
				return false;
			break;
		case RIGHT_UP:
			if(dir != Dir.RIGHT && dir != Dir.UP )
				return false;
			break;
		case LEFT_DOWN:
			if(dir != Dir.LEFT && dir != Dir.DOWN )
				return false;
			break;
		case RIGHT_DOWN:
			if(dir != Dir.RIGHT && dir != Dir.DOWN )
				return false;
			break;
		}
		
		//Amennyiben j� ir�nyba akarjuk mozgatni ide jutunk
		//Le kell ellen�rizni, hogy lehet-e mozgatni, nincs �tk�z�s
		Position futurePos = getNextMovePos(fallingShape, dir);	
		if( willShapeCollideAt(fallingShape, futurePos) ){
			return false;
		}
		
		//Nincs �tk�z�s
		//Mozgatjuk a blokkot arra a poz�ci�ra
		moveFallingShapeByDir(dir);
		
		return true;
	}
	
	/**	Megadja az alakzat k�vetkez� hely�t a gravit�ci�s ponthoz k�zeledve
	 * @param gPoint Gravit�ci�s pont
	 * */
	Position getNextPosByGravity(Shape shape, Position gPoint) {
		Position direction = new Position ( gPoint.x - (shape.getPosition().x + shape.width/2), gPoint.y - (shape.getPosition().y + shape.height/2) );
		direction.eightDirNormalize();
		return new Position(shape.getPosition().x + direction.x, shape.getPosition().y + direction.y);
	}
	
	/**	Megadja az adott poz�ci�ban a gravit�ci� ir�ny�t a gravit�ci�s pont fel�
	 * @param gPoint Gravit�ci�s pont
	 * */
	Position getGravityVector(Position blockPos, Position gPoint) {
		Position direction = new Position( gPoint.x - blockPos.x, gPoint.y - blockPos.y );
		//System.out.println("Direction:" + direction);
		direction.eightDirNormalize();
		//System.out.println("Normalized:" + direction);
		return direction;
	}
	
	/**	Alakzat mozgat�sa billenty�zettel
	 * 	Csak n�gy ir�nyba lehet mozgatni: fel, le jobbra, balra
	 * 
	 * 	@return A poz�ci� ahov� ker�lni fog az alakzat a mozgat�s ut�n
	 * */
	Position getNextMovePos(Shape shape, Dir dir) {
		Position newPos = new Position(shape.getPosition());
		switch(dir) {
			case LEFT:
				newPos.x -= 1;
				break;
			case RIGHT:
				newPos.x += 1;
				break;
			case UP:
				newPos.y -= 1;
				break;
			case DOWN:
				newPos.y += 1;
				break;
			default:
		}
		return newPos;
	}
	
	/**	Az alap alakzatok l�trhoz�sa, mindegyikb�l egyet
	 * 	A Tetris alap h�t form�j�t hozza l�tre �s elhelyezi �ket a sz�mukra kijel�lt t�mbbe
	 * 
	 * 	@implNote Ez az oszt�ly a tov�bbi egyedi form�k egyszer� felv�tel�t szolg�lja a j�t�klogik�ba
	 * */
	void createBasicShapes() {
		basicShapes = new ArrayList<>();
		basicShapes.add( new Shape(Shape.Type.O) );
		basicShapes.add( new Shape(Shape.Type.L) );
		basicShapes.add( new Shape(Shape.Type.J) );
		basicShapes.add( new Shape(Shape.Type.Z) );
		basicShapes.add( new Shape(Shape.Type.S) );
		basicShapes.add( new Shape(Shape.Type.I) );
		basicShapes.add( new Shape(Shape.Type.T) );
	}
	
	/**	Az alakzatok m�ret�nek a meg�llap�t�s�hoz, dinamikusan gener�l�dik a p�lya sz�le, ez az ami meghat�rozza mekkora legyen
	 * 	@return visszaadja a maximum sz�less�get/magass�got, ami a legnagyobb a l�trehozott alap alakzatok k�z�tt
	 * */
	int getMaxOfShapes() {
		int max = 0;
		for(Shape s : basicShapes) {
			if(s.width > max)
				max = s.width;
			if(s.height > max)
				max = s.height;
		}
		return max;
	}
	
	/**	A gravit�ci�s indik�tor gettere
	 * 	A gravit�ci�s indik�tor megmutatja melyik ir�nyba fog esni az alakzat
	 * */
	TriangleIndicator getGravityIndicator() {
		return gravityIndicator;
	}
	
	/**	A gravit�ci� jelz� be�ll�t�sa
	 * 	Alakzat mozgat��sa ut�n megh�v�dik, �gy be�ll�tja az aktu�lis es�si ir�nyt
	 * 
	 * 	Lek�ri a gravit�ci� ir�ny�t
	 * 	Egy le�r�b�l meghat�rozza melyik blokkra kell rajzolnia. Ez mind�g az alakzat mellett/alatt/felett/sarkaiban jelenik meg az es�si ir�nyt�l f�gg�en
	 * 	A kapott gravit�ci�s indik�tort pedig be�ll�tja ennek megfelel�en
	 * */
	void setFallingGravityIndicator(TriangleIndicator indic) {
		if(fallingShape == null) {
			return;
		}
		
		Position gravityVector = getGravityVector( new Position(fallingShape.pos.x + fallingShape.width/2, fallingShape.pos.y + fallingShape.height/2), gravityPoint);
		Position posToDraw = new Position(fallingShape.pos);
		Dir dir  = Dir.UNKNOWN;
		
		switch(gravityVector.x) {
		case -1:
			//Balra mutat
			switch(gravityVector.y) {
			case -1:
				//Balra felfel�
				posToDraw.x -= 1;
				posToDraw.y -= 1;
				dir = Dir.LEFT_UP;
				break;
			case 0:
				//Balra
				posToDraw.x -= 1;
				posToDraw.y += fallingShape.height/2;
				dir = Dir.LEFT;
				break;
			case 1:
				//Balra lefel�
				posToDraw.x -= 1;
				posToDraw.y += fallingShape.height;
				dir = Dir.LEFT_DOWN;
				break;
			}
			break;
		case 0:
			//Nincs oldalra mozg�s
			switch(gravityVector.y) {
			case -1:
				//Felfel�
				posToDraw.x += fallingShape.width/2;
				posToDraw.y -= 1;
				dir = Dir.UP;
				break;
			case 1:
				//Lefel�
				posToDraw.x += fallingShape.width/2;
				posToDraw.y += fallingShape.height;
				dir = Dir.DOWN;
				break;
			}
			break;
		case 1:
			//Jobbra mutat
			switch(gravityVector.y) {
			case -1:
				//Jobbra felfel�
				posToDraw.x += fallingShape.width;
				posToDraw.y -= 1;
				dir = Dir.RIGHT_UP;
				break;
			case 0:
				//Jobbra
				posToDraw.x += fallingShape.width;
				posToDraw.y += fallingShape.height/2;
				dir = Dir.RIGHT;
				break;
			case 1:
				//Jobbra lefel�
				posToDraw.x += fallingShape.width;
				posToDraw.y += fallingShape.height;
				dir = Dir.RIGHT_DOWN;
				break;
			}
			break;
		}
		
		//System.out.println("Gravit�ci�s indik�tor helye: " + posToDraw + " , ir�nya: " + dir.name());
		
		indic.set(posToDraw.x, posToDraw.y, dir);		
	}
	
	

	/**	J�t�kt�r x �s y hely�n l�v� mez� tel�tetts�g�nek gettere
	 * 	A nem l�that� mez�ket nem veszi figyelembe az els� l�that� mez� (bal fels�, lesz a x:0 �s y:0)
	 * 
	 * @return Egy logikai �rt�k ami az adott mez� kit�lt�tts�ge
	 * */
	public boolean isFilled(int x, int y) {
		return gameField[gameOffScreenBorder+x][gameOffScreenBorder+y].isFull();
	}
	
	/**	J�t�kt�r x �s y hely�n l�v� mez� sz�n�nek gettere
	 * 	A nem l�that� mez�ket nem veszi figyelembe az els� l�that� mez� (bal fels�, lesz a x:0 �s y:0)
	 * 
	 * 	@return Egy java.awt.Color t�pust ad vissza ami az adott blokknak a sz�ne
	 * */
	public Color getBlockColorAt(int x, int y) {
		return gameField[gameOffScreenBorder+x][gameOffScreenBorder+y].getColor();
	}
}
