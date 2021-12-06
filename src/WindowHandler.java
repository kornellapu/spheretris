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

/** Az ablakkezel�, egy ablakos alkalmaz�sn�l ez az oszt�ly kezeli a megjelen�tend� elemeket.
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
		
		//�j saj�t bet�t�pus regisztr�l�sa
		try {
			//L�trehoz�s f�jlb�l
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Spheretris.ttf"));
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
	
	boolean setState(GameStateMachine.State state) {
		if(gsm.setState(state)) {
			setComponenstTo(gsm.getAct());
			return true;
		}
		
		return false;
	}
	
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
					//Ha v�ge a j�t�knak ne �rz�kelje a gombnyom�sokat
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
								//Er�ltetett kil�ptet�s
								gl.gameOver = true;
								//V�gs� pont be�ll�t�sa, hogy ne vesszen el
								gl.setFinalScore();
							}
							else if( e.getKeyCode() == KeyEvent.VK_R ) {
								repaintTimer.cancel();
								gl.initNewGame();
								setComponenstTo(GameStateMachine.State.GAME);
							}
							if(success) {
								//Teljes komponens �jrarajzol�sa
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
				
				//V�rakoz�s mie�tt kijelezz�k a pontot
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
