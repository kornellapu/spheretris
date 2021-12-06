import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.awt.Color;

/**	Tartalmazza a játék logikáját. A játékmezõvel kapcsolatos minden adat itt tárolódik és a hozzájuk tartozó függvények
 * 	Kezeli a leesõ blokkokat és mozgatja. Ellenõrzi a játék végét és kezeli és pontozza a teljesen kitöltött teljes gyûrûket a középponttól.
 * */
public class GameLogic {
	/** Az esési irányok meghatározására, és hogy melyik irányba lehet mozgatni az alakzatot.
	 */
	public static enum Dir {LEFT, RIGHT, UP, DOWN, LEFT_UP, RIGHT_UP, LEFT_DOWN, RIGHT_DOWN, NO_DIR, UNKNOWN }
	
	/**	Az alap alakzatokat tartalmazó lista, amibõl tud véletleszerûen sorsolni
	 * */
	ArrayList<Shape> basicShapes;
	/**	A teljes játékmezõ, ami tartalmazza a látható és nem látható mezõket is
	 * @see Block
	 * */
	Block[][] gameField;
	/**	A látható játéktér szélessége
	 * */
	public final int gameWidth  = 41;
	/**	A látható játéktér magassága
	 * */
	public final int gameHeight = 41;
	/**	A nem látható mezõk a játéktér körül, amik a játék vége ellenõrzését szolgálják. Az új alakzatok ide kerülnek ebbe a részbe. A magassága autómatikusan meghatározott
	 * */
	static int gameOffScreenBorder;
	/**	A gravitációs pont, koordináta amihez közeledik az zuhanó alakzat
	 * @see Position
	 * */
	Position gravityPoint;
	/**	A zuhanó alakzat	
	 * */
	Shape fallingShape;
	/**	Az új keletkezés helye, 0 középen fent, 1 jobbra fent, stb... Összesen 8 irányból [0-7]
	 * */
	int shapeSpawnedAt;
	/**	A játék végét jelzõ érték	
	 * */
	boolean gameOver;
	/**	A debuggolásnál az idõzítõ miatt került bele, hogy ne lépjen tovább ha a program a breakpointban áll	
	 * */
	boolean stepReady;
	/**	A játék során elért pont
	 * */
	int score;
	/**	A játék végén elért pont, ez a játék végén frissül csak
	 * */
	int gameOverScore;
	/**	A kezdeti léptetés értéke
	 * */
	int initialMSBetweenSteps = 1000;
	/**	Az egyes esések közötti miliszekundumok száma
	 * */
	int millisBetweenSteps;
	/**	A lerakott alakzatot veszi körül, hogy látható legyen
	 * */
	PlacementIndicator shapeIndicator;
	/**	A gravitációs irány mutatása
	 * */
	TriangleIndicator gravityIndicator;
	
	/** A léptetések késleltetésének gettere
	 * */
	int getStepTime() {
		return millisBetweenSteps;
	}
	
	/**	Az alakzat jelölõnek a gettere
	 * */
	PlacementIndicator getShapeIndicator() {
		return shapeIndicator;
	}
	
	/**	Konstruktor
	 *  Beállítja és inicializálja új játékra
	 * 	A szükséges beállítások egy külön initNewGame függvényben, hogy késõbb is hívható legyen
	 * */
	public GameLogic(){
		initNewGame();
	}
	
	/**	A játék inicializálása
	 * 	
	 * 	Feladatok amiket elvégez:
	 * 	<p>
	 * 		Elkészíti az alakzatokat
	 * 		Beállítja a játéktér nem látható szélének a nagyságát
	 * 		A grevitációs középpontot a pálya közepére állítja
	 * 		Feltölti a játéktér többi blokkját üres mezõkkel (inicializálja azokat)
	 * 		Fehérre állítja a középpontot
	 * 		A leesõ alakzatot null-ra állítja
	 * 		A Léptetés sebességét visszaállítja a kezdeti értékre
	 * 		Az indikátorokat inicializálja
	 * 		A pontot kinullázza
	 * 		A következõ "spawn point"-ot beállítja, a fenti pontra
	 * 		A játék végét kitörli
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
	
	/**	A játémezõre teszi (mozgatja) az alakzatban szereplõ blokkokat (szín és lefedettség)
	 * 	Az alakzat helyét is beállítja
	 * 	A játékmezõt is beállítja
	 * 	Emellett a gravitációs  indikátort is beállítja
	 * */
	void moveShapeTo(Shape shape, Position pos) {
		if(shape == null)
			return;
		
		//Alakzat beállítása
		shape.setPosition(pos);
		//Játékmezõ beállítása
		for(int wi = 0; wi < shape.width; wi++) {
			for(int hi = 0; hi < shape.height; hi++) {
				if(shape.getFullAt(wi, hi)) {
					gameField[pos.x + wi][pos.y + hi].setTo(shape.blocks[wi][hi]);
				}
			}
		}
		
		//System.out.println("Alakzat mozgatva: " + pos);
		
		//Gravitációs indikátor beállítása
		setFallingGravityIndicator(gravityIndicator);
	}
	
