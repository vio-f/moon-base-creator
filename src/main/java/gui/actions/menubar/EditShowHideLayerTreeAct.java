/**
 * 
 */
package gui.actions.menubar;

import gui.MoonWorkspaceFactory;
import gui.MoonWorkspaceInternalFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

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

}
