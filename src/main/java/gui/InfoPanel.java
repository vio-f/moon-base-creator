/**
 * 
 */
package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * @author Viorel Florian
 *
 */
public class InfoPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *  mbcOutput Logger writes here too  
	 * 
	 */
	private static JTextPane mbcOutput = new JTextPane();
	
	/** scrl - scrollpane containing the text pane  
	 * */
	public static JScrollPane scrl = new JScrollPane(mbcOutput);
	

	/**
	 * 
	 */
	public InfoPanel() {
		super();
		this.setLayout(new GridLayout());
		//mbcOutput.setEditable(false);
		this.setPreferredSize(new Dimension(1, 70));
		this.add(scrl);
		
	}


  /**
   * Get mbcOutput.
   * 
   * @return mbcOutput
   */
  public static JTextPane getMbcOutput() {
    return mbcOutput;
  }

}
