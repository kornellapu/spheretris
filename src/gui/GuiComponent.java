package gui;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GuiComponent {
	/**	Referencia az õt tartalmazõ ablakról
	 * 	A méretezés beállításához, hogy pontosan akkora méretre állítsa amekkora eredetileg volt
	 * */
	final WindowHandler winHandler;
	/**	A saját fontra mutató referencia, azért, hogy ne kelljen minden komponensben külön betölteni
	 * */
	final Font customFont;
	
	/**	Minden komponens tartalmaz egy fõ panelt amit inicializál és feltölt az általa használt elemekkel
	 * 	A késöbbiekben csak ez a panel lesz hozzáadva a JFrame-hez
	 * 	Szükséges beállítani a hozzáadás elõtt
	 * */
	JPanel mainPanel;
	
	/**	Konstruktor ami beállítja a windowHandler-t és a saját betûtípust
	 * 	Referenciákat kér el, ezért nem kell mindig létrehozni ezeket így nem foglalnak több erõforrást
	 * 
	 * @param windowHandler Referencia az ablakkezelõre
	 * @param cF Referencia a saját betûtípusra
	 * */
	GuiComponent(WindowHandler windowHandler, Font cF){
		winHandler = windowHandler;
		customFont = cF;
	}
	
	/** A paraméterül kapott frame-hez hozzáadja a saját beállított paneljét
	 * 	Beállítja a méretet és újra is számolja azt
	 * 
	 * 	Megjegyzés: A méretnek nem szabad változnia, az a játéktér beállításaitõl függ
	 * 
	 * 	@param frame Amihez hozzáadja az elkészített elemeket
	 * */
	void addComponentsTo(JFrame frame) {
		//Komponensek hozzáadása
		frame.add( mainPanel );
		frame.setSize(winHandler.windowDimension);
		frame.pack();
	}

}
