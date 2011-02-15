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
	GenericNewTask task;
	GenericNewTask showProgDiag;
	
	GuiActions(JFrame f){
		this.f = f;
	}



	//@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(BaseFrame.fileNewItem)){
			System.out.println("New has been pressed"); // TODO remove this

			//new ProgressDialog(f);
				initMoon();

			/*task = new GenericNewTask(initMoon());
			showProgDiag = new GenericNewTask(true, f);
			
			//TODO ad property change listener
			task.execute();
			
			
			
			if (task.isDone()){
				showProgDiag.cancel(true);
			}*/
			
		
		}
		
		
		
		if (e.getSource().equals(BaseFrame.fileExitItem)){
			if (JOptionPane.showConfirmDialog
		             (null,"Are you sure you wish to exit?")==JOptionPane.YES_OPTION) {
				f.setVisible(false);
		        f.dispose();
		        
		           }

		}
	}


	
	

	public Method initMoon() {
		System.out.println("Starting Moon");//TODO remove this
		//setarile necesare ptr vizualizarea Lunii
		
		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, gov.nasa.worldwind.globes.Moon.class.getName());// seteaza tipul astrului care urmeaza a fi vizualiza
        Configuration.setValue(AVKey.MOON_ELEVATION_MODEL_CONFIG_FILE, "config/Moon/MoonElevationModel.xml");//seteaza  ce fisier de elevatie (inaltimi) sa se foloseasca
        Configuration.setValue(AVKey.LAYERS_CLASS_NAMES, BaseFrame.LAYERS);// LAYERS a fost definit mai sus
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 60000e3);  // 6000km
        
        System.out.println("Config Done");//TODO remove this
        // se creaza canvasul ptr luna
    	gov.nasa.worldwind.awt.WorldWindowGLCanvas worldWindowGLCanvas;
		worldWindowGLCanvas = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();

    	Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        System.out.println("Still here");
		//initializam Canvasul ptr Luna
	    worldWindowGLCanvas.setModel(m); //adauga model-ul la canvas
		BaseFrame.canvasPanel.add(worldWindowGLCanvas);
		
		//firePropertyChange(AVKey.LAYERS_CLASS_NAMES, null, this);
		
		System.out.println("I'm outta here");//TODO remove this
		return null;
	
	}
	

	
}
