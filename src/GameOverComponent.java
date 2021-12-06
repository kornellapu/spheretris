import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import java.awt.Font;
import java.awt.Component;
import java.awt.Color;

/**	A játék végén az elér pontot tartalmazza és egy kitölthetõ mezõt a névvel
 * 	A pontot elmenti egy fájlba a menübe való visszatéréskor
 * */

public class GameOverComponent extends JComponent {

	/** Az Eclipse boldoggá tétele
	 */
	private static final long serialVersionUID = 1L;
	
	/**	Referencia az õt tartalmazó ablakról
	 * 	Szükséges, hiszen dinamikusan változó tartalom miatt az osztály tudja hozzáadni a saját tartalmát ahhoz a JFramehez amit kap
	 * */
	final WindowHandler winHandler;
	/**	A saját fontra mutató referencia, azért, hogy ne kelljen minden komponensben külön betölteni
	 * */
	final Font customFont;
	/** A pontokat kezelõ osztály, ami írja/olvassa a kólön fájlban elhelyezkedõ pontokat
	 * */
	final ScoreFileHandler scrFh;
	
	/**	Referencia az itt található gomra, késõbbi módosítások miatt
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
	/**	
	 * */
	JTextField nameTf;
	
	int finalScore;	
	
	JPanel gameOverPanel;
	
	public GameOverComponent(WindowHandler windowHandler, Font cF, ScoreFileHandler sFh) {
		super();
		customFont = cF;
		winHandler = windowHandler;
		scrFh = sFh;
		
		//Panel létrehozása
		gameOverPanel = new JPanel();
		//Elrendezés beállítása
		gameOverPanel.setLayout( new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS) );
		
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
			windowHandler.setState(GameStateMachine.State.SCORES);
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
		gameOverPanel.add(title);
		gameOverPanel.add(score);
		gameOverPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		gameOverPanel.add(prompt);
		gameOverPanel.add(nameTf);
		gameOverPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		gameOverPanel.add(okBtn);
		
		//Színek beállítása
		gameOverPanel.setBackground(Color.black);
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
		gameOverPanel.setPreferredSize(winHandler.windowDimension);
	}
	
	void addComponentsTo(JFrame frame) {
		//Végsõ pont beállítása
		finalScore = winHandler.gl.getFinalScore();
		System.out.println("Score: " + winHandler.gl.getScore());
		score.setText((scrFh.getHighestScore() < finalScore ? "NEW HIGH " : "") + "SCORE: " + finalScore + " PT" + (finalScore > 1 ? "S" : ""));
		
		//Komponensek hozzáadása
		frame.add(gameOverPanel);
		frame.setSize(winHandler.windowDimension);
		frame.pack();
	}
	
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
