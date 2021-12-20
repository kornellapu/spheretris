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

/** Az ablakkezel�, ebben az egy ablakos alkalmaz�sn�l ez az oszt�ly kezeli a megjelen�tend� elemeket.
 *	Tartalmazza �s �sszefogja a k�l�n�z� k�perny�ket �s azok k�z�s be�ll�t�sait t�rolja.
 *	Referenci�t t�rol a j�t�klogik�ra, az �llapotg�pre, a pont f�jlkezel� oszt�ly�ra, �jrafesti az elemeket (ami egyben l�pteti is a j�t�kot), saj�t font-ot itt t�rol�dik
 **/
public class WindowHandler {
	/** Referencia az alkalmaz�s ablak�ra */
	private final JFrame mainFrame;
	/** A j�t�k k�perny� oszt�ly t�rol�sa */
	private final PlayFieldComponent playField;
	/** A men� k�perny� t�rol�sa */
	private final MenuComponent menu;
	/** A j�t�k v�ge k�perny� t�rol�sa */
	private final GameOverComponent gameOver;
	/** A pontok k�perny� t�rol�sa */
	private final ScoresComponent scores;
	/** Egy id�z�t�, ami fix id�k�z�nk�nt �jra rajzolja, l�pteti a j�t�kot */
	private Timer repaintTimer;
	/** A megjelen� n�v az ablak fejl�c�ben */
	String windowName = "Spheretris";
	/** Az ablak m�rete */
	Dimension windowDimension;
	
	/** Referencia a j�t�k logik�j�ra*/
	final GameLogic gl;
	/** Referencia az �llapotg�pre */
	final GameStateMachine gsm;
	/** Referencia a pontkezel�re */
	final ScoreFileHandler scrFh;
	
	/** Refrencia a k�z�sen haszn�lt saj�t font-ra
	 * 	Az�rt itt t�rol�dik, hogy ne kelljen mindenhol m�shol ugyanezt a fontot l�trehozni*/
	Font customFont = null;
	
	/** Konstruktor, ami be�ll�tja a mez�k alap �rt�keit 
	 * 	Regisztr�lja az �j bet�t�pust �s be�ll�tja a v�ltoz�t, hogy k�s�bb k�nnyebb legyen haszn�lni
	 * 	Komponensek konstruktor�nak h�v�sa
	 * 
	 * 	Az �llapotg�pt�l lek�rdezi melyik komponenst kell megjelen�teni �s anak elemeit adja hozz� az ablakhoz
	 * 	K�z�pre helyezi �s l�that�v� teszi
	 * 
	 * @param gl M�r l�tez� j�t�klogika �tad�sa
	 * @param gsm M�r l�tez� �llapotg�p �tad�sa
	 * @param scrFh M�r l�tez� pontkezel� �tad�sa*/
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
		
		//�j saj�t bet�t�pus regisztr�l�sa
		try {
			//L�trehoz�s f�jlb�l
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Spheretris.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			//Regisztr�l�s
			ge.registerFont(customFont);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		menu = new MenuComponent(this, customFont);
		gameOver = new GameOverComponent(this, customFont, scrFh);
		scores = new ScoresComponent(this, customFont, scrFh);
		
		//�llapotg�pnek megfelel� komponensek hozz�ad�sa
		setComponenstTo(gsm.getAct());
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
	
	/** Be�ll�tja az �llapotot az �llapotg�pben
	 * 	Olyan esetekre, ha az �llapotg�p nem el�rhet�, de a windowHandler igen az adott oszt�lyb�l
	 * 	
	 * 	@param state Amilyen �llapotra az �llapotg�pet be kell �ll�tani
	 *  
	 * 	@return Logikai �rt�k, be tudta �ll�tani vagy sem*/
	boolean setState(GameStateMachine.State state) {
		if(gsm.setState(state)) {
			setComponenstTo(gsm.getAct());
			return true;
		}
		
		return false;
	}
	
	/** Be�ll�tja a megfelel� k�perny�t, att�l f�gg�en ,hogy param�ternek milyen �llapotott kapott
	 * 	A n�gy �llapot k�z�l a megfelel�t �ll�tja be elv�gzi a sz�ks�ges egy�b feladatokat
	 * 	Mindegyik v�lt�s kit�rli az �sszes el�tte l�v� komponenst az ablakb�l
	 * 
	 *  MENU: �j j�t�k inicializ�l�sa
	 *  GAME: A j�t�k �jrarajzol�s�nak be�ll�t�sa, billenty�zet esem�nyek kezel�se (alakzat mozgat�s)
	 *  GAMEOVER: Kit�rli hozz�adja a saj�t komponenseit
	 *  SCORES: Kit�rli az el�z� komponenseket �s hoz��adja a saj�tjait
	 *  
	 *  @param state Az �llapot, ami alapj�n be�ll�tja a k�perny�ket
	 *  */
	private void setComponenstTo(GameStateMachine.State state) {
		if(state == GameStateMachine.State.MENU) {
			//Az el�z� komponensek kit�rl�se
			mainFrame.getContentPane().removeAll();
			
			//�j j�t�k be�ll�t�sa
			gl.initNewGame();
			//Az ablak nev�nek be�ll�t�sa
			mainFrame.setTitle(windowName);
			
			//Menu componens hozz�ad�sa
			menu.addComponentsTo(mainFrame);
		}
		else if(state == GameStateMachine.State.GAME) {
			//Az el�z� komponensek kit�rl�se
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
									// Er�ltetett kil�ptet�s
									gl.setGameOver(true);
									// V�gs� pont be�ll�t�sa, hogy ne vesszen el
									gl.setFinalScore();
								} else if (e.getKeyCode() == KeyEvent.VK_R) {
									repaintTimer.cancel();
									gl.initNewGame();
									setComponenstTo(GameStateMachine.State.GAME);
								}
								if (success) {
									// Teljes komponens �jrarajzol�sa
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
			//Az el�z� komponensek kit�rl�se
			mainFrame.getContentPane().removeAll();
			
			//Menu componens hozz�ad�sa
			gameOver.addComponentsTo(mainFrame);
		}
		else if(state == GameStateMachine.State.SCORES) {
			//Az el�z� komponensek kit�rl�se
			mainFrame.getContentPane().removeAll();
			
			//Menu componens hozz�ad�sa
			scores.addComponentsTo(mainFrame);
		}
	}
	
	/**	Az �jrarajzol�s id�z�t�je, ami fix id�pontokban �jrarajzolja a j�t�k k�perny� tartalm�t
	 * 	A repaint megh�vja a komponens paint-j�t, amiben egy steppel l�pteti a sz�ks�ges dolgokat majd a friss�tett �llapotot kirajzolja
	 * 	Ha nem nulla a pont a fejl�cben ezt kijelzi
	 * 	Ha t�rt�nt a pont�rt�kben v�ltoz�s, akkor gyors�tja a kiralyzol�st, gyorsabb lesz a j�t�k
	 * 	Ha v�ge van a j�t�knak akkor ezt az id�z�t�t nem befejezi, nem h�vja meg t�bbet �s be�ll�tja a GAMEOVER k�perny�t �s �llapotot
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
				
				//V�rakoz�s miel�tt kijelezz�k a pontot
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
