import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreFileHandler implements Serializable{
	
	//	A soros�that�s�g miatt
	private static final long serialVersionUID = 1L;
	
	ArrayList<Score> scores = new ArrayList<>();
	final String filePath = "scores";
	
	public ScoreFileHandler() {
		readFromFile();
	}
	
	/**	F�jlb�l olvas�s
	 * */
	@SuppressWarnings("unchecked")
	void readFromFile(){
		try {
		FileInputStream f = new FileInputStream(filePath);
		ObjectInputStream in = new ObjectInputStream(f);
		ArrayList<Score> readScores = (ArrayList<Score>) in.readObject();
		in.close();
		
		scores = readScores;
		
		}
		catch(IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		

	}
	
	/**	A soros�that� oszt�ly f�jlba ki�r�sa 
	 * */
	void saveToFile() {
		try {
		FileOutputStream f = new FileOutputStream(filePath);
		ObjectOutputStream out = new ObjectOutputStream(f);
		out.writeObject(this.scores);
		out.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**	�j pont hozz�ad�sa a pontokhoz �s egyb�l lement�se
	 * */
	void addNewScore(int score, String name) {
		Score newScore = new Score(score,name);
		scores.add(newScore);
		
		saveToFile();
	}
	
	/**	A legnagyobb pont lek�rdez�se
	 * */
	int getHighestScore(){
		int highest = 0;
		
		if(scores.size() > 0) {
			Collections.sort(scores);
			Collections.reverse(scores);
			
			highest = scores.get(0).score;
		}
		
		return highest;
	}
	
	/**	A lista gettere
	 * */
	ArrayList<Score> getScores(){
		return scores;
	}
	
	/**	Egyszer� bels� oszt�ly a v�ltoz�k t�rol�s�hoz
	 */
	@SuppressWarnings("serial")
	class Score implements Comparable<Score>, Serializable{
		int score;
		String name;
		Score(int s, String n){
			score = s;
			name = n;
		}
		@Override
		public int compareTo(ScoreFileHandler.Score o) {
			return ((Integer)score).compareTo( o.score );
		}
	}

}
