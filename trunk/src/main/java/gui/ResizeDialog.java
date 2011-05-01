/**
 * 
 */
package gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Viorel Florian
 * 
 */
public class ResizeDialog extends JDialog {
	/**
	 * @author Viorel Florian
	 *
	 */


	protected static JSlider slider = new JSlider(10, 1000, 100);
	protected static int sizeBoxValue = 100;
	protected static JFormattedTextField sizeBox = new JFormattedTextField(
			sizeBoxValue); // defines the format of the JFormattedTextField

	/**
	 * 
	 */
	public ResizeDialog() {
		super();
		this.setTitle("Resize Component");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new FlowLayout());
		sizeBox.setColumns(6);
		
		
		slider.addChangeListener(new SliderChangeListener());
		sizeBox.addActionListener(new BoxListener());

		this.add(slider);
		this.add(sizeBox);

		this.pack();
		this.setVisible(true);
	}

	class SliderChangeListener implements ChangeListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
		 * )
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			sizeBoxValue = slider.getValue();
			sizeBox.setValue(sizeBoxValue);
		}
	}


	class BoxListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			sizeBoxValue = (Integer)sizeBox.getValue();
			slider.setValue(sizeBoxValue);
		}

	}
	
	//EOF
}


