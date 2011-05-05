/**
 * 
 */
package gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * @author Viorel Florian
 *
 */
public class InfoPanel extends JPanel {
	public JTextPane mbcOutput = new JTextPane();
	public JScrollPane scrl = new JScrollPane(mbcOutput);
	

	/**
	 * 
	 */
	public InfoPanel() {
		super();
		this.setLayout(new GridLayout());
		//mbcOutput.setEditable(false);
		this.add(scrl);
		
	}

}
