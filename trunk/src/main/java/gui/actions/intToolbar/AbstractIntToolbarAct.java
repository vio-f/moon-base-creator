/**
 * 
 */
package gui.actions.intToolbar;


import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import _workspace.shapes.DomeShape;
import _workspace.shapes.IShape;

/**
 * @author Viorel Florian
 * 
 */
public abstract class AbstractIntToolbarAct extends AbstractAction {
	

	/**
	 * Constructor
	 */
	public AbstractIntToolbarAct() {
		super();
	}

	/**
	 * Sets the default properties of the button
	 */
	protected abstract void setDefaultPropreties();


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public abstract void actionPerformed(ActionEvent arg0);
	

}
