package indicator;
import java.util.ArrayList;
import java.util.Collections;

import basic.Position;
import basic.Shape;
import logic.GameLogic;

/**	A leesõ alakzathoz tartozó plusz információt jelölõ indikátor
 * 	Amikor megjelenik segít a felhasználónak, hogy látható legyen hol kell még kitölteni, hogy teljes négyzetes gyûrût kapjon
 * 	A játék léptetéseivel egyre jobban elhalványul és végül eltûnik
 * */
public class PlacementIndicator {
	/**	A középponttól vett távolság ahol ki kell jelezni még az indikátort
	 * */
	int outerDist = 0;
	/**	A középponthoz legközelebb esõ távolság, ahol meg kell jeleníteni az indikátort
	 * */
	int innerDist = 0;
	/**	Az élettartama, ameddig látható lesz egy része is
	 * */
	int lifeTime = 10;
	/**	Az aktuális élettartama, ami folyamatosan csökken
	 * */
	int currentLife = 0;
	
	/**	Referencia a játéklogikára, hogy lekérdezhesse az aktuális értékeket az alakzatról
	 * */
	final GameLogic gl;
	
	/**	Konstruktor, ami beállítja a játék logika referenciáját
	 * 	
	 * 	@param gameLogic Referencia a játéklogika eltárolásához
	 * */
	public PlacementIndicator(GameLogic gameLogic){
		gl = gameLogic;
	}
	
	/**	Visszaad egy értéket 0 és 255 között attól függõen, hogy mekkora az aktuális élettratama és mekkora a paraméterben magadott csökkentés mértéke
	 * 	Ha csökkentés mértéke nagyobb, mint az aktuális élettartam érték, akkor 0-t ad vissza
	 * 	@param decreaseLevel Csökkenti egy szinttel a  visszaadott értéket arányosan az élettartalomhoz képest.
	 * 	@return 0 és 255 közötti érték, ami az "alpha" érték egy színhez
	 * */
	public int getOpac(int decreaseLevel){
		double decreasedActual = currentLife - decreaseLevel;
		decreasedActual = decreasedActual < 0 ? 0 : decreasedActual;
		double ratio = decreasedActual/((double)(lifeTime)); 
		double maximumAlpha = 150;
		return (int)(maximumAlpha * ratio);
	}
	
	/**	Beállítja a paraméterül kapott alakzathoz a belsõ és külsõ távolságokat
	 *	Beállítja az aktuális élettartamot, a maximum értékre 
	 *
	 *	@param shape Az alakzat ami köré fogja rajzolni az indikátort
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
	
	/**	Belsõ távolság gettere
	 * 	@return Belsõ távolság
	 * */
	public int getInnerDist() {
		return innerDist;
	}
	
	/**	Külsõ távolság gettere
	 * 	@return Külsõ távolság
	 * */
	public int getOuterDist() {
		return outerDist;
	}
	
	/**	Az aktuális élettartam gettere
	 * 	@return Az aktuális élet 
	 * */
	int getLife() {
		return currentLife;
	}
	
	/**	Az aktuális élettartam csökkentése, csak akkor csökkenti, ha nullánál több az aktuális élet
	 * */
	public void decreaseLife() {
		if(currentLife > 0) {
			currentLife--;
		}
	}
}
