package gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

//TODO explore EVENTHANDLER
/**
 * 
 */
public class GuiActions extends AbstractAction {
	JFrame f;
	GenericThread task;

	// GenericNewTask showProgDiag;

	GuiActions(JFrame f) {
		this.f = f;
	}

	// @Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(BaseFrame.fileNewItem)) {
			System.out.println("New has been pressed");
			// TODO remove this
			final ProgressDialog progressDialog = new ProgressDialog(f);
			// final Thread progressBarThread = new Thread(progressDialog);

			@SuppressWarnings("rawtypes")
			SwingWorker sw = new SwingWorker() {

				@Override
				protected Object doInBackground() throws Exception {

					progressDialog.start();

					new MoonWorkspaceInternalFrame();

					progressDialog.stop();

					return null;
				}

			};

			sw.execute();

		}

		if (e.getSource().equals(BaseFrame.fileExitItem)) {
			if (JOptionPane.showConfirmDialog(null,
					"Are you sure you wish to exit?") == JOptionPane.YES_OPTION) {
				f.setVisible(false);
				f.dispose();
				System.exit(0);
			}

		}
	}

}
