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

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

//TODO explore EVENTHANDLER
public class CopyOfGuiActionswithprogressmonitor extends AbstractAction implements PropertyChangeListener {
	JFrame f;
	ProgressMonitor progressMonitor;
	GenericNewTask task;
	
	CopyOfGuiActionswithprogressmonitor(JFrame f){
		this.f = f;
	}



	//@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(BaseFrame.fileNewItem)){
			System.out.println("New has been pressed"); // TODO remove this
			progressMonitor = new ProgressMonitor(f,
                    "Rabdarea este o virtute!!!",
                    "", 0, 100);
			task = new GenericNewTask(initMoon());
			
			
			task.addPropertyChangeListener(this);
			task.execute();
			//startMoon();
		}
		
		if (e.getSource().equals(BaseFrame.fileExitItem)){
			if (JOptionPane.showConfirmDialog
		             (null,"Are you sure you wish to exit?")==JOptionPane.YES_OPTION) {
				f.setVisible(false);
		        f.dispose();
		        
		           }

		}
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
        Random random = new Random();
        if ("progress" == evt.getPropertyName() ) {
            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(random.nextInt(100));
            
            
            if (progressMonitor.isCanceled() || task.isDone()) {
                Toolkit.getDefaultToolkit().beep();
                if (progressMonitor.isCanceled()) {
                    task.cancel(true);
                } 
            }
        }
		
	}
	

	public Method initMoon() {
		//setarile necesare ptr vizualizarea Lunii
		
		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, gov.nasa.worldwind.globes.Moon.class.getName());// seteaza tipul astrului care urmeaza a fi vizualiza
        Configuration.setValue(AVKey.MOON_ELEVATION_MODEL_CONFIG_FILE, "config/Moon/MoonElevationModel.xml");//seteaza  ce fisier de elevatie (inaltimi) sa se foloseasca
        Configuration.setValue(AVKey.LAYERS_CLASS_NAMES, BaseFrame.LAYERS);// LAYERS a fost definit mai sus
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 60000e3);  // 6000km
        
        // se creaza canvasul ptr luna
    	gov.nasa.worldwind.awt.WorldWindowGLCanvas worldWindowGLCanvas;
    	Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
    	
		//initializam Canvasul ptr Luna
		worldWindowGLCanvas = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();
	    worldWindowGLCanvas.setModel(m); //adauga model-ul la canvas
		BaseFrame.canvasPanel.add(worldWindowGLCanvas);
		return null;
	
	}
	

	
}
