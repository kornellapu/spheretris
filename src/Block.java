import java.awt.Color;
import java.util.Random;

/** Az alap építõ elemek, az alakzatokhoz és a pályán tárolt mezõkhöz.
 * 	Rendelkezik egy kitöltöttségi állapottal és színnel amivel a mezõt ki fogja tölteni
 * */
public class Block {
	//A kitöltés színe
	Color color;
	//A kitöltés állapota
	boolean full;
	
	/**	Beállítja az új Block mezõit
	 * 	@param pfull Kitöltött vagy üres blokkot eredményez
	 * 	Amennyiben kitöltött véletlenszerû színt állít be
	 * 	Egyébként fekete színûre állítja a blokkot
	 * */
	Block(boolean pfull){
		full = pfull;
		
		if(pfull) {
			setRandomColor();
		}
		else
			color = Color.black;
		//System.out.println("Block constructor run...");
	}
	
	/**	Másoló konstruktor
	 * 	@param pb Amirõl lemásolja az új blokkot, beállítja a színét és kitöltöttségét
	 * */
	Block(Block pb){
		color = new Color(pb.color.getRGB());
		full = pb.full;
	}
	
	/**	Adott színû blokkot hoz létre
	 * @param c A létrehozott Block színe
	 * Emellett beállítja, hogy a kitöltöttség igaz legyen
	 * */
	Block(Color c){
		color = c;
		full = true;
	}
	
	/**	Beállítja a Blockot véletlenszerû színre
	 * 	A kitöltöttséget nem állítja
	 * */
	void setRandomColor() {
		Random rnd = new Random(System.currentTimeMillis());
		int r = rnd.nextInt(256);
		int g = rnd.nextInt(256);
		int b = rnd.nextInt(256);
		color = new Color(r,g,b);
		//System.out.println("Block with RNDColor:(" +r+ ","+g+","+b+")");
	}
	
	/**	Visszaadja üres-e (nincs kitöltve) az adott Block
	 * */
	boolean isEmpty() {
		return !full;
	}
	
	/**	Visszaadja teli-e (ki van töltve) az adott Block
	 * */
	boolean isFull() {
		return full;
	}
	
	/**	Visszaadja a Block színét
	 * */
	Color getColor() {
		return color;
	}
	
	/**	Beállítja az adott Block-ot a paraméterben kapottra
	 * 	@param b Amirõl le kell másolni a színt és a kitöltöttséget
	 * */
	void setTo(Block b) {
		full = b.full;
		color = b.color;
	}
	
	/**	Beállítja a kitöltöttséget
	 * 	@param pfull A kitöltöttség, amire beállítja
	 * 	Ha üresre állítja a színét is átállítja feketére
	 * */
	void setFull(boolean pfull) {
		if(!pfull) {
			color = Color.black;
		}
		full = pfull;
	}
	
	/**	A szín beállítása
	 * @param c A szín, amire beállítja a Block színét
	 * */
	void setColor( Color c) {
		if(c == Color.black)
			full = false;
		color = c;
	}
}
