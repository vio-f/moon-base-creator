
package gui.actions.menubar;

import gui.BaseFrame;
import gui.ProgressDialog;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import _workspace.MoonWorkspaceFactory;

import utility.MyLogger;

/**
 * @author Viorel Florian
 * <p>Defines the action for File->New item
 */
public class FileNewAct extends AbstractAction  {
	private JFrame f;
	
	
	  /**
	   * Constructs a new instance.
	   * @param Parent JFrame
	   */
	  public FileNewAct(JFrame f) {
	    super();
	    this.f = f;
	    setDefaultPropreties();
	  }

	/**
	 *  Sets the default properties of the button
	 */
	private void setDefaultPropreties() {
	    putValue(Action.NAME, "New...");
	    KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
	    putValue(Action.ACCELERATOR_KEY, key);
	    
	
	}
	  

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MyLogger.info(this, "New has been pressed");
		BaseFrame.editShowHideLayerTreeItem.setEnabled(true);
		// TODO remove this
		final ProgressDialog progressDialog = new ProgressDialog(this.f);

		@SuppressWarnings("rawtypes")
		SwingWorker sw = new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				try {
					progressDialog.start();
					
					MoonWorkspaceFactory.getInstance().newMoonWorkspace();
					progressDialog.stop();
				} catch (Exception e) {
					//Exception e1 = new Exception("Error while workspace creation.", e);
					MyLogger.error(this, "Error while workspace creation.",e);
					
				}
				return null;
			}
		};

		sw.execute();
		
	}

}
