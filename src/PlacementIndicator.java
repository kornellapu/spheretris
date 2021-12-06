import java.util.ArrayList;
import java.util.Collections;

public class PlacementIndicator {
	int outerDist = 0;
	int innerDist = 0;
	int lifeTime = 5;
	int currentLife = 0;
	
	final GameLogic gl;
	
	PlacementIndicator(GameLogic gameLogic){
		gl = gameLogic;
	}
	
	int getOpac(int decreaseLevel){
		double decreasedActual = currentLife - decreaseLevel;
		decreasedActual = decreasedActual < 0 ? 0 : decreasedActual;
		double ratio = decreasedActual/((double)(lifeTime)); 
		double maximumAlpha = 150;
		return (int)(maximumAlpha * ratio);
	}
	
	void setVisible(Shape shape) {
		if (shape == null)
			return;
		
		currentLife = lifeTime;
		ArrayList<Integer> distances = gl.getAllBlockDistanceIn(shape, new Position(GameLogic.gameOffScreenBorder+gl.gameWidth/2, GameLogic.gameOffScreenBorder+gl.gameHeight/2));
		Collections.sort(distances);
		innerDist = distances.get(0) -1;
		Collections.reverse(distances);
		outerDist = distances.get(0);		
	}
	
	int getInnerDist() {
		return innerDist;
	}
	
	int getOuterDist() {
		return outerDist;
	}
	
	int getLife() {
		return currentLife;
	}
	
	void decreaseLife() {
		if(currentLife > 0) {
			currentLife--;
		}
	}
}
