/**
 * 
 */
package gui.actions.menubar;

import gui.BaseFrame;
import gui.jaccordian.JAcordionBar;
import gui.jaccordian.jAccordionPanels.GoToPositionPanel;

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
@SuppressWarnings("serial")
public class EditGoToAct extends AbstractAction {


  /** COMPONET_NAME */
  private final String COMPONET_NAME = "Go To Position";

  /**
	 * 
	 */
	public EditGoToAct() {
	    super();
	    setDefaultPropreties();
	}

	/**
	 * Sets the default properties of the button
	 */
	private void setDefaultPropreties() {
		putValue(Action.NAME, "Set view to...");
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_G,
				InputEvent.CTRL_DOWN_MASK);
		putValue(Action.ACCELERATOR_KEY, key);

	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
	  BaseFrame.getInstance().getPropertiesPanel().addBar(this.COMPONET_NAME, new JAcordionBar(this.COMPONET_NAME, GoToPositionPanel.getInstance()));

	}
}
