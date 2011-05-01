/**
 * 
 */
package gui.actions.menubar;

import gui.MoonWorkspaceFactory;
import gui.MoonWorkspaceInternalFrame;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import utility.MyLogger;

/**
 * @author Viorel Florian
 *
 */
public class EditShowHideLayerTreeAct extends AbstractAction {
	MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

	
	/**
	 * Constructs a new instance.
	 */
	public EditShowHideLayerTreeAct() {
		super();
		setDefaultPropreties();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		MyLogger.info(this, "Show/hide has been pressed");
		
		if (selectedIntFr != null) {
			selectedIntFr.showHideLayerTree();
		} else
		MyLogger.error(this, "No suitable wwCanvas was found"); //new NullPointerException()
	}

	private void setDefaultPropreties() {
	    putValue(Action.NAME, "Show/Hide Layer Tree");
	    /*KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
	    putValue(Action.ACCELERATOR_KEY, key);*/
	    
	
	}
	
	
}
