/**
 * 
 */
package gui.actions.intToolbar;

import gui.MoonWorkspaceFactory;
import gui.MoonWorkspaceInternalFrame;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import utility.MyLogger;
import _workspace.shapes.DomeShape;

/**
 * @author Viorel Florian
 * 
 */
public class AddDome extends AbstractAction {
	private DomeShape lastDome = null;
	Icon sphereIcon = new ImageIcon(getClass().getResource("/res/sphere.png"));

	/**
	 * 
	 */
	public AddDome() {
		super();
		setDefaultPropreties();
	}

	/**
	 * Sets the default properties of the button
	 */
	private void setDefaultPropreties() {
		putValue(Action.NAME, "");
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_DOWN_MASK);
		putValue(Action.ACCELERATOR_KEY, key);
		putValue(Action.SMALL_ICON, sphereIcon);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory
				.getInstance().getLastSelectedIntFr();
		DomeShape d;
		try {
			MyLogger.info(this, "New sphere pressed");
			if (selectedIntFr != null) {
				d = new DomeShape(selectedIntFr.wwGLCanvas);

				this.lastDome = d;
				MyLogger.info(this, "" + d.getIdentifier() + " added");
			} else
				MyLogger.error(this, "No suitable workspace was found"); // new
																			// NullPointerException()
																			// can
																			// be
																			// added
		} catch (Exception e) {
			MyLogger.error(this, e); // new NullPointerException() can be added
		}
	}

}
