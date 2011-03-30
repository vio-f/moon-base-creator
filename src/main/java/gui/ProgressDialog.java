package gui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * 
 * @author Viorel Florian
 * 
 *
 */
public class ProgressDialog extends JFrame {

	JProgressBar pBar = new JProgressBar(0, 500);

	boolean keepRunning = false;

	// TODO JButton cancelButton = new JButton("Cancel process...");

	public ProgressDialog(JFrame f) {
		super("Patience is a virtue...");

		// progDiag.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setLocationRelativeTo(f);
		this.setUndecorated(true);
		this.setBackground(new Color(0, 0, 0, 0));

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pBar.setString("Good things in progress...");
		pBar.setStringPainted(true);
		// pBar.setValue(0);
		pBar.setIndeterminate(true);
		
		this.add(this.pBar);
		this.pack();
	}

	/**
	 * 
	 */
	public void start() {

		this.keepRunning = true;
		this.update();
	}

	/**
	 * 
	 */
	public void stop() {

		this.keepRunning = false;
		this.update();
	}

	/**
	 * 
	 */
	private void update() {

		this.setVisible(this.keepRunning);
		this.pBar.setVisible(this.keepRunning);
		this.pBar.setEnabled(this.keepRunning);
	}

}
