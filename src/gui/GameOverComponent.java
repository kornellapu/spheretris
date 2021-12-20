package gui;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import fileio.ScoreFileHandler;

import java.awt.Font;
import java.awt.Component;
import java.awt.Color;

/**	A játék végén az elér pontot tartalmazza és egy kitölthetõ mezõt a névvel
 * 	A pontot elmenti egy fájlba a menübe való visszatéréskor
 * */

public class GameOverComponent extends GuiComponent{
	
	/** A pontokat kezelõ osztály, ami írja/olvassa a külön fájlban elhelyezkedõ pontokat
	 * */
	final ScoreFileHandler scrFh;
	
	/**	Referencia az itt található gombra, késõbbi módosítások miatt
	 * */
	JButton okBtn;
	/**	Referencia egy szövegmezõre, cím kijelzésére
	 * */
	JTextField title;
	/**	Referencia egy szövegmezõre, a végleges pont megjelenítéséhez  
	 * */
	JTextField score;
	/**	Referencia egy szövegmezõre, hogy kiírja, hogy adja meg a nevet
	 * */
	JTextField prompt;
	/**	Referencia egy beviteli szövegmezõre, ide kell írni a nevet
	 * */
	JTextField nameTf;
	
	/**	A végleges pontszám
	 * */
	int finalScore;	
	
	/**	Konstruktor, ami beállítja komponenst és elhelyezi azt egy panelben.
	 * 
	 * 	Az ellátott feladatok:
	 * 	Panel létrehozása, elrendezés beállítása, elemeke létrehozása, elemek beállítása,
	 * 	saját font beállítása, szövegek beállítása, a gomb callback függvények beállítása,
	 * 	a nevet nagybetûkre transzformálja egy belsõ osztálybeli documentFilterrel,
	 * 	méretezés beállítása, elrendezés és eltolások beállítása fentrõl lefelé, színek beállítása
	 * 	 és végül panel méretezése
	 * 
	 * 	@param windowHandler Ablakkezelõ a refrencia eltárolásához
	 * 	@param cF Saját Font a refrencia eltárolásához
	 * 	@param sFh Pont fájl kezelõ a refrencia eltárolásához
	 * */
	public GameOverComponent(WindowHandler windowHandler, Font cF, ScoreFileHandler sFh) {
		super(windowHandler, cF);
		
		scrFh = sFh;
		
		//Panel létrehozása
		mainPanel = new JPanel();
		//Elrendezés beállítása
		mainPanel.setLayout( new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
		
		//Elemek létrehozása
		okBtn = new JButton();
		title = new JTextField();
		score = new JTextField();
		nameTf = new JTextField();
		prompt = new JTextField();
		//Beállítások
		title.setEditable(false);
		score.setEditable(false);
		prompt.setEditable(false);
		title.setHorizontalAlignment(JTextField.CENTER);
		score.setHorizontalAlignment(JTextField.CENTER);
		prompt.setHorizontalAlignment(JTextField.CENTER);
		nameTf.setHorizontalAlignment(JTextField.CENTER);
		
		//Saját fonttal
		if(customFont != null) {
			okBtn.setFont(customFont.deriveFont(90f));
			okBtn.revalidate();
			title.setFont(customFont.deriveFont(180f));
			title.revalidate();
			score.setFont(customFont.deriveFont(90f));
			score.revalidate();
			prompt.setFont(customFont.deriveFont(90f));
			prompt.revalidate();
			nameTf.setFont(customFont.deriveFont(120f));
			nameTf.revalidate();
		}
		
		//Szövegek beállítása
		okBtn.setText("OK!");
		title.setText("GAME OVER");		
		prompt.setText("ENTER YOUR NAME:");
		
		//Gomb callback függvénye
		okBtn.addActionListener( e -> {
			
			//Pont mentése
			String name = nameTf.getText();
			if(name.isBlank()) {
				name = "ANONYMOUS";
			}
			//Autómatikusan elmenti
			scrFh.addNewScore(finalScore, name);
			
			
			//Átlépés a pontokhoz
			windowHandler.setState(logic.GameStateMachine.State.SCORES);
		} );
		
		//A név beírása csak nagybetûkre állítással
		DocumentFilter df = new UpperCaseDocumentFilter();
		AbstractDocument nameDoc = (AbstractDocument) nameTf.getDocument();
		nameDoc.setDocumentFilter(df);
		
		//Méretezések
		//BoxLayout miatt setMaximumSize a hatásos
		int size = 260;
		okBtn.setMaximumSize(new Dimension(size, size/2));
		title.setMaximumSize(new Dimension(size*2, size-100));
		score.setMaximumSize(new Dimension(size*3, size/4+20));
		nameTf.setMaximumSize(new Dimension(size*2, size/2));
		prompt.setMaximumSize(new Dimension(size*2, size/4));
		okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		score.setAlignmentX(Component.CENTER_ALIGNMENT);
		nameTf.setAlignmentX(Component.CENTER_ALIGNMENT);
		prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Elrendezés és eltolások fentrõl lefelé
		mainPanel.add(title);
		mainPanel.add(score);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		mainPanel.add(prompt);
		mainPanel.add(nameTf);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		mainPanel.add(okBtn);
		
		//Színek beállítása
		mainPanel.setBackground(Color.black);
		title.setOpaque(false);
		title.setBorder(null);
		title.setForeground(Color.white);
		score.setOpaque(false);
		score.setBorder(null);
		score.setForeground(Color.white);
		nameTf.setOpaque(false);
		nameTf.setForeground(Color.white);
		okBtn.setContentAreaFilled(false);
		okBtn.setFocusPainted(false);
		okBtn.setForeground(Color.white);
		prompt.setForeground(Color.black);
		
		//A panel méretezése
		mainPanel.setPreferredSize(winHandler.windowDimension);
	}
	
	/** A paraméterül kapott frame-hez hozzáadja a saját beállított paneljét
	 * 	Emellett beállítja a végleges pontot, amit kijelez
	 * 
	 * 	@param frame Amihez hozzáadja az elkészített elemeket
	 * */
	void addComponentsTo(JFrame frame) {
		//Végsõ pont beállítása
		finalScore = winHandler.gl.getFinalScore();
		System.out.println("Score: " + winHandler.gl.getScore());
		score.setText((scrFh.getHighestScore() < finalScore ? "NEW HIGH " : "") + "SCORE: " + finalScore + " PT" + (finalScore > 1 ? "S" : ""));
		
		//Komponensek hozzáadása, az õsben megvalósítva
		super.addComponentsTo(frame);
	}
	
	/**	A nagybetûkre alakítás miatt szükséges
	 * 	A név mezõhöz van csatolva, hogy minden karaktert alakítson nagy betûkre.
	 * */
	class UpperCaseDocumentFilter extends DocumentFilter{
		@Override
		public void insertString(DocumentFilter.FilterBypass filterBypass, int offs, String text, AttributeSet attrSet) throws BadLocationException {
			filterBypass.insertString(offs, text.toUpperCase(), attrSet);
		}
		
		@Override
		public void replace(DocumentFilter.FilterBypass filterBypass, int offs, int length, String text, AttributeSet attrSet) throws BadLocationException {
			filterBypass.replace(offs, length, text.toUpperCase(), attrSet);
		}
	}

}
