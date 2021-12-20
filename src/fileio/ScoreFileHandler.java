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

/**	A pontokat kiolvasó és elmentõ segítõ osztály
 * 	Tárolja a fájl helyét
 * 	A fájlból beolvasott/ fáljba kiírt pontok tömbjét
 * */
public class ScoreFileHandler implements Serializable{
	
	//	A sorosíthatóság miatt
	private static final long serialVersionUID = 1L;
	
	ArrayList<Score> scores = new ArrayList<>();
	String filePath = "./resources/scores";
	
	public ScoreFileHandler() {
		readFromFile(filePath);
	}
	
	/** Tesztelésre használt konstruktor, alap esetben nem szükséges megadni a fájl elhelyezkedését */
	public ScoreFileHandler(String file) {
		filePath = file;
		readFromFile(file);
	}
	
	/**	Fájlból beolvasás és az adatok feltöltése
	 * 	Ha nem találja a fájlt létrehoz elõre megírt pontokkal egy fájl a mentés segítségével
	 * 	Ha kivétel dobódik helyileg kezeli, és nem tölti fel az adatokat
	 * 	Ha nem találja a scores fájl akkor létrehoz egy újat, amit péda adatokkal tölt fel
	 * */
	public void readFromFile(String file){
		//Ha üres legyen az alap értelmezett elérési útvonal
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
	
	/**	Ha nincs megadva melyik fájlból olvasson akkor az eredetibõl fog
	 * 	Belül a readFromFile(String) függvényt hívja
	 * 	@see readFromFile(String file);
	 * */
	public void readFromFile(){
		readFromFile(filePath);
	}
	
	/**	A sorosítható osztály fájlba kiírása
	 * 	Ha nem találja a mappát létrehozza, ha nem találja a fájlt létrehozza
	 * 	Ha IOException dobódik azt elkapja és nem olvassa be a helyi fájlból az adatokat
	 * */
	void saveToFile(String file) {
		// Ha üres legyen az alap értelmezett elérési útvonal
		if (file == null || file.isBlank()) {
			file = filePath;
		}
		
		
		//Mappa létezésének ellenõrzése
		File scoreFile = new File(file);
		
		System.out.println("Reading scores from: \"" + scoreFile.getAbsoluteFile() + "\"");
		
		File scoreDir = scoreFile.getParentFile();
		if( scoreDir != null && !scoreDir.exists() ) {
			//Ha nem létezik akkor létrehozza
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
	
	/**	Elõre meghatározott adatokkal való feltöltés, ez csak akkor történik meg ha nem találja a pontokat
	 * 	Pontok létrehozása, majd hozzáadása a listához, majd mentése
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
	
	/**	Új pont hozzáadása a pontokhoz és egybõl lementése a helyi fájlba
	 * 	@param score Az új pont értéke
	 * 	@param name Az új ponthoz tartozó név
	 * */
	public void addNewScore(int score, String name) {
		Score newScore = new Score(score,name);
		scores.add(newScore);
		
		saveToFile(filePath);
	}
	
	/**	A legnagyobb pont lekérdezése az adatok közül
	 * 	A játék végén az új rekord kiírása miatt kell tudni
	 * 	Rendezi a tárolót majd megfordítja és a legelsõt visszaadja
	 * 	Ha üres 0-t ad vissza
	 * 
	 * 	@return A legtöbb eddig elért pont az adatok között
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
	 * 	@return Egy lista pontokkal feltöltve
	 * */
	public ArrayList<Score> getScores(){
		return scores;
	}
}
