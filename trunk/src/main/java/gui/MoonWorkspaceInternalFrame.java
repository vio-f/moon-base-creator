package gui;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.util.StatusBar;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class MoonWorkspaceInternalFrame extends JInternalFrame {
	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;
	public static gov.nasa.worldwind.awt.WorldWindowGLCanvas wwGLCanvas;
	
	public MoonWorkspaceInternalFrame() {
		    super("New workspace " + (++openFrameCount),
		          true, //resizable
		          true, //closable
		          true, //maximizable
		          true);//iconifiable
		    
	        // se creaza canvasul ptr luna
	    	System.out.println("Creating Moon");
			wwGLCanvas = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();

	    	Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
	    	
			//initializam Canvasul ptr Luna
		    wwGLCanvas.setModel(m); //adauga model-ul la canvas
		    
		    
		    
		    this.setSize(480, 320);
		    this.setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
		    JPanel jPanel=new JPanel(new BorderLayout());
		    jPanel.add(wwGLCanvas);
		    StatusBar status=new StatusBar();
		    jPanel.add(status,BorderLayout.SOUTH);
		    getContentPane().add(jPanel);
	    	System.out.println("Moon Canvas added");
	    	
	    	new ShapesExample(this);
	    	wwGLCanvas.redrawNow();
	    	
			BaseFrame.desktop.add(this);

			this.setVisible(true);
		    try {
		    	this.setSelected(true);
		    } catch (java.beans.PropertyVetoException e) {} 
		}
	
	
	
	public static LayerList getStuff(){
	LayerList layers = wwGLCanvas.getModel().getLayers();
	return layers;
	}
	
	
	
	
	
	

}