	/**	A játéktér leírója, ami visszaad egy pozíciót, ahol az alakzat létrejöhet úgy, hogy következõ lépésre essen be a játéktérbe.
	 * Figyelembe veszi az alakzat alakját is, úgy, hogy az alakzatot a megfelelõ offsettel eltolja
	 * Az összes "spawn point" leíróját is tartalmazza
	 * @param dir Megadja, hogy a 8 irány közül melyiket szeretnénk kiválasztani (0: fent középen, 1: jobb felül, stb...)
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
		//DEBUG POINT - Spawn point fixálása
		//spawnPoint = 4;
		
		//Összes spawn point leíró
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
		//Az alakzatnak az offsetjével eltolja
		newPos.x += shape.getSpawnOffset(spawnPoint).x;
		newPos.y += shape.getSpawnOffset(spawnPoint).y;
		
		return newPos;
	}
	
	/**	Új véletlenszerû alakzat létrehoása és elhelyezése a nem látható játéktéren
	 * 	Létrehozza az új alakzatot véletlenszerûen az összes alap formából
	 * 	A posicióját a "spawn point"-ra helyezi és úgy állítja, be hogy a következõ lépésre már látható legyen
	 * 	Az alakzatot a megfelelõ helyre mozgatja
	 * */
	synchronized void spawnNewFallingShape() {		
		Random rnd = new Random(System.currentTimeMillis());
		Shape.Type randomType = basicShapes.get(rnd.nextInt(basicShapes.size())).getType();
		//DEBUG POINT - Alakzat fixálása
		// randomType = Shape.Type.I;
		//Alakzat készítése
		fallingShape = new Shape(randomType);
		Position spawnPoint = getSpawnPosFromEightDir(fallingShape, shapeSpawnedAt);
		// spawnPoint = getPosFromEightDir(fallingShape, 0);
		//Következõ spawn pontra állítás
		shapeSpawnedAt++;
		//Alakzat mozgatása a spawnpointra
		moveShapeTo(fallingShape, new Position(spawnPoint));
	}
	
	/**	Ellenõrzõ függvény, ami megvizsgálja, hogy a középponttól adott távolságban (négyzetes gyûrûk száma) minden block teli-e?
	 * 	Kör alakban, a bal felsõ sarokból végignézi, hogy minden blokk teli-e?
	 * 	Ha nem azonnal visszatér hamis értékkel
	 * 	Ha mindegyik teli, akkor minden blokkott végignézett
	 * */
	boolean isLoopFullFromMiddle(int distanceFromMiddle) {
		if(distanceFromMiddle < 1 || distanceFromMiddle > gameWidth/2 || distanceFromMiddle > gameHeight/2)
			return false;
		
		//Közepének a meghatározása
		int MID_X = gameOffScreenBorder + gameWidth/2;
		int MID_Y = gameOffScreenBorder + gameHeight/2;
		//Iterálás a megfelelõ méretû négyzetes gyûrûn
		int edgeLength = distanceFromMiddle*2 +1;
		//Bal felsõ sarokból ellenõrizni a jobb felsõ sarokig
		for(int i=0; i < edgeLength; i++) {
			if(gameField[MID_X-distanceFromMiddle + i][MID_Y-distanceFromMiddle].isEmpty()){
				//Ha talál üreset visszatér
				return false;
			}
		}
		//Jobb felsõbõl a job alsóba
		for(int i=0; i < edgeLength; i++) {
			if(gameField[MID_X+distanceFromMiddle][MID_Y-distanceFromMiddle + i].isEmpty()) {
				return false;
			}
		}
		//Jobb alsóból a bal alsóba
		for(int i=0; i < edgeLength; i++) {
			if(gameField[MID_X+distanceFromMiddle - i][MID_Y+distanceFromMiddle].isEmpty()) {
				return false;
			}
		}
		//Bal alsóból a bal felsõbe
		for (int i = 0; i < edgeLength; i++) {
			if (gameField[MID_X - distanceFromMiddle][MID_Y+distanceFromMiddle - i].isEmpty()) {
				return false;
			}
		}
		
		//Minden éle a blokkoknak foglalt volt
		return true;
	}
	
