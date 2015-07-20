/**
 * 
 */
package gui.actions.intToolbar;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * @author Viorel Florian
 * 
 */
public abstract class AbstractIntToolbarAct extends AbstractAction {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


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
