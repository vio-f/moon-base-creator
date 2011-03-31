package gui;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.examples.util.ExtrudedPolygonEditor;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.util.StatusBar;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import utility.MyLogger;

public class MoonWorkspaceInternalFrame extends JInternalFrame {
	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;
	public static gov.nasa.worldwind.awt.WorldWindowGLCanvas wwGLCanvas;
	static CanvasLayerTree canvasLT;
	private static Boolean statusLayerTree = false;
	public static MyShapesExample vio;

	public MoonWorkspaceInternalFrame() {
		super("New workspace " + (++openFrameCount), true, // resizable
				true, // closable
				true, // maximizable
				true);// iconifiable

		// se creaza canvasul ptr luna
		// System.out.println("Creating Moon");
		MyLogger.info(this, "Creating Moon canvas");
		wwGLCanvas = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();

		Model m = (Model) WorldWind
				.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);

		// initializam Canvasul ptr Luna
		wwGLCanvas.setModel(m); // adauga model-ul la canvas

		this.setSize(480, 320);
		this.setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
		JPanel jPanel = new JPanel(new BorderLayout());
		jPanel.add(wwGLCanvas);
		StatusBar status = new StatusBar();
		jPanel.add(status, BorderLayout.SOUTH);
		getContentPane().add(jPanel);
		// System.out.println("Moon Canvas added");
		MyLogger.info(this, "Moon Canvas added");



		vio = new MyShapesExample();
		
		
		
		
		//new MyFirstExtrudedShape();
		
		
		//MyLogger.info(this, "Adding LayerTree");
		//canvasLT = new CanvasLayerTree();
		
		wwGLCanvas.redrawNow();
		
		BaseFrame.desktop.add(this);

		this.setVisible(true);
		try {
			this.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
		}
	}

	public static LayerList getStuff() {
		LayerList layers = wwGLCanvas.getModel().getLayers();
		return layers;
	}
	
	
	/**
	 * @return
	 * <i><b>true</b></i> - if LayerTree is visible 
	 * <p><i><b>false</b></i> - if LayerTree is NOT visible 
	 */
	public static Boolean isLayerTreeVisible(){
		return statusLayerTree;		
	}
	
	
	/**
	 * @param b 
	 * <p><i><b>true</i></b> - <i>LayerTree</i> will become visible,
	 * <p><i><b>false</i></b> - <i>LayerTree</i> will be removed
	 */
	public static void setLayerTreeVisible(Boolean b){
		statusLayerTree = b;
		if(statusLayerTree == true){
			MyLogger.getLogger().info("Adding LayerTree");
			canvasLT = new CanvasLayerTree();
			statusLayerTree = true;
			System.gc();
			
		}
		else{
			MyLogger.getLogger().info("Removing LayerTree");
			MoonWorkspaceInternalFrame.getStuff().remove(CanvasLayerTree.hiddenLayer);
			canvasLT = null;
			statusLayerTree = false;
			System.gc();
			
		}
		
	}

}
