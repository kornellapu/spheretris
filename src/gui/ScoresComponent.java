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
 * A pontok k�perny� kijelz�s�hez, egy ypanelhez hozz�adva t�rolja a komponenseit �s jelen�ti meg
 */
public class ScoresComponent extends GuiComponent{
	/**	A pontokat kezel� oszt�ly, ami �rja/olvassa egy helyi f�jlban elhelyezett pontokat
	 * */
	final ScoreFileHandler scrFh;
	/**	Referencia a men�be vissza gombra
	 * */
	JButton backBtn;
	/**	Referencia sz�vegmez�re, ami kijelzi a c�met
	 * */
	JTextField title;
	/**	Referencia egy g�rgethet� panelre, ami az �sszes pontot tartalmazza
	 * */
	JScrollPane scoreSp;

	/**	Konstrukor ami be�ll�tja a sz�ks�ges referenci�kat �s inicializ�lja a pontokat
	 * 	Az�rt sz�ks�ges a k�l�n f�ggv�ny, hogy a megfelel� m�rettel hozza l�tre a lista komponenst
	 * 	Sajnos a pnel minden elem�t ki kell t�r�lni ez�rt �gyis �jra el kell k�sz�teni az eg�sz panelt ha egy elemet kell friss�teni
	 * 
	 * 	@param wh	Referencia az ablakkezel� elt�rol�s�ra
	 * 	@param cf	Referencia az saj�t font elt�rol�s�ra
	 * 	@param sfh	Referencia az pont f�jl kezel� elt�rol�s�ra
	 * */
	public ScoresComponent(WindowHandler wh, Font cf, ScoreFileHandler sfh) {
		super(wh, cf);
		scrFh = sfh;

		initComponent();
	}
	
	/**	Elk�sz�t egy teljesen �j panelt, friss adatokkal
	 * 	Feladatai: panel/elemek l�trehoz�sa �s ezek be�ll�t�sa, adatokkal felt�lt�se a scrollpane-nek,
	 * 	saj�t font be�ll�t�sa, sz�vegek be�ll�t�sa, gomb callback be�ll�t�sa, m�retez�s,
	 * 	elrendez�s �s sz�nek be�ll�t�sa v�g�l pedig a panel m�retez�se
	 * */
	void initComponent() {
		// Panel l�trehoz�sa
		mainPanel = new JPanel();
		// Elrendez�s f�gg�legesen
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// Elemek l�trehoz�sa
		backBtn = new JButton();
		title = new JTextField();
		// Be�ll�t�sok
		title.setEditable(false);
		title.setHorizontalAlignment(JTextField.CENTER);

		// Adatokkal felt�lt�se a Scrollpane-nek
		JPanel list = createListFromData();
		scoreSp = new JScrollPane(list);

		// Saj�t font be�ll�t�sa
		if (customFont != null) {
			title.setFont(customFont.deriveFont(180f));
			title.revalidate();
			backBtn.setFont(customFont.deriveFont(60f));
			backBtn.revalidate();
			// scoreList.setFont(customFont.deriveFont(50f));
			// scoreList.revalidate();
		}

		// Sz�vegek be�ll�t�sa
		backBtn.setText("BACK TO MENU");
		title.setText("SCORES");

		// Gomb callback f�ggv�nye
		backBtn.addActionListener(e -> {
			// �tl�p�s a Men�be
			winHandler.setState(logic.GameStateMachine.State.MENU);
		});

		// M�retez�sek
		// BoxLayout miatt setMaximumSize a hat�sos
		int size = 260;
		backBtn.setMaximumSize(new Dimension(size, size / 3));
		title.setMaximumSize(new Dimension(size * 2, size));
		scoreSp.setMaximumSize(new Dimension(size * 3, size * 2));
		backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		// scoreList.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreSp.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreSp.setBorder(null);

		// Elrendez�s
		mainPanel.add(title);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(scoreSp);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(backBtn);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		// Sz�nek be�ll�t�sa
		mainPanel.setBackground(Color.black);
		scoreSp.setOpaque(false);
		scoreSp.getViewport().setOpaque(false);
		title.setForeground(Color.white);
		title.setOpaque(false);
		title.setBorder(null);
		backBtn.setContentAreaFilled(false);
		backBtn.setFocusPainted(false);
		backBtn.setForeground(Color.white);

		// Panel m�retez�se
		mainPanel.setPreferredSize(winHandler.windowDimension);
	}

	/**	Friss�ti az adatokat (�jra �ssze�ll�tja a panelt) majd ut�na	
	 * 	A param�terben kapott frame-hez hozz�adja az el�re elk�sz�tett komponenssekkel felt�lttt panelt
	 * @param frame Az ablak amihez hozz�adja a komponenseket
	 * */
	void addComponentsTo(JFrame frame) {
		// Adatokkal felt�lt�se a ScrollPane-nek
		initComponent();

		// Komponensek hozz�ad�sa az �sben megval�s�tva
		super.addComponentsTo(frame);
		
	}
	
	/**	Egy olyan panelt ad vissza ami egy lista n�zetet eredm�nyez
	 * 	A pontok kijelz�s�hez haszn�lt panel aut�matikusan k�sz�l el helyi pontok f�jlb�l
	 * 	Ez beleker�l majd egy g�rgethet� ablakba
	 * 	A listaelemek lefel� sima sz�vegmez�kb�l �llnak, amelyek egyes�vel be�ll�t�sra ker�lnek
	 * 
	 * 	@return A panel ami tartalmazza a list�t a pontokkal
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
