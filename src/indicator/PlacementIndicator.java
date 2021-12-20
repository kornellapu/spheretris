package indicator;
import java.util.ArrayList;
import java.util.Collections;

import basic.Position;
import basic.Shape;
import logic.GameLogic;

/**	A lees� alakzathoz tartoz� plusz inform�ci�t jel�l� indik�tor
 * 	Amikor megjelenik seg�t a felhaszn�l�nak, hogy l�that� legyen hol kell m�g kit�lteni, hogy teljes n�gyzetes gy�r�t kapjon
 * 	A j�t�k l�ptet�seivel egyre jobban elhalv�nyul �s v�g�l elt�nik
 * */
public class PlacementIndicator {
	/**	A k�z�ppontt�l vett t�vols�g ahol ki kell jelezni m�g az indik�tort
	 * */
	int outerDist = 0;
	/**	A k�z�pponthoz legk�zelebb es� t�vols�g, ahol meg kell jelen�teni az indik�tort
	 * */
	int innerDist = 0;
	/**	Az �lettartama, ameddig l�that� lesz egy r�sze is
	 * */
	int lifeTime = 10;
	/**	Az aktu�lis �lettartama, ami folyamatosan cs�kken
	 * */
	int currentLife = 0;
	
	/**	Referencia a j�t�klogik�ra, hogy lek�rdezhesse az aktu�lis �rt�keket az alakzatr�l
	 * */
	final GameLogic gl;
	
	/**	Konstruktor, ami be�ll�tja a j�t�k logika referenci�j�t
	 * 	
	 * 	@param gameLogic Referencia a j�t�klogika elt�rol�s�hoz
	 * */
	public PlacementIndicator(GameLogic gameLogic){
		gl = gameLogic;
	}
	
	/**	Visszaad egy �rt�ket 0 �s 255 k�z�tt att�l f�gg�en, hogy mekkora az aktu�lis �lettratama �s mekkora a param�terben magadott cs�kkent�s m�rt�ke
	 * 	Ha cs�kkent�s m�rt�ke nagyobb, mint az aktu�lis �lettartam �rt�k, akkor 0-t ad vissza
	 * 	@param decreaseLevel Cs�kkenti egy szinttel a  visszaadott �rt�ket ar�nyosan az �lettartalomhoz k�pest.
	 * 	@return 0 �s 255 k�z�tti �rt�k, ami az "alpha" �rt�k egy sz�nhez
	 * */
	public int getOpac(int decreaseLevel){
		double decreasedActual = currentLife - decreaseLevel;
		decreasedActual = decreasedActual < 0 ? 0 : decreasedActual;
		double ratio = decreasedActual/((double)(lifeTime)); 
		double maximumAlpha = 150;
		return (int)(maximumAlpha * ratio);
	}
	
	/**	Be�ll�tja a param�ter�l kapott alakzathoz a bels� �s k�ls� t�vols�gokat
	 *	Be�ll�tja az aktu�lis �lettartamot, a maximum �rt�kre 
	 *
	 *	@param shape Az alakzat ami k�r� fogja rajzolni az indik�tort
	 * */
	public void setVisible(Shape shape) {
		if (shape == null)
			return;
		
		currentLife = lifeTime;
		ArrayList<Integer> distances = gl.getAllBlockDistanceIn(shape, new Position(gl.getOffScreenBorder()+gl.gameWidth/2, gl.getOffScreenBorder()+gl.gameHeight/2));
		Collections.sort(distances);
		innerDist = distances.get(0) -1;
		Collections.reverse(distances);
		outerDist = distances.get(0);		
	}
	
	/**	Bels� t�vols�g gettere
	 * 	@return Bels� t�vols�g
	 * */
	public int getInnerDist() {
		return innerDist;
	}
	
	/**	K�ls� t�vols�g gettere
	 * 	@return K�ls� t�vols�g
	 * */
	public int getOuterDist() {
		return outerDist;
	}
	
	/**	Az aktu�lis �lettartam gettere
	 * 	@return Az aktu�lis �let 
	 * */
	int getLife() {
		return currentLife;
	}
	
	/**	Az aktu�lis �lettartam cs�kkent�se, csak akkor cs�kkenti, ha null�n�l t�bb az aktu�lis �let
	 * */
	public void decreaseLife() {
		if(currentLife > 0) {
			currentLife--;
		}
	}
}
