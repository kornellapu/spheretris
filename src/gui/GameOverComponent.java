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

/**	A j�t�k v�g�n az el�r pontot tartalmazza �s egy kit�lthet� mez�t a n�vvel
 * 	A pontot elmenti egy f�jlba a men�be val� visszat�r�skor
 * */

public class GameOverComponent extends GuiComponent{
	
	/** A pontokat kezel� oszt�ly, ami �rja/olvassa a k�l�n f�jlban elhelyezked� pontokat
	 * */
	final ScoreFileHandler scrFh;
	
	/**	Referencia az itt tal�lhat� gombra, k�s�bbi m�dos�t�sok miatt
	 * */
	JButton okBtn;
	/**	Referencia egy sz�vegmez�re, c�m kijelz�s�re
	 * */
	JTextField title;
	/**	Referencia egy sz�vegmez�re, a v�gleges pont megjelen�t�s�hez  
	 * */
	JTextField score;
	/**	Referencia egy sz�vegmez�re, hogy ki�rja, hogy adja meg a nevet
	 * */
	JTextField prompt;
	/**	Referencia egy beviteli sz�vegmez�re, ide kell �rni a nevet
	 * */
	JTextField nameTf;
	
	/**	A v�gleges pontsz�m
	 * */
	int finalScore;	
	
	/**	Konstruktor, ami be�ll�tja komponenst �s elhelyezi azt egy panelben.
	 * 
	 * 	Az ell�tott feladatok:
	 * 	Panel l�trehoz�sa, elrendez�s be�ll�t�sa, elemeke l�trehoz�sa, elemek be�ll�t�sa,
	 * 	saj�t font be�ll�t�sa, sz�vegek be�ll�t�sa, a gomb callback f�ggv�nyek be�ll�t�sa,
	 * 	a nevet nagybet�kre transzform�lja egy bels� oszt�lybeli documentFilterrel,
	 * 	m�retez�s be�ll�t�sa, elrendez�s �s eltol�sok be�ll�t�sa fentr�l lefel�, sz�nek be�ll�t�sa
	 * 	 �s v�g�l panel m�retez�se
	 * 
	 * 	@param windowHandler Ablakkezel� a refrencia elt�rol�s�hoz
	 * 	@param cF Saj�t Font a refrencia elt�rol�s�hoz
	 * 	@param sFh Pont f�jl kezel� a refrencia elt�rol�s�hoz
	 * */
	public GameOverComponent(WindowHandler windowHandler, Font cF, ScoreFileHandler sFh) {
		super(windowHandler, cF);
		
		scrFh = sFh;
		
		//Panel l�trehoz�sa
		mainPanel = new JPanel();
		//Elrendez�s be�ll�t�sa
		mainPanel.setLayout( new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
		
		//Elemek l�trehoz�sa
		okBtn = new JButton();
		title = new JTextField();
		score = new JTextField();
		nameTf = new JTextField();
		prompt = new JTextField();
		//Be�ll�t�sok
		title.setEditable(false);
		score.setEditable(false);
		prompt.setEditable(false);
		title.setHorizontalAlignment(JTextField.CENTER);
		score.setHorizontalAlignment(JTextField.CENTER);
		prompt.setHorizontalAlignment(JTextField.CENTER);
		nameTf.setHorizontalAlignment(JTextField.CENTER);
		
		//Saj�t fonttal
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
		
		//Sz�vegek be�ll�t�sa
		okBtn.setText("OK!");
		title.setText("GAME OVER");		
		prompt.setText("ENTER YOUR NAME:");
		
		//Gomb callback f�ggv�nye
		okBtn.addActionListener( e -> {
			
			//Pont ment�se
			String name = nameTf.getText();
			if(name.isBlank()) {
				name = "ANONYMOUS";
			}
			//Aut�matikusan elmenti
			scrFh.addNewScore(finalScore, name);
			
			
			//�tl�p�s a pontokhoz
			windowHandler.setState(logic.GameStateMachine.State.SCORES);
		} );
		
		//A n�v be�r�sa csak nagybet�kre �ll�t�ssal
		DocumentFilter df = new UpperCaseDocumentFilter();
		AbstractDocument nameDoc = (AbstractDocument) nameTf.getDocument();
		nameDoc.setDocumentFilter(df);
		
		//M�retez�sek
		//BoxLayout miatt setMaximumSize a hat�sos
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
		
		//Elrendez�s �s eltol�sok fentr�l lefel�
		mainPanel.add(title);
		mainPanel.add(score);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		mainPanel.add(prompt);
		mainPanel.add(nameTf);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		mainPanel.add(okBtn);
		
		//Sz�nek be�ll�t�sa
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
		
		//A panel m�retez�se
		mainPanel.setPreferredSize(winHandler.windowDimension);
	}
	
	/** A param�ter�l kapott frame-hez hozz�adja a saj�t be�ll�tott panelj�t
	 * 	Emellett be�ll�tja a v�gleges pontot, amit kijelez
	 * 
	 * 	@param frame Amihez hozz�adja az elk�sz�tett elemeket
	 * */
	void addComponentsTo(JFrame frame) {
		//V�gs� pont be�ll�t�sa
		finalScore = winHandler.gl.getFinalScore();
		System.out.println("Score: " + winHandler.gl.getScore());
		score.setText((scrFh.getHighestScore() < finalScore ? "NEW HIGH " : "") + "SCORE: " + finalScore + " PT" + (finalScore > 1 ? "S" : ""));
		
		//Komponensek hozz�ad�sa, az �sben megval�s�tva
		super.addComponentsTo(frame);
	}
	
	/**	A nagybet�kre alak�t�s miatt sz�ks�ges
	 * 	A n�v mez�h�z van csatolva, hogy minden karaktert alak�tson nagy bet�kre.
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
