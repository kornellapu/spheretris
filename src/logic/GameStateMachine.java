package logic;
/**	A program �llapot�tmeneteit kezeli
 * 	Elt�rolja a jelenlegi �llapotot �s csak akkor engedi �tl�pni m�sikba, ha az �llapotg�pben ez lehets�ges
 * */
public class GameStateMachine {
	/** A program lehets�ges �llapotai
	 * 	Ezekb�l a k�perny�kb�l �p�l fel a program, minden �llapothoz egy megjelen�tend� k�perny� tartozik
	 */
	public enum State { MENU, GAME, GAMEOVER, SCORES };
	
	/** Az aktu�lis �llapot elt�rol�sa
	 * */
	State actual;
	
	/**	Konstruktor, be�ll�tja a kezd� �llapotot (�s k�perny�t, amivel indul az alkalmaz�s)
	 * */
	public GameStateMachine(){
		//Kezdeti �llapot be�ll�t�sa
		actual = State.MENU;
	}
	
	/**	Az aktu�lis �llapot gettere
	 * 	@return Az aktu�lis �llapot
	 * */
	public State getAct() {
		return actual;
	}
	
	/**	Az �llapot�tmeneteket kezeli, csak akkor ad vissza igazat ha �t tudta �ll�tani az aktu�lis �llapotot
	 * 
	 * 	A MENU-b�l csak GAME �s SCORES �llapotba lehet menni
	 * 	A GAME-b�l csak a GAMEOVER-be lehet menni
	 * 	A GAMEOVER-b�l cask a SCORES-ba lehet menni
	 * 	A SCORES-b�l csak a MENU-be lehet menni
	 * 	
	 * 	@param state Az �llapot amire be akarjuk �ll�tani
	 * 
	 * 	@return Logikai �rt�ket ad vissza, hogy siker�lt-e az �llapot �tmenet vagy sem
	 * */
	public boolean setState(State state) {
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
			if(state == State.SCORES) {
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
