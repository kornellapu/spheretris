import javax.swing.JFrame;
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

/** Az ablakkezelõ, egy ablakos alkalmazásnál ez az osztály kezeli a megjelenítendõ elemeket.
 *
 **/
public class WindowHandler {
	private final JFrame mainFrame;
	private final PlayFieldComponent playField;
	private final MenuComponent menu;
	private final GameOverComponent gameOver;
	private final ScoresComponent scores;
	private Timer repaintTimer;
	String windowName = "Spheretris";
	Dimension windowDimension;
	
	final GameLogic gl;
	final GameStateMachine gsm;
	final ScoreFileHandler scrFh;
	
	Font customFont = null;
	
	public WindowHandler(GameLogic gl, GameStateMachine gsm, ScoreFileHandler scrFh){
		this.gl = gl;
		this.gsm = gsm;
		this.scrFh = scrFh;
		
		playField = new PlayFieldComponent(gl);
		windowDimension = playField.getPreferredSize();
		
		mainFrame = new JFrame(windowName);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(false);
		
		//Új saját betûtípus regisztrálása
		try {
			//Létrehozás fájlból
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Spheretris.ttf"));
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
	
	boolean setState(GameStateMachine.State state) {
		if(gsm.setState(state)) {
			setComponenstTo(gsm.getAct());
			return true;
		}
		
		return false;
	}
	
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
					//Ha vége a játéknak ne érzékelje a gombnyomásokat
					if(gl.gameOver)
						return false;
					
					int keyState = e.getID();
					switch(keyState) {
						case KeyEvent.KEY_PRESSED:
							boolean success = false;
							if( e.getKeyCode() == KeyEvent.VK_DOWN ) {
								success = gl.moveFallingShape(GameLogic.Dir.DOWN);
							}
							else if( e.getKeyCode() == KeyEvent.VK_UP ) {
								success = gl.moveFallingShape(GameLogic.Dir.UP); 
							}
							else if( e.getKeyCode() == KeyEvent.VK_LEFT ) {
								success = gl.moveFallingShape(GameLogic.Dir.LEFT); 
							}
							else if( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
								success = gl.moveFallingShape(GameLogic.Dir.RIGHT); 
							}
							else if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
								success = gl.rotateFallingShape();
							}
							else if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
								//Eröltetett kiléptetés
								gl.gameOver = true;
								//Végsõ pont beállítása, hogy ne vesszen el
								gl.setFinalScore();
							}
							else if( e.getKeyCode() == KeyEvent.VK_R ) {
								repaintTimer.cancel();
								gl.initNewGame();
								setComponenstTo(GameStateMachine.State.GAME);
							}
							if(success) {
								//Teljes komponens újrarajzolása
								playField.repaint(new Rectangle(playField.getWidth(), playField.getHeight()));
								return true;
							}
							break;
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
			
			if(gl.gameOver) {
				this.cancel();
				
				//Várakozás mieõtt kijelezzük a pontot
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
