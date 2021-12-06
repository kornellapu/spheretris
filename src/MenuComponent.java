import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Component;
import java.awt.Color;

/**	Menü elemeket tartalamzó komponens
 * */
public class MenuComponent extends JComponent{

	/**	Eclipse boldogítása
	 */
	private static final long serialVersionUID = 1L;
	
	final WindowHandler winHandler;
	final Font customFont;
	
	JPanel menuPanel;
	JButton newGameBtn;
	JButton scoresBtn;
	JTextField title;
	
	
	public MenuComponent(WindowHandler windowHandler, Font cF) {
		super();
		customFont = cF;
		winHandler = windowHandler;
		
		//Panel létrehozása
		menuPanel = new JPanel();
		//Panel elrendezésének beállítása
		menuPanel.setLayout( new BoxLayout(menuPanel, BoxLayout.Y_AXIS) );
		
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
			windowHandler.setState(GameStateMachine.State.GAME);
		} );
		scoresBtn.addActionListener( e -> {
			windowHandler.setState(GameStateMachine.State.SCORES);
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
		menuPanel.add(title);
		menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		menuPanel.add(newGameBtn);
		menuPanel.add(Box.createRigidArea(new Dimension(0, 60)));
		menuPanel.add(scoresBtn);
		
		//Színek beállítása
		menuPanel.setBackground(Color.black);
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
		menuPanel.setPreferredSize(winHandler.windowDimension);
	}
	
	void addComponentsTo(JFrame frame) {
		frame.add(menuPanel);
		frame.setSize(winHandler.windowDimension);
		frame.pack();
	}

}