	/**	Egy adott távolságban az összes blokkot átszínezi
	 * 	A középpont meghatározása után iterál a a megfelelõ távolságba lévõ nyégyzetes gyûrûn
	 * 	Minden oldalon megy végig egyszerre, tehát csak egy ciklust tartalmaz, amiben színez		
	 * 
	 * @param distanceFromMiddle Az adott távolság
	 * */
	void changeColorInLoop(int distanceFromMiddle, Color color) {
		// Közepének a meghatározása
		int MID_X = gameOffScreenBorder + gameWidth / 2;
		int MID_Y = gameOffScreenBorder + gameHeight / 2;
		// Iterálás a megfelelõ méretû négyzetes gyûrûn
		int length = distanceFromMiddle * 2 + 1;
		//Sarkokmeghatározása
		int TOP_Y   = MID_Y - distanceFromMiddle;
		int LEFT_X  = MID_X - distanceFromMiddle;
		int RIGHT_X = MID_X + distanceFromMiddle;
		int BOT_Y   = MID_Y + distanceFromMiddle;
		
		//Végigmegyünk minden oldalon egyszerre
		//Átszínezzük
		for(int i = 0; i < length; i++) {
			gameField[LEFT_X  +i][TOP_Y   ].color = color;
			gameField[RIGHT_X   ][TOP_Y +i].color = color;
			gameField[RIGHT_X -i][BOT_Y   ].color = color;
			gameField[LEFT_X    ][BOT_Y -i].color = color;
		}
	}
	
	/**	Visszaad egy kollekciót amik az összes lehetséges távolságot tartalmazza, ami a pont és az alakzat összes teli blokkja között lehet
	 * 	Az alakzat blokkjain végigiterál és ha az alakzatban az adott blokk teli, akkor a blokk gyûrû távolságát kiszámolja és ha nincs benne a végeredménybe hozzáadja
	 * 	@return Az összes olyan gyûrû távolság egy ArrayList-ben amiben az alakzat blokkjai megtalálhatóan a középponttól.
	 * */
	ArrayList<Integer> getAllBlockDistanceIn(Shape shape, Position pos){
		ArrayList<Integer> distances = new ArrayList<>();
		//Alakzat blokkjain iterálás
		for(int sxi=0; sxi<shape.getBlocksWidth(); sxi++) {
			for(int syi=0; syi<shape.getBlocksHeight(); syi++) {
				if( shape.getFullAt(sxi, syi) ) {
					//Ha az adott blokk teli csak akkor számolja
					//Az alakzat origójához (jobb felsõ sarok)
					Position actBlockPos = new Position(shape.getPosition());
					//Az iterált értékek hozzáadása
					actBlockPos.x += sxi;
					actBlockPos.y += syi;
					//Ennek a távolsága koncentrikus négyzetekkel a pos-tól
					int actDist = actBlockPos.getLoopDistanceFrom(pos);
					if( !distances.contains(actDist) ) {
						//Ha nincs benne ez a távolság
						distances.add(actDist);
					}
				}
			}
		}
		
		return distances;
	}
	
