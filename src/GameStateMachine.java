/**	A progtam �llapot�tmeneteit kezeli
 * */
public class GameStateMachine {
	//A program lehets�ges �llapotai
	public enum State { MENU, GAME, GAMEOVER, SCORES };
	
	//Az aktu�lis �llapot elt�rol�sa
	State actual;
	
	GameStateMachine(){
		//Kezdeti �llapot be�ll�t�sa
		actual = State.MENU;
	}
	
	/**	Az aktu�lis �llapot gettere
	 * */
	State getAct() {
		return actual;
	}
	
	/**	Az �llapot�tmeneteket kezeli, csak akkor ad vissza igazat ha �t tudta �ll�tani az aktu�lis �llapotot
	 * */
	boolean setState(State state) {
		if(actual == State.MENU) {
			//Ha a men�ben vagyunk
			if(state == State.GAME) {
				//j�t�kot kezdhet�nk
				actual = State.GAME;
				return true;
			}
			//Pontokat n�zhetj�k meg
			else if(state == State.SCORES) {
				actual = State.SCORES;
				return true;
			}
		}
		else if(actual == State.GAME) {
			//Ha a j�t�kban vagyunk
			if(state == State.GAMEOVER) {
				//j�t�k v�ge
				actual = State.GAMEOVER;
				return true;
			}
		}
		else if(actual == State.GAMEOVER) {
			//Ha a j�t�kv�ge
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
			//Ha a pontokn�l vagyunk
			if(state == State.MENU) {
				//Menube menjunk
				actual = State.MENU;
				return true;
			}
		}
		
		//Egy�bk�nt ne v�ltoztassa az �llapotot �s adjon false-t
		return false;
	}
	
}
