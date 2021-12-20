package gui;
import javax.swing.JFrame;

import fileio.ScoreFileHandler;
import logic.GameLogic;
import logic.GameStateMachine;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.TimerTask;
import java.util.Timer;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;

/** Az ablakkezelõ, ebben az egy ablakos alkalmazásnál ez az osztály kezeli a megjelenítendõ elemeket.
 *	Tartalmazza és összefogja a különözõ képernyõket és azok közös beállításait tárolja.
 *	Referenciát tárol a játéklogikára, az állapotgépre, a pont fájlkezelõ osztályára, újrafesti az elemeket (ami egyben lépteti is a játékot), saját font-ot itt tárolódik
 **/
public class WindowHandler {
	/** Referencia az alkalmazás ablakára */
	private final JFrame mainFrame;
	/** A játék képernyõ osztály tárolása */
	private final PlayFieldComponent playField;
	/** A menü képernyõ tárolása */
	private final MenuComponent menu;
	/** A játék vége képernyõ tárolása */
	private final GameOverComponent gameOver;
	/** A pontok képernyõ tárolása */
	private final ScoresComponent scores;
	/** Egy idõzítõ, ami fix idõközönként újra rajzolja, lépteti a játékot */
	private Timer repaintTimer;
	/** A megjelenõ név az ablak fejlécében */
	String windowName = "Spheretris";
	/** Az ablak mérete */
	Dimension windowDimension;
	
	/** Referencia a játék logikájára*/
	final GameLogic gl;
	/** Referencia az állapotgépre */
	final GameStateMachine gsm;
	/** Referencia a pontkezelõre */
	final ScoreFileHandler scrFh;
	
	/** Refrencia a közösen használt saját font-ra
	 * 	Azért itt tárolódik, hogy ne kelljen mindenhol máshol ugyanezt a fontot létrehozni*/
	Font customFont = null;
	
