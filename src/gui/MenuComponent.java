package gui;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Component;
import java.awt.Color;

/**	Men� elemeket tartalamz� komponens, ami a j�t�k f�men�j�t reprezent�lja
 * 	�j j�t�kot lehet kezdeni vagy a pontokat megtekinteni
 * */
public class MenuComponent extends GuiComponent{	
	/**	Referencia az �j j�t�k gombra
	 * */
	JButton newGameBtn;
	/**	Referencia a pontok gombra
	 * */
	JButton scoresBtn;
	/**	Referencia a men�ben tal�lhat� sz�vegmez�re, ez a j�t�k c�me
	 * */
	JTextField title;
	
	/**	Konstruktor, ami be�ll�tja komponenst �s elhelyezi azt egy panelre
	 * 	Az ell�tott feladatok:
	 * 	Panel l�trehoz�sa �s be�ll�t�sa, elemek l�trehoz�sa �s be�ll�t�sa a funkci�juknak megfelel�en,
	 * 	saj�t font elk�r�se, sz�vegek be�ll�t�sa, a gombok funkci�inak hozz�ad�sa, m�retek be�ll�t�sa,
	 * 	elemek elrendez�se �s eltol�sa, sz�nek be�ll�t�sa
	 * 
	 * 	@param windowHandler Ablakkezel� a refrencia elt�rol�s�hoz
	 * 	@param cF Saj�t font a refrencia elt�rol�s�hoz
	 * */
	public MenuComponent(WindowHandler windowHandler, Font cF) {
		super(windowHandler, cF);
		
		//Panel l�trehoz�sa
		mainPanel = new JPanel();
		//Panel elrendez�s�nek be�ll�t�sa
		mainPanel.setLayout( new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
		
		//�j gombok l�trehoz�sa
		newGameBtn = new JButton();
		scoresBtn = new JButton();
		//Sz�veg l�trehoz�sa
		title = new JTextField();
		title.setEditable(false);
		title.setHorizontalAlignment(JTextField.CENTER);
		
		//Saj�t fonttal
		if(customFont != null) {
			newGameBtn.setFont(customFont.deriveFont(90f));
			newGameBtn.revalidate();
			scoresBtn.setFont(customFont.deriveFont(90f));
			scoresBtn.revalidate();
			title.setFont(customFont.deriveFont(180f));
			title.revalidate();
		}
		
		//A megjelen� sz�vegek
		newGameBtn.setText("NEW GAME");
		scoresBtn.setText("SCORES");
		title.setText("SPHERETRIS");
		
		//Gombok hat�sainak hozz�ad�sa az egyes gombokhoz
		newGameBtn.addActionListener( e -> {
			windowHandler.setState(logic.GameStateMachine.State.GAME);
		} );
		scoresBtn.addActionListener( e -> {
			windowHandler.setState(logic.GameStateMachine.State.SCORES);
		} );
		
		int size = 260;
		//Box layout a maximum size-al m�k�dik j�l
		newGameBtn.setMaximumSize(new Dimension(size, size/2));
		scoresBtn.setMaximumSize(new Dimension(size, size/2));
		title.setMaximumSize(new Dimension(size*2, size-20));
		newGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoresBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Elrendez�s �s eltol�sok
		mainPanel.add(title);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(newGameBtn);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 60)));
		mainPanel.add(scoresBtn);
		
		//Sz�nek be�ll�t�sa
		mainPanel.setBackground(Color.black);
		title.setOpaque(false);
		title.setBorder(null);
		title.setForeground(Color.white);
		newGameBtn.setContentAreaFilled(false);
		newGameBtn.setBorderPainted(true);
		newGameBtn.setFocusPainted(false);
		newGameBtn.setForeground(Color.white);
		scoresBtn.setContentAreaFilled(false);
		scoresBtn.setBorderPainted(true);
		scoresBtn.setFocusPainted(false);
		scoresBtn.setForeground(Color.white);
		
		//A panel m�retez�se
		mainPanel.setPreferredSize(winHandler.windowDimension);
	}
	
	/**	A param�ter�l kapott frame-hez hozz�adja ennek az oszt�lynak az elk�sz�tett komponenseit egy panelben csomagolva
	 * 	M�retet be�ll�tja, m�st nem �ll�t be
	 * 
	 * 	Megjegyz�s:
	 * 	Ebben az oszt�lyban semmi �jat nem kell hozz�adni, �gy az �sben megval�s�tott f�ggv�ny ker�l h�v�sra
	 * 	Nem is sz�ks�ges ez a f�ggv�ny itt, am�g nem ad hozz� plusz elemet/v�gez m�s m�veletet.
	 * 	Nem kell defini�lni, �gy is j�l m�k�dik, hiszen az �sben megtal�lhat� ez a f�ggv�ny
	 * 
	 * 	@param frame Amihez a komponensek hozz� lesznek adva
	 * */
	/*
	void addComponentsTo(JFrame frame) {
		//A megval�s�t�s az �sben l�v�vel egyezik
		super.addComponentsTo(frame);
	}
	*/

}
