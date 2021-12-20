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

/**	Menü elemeket tartalamzó komponens, ami a játék fõmenüjét reprezentálja
 * 	Új játékot lehet kezdeni vagy a pontokat megtekinteni
 * */
public class MenuComponent extends GuiComponent{	
	/**	Referencia az új játék gombra
	 * */
	JButton newGameBtn;
	/**	Referencia a pontok gombra
	 * */
	JButton scoresBtn;
	/**	Referencia a menüben található szövegmezõre, ez a játék címe
	 * */
	JTextField title;
	
	/**	Konstruktor, ami beállítja komponenst és elhelyezi azt egy panelre
	 * 	Az ellátott feladatok:
	 * 	Panel létrehozása és beállítása, elemek létrehozása és beállítása a funkciójuknak megfelelõen,
	 * 	saját font elkérése, szövegek beállítása, a gombok funkcióinak hozzáadása, méretek beállítása,
	 * 	elemek elrendezése és eltolása, színek beállítása
	 * 
	 * 	@param windowHandler Ablakkezelõ a refrencia eltárolásához
	 * 	@param cF Saját font a refrencia eltárolásához
	 * */
	public MenuComponent(WindowHandler windowHandler, Font cF) {
		super(windowHandler, cF);
		
		//Panel létrehozása
		mainPanel = new JPanel();
		//Panel elrendezésének beállítása
		mainPanel.setLayout( new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
		
		//Új gombok létrehozása
		newGameBtn = new JButton();
		scoresBtn = new JButton();
		//Szöveg létrehozása
		title = new JTextField();
		title.setEditable(false);
		title.setHorizontalAlignment(JTextField.CENTER);
		
		//Saját fonttal
		if(customFont != null) {
			newGameBtn.setFont(customFont.deriveFont(90f));
			newGameBtn.revalidate();
			scoresBtn.setFont(customFont.deriveFont(90f));
			scoresBtn.revalidate();
			title.setFont(customFont.deriveFont(180f));
			title.revalidate();
		}
		
		//A megjelenõ szövegek
		newGameBtn.setText("NEW GAME");
		scoresBtn.setText("SCORES");
		title.setText("SPHERETRIS");
		
		//Gombok hatásainak hozzáadása az egyes gombokhoz
		newGameBtn.addActionListener( e -> {
			windowHandler.setState(logic.GameStateMachine.State.GAME);
		} );
		scoresBtn.addActionListener( e -> {
			windowHandler.setState(logic.GameStateMachine.State.SCORES);
		} );
		
		int size = 260;
		//Box layout a maximum size-al mûködik jól
		newGameBtn.setMaximumSize(new Dimension(size, size/2));
		scoresBtn.setMaximumSize(new Dimension(size, size/2));
		title.setMaximumSize(new Dimension(size*2, size-20));
		newGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoresBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Elrendezés és eltolások
		mainPanel.add(title);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(newGameBtn);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 60)));
		mainPanel.add(scoresBtn);
		
		//Színek beállítása
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
		
		//A panel méretezése
		mainPanel.setPreferredSize(winHandler.windowDimension);
	}
	
	/**	A paraméterül kapott frame-hez hozzáadja ennek az osztálynak az elkészített komponenseit egy panelben csomagolva
	 * 	Méretet beállítja, mást nem állít be
	 * 
	 * 	Megjegyzés:
	 * 	Ebben az osztályban semmi újat nem kell hozzáadni, így az õsben megvalósított függvény kerül hívásra
	 * 	Nem is szükséges ez a függvény itt, amíg nem ad hozzá plusz elemet/végez más mûveletet.
	 * 	Nem kell definiálni, így is jól mûködik, hiszen az õsben megtalálható ez a függvény
	 * 
	 * 	@param frame Amihez a komponensek hozzá lesznek adva
	 * */
	/*
	void addComponentsTo(JFrame frame) {
		//A megvalósítás az õsben lévõvel egyezik
		super.addComponentsTo(frame);
	}
	*/

}