	/** Konstruktor, ami beállítja a mezõk alap értékeit 
	 * 	Regisztrálja az új betûtípust és beállítja a változót, hogy késõbb könnyebb legyen használni
	 * 	Komponensek konstruktorának hívása
	 * 
	 * 	Az állapotgéptõl lekérdezi melyik komponenst kell megjeleníteni és anak elemeit adja hozzá az ablakhoz
	 * 	Középre helyezi és láthatóvá teszi
	 * 
	 * @param gl Már létezõ játéklogika átadása
	 * @param gsm Már létezõ állapotgép átadása
	 * @param scrFh Már létezõ pontkezelõ átadása*/
	public WindowHandler(GameLogic gl, GameStateMachine gsm, ScoreFileHandler scrFh){
		this.gl = gl;
		this.gsm = gsm;
		this.scrFh = scrFh;
		
		playField = new PlayFieldComponent(gl);
		windowDimension = playField.getPreferredSize();
		
		mainFrame = new JFrame(windowName);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(false);
		
		Image icon = Toolkit.getDefaultToolkit().getImage("resources/SpheretrisIcon.png");  
		mainFrame.setIconImage(icon);  
		
		//Új saját betûtípus regisztrálása
		try {
			//Létrehozás fájlból
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Spheretris.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			//Regisztrálás
			ge.registerFont(customFont);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		menu = new MenuComponent(this, customFont);
		gameOver = new GameOverComponent(this, customFont, scrFh);
		scores = new ScoresComponent(this, customFont, scrFh);
		
		//Állapotgépnek megfelelõ komponensek hozzáadása
		setComponenstTo(gsm.getAct());
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
	
	/** Beállítja az állapotot az állapotgépben
	 * 	Olyan esetekre, ha az állapotgép nem elérhetõ, de a windowHandler igen az adott osztályból
	 * 	
	 * 	@param state Amilyen állapotra az állapotgépet be kell állítani
	 *  
	 * 	@return Logikai érték, be tudta állítani vagy sem*/
	boolean setState(GameStateMachine.State state) {
		if(gsm.setState(state)) {
			setComponenstTo(gsm.getAct());
			return true;
		}
		
		return false;
	}
	
	/** Beállítja a megfelelõ képernyõt, attõl függõen ,hogy paraméternek milyen állapotott kapott
	 * 	A négy állapot közül a megfelelõt állítja be elvégzi a szükséges egyéb feladatokat
	 * 	Mindegyik váltás kitörli az összes elõtte lévõ komponenst az ablakból
	 * 
	 *  MENU: Új játék inicializálása
	 *  GAME: A játék újrarajzolásának beállítása, billentyûzet események kezelése (alakzat mozgatás)
	 *  GAMEOVER: Kitörli hozzáadja a saját komponenseit
	 *  SCORES: Kitörli az elõzõ komponenseket és hozááadja a sajátjait
	 *  
	 *  @param state Az állapot, ami alapján beállítja a képernyõket
	 *  */
	private void setComponenstTo(GameStateMachine.State state) {
		if(state == GameStateMachine.State.MENU) {
			//Az elõzõ komponensek kitörlése
			mainFrame.getContentPane().removeAll();
			
			//Új játék beállítása
			gl.initNewGame();
			//Az ablak nevének beállítása
			mainFrame.setTitle(windowName);
			
			//Menu componens hozzáadása
			menu.addComponentsTo(mainFrame);
		}
		else if(state == GameStateMachine.State.GAME) {
			//Az elõzõ komponensek kitörlése
			mainFrame.getContentPane().removeAll();
			
			mainFrame.add(playField, BorderLayout.CENTER);
			
			mainFrame.pack();
			
			repaintTimer = new Timer();
			repaintTimer.scheduleAtFixedRate( new RepaintTask(), 0, gl.getStepTime());
			
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( 
				(KeyEvent e) -> {
					//System.out.println("Got key event! KeyEvent:" + e);
					int keyState = e.getID();
					switch(keyState) {
						case KeyEvent.KEY_PRESSED:
							if (gsm.getAct() == GameStateMachine.State.GAME) {
								boolean success = false;
								if (e.getKeyCode() == KeyEvent.VK_DOWN) {
									success = gl.moveFallingShape(GameLogic.Dir.DOWN);
								} else if (e.getKeyCode() == KeyEvent.VK_UP) {
									success = gl.moveFallingShape(GameLogic.Dir.UP);
								} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
									success = gl.moveFallingShape(GameLogic.Dir.LEFT);
								} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
									success = gl.moveFallingShape(GameLogic.Dir.RIGHT);
								} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
									success = gl.rotateFallingShape();
								} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
									// Eröltetett kiléptetés
									gl.setGameOver(true);
									// Végsõ pont beállítása, hogy ne vesszen el
									gl.setFinalScore();
								} else if (e.getKeyCode() == KeyEvent.VK_R) {
									repaintTimer.cancel();
									gl.initNewGame();
									setComponenstTo(GameStateMachine.State.GAME);
								}
								if (success) {
									// Teljes komponens újrarajzolása
									playField.repaint(new Rectangle(playField.getWidth(), playField.getHeight()));
									return true;
								}
								break;
							}
						default:
							
					}
					return false;
				});
		}
		else if(state == GameStateMachine.State.GAMEOVER) {
			//Az elõzõ komponensek kitörlése
			mainFrame.getContentPane().removeAll();
			
			//Menu componens hozzáadása
			gameOver.addComponentsTo(mainFrame);
		}
		else if(state == GameStateMachine.State.SCORES) {
			//Az elõzõ komponensek kitörlése
			mainFrame.getContentPane().removeAll();
			
			//Menu componens hozzáadása
			scores.addComponentsTo(mainFrame);
		}
	}
	
	/**	Az újrarajzolás idõzítõje, ami fix idõpontokban újrarajzolja a játék képernyõ tartalmát
	 * 	A repaint meghívja a komponens paint-jét, amiben egy steppel lépteti a szükséges dolgokat majd a frissített állapotot kirajzolja
	 * 	Ha nem nulla a pont a fejlécben ezt kijelzi
	 * 	Ha történt a pontértékben változás, akkor gyorsítja a kiralyzolást, gyorsabb lesz a játék
	 * 	Ha vége van a játéknak akkor ezt az idõzítõt nem befejezi, nem hívja meg többet és beállítja a GAMEOVER képernyõt és állapotot
	 * */
	class RepaintTask extends TimerTask {
		public void run() {
			int beforeScore = gl.getScore();
			
			gl.step();
			playField.repaint(new Rectangle(playField.getWidth(), playField.getHeight()));
			
			int actScore = gl.getScore();
			if(actScore > 0) {
				mainFrame.setTitle(windowName + " - Score: " + actScore + "pt" + (actScore>1 ? "s" : ""));
			}
			
			if(beforeScore != actScore) {
				this.cancel();
				repaintTimer.scheduleAtFixedRate( new RepaintTask(), 0, gl.getStepTime());
				//System.out.println("Refresh rate at: " + gl.getStepTime());
			}
			
			if(gl.isGameOver()) {
				this.cancel();
				
				//Várakozás mielõtt kijelezzük a pontot
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setState(GameStateMachine.State.GAMEOVER);
			}
		}
	}
}
