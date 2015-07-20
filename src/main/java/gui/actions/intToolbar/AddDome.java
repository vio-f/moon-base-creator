/**
 * 
 */
package gui.actions.intToolbar;


import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import _workspace.shapes.DomeShape;
import _workspace.shapes.IShape;

/**
 * @author Viorel Florian
 * 
 */
public class AddDome extends AbstractIntToolbarAct {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7271820311126222043L;
	@SuppressWarnings("unused")
	private IShape lastDome = null; //TODO link this to the ShapeListner
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
	@Override
  protected void setDefaultPropreties() {
		putValue(Action.NAME, "");
		putValue(Action.SHORT_DESCRIPTION, "Creates a Dome relative to current " +
				"altitude in the center of the workspace");
		putValue(Action.SMALL_ICON, sphereIcon);

	}

	/**
	 * @see gui.actions.intToolbar.AbstractIntToolbarAct#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory
				.getInstance().getLastSelectedIntFr();
		IShape d;
		try {
			MyLogger.info(this, "New sphere pressed");
			if (selectedIntFr != null) {
				d = new DomeShape(selectedIntFr.getWwGLCanvas());

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
