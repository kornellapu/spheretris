package logic;
/**	A program állapotátmeneteit kezeli
 * 	Eltárolja a jelenlegi állapotot és csak akkor engedi átlépni másikba, ha az állapotgépben ez lehetséges
 * */
public class GameStateMachine {
	/** A program lehetséges állapotai
	 * 	Ezekbõl a képernyõkbõl épül fel a program, minden állapothoz egy megjelenítendõ képernyõ tartozik
	 */
	public enum State { MENU, GAME, GAMEOVER, SCORES };
	
	/** Az aktuális állapot eltárolása
	 * */
	State actual;
	
	/**	Konstruktor, beállítja a kezdõ állapotot (és képernyõt, amivel indul az alkalmazás)
	 * */
	public GameStateMachine(){
		//Kezdeti állapot beállítása
		actual = State.MENU;
	}
	
	/**	Az aktuális állapot gettere
	 * 	@return Az aktuális állapot
	 * */
	public State getAct() {
		return actual;
	}
	
	/**	Az állapotátmeneteket kezeli, csak akkor ad vissza igazat ha át tudta állítani az aktuális állapotot
	 * 
	 * 	A MENU-bõl csak GAME és SCORES állapotba lehet menni
	 * 	A GAME-bõl csak a GAMEOVER-be lehet menni
	 * 	A GAMEOVER-bõl cask a SCORES-ba lehet menni
	 * 	A SCORES-ból csak a MENU-be lehet menni
	 * 	
	 * 	@param state Az állapot amire be akarjuk állítani
	 * 
	 * 	@return Logikai értéket ad vissza, hogy sikerült-e az állapot átmenet vagy sem
	 * */
	public boolean setState(State state) {
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
			if(state == State.SCORES) {
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
