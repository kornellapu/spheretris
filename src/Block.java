import java.awt.Color;
import java.util.Random;

/** Az alap �p�t� elemek, az alakzatokhoz �s a p�ly�n t�rolt mez�kh�z.
 * 	Rendelkezik egy kit�lt�tts�gi �llapottal �s sz�nnel amivel a mez�t ki fogja t�lteni
 * */
public class Block {
	//A kit�lt�s sz�ne
	Color color;
	//A kit�lt�s �llapota
	boolean full;
	
	/**	Be�ll�tja az �j Block mez�it
	 * 	@param pfull Kit�lt�tt vagy �res blokkot eredm�nyez
	 * 	Amennyiben kit�lt�tt v�letlenszer� sz�nt �ll�t be
	 * 	Egy�bk�nt fekete sz�n�re �ll�tja a blokkot
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
	
	/**	M�sol� konstruktor
	 * 	@param pb Amir�l lem�solja az �j blokkot, be�ll�tja a sz�n�t �s kit�lt�tts�g�t
	 * */
	Block(Block pb){
		color = new Color(pb.color.getRGB());
		full = pb.full;
	}
	
	/**	Adott sz�n� blokkot hoz l�tre
	 * @param c A l�trehozott Block sz�ne
	 * Emellett be�ll�tja, hogy a kit�lt�tts�g igaz legyen
	 * */
	Block(Color c){
		color = c;
		full = true;
	}
	
	/**	Be�ll�tja a Blockot v�letlenszer� sz�nre
	 * 	A kit�lt�tts�get nem �ll�tja
	 * */
	void setRandomColor() {
		Random rnd = new Random(System.currentTimeMillis());
		int r = rnd.nextInt(256);
		int g = rnd.nextInt(256);
		int b = rnd.nextInt(256);
		color = new Color(r,g,b);
		//System.out.println("Block with RNDColor:(" +r+ ","+g+","+b+")");
	}
	
	/**	Visszaadja �res-e (nincs kit�ltve) az adott Block
	 * */
	boolean isEmpty() {
		return !full;
	}
	
	/**	Visszaadja teli-e (ki van t�ltve) az adott Block
	 * */
	boolean isFull() {
		return full;
	}
	
	/**	Visszaadja a Block sz�n�t
	 * */
	Color getColor() {
		return color;
	}
	
	/**	Be�ll�tja az adott Block-ot a param�terben kapottra
	 * 	@param b Amir�l le kell m�solni a sz�nt �s a kit�lt�tts�get
	 * */
	void setTo(Block b) {
		full = b.full;
		color = b.color;
	}
	
	/**	Be�ll�tja a kit�lt�tts�get
	 * 	@param pfull A kit�lt�tts�g, amire be�ll�tja
	 * 	Ha �resre �ll�tja a sz�n�t is �t�ll�tja feket�re
	 * */
	void setFull(boolean pfull) {
		if(!pfull) {
			color = Color.black;
		}
		full = pfull;
	}
	
	/**	A sz�n be�ll�t�sa
	 * @param c A sz�n, amire be�ll�tja a Block sz�n�t
	 * */
	void setColor( Color c) {
		if(c == Color.black)
			full = false;
		color = c;
	}
}
