import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Component;
import java.awt.Color;

/**
 * A pontok kijelzéséhez
 */

public class ScoresComponent extends JComponent {

	private static final long serialVersionUID = 1L;

	final WindowHandler winHandler;
	final Font customFont;
	final ScoreFileHandler scrFh;

	JButton backBtn;
	JTextField title;
	JScrollPane scoreSp;

	JPanel scoresPanel;

	public ScoresComponent(WindowHandler wh, Font cf, ScoreFileHandler sfh) {
		super();
		winHandler = wh;
		customFont = cf;
		scrFh = sfh;

		initComponent();
	}
	
	void initComponent() {
		// Panel létrehozása
				scoresPanel = new JPanel();
				// Elrendezés függõlegesen
				scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));

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
					winHandler.setState(GameStateMachine.State.MENU);
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
				scoresPanel.add(title);
				scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));
				scoresPanel.add(scoreSp);
				scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));
				scoresPanel.add(backBtn);
				scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));

				// Színek beállítása
				scoresPanel.setBackground(Color.black);
				scoreSp.setOpaque(false);
				scoreSp.getViewport().setOpaque(false);
				title.setForeground(Color.white);
				title.setOpaque(false);
				title.setBorder(null);
				backBtn.setContentAreaFilled(false);
				backBtn.setFocusPainted(false);
				backBtn.setForeground(Color.white);

				// Panel méretezése
				scoresPanel.setPreferredSize(winHandler.windowDimension);
	}

	void addComponentsTo(JFrame frame) {
		// Adatokkal feltöltése a ScrollPane-nek
		initComponent();

		// Komponensek hozzáadása
		frame.add(scoresPanel);
		frame.setSize(winHandler.windowDimension);
		frame.pack();
	}

	JPanel createListFromData() {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

		scrFh.readFromFile();

		ArrayList<ScoreFileHandler.Score> data = scrFh.getScores();
		Collections.sort(data);
		Collections.reverse(data);
		
		for (int i = 0; i < data.size(); i++) {
			JTextField listItem = new JTextField(
					"#" + (i + 1) + " " + data.get(i).name + " " + data.get(i).score + " PTS");
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
