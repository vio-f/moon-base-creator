package gui;

import gui.actions.intToolbar.AddDome;
import gui.actions.intToolbar.RemoveShape;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
@SuppressWarnings("serial")
public class InternalPalleteToobar extends JPanel {
	/** toolButtons */
	public static ArrayList<JButton> toolButtons = new ArrayList<JButton>();
	/** addDome */
	AddDome addDome = new AddDome();
	/** remShape */
	RemoveShape remShape = new RemoveShape();
	
	/**
	 * Returns a new instance of the tool bars
	 */
	public InternalPalleteToobar() {

	    super();
		this.setPreferredSize(new Dimension(50, 300));
		
		this.setLayout(new GridLayout());
		// index 0 - sphere button + icon
		toolButtons.add(new JButton(""));
		//make button size relative to icon
		//toolButtons.get(0).setPreferredSize(new Dimension(20, 22));
		toolButtons.get(0).setAlignmentY(BOTTOM_ALIGNMENT);
		toolButtons.get(0).setAction(this.addDome);

		toolButtons.add(new JButton(""));
		//make button size relative to icon
		toolButtons.get(1).setPreferredSize(new Dimension(20, 22));
		toolButtons.get(1).setAlignmentY(BOTTOM_ALIGNMENT);
		toolButtons.get(1).setAction(this.remShape);
		
		
		JToolBar tb = new JToolBar(SwingConstants.VERTICAL);
		tb.setFloatable(false);
		for (JButton b : toolButtons) {
          tb.add(b);
        }
		this.add(tb);
		
		
		this.setVisible(true);
	}

	
	

}
