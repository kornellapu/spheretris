package basic;

import java.io.Serializable;

/**	Egyszerû osztály a pont változók tárolásához
 * 	Egy pont értékkel és egy szöveges névvel rendelkezik
 * 	Sorosítható, hogy fájlba lehessen írni
 * 	Komparálható a pontokon, hogy rendezni lehessen pontok szerint
 */
public class Score implements Comparable<Score>, Serializable{
	/**	A serializáció miatt */
	private static final long serialVersionUID = 1L;
	/** Pont értéke*/
	private int score;
	/** Játékos neve */
	private String name;
	/** Egyszerû konstruktor, beállítja a pontot és nevet
	 * @param s Pont
	 * @param n Név*/
	public Score(int s, String n){
		setScore(s);
		setName(n);
	}
	/** Komparátor megvalósítása, az összehasonlításhoz */
	@Override
	public int compareTo(Score o) {
		return ((Integer)getScore()).compareTo( o.getScore() );
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
