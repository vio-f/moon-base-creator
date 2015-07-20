package stuff;

import gui.BaseFrame;
import gui.ProgressDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;

//TODO explore EVENTHANDLER
/**
 * 
 */
public class GuiActions extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFrame f;

	GuiActions(JFrame f) {
		this.f = f;
	}

	// @Override
	public void actionPerformed(ActionEvent e) {
//		MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

		if (e.getSource().equals(BaseFrame.getInstance().getFileNewItem())) {
			// System.out.println("New has been pressed");
			MyLogger.info(this, "New has been pressed");
			// TODO remove this
			final ProgressDialog progressDialog = new ProgressDialog(f);

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
		if (e.getSource().equals(BaseFrame.getInstance().getFileExitItem())) {
			if (JOptionPane.showConfirmDialog(null,
					"Are you sure you wish to exit?") == JOptionPane.YES_OPTION) {
				f.setVisible(false);
				f.dispose();
				MyLogger.info(this, "Application closed on request of user");
				System.exit(0);
			}
		}

		/*if (e.getSource().equals(BaseFrame.getInstance().getEditTiltItem())) {
			MyLogger.info(this, "Show/hide has been pressed");
			
			if (selectedIntFr != null) {
				selectedIntFr.showHideLayerTree();
				
				
			} else
			MyLogger.error(this, "No suitable wwCanvas was found"); //new NullPointerException()
		}
		
		if (e.getSource().equals(BaseFrame.getInstance().getEditMoveItem())) {
			MyLogger.getLogger().info("Move pressed");
			//selectedIntFr.moveMe(Position.fromDegrees(1, 1, 2));
			//selectedIntFr.wwGLCanvas.redraw();
		}*/
	}

}
