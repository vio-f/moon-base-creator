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

import sun.security.jca.GetInstance;
import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import _workspace.shapes.IShape;
import _workspace.shapes.ShapeListener;

/**
 * @author Viorel Florian
 * 
 */
public class RemoveShape extends AbstractAction {
	Icon remIcon = new ImageIcon(getClass().getResource("/res/cross.png"));
	private MoonWorkspaceInternalFrame selectedIntFr = null;
	private static IShape lastshape = null;

	public RemoveShape() {
		super();
		setDefaultPropreties();
	}

	/**
	 * Sets the default properties of the button
	 */
	private void setDefaultPropreties() {
		putValue(Action.NAME, "");
		putValue(Action.SHORT_DESCRIPTION, "Removes/deletes selected component");
		putValue(Action.LARGE_ICON_KEY, remIcon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MyLogger.info(this, "Atempting to remove component");

		selectedIntFr = MoonWorkspaceFactory.getInstance()
				.getLastSelectedIntFr();
		if (selectedIntFr != null) {
			lastshape = (IShape) ShapeListener.lastSelectedObj;
			try {
				if (lastshape != null) {
					lastshape.removeMe();
					MyLogger.info(this, "" + lastshape.getIdentifier()
							+ " removed");
				} else
					MyLogger.getLogger().error("No component selected");

			} catch (Exception e) {
				MyLogger.getLogger().error("Component could not be removed");

			}
		} else {
			MyLogger.getLogger().error("No valid workspace was found");
		}

	}

}
