package gui;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

public class MoonWorkspaceInternalFrame extends JInternalFrame implements Runnable{
	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;
	static gov.nasa.worldwind.awt.WorldWindowGLCanvas worldWindowGLCanvas;
	
	public MoonWorkspaceInternalFrame() {
		    super("New workspace " + (++openFrameCount),
		          true, //resizable
		          true, //closable
		          true, //maximizable
		          true);//iconifiable
		    
	        // se creaza canvasul ptr luna
	    	System.out.println("Creating Moon");
			worldWindowGLCanvas = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();

	    	Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
	    	
			//initializam Canvasul ptr Luna
		    worldWindowGLCanvas.setModel(m); //adauga model-ul la canvas
		    
		    
		    
		    this.setSize(480, 320);
		    this.setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
		    getContentPane().add(worldWindowGLCanvas);
	    	System.out.println("Moon Canvas added");
			BaseFrame.desktop.add(this);

			this.setVisible(true);
		    try {
		    	this.setSelected(true);
		    } catch (java.beans.PropertyVetoException e) {}
		    
		    
		    
	
	}

	@Override
	public void run() {
		

	    
		
		
	}

}
