package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

//TODO explore EVENTHANDLER
public class GuiActions extends AbstractAction {
	JFrame f;
	GuiActions(JFrame f){
		this.f = f;
	}



	//@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(BaseFrame.fileNewItem)){
			
		}
		
		if (e.getSource().equals(BaseFrame.fileExitItem)){
			if (JOptionPane.showConfirmDialog
		             (null,"Are you sure you wish to exit?")==JOptionPane.YES_OPTION) {
				f.setVisible(false);
		        f.dispose();
		        
		           }

		}
	}

}
