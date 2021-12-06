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
 * A pontok kijelz�s�hez
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
		// Panel l�trehoz�sa
				scoresPanel = new JPanel();
				// Elrendez�s f�gg�legesen
				scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));

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
					winHandler.setState(GameStateMachine.State.MENU);
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
				scoresPanel.add(title);
				scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));
				scoresPanel.add(scoreSp);
				scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));
				scoresPanel.add(backBtn);
				scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));

				// Sz�nek be�ll�t�sa
				scoresPanel.setBackground(Color.black);
				scoreSp.setOpaque(false);
				scoreSp.getViewport().setOpaque(false);
				title.setForeground(Color.white);
				title.setOpaque(false);
				title.setBorder(null);
				backBtn.setContentAreaFilled(false);
				backBtn.setFocusPainted(false);
				backBtn.setForeground(Color.white);

				// Panel m�retez�se
				scoresPanel.setPreferredSize(winHandler.windowDimension);
	}

	void addComponentsTo(JFrame frame) {
		// Adatokkal felt�lt�se a ScrollPane-nek
		initComponent();

		// Komponensek hozz�ad�sa
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
