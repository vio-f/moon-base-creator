/**
 * 
 */
package gui.actions.menubar;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Position;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import _workspace.MoonWorkspaceFactory;

/**
 * @author Viorel Florian
 * 
 */
public class EditResizeAct extends AbstractAction {

	/**
	 * 
	 */
	public EditResizeAct() {
	    super();
	    setDefaultPropreties();
	}

	/**
	 * Sets the default properties of the button
	 */
	private void setDefaultPropreties() {
		putValue(Action.NAME, "Resize component");
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_R,
				InputEvent.CTRL_DOWN_MASK);
		putValue(Action.ACCELERATOR_KEY, key);

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//ToolWindowsPanel.addCompToThisPanel(ResizeComponent.getInstance());
	  View v = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr().wwGLCanvas.getView();
	  v.setEyePosition(Position.fromDegrees(0, 0, 5000));
	  MoonWorkspaceFactory.getInstance().getLastSelectedIntFr().wwGLCanvas.setView(v);
	  MoonWorkspaceFactory.getInstance().getLastSelectedIntFr().wwGLCanvas.redraw();
	  

	}
}
