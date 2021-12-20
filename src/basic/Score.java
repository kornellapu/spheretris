package basic;

import java.io.Serializable;

/**	Egyszer� oszt�ly a pont v�ltoz�k t�rol�s�hoz
 * 	Egy pont �rt�kkel �s egy sz�veges n�vvel rendelkezik
 * 	Soros�that�, hogy f�jlba lehessen �rni
 * 	Kompar�lhat� a pontokon, hogy rendezni lehessen pontok szerint
 */
public class Score implements Comparable<Score>, Serializable{
	/**	A serializ�ci� miatt */
	private static final long serialVersionUID = 1L;
	/** Pont �rt�ke*/
	private int score;
	/** J�t�kos neve */
	private String name;
	/** Egyszer� konstruktor, be�ll�tja a pontot �s nevet
	 * @param s Pont
	 * @param n N�v*/
	public Score(int s, String n){
		setScore(s);
		setName(n);
	}
	/** Kompar�tor megval�s�t�sa, az �sszehasonl�t�shoz */
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
