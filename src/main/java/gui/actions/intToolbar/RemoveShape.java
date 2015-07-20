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
import _workspace.shapes.IShape;
import _workspace.shapes.ShapeListener;

/**
 * @author Viorel Florian
 * 
 */
public class RemoveShape extends AbstractIntToolbarAct {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2220196133534513998L;
	Icon remIcon = new ImageIcon(getClass().getResource("/res/cross.png"));
	private MoonWorkspaceInternalFrame selectedIntFr = null;
	private static IShape lastshape = null;

	/**
	 * Constructs a new instance.
	 */
	public RemoveShape() {
		super();
		setDefaultPropreties();
	}

	/**
	 * Sets the default properties of the button
	 */
	protected void setDefaultPropreties() {
		putValue(Action.NAME, "");
		putValue(Action.SHORT_DESCRIPTION, "Removes/deletes selected component");
		putValue(Action.LARGE_ICON_KEY, this.remIcon);
	}


	/**
	 * @see gui.actions.intToolbar.AbstractIntToolbarAct#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MyLogger.info(this, "Atempting to remove component");

		this.selectedIntFr = MoonWorkspaceFactory.getInstance()
				.getLastSelectedIntFr();
		if (this.selectedIntFr != null) {
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
