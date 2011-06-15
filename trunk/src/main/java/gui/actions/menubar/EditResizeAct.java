/**
 * 
 */
package gui.actions.menubar;

import gui.ResizeComponent;
import gui.ToolWindowsPanel;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

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
		ToolWindowsPanel.addCompToThisPanel(ResizeComponent.getInstance());

	}
}
