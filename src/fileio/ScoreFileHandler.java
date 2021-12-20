package fileio;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import basic.Score;

/**	A pontokat kiolvas� �s elment� seg�t� oszt�ly
 * 	T�rolja a f�jl hely�t
 * 	A f�jlb�l beolvasott/ f�ljba ki�rt pontok t�mbj�t
 * */
public class ScoreFileHandler implements Serializable{
	
	//	A soros�that�s�g miatt
	private static final long serialVersionUID = 1L;
	
	ArrayList<Score> scores = new ArrayList<>();
	String filePath = "./resources/scores";
	
	public ScoreFileHandler() {
		readFromFile(filePath);
	}
	
	/** Tesztel�sre haszn�lt konstruktor, alap esetben nem sz�ks�ges megadni a f�jl elhelyezked�s�t */
	public ScoreFileHandler(String file) {
		filePath = file;
		readFromFile(file);
	}
	
	/**	F�jlb�l beolvas�s �s az adatok felt�lt�se
	 * 	Ha nem tal�lja a f�jlt l�trehoz el�re meg�rt pontokkal egy f�jl a ment�s seg�ts�g�vel
	 * 	Ha kiv�tel dob�dik helyileg kezeli, �s nem t�lti fel az adatokat
	 * 	Ha nem tal�lja a scores f�jl akkor l�trehoz egy �jat, amit p�da adatokkal t�lt fel
	 * */
	public void readFromFile(String file){
		//Ha �res legyen az alap �rtelmezett el�r�si �tvonal
		if(file == null || file.isBlank()) {
			file = filePath;
		}
		
		try {
		FileInputStream f = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(f);
		@SuppressWarnings("unchecked")
		ArrayList<Score> readScores = (ArrayList<Score>) in.readObject();
		in.close();
		
		scores = readScores;
		
		}
		catch(FileNotFoundException e) {
			System.out.println("Scores file not found at:\"" + new File(file).getAbsoluteFile() + "\"");
			createDummyScores();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**	Ha nincs megadva melyik f�jlb�l olvasson akkor az eredetib�l fog
	 * 	Bel�l a readFromFile(String) f�ggv�nyt h�vja
	 * 	@see readFromFile(String file);
	 * */
	public void readFromFile(){
		readFromFile(filePath);
	}
	
	/**	A soros�that� oszt�ly f�jlba ki�r�sa
	 * 	Ha nem tal�lja a mapp�t l�trehozza, ha nem tal�lja a f�jlt l�trehozza
	 * 	Ha IOException dob�dik azt elkapja �s nem olvassa be a helyi f�jlb�l az adatokat
	 * */
	void saveToFile(String file) {
		// Ha �res legyen az alap �rtelmezett el�r�si �tvonal
		if (file == null || file.isBlank()) {
			file = filePath;
		}
		
		
		//Mappa l�tez�s�nek ellen�rz�se
		File scoreFile = new File(file);
		
		System.out.println("Reading scores from: \"" + scoreFile.getAbsoluteFile() + "\"");
		
		File scoreDir = scoreFile.getParentFile();
		if( scoreDir != null && !scoreDir.exists() ) {
			//Ha nem l�tezik akkor l�trehozza
			System.out.println("Directory created for scores...");
			scoreDir.mkdir();
		}
		try {
			
		FileOutputStream f = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(f);
		out.writeObject(this.scores);
		out.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("Scores file: \"" + scoreFile.getAbsoluteFile() +"\" not found...");
			createDummyScores();
		}
		catch(IOException ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
	}
	
	/**	El�re meghat�rozott adatokkal val� felt�lt�s, ez csak akkor t�rt�nik meg ha nem tal�lja a pontokat
	 * 	Pontok l�trehoz�sa, majd hozz�ad�sa a list�hoz, majd ment�se
	 * */
	void createDummyScores() {
		scores.add( new Score(0,	"PEBBLE") );
		//scores.add(new Score(10, 	"METEOR"));
		//scores.add(new Score(20,	"PLANET"));
		//scores.add(new Score(50, 	"STAR"));
		//scores.add(new Score(100,	"SUPERNOVA"));
		//scores.add(new Score(200, "BLACK HOLE"));
		//scores.add(new Score(500,	"GALAXY"));
		//scores.add(new Score(1000, "UNIVERSE"));
		
		System.out.println("Scores file will be created with dummy data at: \"" + new File(filePath).getAbsoluteFile() + "\"");
		saveToFile(filePath);
	}
	
	/**	�j pont hozz�ad�sa a pontokhoz �s egyb�l lement�se a helyi f�jlba
	 * 	@param score Az �j pont �rt�ke
	 * 	@param name Az �j ponthoz tartoz� n�v
	 * */
	public void addNewScore(int score, String name) {
		Score newScore = new Score(score,name);
		scores.add(newScore);
		
		saveToFile(filePath);
	}
	
	/**	A legnagyobb pont lek�rdez�se az adatok k�z�l
	 * 	A j�t�k v�g�n az �j rekord ki�r�sa miatt kell tudni
	 * 	Rendezi a t�rol�t majd megford�tja �s a legels�t visszaadja
	 * 	Ha �res 0-t ad vissza
	 * 
	 * 	@return A legt�bb eddig el�rt pont az adatok k�z�tt
	 * */
	public int getHighestScore(){
		int highest = 0;
		
		if(scores.size() > 0) {
			Collections.sort(scores);
			Collections.reverse(scores);
			
			highest = scores.get(0).getScore();
		}
		
		return highest;
	}
	
	/**	A lista gettere
	 * 	
	 * 	@return Egy lista pontokkal felt�ltve
	 * */
	public ArrayList<Score> getScores(){
		return scores;
	}
}
