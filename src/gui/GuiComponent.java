package gui;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GuiComponent {
	/**	Referencia az �t tartalmaz� ablakr�l
	 * 	A m�retez�s be�ll�t�s�hoz, hogy pontosan akkora m�retre �ll�tsa amekkora eredetileg volt
	 * */
	final WindowHandler winHandler;
	/**	A saj�t fontra mutat� referencia, az�rt, hogy ne kelljen minden komponensben k�l�n bet�lteni
	 * */
	final Font customFont;
	
	/**	Minden komponens tartalmaz egy f� panelt amit inicializ�l �s felt�lt az �ltala haszn�lt elemekkel
	 * 	A k�s�bbiekben csak ez a panel lesz hozz�adva a JFrame-hez
	 * 	Sz�ks�ges be�ll�tani a hozz�ad�s el�tt
	 * */
	JPanel mainPanel;
	
	/**	Konstruktor ami be�ll�tja a windowHandler-t �s a saj�t bet�t�pust
	 * 	Referenci�kat k�r el, ez�rt nem kell mindig l�trehozni ezeket �gy nem foglalnak t�bb er�forr�st
	 * 
	 * @param windowHandler Referencia az ablakkezel�re
	 * @param cF Referencia a saj�t bet�t�pusra
	 * */
	GuiComponent(WindowHandler windowHandler, Font cF){
		winHandler = windowHandler;
		customFont = cF;
	}
	
	/** A param�ter�l kapott frame-hez hozz�adja a saj�t be�ll�tott panelj�t
	 * 	Be�ll�tja a m�retet �s �jra is sz�molja azt
	 * 
	 * 	Megjegyz�s: A m�retnek nem szabad v�ltoznia, az a j�t�kt�r be�ll�t�sait�l f�gg
	 * 
	 * 	@param frame Amihez hozz�adja az elk�sz�tett elemeket
	 * */
	void addComponentsTo(JFrame frame) {
		//Komponensek hozz�ad�sa
		frame.add( mainPanel );
		frame.setSize(winHandler.windowDimension);
		frame.pack();
	}

}
