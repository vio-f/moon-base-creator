package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

//TODO explore EVNTHANDLER
public class GuiActions extends BaseFrame implements ActionListener {
	JFrame f;
	String src;
	GuiActions(JFrame f){
		this.f = f;
		this.src = src;
	}



	//@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("An event was fired");
		System.out.println(e.getSource().toString());
		System.out.println(e.getActionCommand());
		if (src == "fileExitItem"){
			if (JOptionPane.showConfirmDialog
		             (null,"Are you sure you wish to exit?")==JOptionPane.YES_OPTION) {
				
		           f.setVisible(false);
		           f.dispose();
		           }

		}
	}

}
