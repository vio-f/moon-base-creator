/**
 * 
 */
package gui.actions.menubar;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Position;
import gui.BaseFrame;
import gui.jaccordian.JAcordionBar;
import gui.jaccordian.jAccordionPanels.ChangeRollPanel;
import gui.jaccordian.jAccordionPanels.ChangeTiltPanel;
import gui.jaccordian.jAccordionPanels.ResizePanel;

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
@SuppressWarnings("serial")
public class EditRollAct extends AbstractAction {
  /** COMPONET_NAME */
  private final String  COMPONET_NAME = "Roll component";

	/**
	 * 
	 */
	public EditRollAct() {
	    super();
	    setDefaultPropreties();
	}

	/**
	 * Sets the default properties of the button
	 */
	private void setDefaultPropreties() {
		putValue(Action.NAME, this.COMPONET_NAME);
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				InputEvent.CTRL_DOWN_MASK);
		putValue(Action.ACCELERATOR_KEY, key);

	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
	  
	  BaseFrame.getInstance().getPropertiesPanel().addBar(this.COMPONET_NAME, new JAcordionBar(this.COMPONET_NAME, ChangeRollPanel.getInstance()));
	  

	}
}
