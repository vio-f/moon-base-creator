/**
 * 
 */
package gui.actions.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import utility.MyLogger;

/**
 * @author Viorel Florian
 * Defines the action for File->Exit item
 */
public class FileExitAct extends AbstractAction {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public FileExitAct() {
	    super();
	    setDefaultPropreties();
	}
	
	
	/**
	 *  Sets the default properties of the button
	 */
	private void setDefaultPropreties() {
	    putValue(Action.NAME, "Exit...");
	    KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
	    putValue(Action.ACCELERATOR_KEY, key);
	    
	
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (JOptionPane.showConfirmDialog(null,
		"Are you sure you wish to exit?") == JOptionPane.YES_OPTION) {
			//f.setVisible(false);
			//f.dispose();
			MyLogger.info(this, "Application closed on request of user");
			System.exit(0);
		}
	}
}
