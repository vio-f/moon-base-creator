package gui;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gui.src.components.ProgressMonitorDemo;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.RepaintManager;
import javax.swing.UnsupportedLookAndFeelException;

//TODO explore EVENTHANDLER
public class GuiActions extends AbstractAction {
	static JFrame f;
	GenericThread task;
	//GenericNewTask showProgDiag;
	
	GuiActions(JFrame f){
		this.f = f;
	}



	//@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(BaseFrame.fileNewItem)){
			System.out.println("New has been pressed"); 
			// TODO remove this
			
			
		
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
