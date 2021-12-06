/**	A progtam állapotátmeneteit kezeli
 * */
public class GameStateMachine {
	//A program lehetséges állapotai
	public enum State { MENU, GAME, GAMEOVER, SCORES };
	
	//Az aktuális állapot eltárolása
	State actual;
	
	GameStateMachine(){
		//Kezdeti állapot beállítása
		actual = State.MENU;
	}
	
	/**	Az aktuális állapot gettere
	 * */
	State getAct() {
		return actual;
	}
	
	/**	Az állapotátmeneteket kezeli, csak akkor ad vissza igazat ha át tudta állítani az aktuális állapotot
	 * */
	boolean setState(State state) {
		if(actual == State.MENU) {
			//Ha a menüben vagyunk
			if(state == State.GAME) {
				//játékot kezdhetünk
				actual = State.GAME;
				return true;
			}
			//Pontokat nézhetjük meg
			else if(state == State.SCORES) {
				actual = State.SCORES;
				return true;
			}
		}
		else if(actual == State.GAME) {
			//Ha a játékban vagyunk
			if(state == State.GAMEOVER) {
				//játék vége
				actual = State.GAMEOVER;
				return true;
			}
		}
		else if(actual == State.GAMEOVER) {
			//Ha a játékvége
			if(state == State.MENU) {
				//Menube menjunk
				actual = State.MENU;
				return true;
			}
			else if(state == State.SCORES) {
				//Pontokhoz menjunk
				actual = State.SCORES;
				return true;
			}
		}
		else if(actual == State.SCORES) {
			//Ha a pontoknál vagyunk
			if(state == State.MENU) {
				//Menube menjunk
				actual = State.MENU;
				return true;
			}
		}
		
		//Egyébként ne változtassa az állapotot és adjon false-t
		return false;
	}
	
}
