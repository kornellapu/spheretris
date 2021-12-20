import fileio.ScoreFileHandler;
import gui.WindowHandler;
import logic.GameLogic;
import logic.GameStateMachine;

public class Main {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		final GameLogic gl = new GameLogic();
		final GameStateMachine gsm = new GameStateMachine();
		final ScoreFileHandler scrFh = new ScoreFileHandler();
		WindowHandler mainWindowHandler = new WindowHandler(gl, gsm, scrFh);
	}

}
