
package stuff;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
public class MyAction extends AbstractAction {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
   * Constructs a new instance.
   */
  public MyAction() {
    super();
    putValue(Action.NAME, "Aaaaa");
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    System.out.println("MyAction.actionPerformed()");
  }

}
