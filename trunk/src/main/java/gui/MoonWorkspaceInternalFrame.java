/**
 * @author Vio
 * 
 */
package gui;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.examples.util.LayerManagerLayer;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import _workspace.shapes.MyShapesExample;

import com.sun.org.apache.bcel.internal.generic.NEW;

import utility.MyLogger;


public class MoonWorkspaceInternalFrame extends JInternalFrame implements
		InternalFrameListener {
	static int openFrameCount = 0;
	static final int xOffset = 20, yOffset = 20;
	public WorldWindowGLCanvas wwGLCanvas;
	public CanvasLayerTree canvasLT;
	private Boolean layerTreeStatus = false;
	private Boolean intframeStatus = false;
	public RenderableLayer rendLayer = new RenderableLayer();


	protected MoonWorkspaceInternalFrame() {
		super("New workspace " + (++openFrameCount), true, // resizable
				true, // closable
				true, // maximizable
				false);// iconifiable
		this.addInternalFrameListener(this);
		setStatus(true);
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
		this.getContentPane().add(jPanel);
		// System.out.println("Moon Canvas added");
		MyLogger.info(this, "Moon Canvas added");

		BaseFrame.desktop.add(this);
		this.getLayers().add(rendLayer);
		//TODO make this optional
		this.getLayers().add(new CustomLayerManager(wwGLCanvas));
		wwGLCanvas.redrawNow();

		this.setVisible(true);
		try {
			this.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
		}
		
		rendLayer.setName("Renderable layer");
		MyShapesExample mm = new MyShapesExample();
		
	}
	

	public LayerList getLayers() {
		LayerList layers = new LayerList();
		layers = wwGLCanvas.getModel().getLayers();
		return layers;

	}

	/**
	 * @return <i><b>true</b></i> - if LayerTree is visible
	 *         <p>
	 *         <i><b>false</b></i> - if LayerTree is NOT visible
	 */
	public Boolean isLayerTreeVisible() {
		return this.layerTreeStatus;
	}

	/**
	 * @param b
	 *            <p>
	 *            <i><b>true</i></b> - <i>LayerTree</i> will become visible,
	 *            <p>
	 *            <i><b>false</i></b> - <i>LayerTree</i> will be removed
	 */
	public void showHideLayerTree() {
		if (this.layerTreeStatus == false) {
			MyLogger.getLogger().info("Adding LayerTree");
			this.canvasLT = new CanvasLayerTree(this);
			this.layerTreeStatus = true;
			System.gc();
		} else if(this.layerTreeStatus == true){
			MyLogger.getLogger().info("Removing LayerTree");
			this.getLayers().remove(this.canvasLT);
			this.canvasLT = null;
			this.layerTreeStatus = false;
			System.gc();
		}
	}

	public void setStatus(Boolean status) {
		this.intframeStatus = status;
	}

	public Boolean getStatus() {
		return this.intframeStatus;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		MoonWorkspaceFactory.getInstance().setLastSelectedIntFr(this);
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		del_This_If_LastSelected();
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		del_This_If_LastSelected();
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		//TODO unimplemented yet
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		MoonWorkspaceFactory.getInstance().setLastSelectedIntFr(this);
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		del_This_If_LastSelected();
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		MoonWorkspaceFactory.getInstance().setLastSelectedIntFr(this);
		
	}
	private void del_This_If_LastSelected(){
		if (MoonWorkspaceFactory.getInstance().getLastSelectedIntFr() == this){
			MoonWorkspaceFactory.getInstance().setLastSelectedIntFr(null);
		}
		
	}


}
