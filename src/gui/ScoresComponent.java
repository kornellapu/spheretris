package gui;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import basic.Score;
import fileio.ScoreFileHandler;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Component;
import java.awt.Color;

/**
 * A pontok képernyõ kijelzéséhez, egy ypanelhez hozzáadva tárolja a komponenseit és jeleníti meg
 */
public class ScoresComponent extends GuiComponent{
	/**	A pontokat kezelõ osztály, ami írja/olvassa egy helyi fájlban elhelyezett pontokat
	 * */
	final ScoreFileHandler scrFh;
	/**	Referencia a menübe vissza gombra
	 * */
	JButton backBtn;
	/**	Referencia szövegmezõre, ami kijelzi a címet
	 * */
	JTextField title;
	/**	Referencia egy görgethetõ panelre, ami az összes pontot tartalmazza
	 * */
	JScrollPane scoreSp;

	/**	Konstrukor ami beállítja a szükséges referenciákat és inicializálja a pontokat
	 * 	Azért szükséges a külön függvény, hogy a megfelelõ mérettel hozza létre a lista komponenst
	 * 	Sajnos a pnel minden elemét ki kell törölni ezért úgyis újra el kell készíteni az egész panelt ha egy elemet kell frissíteni
	 * 
	 * 	@param wh	Referencia az ablakkezelõ eltárolására
	 * 	@param cf	Referencia az saját font eltárolására
	 * 	@param sfh	Referencia az pont fájl kezelõ eltárolására
	 * */
	public ScoresComponent(WindowHandler wh, Font cf, ScoreFileHandler sfh) {
		super(wh, cf);
		scrFh = sfh;

		initComponent();
	}
	
	/**	Elkészít egy teljesen új panelt, friss adatokkal
	 * 	Feladatai: panel/elemek létrehozása és ezek beállítása, adatokkal feltöltése a scrollpane-nek,
	 * 	saját font beállítása, szövegek beállítása, gomb callback beállítása, méretezés,
	 * 	elrendezés és színek beállítása végül pedig a panel méretezése
	 * */
	void initComponent() {
		// Panel létrehozása
		mainPanel = new JPanel();
		// Elrendezés függõlegesen
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// Elemek létrehozása
		backBtn = new JButton();
		title = new JTextField();
		// Beállítások
		title.setEditable(false);
		title.setHorizontalAlignment(JTextField.CENTER);

		// Adatokkal feltöltése a Scrollpane-nek
		JPanel list = createListFromData();
		scoreSp = new JScrollPane(list);

		// Saját font beállítása
		if (customFont != null) {
			title.setFont(customFont.deriveFont(180f));
			title.revalidate();
			backBtn.setFont(customFont.deriveFont(60f));
			backBtn.revalidate();
			// scoreList.setFont(customFont.deriveFont(50f));
			// scoreList.revalidate();
		}

		// Szövegek beállítása
		backBtn.setText("BACK TO MENU");
		title.setText("SCORES");

		// Gomb callback függvénye
		backBtn.addActionListener(e -> {
			// Átlépés a Menübe
			winHandler.setState(logic.GameStateMachine.State.MENU);
		});

		// Méretezések
		// BoxLayout miatt setMaximumSize a hatásos
		int size = 260;
		backBtn.setMaximumSize(new Dimension(size, size / 3));
		title.setMaximumSize(new Dimension(size * 2, size));
		scoreSp.setMaximumSize(new Dimension(size * 3, size * 2));
		backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		// scoreList.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreSp.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreSp.setBorder(null);

		// Elrendezés
		mainPanel.add(title);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(scoreSp);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(backBtn);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		// Színek beállítása
		mainPanel.setBackground(Color.black);
		scoreSp.setOpaque(false);
		scoreSp.getViewport().setOpaque(false);
		title.setForeground(Color.white);
		title.setOpaque(false);
		title.setBorder(null);
		backBtn.setContentAreaFilled(false);
		backBtn.setFocusPainted(false);
		backBtn.setForeground(Color.white);

		// Panel méretezése
		mainPanel.setPreferredSize(winHandler.windowDimension);
	}

	/**	Frissíti az adatokat (újra összeállítja a panelt) majd utána	
	 * 	A paraméterben kapott frame-hez hozzáadja az elõre elkészített komponenssekkel feltölttt panelt
	 * @param frame Az ablak amihez hozzáadja a komponenseket
	 * */
	void addComponentsTo(JFrame frame) {
		// Adatokkal feltöltése a ScrollPane-nek
		initComponent();

		// Komponensek hozzáadása az õsben megvalósítva
		super.addComponentsTo(frame);
		
	}
	
	/**	Egy olyan panelt ad vissza ami egy lista nézetet eredményez
	 * 	A pontok kijelzéséhez használt panel autómatikusan készül el helyi pontok fájlból
	 * 	Ez belekerül majd egy görgethetõ ablakba
	 * 	A listaelemek lefelé sima szövegmezõkbõl állnak, amelyek egyesével beállításra kerülnek
	 * 
	 * 	@return A panel ami tartalmazza a listát a pontokkal
	 * */
	JPanel createListFromData() {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

		scrFh.readFromFile();

		ArrayList<Score> data = scrFh.getScores();
		Collections.sort(data);
		Collections.reverse(data);
		
		for (int i = 0; i < data.size(); i++) {
			JTextField listItem = new JTextField(
					"#" + (i + 1) + " " + data.get(i).getName() + " " + data.get(i).getScore() + " PTS");
			if (customFont != null) {
				listItem.setFont(customFont.deriveFont(50f));
			}
			listItem.setEditable(false);
			listItem.setForeground(Color.white);
			listItem.setBorder(null);
			int width = 500;
			int height = 50;
			listItem.setPreferredSize(new Dimension(width, height));
			listItem.setMaximumSize(new Dimension(width, height));
			listItem.setMinimumSize(new Dimension(width, height));
			listItem.setAlignmentX(Component.CENTER_ALIGNMENT);
			listItem.setHorizontalAlignment(JTextField.CENTER);
			listItem.setOpaque(false);
			listPanel.add(listItem);
		}

		listPanel.setOpaque(false);
		//listPanel.setPreferredSize(new Dimension(winHandler.windowDimension.width - 20, 0));
		listPanel.setBorder(null);

		return listPanel;
	}

}