	/**	Úgymond cella autómata, ami a középpont felé viszi a lerakott blokkokat egy léptetéssel
	 * 	A középpont felé esnek eggyel beljebb azok, amik felette, alatta vagy mellette vannak.
	 * 	A sarkokból megnézi a kifelé található 3 cellát és ha azok között valamelyik teli akkor az lesz
	 * 	A benti gyûrû sarkai, az eggyel távolabb található 3 L-alakú sarokelembõl áll össze, ha ott bármelyik teli a belsõ gyûrûben is teli lesz
	 * 	Csak akkor marad üres ha mind három üres volt elõtte
	 * */
	void moveOuterBlocksCloserToMiddle(int distanceFromMiddle) {
		//Közepének a meghatározása
		int MID_X = gameOffScreenBorder + gameWidth/2;
		int MID_Y = gameOffScreenBorder + gameHeight/2;
		
		// Iterálás az összes gyûrûn az adott mérettõl kezdve
		// +1, hogy az utolsó távolságban a képernyõn kívülrõl is mozgassa az üres blokkokat befelé
		for (int dist = distanceFromMiddle; dist < gameWidth / 2 +1; dist++) {
			//System.out.println("" + dist + " tavolsagban beljebb mozgatás");

			int edgeLength = dist * 2 - 1;
			int offs = (int) Math.floor(edgeLength/2);

			// Bal felsõ sarokból a jobb felsõ sarokig átrakni a felette lévõ sorból, amik
			// nem sarok elemek
			for (int i = 0; i < edgeLength; i++) {
				//System.out.println("Offs:" + offs);
				gameField[MID_X - offs + i][MID_Y - dist] = new Block(gameField[MID_X - offs + i][MID_Y - dist - 1]);
			}
			// Jobb felsõbõl a job alsóba
			for (int i = 0; i < edgeLength; i++) {
				gameField[MID_X + dist][MID_Y - offs + i] = new Block(gameField[MID_X + dist + 1][MID_Y - offs + i]);
			}
			// Jobb alsóból a bal alsóba
			for (int i = 0; i < edgeLength; i++) {
				gameField[MID_X + offs - i][MID_Y + dist] = new Block(gameField[MID_X + offs - i][MID_Y + dist + 1]);
			}
			// Bal alsóból a bal felsõbe
			for (int i = 0; i < edgeLength; i++) {
				gameField[MID_X - dist][MID_Y + offs - i] = new Block(gameField[MID_X - dist - 1][MID_Y + offs - i]);
			}

			// Sarkok ellenõrzése
			// Bal felsõ sarok mind a három irányból
			if (gameField[MID_X - dist - 1][MID_Y - dist - 1].isFull()) {
				// Elõször az átlót nézzük meg
				gameField[MID_X - dist][MID_Y - dist] = new Block(gameField[MID_X - dist - 1][MID_Y - dist - 1]);
			} else if (gameField[MID_X - dist][MID_Y - dist - 1].isFull()) {
				// Majd a felette lévõt
				gameField[MID_X - dist][MID_Y - dist] = new Block(gameField[MID_X - dist][MID_Y - dist - 1]);
			} else if (gameField[MID_X - dist - 1][MID_Y - dist].isFull()) {
				// Majd a mellette lévõt balra
				gameField[MID_X - dist][MID_Y - dist] = new Block(gameField[MID_X - dist - 1][MID_Y - dist].isFull());
			} else {
				gameField[MID_X - dist][MID_Y - dist] = new Block(false);
			}

			// Jobb felsõ sarok
			if (gameField[MID_X + dist + 1][MID_Y - dist - 1].isFull()) {
				// Átló
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

			// Jobb alsó sarok
			if (gameField[MID_X + dist + 1][MID_Y + dist + 1].isFull()) {
				// Átló
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

			// Bal alsó sarok
			if (gameField[MID_X - dist - 1][MID_Y + dist + 1].isFull()) {
				// Átló
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
	
	/**	Blokkok kitörlése adott távolságban a középponttól
	 * 	A középpont meghatározása után, végigmegy a megfelelõ távolságban lévõ gyûrûn a bal felsõ sarokból az óramutató járásavl megfelelõen
	 * 	Az oldalakon egyesével halad végig és mindenhol a blokkok kitöltöttségét hamis-ra állítja
	 * */
	void removeBlocksInLoop(int distanceFromMiddle){
		// A blokkok törlése a teljes loop-ban
		// Közepének a meghatározása
		int MID_X = gameOffScreenBorder + gameWidth / 2;
		int MID_Y = gameOffScreenBorder + gameHeight / 2;
		// Iterálás a megfelelõ méretû négyzetes gyûrûn
		int edgeLength = distanceFromMiddle * 2 + 1;
		// Bal felsõ sarokból ellenõrizni a jobb felsõ sarokig
		for (int i = 0; i < edgeLength; i++) {
			gameField[MID_X - distanceFromMiddle + i][MID_Y - distanceFromMiddle] = new Block(false);
		}
		// Jobb felsõbõl a job alsóba
		for (int i = 0; i < edgeLength; i++) {
			gameField[MID_X + distanceFromMiddle][MID_Y - distanceFromMiddle + i] = new Block(false);
		}
		// Jobb alsóból a bal alsóba
		for (int i = 0; i < edgeLength; i++) {
			gameField[MID_X + distanceFromMiddle - i][MID_Y + distanceFromMiddle] = new Block(false);
		}
		// Bal alsóból a bel felsõbe
		for (int i = 0; i < edgeLength; i++) {
			gameField[MID_X - distanceFromMiddle][MID_Y + distanceFromMiddle - i] = new Block(false);
		}
	}
	
	/** Az adott alakzatot reprezentáló részben leellenõrzi, hogy van-e teljes gyûrû, ha van pontot hozzáadja, kitörli a blokkokat és mozgatja a többit befelé
	 * 	Csak azokat a gyûrûket ellenõrzi ahol az alakzat lehet, minden ami az alakzatban szerepel, ezek közül lehetséges, hogy nemm indegyik megfelelõ
	 * 	Miután megállapította az összes lehetséges távolságot, leellenõrzi, hogy ezekben a távolságokban van-e teljes gyûrû
	 * 	Ha teljes a gyûrû pontokat hozzáírja az eddigi ponthoz és a gyûrût üresre állítja (kitörli a blokkokat)
	 * 	A pont alapján gyorsítja a játékot
	 * 	Ezeket a teljes távolságokat összegyûjti és kívülrõl befelé haladva az összes blokkot befelé mozgatja a középpont felé, így a már üres helyekre beleeshetnek a külsõ blokkok.
	 * 	Azért iterál kívülrõl befelé, hogy a legkülsõ blokk akár többször is leeshessen addig amíg tud
	 * 	Emellett ha ezek után is van még teli gyûrû az csak azért lehet, mert egy külsõ nem teli gyûrû beljebb esve teli lett.
	 * 	Ezt a játék átszínezi fehérre, jelezve, hogy ez a gyûrû nem törölhetõ innentõl, ezzel is nehezítve a játékot
	 * */
	void scoreAllLoopIn(Shape shape) {
		//Csak azokat a gyûrûket ellenõrzi ahol az alakzat lehet, minden ami az alakzatban szerepel
		//Ezek közül lehetséges, hogy nemm indegyik megfelelõ
		ArrayList<Integer> distances = getAllBlockDistanceIn(shape, new Position(gameOffScreenBorder+gameWidth/2, gameOffScreenBorder+gameHeight/2));
		//A valóban teljes gyûrûk távolságának eltárolására
		ArrayList<Integer> fullDistances = new ArrayList<>();
		
		//Rendezés növekvõ sorrendbe
		Collections.sort(distances);
		
		for(Integer dist : distances) {
			if( isLoopFullFromMiddle(dist) ) {
				//Pontozás
				//A négyszög gyûrû mérete: oldalhosszúság *4
				//Az oldalhosszúság: távolság*2
				//Szorozva a távolsággal még egyszer
				score += dist*2 *4 * dist;
				
				//Beállítja az idõ léptetést
				//Egyre gyorsul
				millisBetweenSteps = initialMSBetweenSteps - (score / 10) * 25;
				if(millisBetweenSteps < 150) {
					millisBetweenSteps = 150;
				}
				
				//Kitörlés
				removeBlocksInLoop(dist);
				//A listához adás ahonnan kívül mozgatni kell majd
				fullDistances.add(dist);
			}
		}
		
		//Kívülrõl befelé léptetve, hogy elkerülje a hibás eseteket (egymást követõ két sor stb.)
		Collections.sort(fullDistances);
		Collections.reverse(fullDistances);
		
		for(Integer dist : fullDistances) {
			//System.out.println("A " + dist + " távolságnál nagyobbak léptetése beljebb");
			//A kitöröltek helyére belépteti a kívül esõ blokkokat
			moveOuterBlocksCloserToMiddle(dist);
		}
		
		//Itt elõfordulhat, hogy vannak teljes loopok a cella autómata miatt
		//Ha még mindég vannak azokat fehérre változtatja, hiszen azokat nem a játékos hozta létre
		//Ezáltal is nehezítve a játékot, magasabb lesz a pálya és nem ér pontot
		for(int dist = 1; dist < gameWidth/2; dist++) {
			if(isLoopFullFromMiddle(dist)) {
				changeColorInLoop(dist, Color.white);
			}
		}
	}
	
	/** Az aktuális pont gettere
	 * 	@return Az aktuális pontot
	 */
	int getScore() {
		return score;
	}
	
	/**
	 * Játék végi pont gettere
	 * @return A végleges pont
	 */
	int getFinalScore() {
		return gameOverScore;
	}
	
	/**	A függvény ami a léptetést végzi, minden idõegység alatt egyszer fut le
	 * 	Az idõzítõ ezt hívja meg, hogy "mozogjon az alakzat"
	 * 
	 * 	A feladatok amiket elvégez:
	 * 	<p>
	 * 		A játék végi jelzõ figyelembevétele, ha vége van ne csinájon semmit
	 * 		Ha nincs éppen zuhanó alakzat létrehoz és elhelyez egy újat a következõ "spawn point"-on
	 * 		Ha van éppen zuhanó alakzat, akkor megnézi, hogy következõ lépésre ütközni, fog-e
	 * 			Ha ütközik akkor kirajzolja az indikátort, hogy jelezze a pontos távolságokat, pontoz ha szükséges majd beállítja a játék végét ha kell. A zuhanó alakzatnak létrehoz egy újat.
	 * 		Ha nem fog ütközni, akkor lépteti az éppen zuhanó alakzatot eggyel beljebb a középpont felé	
	 * 	<p>
	 * */
	void step() {		
		//Játék vége ellenõrzése és 
		//Debug miatt bekerült egy plusz feltétel, hogy a Timer ne hívja meg ha még fut egy.
		if (!gameOver && stepReady) {
			
			stepReady = false;
			if (fallingShape == null) {
				//Éppen zuhanó alakzat mozgatása
				spawnNewFallingShape();
			} else {
				// Megnézni, hogy ütközni fog-e a leesõ blokk
				if (willFallingCollide()) {
					//Indikátor kirajzolása
					shapeIndicator.setVisible(fallingShape);
					
					//Ellenõrizni van-e teljes kör
					scoreAllLoopIn(fallingShape);
					// Ellenõrizni, hogy a nem látható borderben van-e blokk
					if ( checkGameOver() ) {
						return;
					}
					//Hozzon létre egy új alakzatot
					spawnNewFallingShape();
				} else {
					// Ha nem fog ütközni más blokkokkal akkor lépteti eggyel
					moveFallingShapeByGravity();
					// moveShapeTo(fallingShape, getNextPos(fallingShape, gravityPoint));
				}
			}
		}
		
		stepReady = true;
	}
	
	/**	Leellenõrzi, hogy a pozíció a látható játéktérben van-e
	 * 	Ha bekük esik igazat ad vissza
	 * 	Ha kívül esik a nem látható részre hamisat ad vissza
	 * 	@return Belül helyezkedik-e el
	 * */
	boolean isInsideGameField(Position testPos){
		if(testPos.x >= gameOffScreenBorder && testPos.x < gameOffScreenBorder+gameWidth  &&
		   testPos.y >= gameOffScreenBorder && testPos.y < gameOffScreenBorder+gameHeight ) {
			return true;
		}
		
		return false;
	}
	
	/**	A játék végének ellenõrzése. Az éppen zuhanó alakzatra megnézi, hogy hol helyezkedik el
	 * 	Csak akkor ellenõrzi ha van az alakzatnak olyan blokkja ami kívül eshet a játéktéren
	 * 	Végigiterál az alakzat blokkjain, ha talál telit akkor leellenõrzi, hogy a játéktéren van-e vagy sem
	 * 	Ha nincs a játéktéren teli blokkja az alakzatnak, akkor a játék véget ér
	 * 	Beállítja a végsõ pontot
	 * */
	boolean checkGameOver() {
		//Csak akkor ellenõrizze a játék végét ha kívül esik a határokon
		if(	fallingShape.getPosition().x < gameOffScreenBorder || 
			fallingShape.getPosition().y < gameOffScreenBorder ||
			fallingShape.getPosition().x+fallingShape.getBlocksWidth()  > gameWidth+gameOffScreenBorder ||
			fallingShape.getPosition().y+fallingShape.getBlocksHeight() > gameHeight+gameOffScreenBorder)
		{
			Position testBlockPos = new Position();
			//Alakzat blokkjain való iterálás
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
	
	/**	A végsõ pont beállítása
	 * */
	void setFinalScore() {
		gameOverScore = score;
	}
	
	/**	Megnézi, hogy a következõ lépésre a ütközik-e az éppen esõ alakzat alakzat bármivel
	 * 	Megállapítja az aktuális alakzat következõ helyét
	 * 	Ha eléri a közepét és nem tud mozogni, akkor is álljon meg a zuhanás
	 * 	Leellenõrzi, hogy a következõ pozícióban érintkezik-e más blokkokkal
	 * 
	 * 	@return Lesz-e érintkezés ha mozgatás történik
	 * */
	boolean willFallingCollide() {
		boolean colliding = false;
		
		if(fallingShape != null) {
			Position futurePos = getNextPosByGravity(fallingShape, gravityPoint);
			
			//Ha eléri a közepét és nem tud mozogni akkor is álljon meg a zuhanás
			if(futurePos.equals(fallingShape.getPosition())) {
				return true;
			}
			//Érintkezés ellenõrzés egy adott pontban
			colliding = willShapeCollideAt(fallingShape, futurePos);
		}
		
		return colliding;
	}
	
	/**	Végignézi az alakzat minden blokkjára, hogy érinntkezni, fog-e más blokkal a megadott helyen
	 * 	Itt még nem történik semmilyen mozgatás csak ellenõrzés
	 * 	Egyesével megnézni, hogy az alakzatnak van-e kitöltött blokkja ami érintkezne mással
	 * 	Ha az esés elõtti formának nincsen ott blokkja és az esés után van kitöltött blokkja csak akkor történik meg az ellenõrzés, hogy abban a pozícióban van-e teli blokk a játéktéren
	 * 
	 * 	@param pos A megadott hely, ahol az alakzatra ellenõrizni kell
	 * */
	boolean willShapeCollideAt(Shape shape, Position pos) {
		boolean colliding = false;
		//Egyesével megnézni, hogy az alakzatnak van-e olyan blokkja ami érintkezne mással
		for(int sxi = 0; !colliding && sxi < shape.getBlocksWidth(); sxi++) {
			for(int syi = 0; !colliding && syi < shape.getBlocksHeight(); syi++){
				Position oneBlockPos = new Position(pos.x+sxi, pos.y+syi);
				//Ha az esés elõtti formának nincsenek ott blokkjai és a formának van kitöltött blokkja
				if( !shape.isPresentAt(oneBlockPos) && shape.getFullAt(sxi, syi) ){
					colliding = gameField[oneBlockPos.x][oneBlockPos.y].full;
				}
			}
		}
		return colliding;
	}
	
	/**	Zuhanó alakzat mozgatása
	 * 	Kitöli az eredeti helyén lévõt, majd elhelyezi az új helyre, ami közelebb van a gravitációs ponthoz
	 * */
	void moveFallingShapeByGravity() {
		clearInShape(gameField, fallingShape);
		moveShapeTo(fallingShape, getNextPosByGravity(fallingShape, gravityPoint));
	}
	
	/**	A felhasználó által mozgatható irányok valamelyikébe mozgatás
	 * 	Fontos, hogy csak a gravitációra merõlegesen mozgatható
	 * */
	void moveFallingShapeByDir(Dir dir) {
		clearInShape(gameField, fallingShape);
		moveShapeTo(fallingShape, getNextMovePos(fallingShape, dir));
	}
	
	/**	Kitörli az alakzat blokkjait a játékmezõrõl
	 * 	Végigiterál a blokkokon és beállítja a mezõket a játéktéren, hogy ne legyenek kitöltöttek, ott ahol az alakzatnak vannak kitöltött blokkjai
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
	
	/**	Zuhanó alakzat forgatása
	 * 	Leellenõrzi, hogy okozna-e a forgatás ütközést
	 * 	Ha nem okoz ütközést kitörli az aktuális alakzatot a játéktérrõl és mozgatja az új elforgatott alakzatot az eredeti helyére
	 * 
	 * 	@return Történt-e forgatás
	 * */
	boolean rotateFallingShape(){
		if(fallingShape == null)
			return false;
		
		//Ellenõrizni, hogy nem okoz-e érintkezést
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
	
	/**	A felhasználó általi mozgatás ellenõrzése, a gravitációra merõlegesen mozgatható
	 * 	Elõször ellenõrzi, hogy a gravitáció irányára merõlegesen vagy annak irányába akarjuk mozgatni
	 * 	Ha nem abba az irányba akarjuk mozgatni akkor visszatér hamissal és nem mozgatja
	 * 	Ha megfelelõ a mozgatás iránya, akkor leellenõrzi, hogy ütközne-e az alakzat
	 * 	Ha nincs ütközés átmozgatja az alakzatot és igazzal tér vissza
	 * 
	 * 	@return Megtörtént-e a mozgatás
	 * */
	boolean moveFallingShape(Dir dir) {
		if(fallingShape == null)
			return false;
		
		//Ellenõrizni, hogy az irányvektorra merõlegesen mozgatja-e
		Position direction = new Position ( gravityPoint.x - (fallingShape.getPosition().x + fallingShape.width/2), gravityPoint.y - (fallingShape.getPosition().y + fallingShape.height/2) );
		direction.eightDirNormalize();
		Dir gravityDir = direction.transformToDirection();
		//Filterezés a gravitáció irányának megfelelõen, maximum két irányba lehet mozgatni
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
		
		//Amennyiben jó irányba akarjuk mozgatni ide jutunk
		//Le kell ellenõrizni, hogy lehet-e mozgatni, nincs ütközés
		Position futurePos = getNextMovePos(fallingShape, dir);	
		if( willShapeCollideAt(fallingShape, futurePos) ){
			return false;
		}
		
		//Nincs ütközés
		//Mozgatjuk a blokkot arra a pozícióra
		moveFallingShapeByDir(dir);
		
		return true;
	}
	
	/**	Megadja az alakzat következõ helyét a gravitációs ponthoz közeledve
	 * @param gPoint Gravitációs pont
	 * */
	Position getNextPosByGravity(Shape shape, Position gPoint) {
		Position direction = new Position ( gPoint.x - (shape.getPosition().x + shape.width/2), gPoint.y - (shape.getPosition().y + shape.height/2) );
		direction.eightDirNormalize();
		return new Position(shape.getPosition().x + direction.x, shape.getPosition().y + direction.y);
	}
	
	/**	Megadja az adott pozícióban a gravitáció irányát a gravitációs pont felé
	 * @param gPoint Gravitációs pont
	 * */
	Position getGravityVector(Position blockPos, Position gPoint) {
		Position direction = new Position( gPoint.x - blockPos.x, gPoint.y - blockPos.y );
		//System.out.println("Direction:" + direction);
		direction.eightDirNormalize();
		//System.out.println("Normalized:" + direction);
		return direction;
	}
	
	/**	Alakzat mozgatása billentyûzettel
	 * 	Csak négy irányba lehet mozgatni: fel, le jobbra, balra
	 * 
	 * 	@return A pozíció ahová kerülni fog az alakzat a mozgatás után
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
	
	/**	Az alap alakzatok létrhozása, mindegyikbõl egyet
	 * 	A Tetris alap hét formáját hozza létre és elhelyezi õket a számukra kijelölt tömbbe
	 * 
	 * 	@implNote Ez az osztály a további egyedi formák egyszerû felvételét szolgálja a játéklogikába
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
	
	/**	Az alakzatok méretének a megállapításához, dinamikusan generálódik a pélya széle, ez az ami meghatározza mekkora legyen
	 * 	@return visszaadja a maximum szélességet/magasságot, ami a legnagyobb a létrehozott alap alakzatok között
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
	
	/**	A gravitációs indikátor gettere
	 * 	A gravitációs indikátor megmutatja melyik irányba fog esni az alakzat
	 * */
	TriangleIndicator getGravityIndicator() {
		return gravityIndicator;
	}
	
	/**	A gravitáció jelzõ beállítása
	 * 	Alakzat mozgatáûsa után meghívódik, így beállítja az aktuális esési irányt
	 * 
	 * 	Lekéri a gravitáció irányát
	 * 	Egy leíróból meghatározza melyik blokkra kell rajzolnia. Ez mindég az alakzat mellett/alatt/felett/sarkaiban jelenik meg az esési iránytõl függõen
	 * 	A kapott gravitációs indikátort pedig beállítja ennek megfelelõen
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
				//Balra felfelé
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
				//Balra lefelé
				posToDraw.x -= 1;
				posToDraw.y += fallingShape.height;
				dir = Dir.LEFT_DOWN;
				break;
			}
			break;
		case 0:
			//Nincs oldalra mozgás
			switch(gravityVector.y) {
			case -1:
				//Felfelé
				posToDraw.x += fallingShape.width/2;
				posToDraw.y -= 1;
				dir = Dir.UP;
				break;
			case 1:
				//Lefelé
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
				//Jobbra felfelé
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
				//Jobbra lefelé
				posToDraw.x += fallingShape.width;
				posToDraw.y += fallingShape.height;
				dir = Dir.RIGHT_DOWN;
				break;
			}
			break;
		}
		
		//System.out.println("Gravitációs indikátor helye: " + posToDraw + " , iránya: " + dir.name());
		
		indic.set(posToDraw.x, posToDraw.y, dir);		
	}
	
	

	/**	Játéktér x és y helyén lévõ mezõ telítettségének gettere
	 * 	A nem látható mezõket nem veszi figyelembe az elsõ látható mezõ (bal felsõ, lesz a x:0 és y:0)
	 * 
	 * @return Egy logikai érték ami az adott mezõ kitöltöttsége
	 * */
	public boolean isFilled(int x, int y) {
		return gameField[gameOffScreenBorder+x][gameOffScreenBorder+y].isFull();
	}
	
	/**	Játéktér x és y helyén lévõ mezõ színének gettere
	 * 	A nem látható mezõket nem veszi figyelembe az elsõ látható mezõ (bal felsõ, lesz a x:0 és y:0)
	 * 
	 * 	@return Egy java.awt.Color típust ad vissza ami az adott blokknak a színe
	 * */
	public Color getBlockColorAt(int x, int y) {
		return gameField[gameOffScreenBorder+x][gameOffScreenBorder+y].getColor();
	}
}
