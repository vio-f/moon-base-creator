package _workspace;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.util.StatusBar;
import gui.BaseFrame;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import utility.MyLogger;
import _workspace.shapes.ShapeListener;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
public class MoonWorkspaceInternalFrame extends JInternalFrame implements InternalFrameListener {
  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /** openFrameCount */
  static int openFrameCount = 0;

  /** XOFFSET */
  /** YOFFSET */
  static final int XOFFSET = 20, YOFFSET = 20;

  /** wwGLCanvas */
  private WorldWindowGLCanvas wwGLCanvas;

  /** canvasLT */
  private CanvasLayerTree canvasLT;

  /** layerTreeStatus */
  private Boolean layerTreeStatus = false;

  /** rendLayer */
  private RenderableLayer rendLayer = new RenderableLayer();

  /** annotationLayer */
  private AnnotationLayer annotationLayer = new AnnotationLayer();

  /**
   * Constructs a new instance.
   */
  protected MoonWorkspaceInternalFrame() {
    super("New workspace " + (++openFrameCount), true, // resizable
        true, // closable
        true, // maximizable
        false);// iconifiable
    this.addInternalFrameListener(this);
    // setStatus(true);
    // se creaza canvasul ptr luna
    // System.out.println("Creating Moon");
    MyLogger.info(this, "Creating Moon canvas");

    this.setWwGLCanvas(new gov.nasa.worldwind.awt.WorldWindowGLCanvas());
    MyLogger.info(this, "Canvas is double buffered:  " + this.getWwGLCanvas().isDoubleBuffered());
    Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);

    // initializam Canvasul ptr Luna
    this.getWwGLCanvas().setModel(m); // adauga model-ul la canvas

    this.setSize(480, 320);
    this.setLocation(XOFFSET * openFrameCount, YOFFSET * openFrameCount);
    JPanel canvasContainer = new JPanel(new BorderLayout());
    canvasContainer.add(this.getWwGLCanvas());
    StatusBar status = new StatusBar();
    canvasContainer.add(status, BorderLayout.SOUTH);
    this.getContentPane().add(canvasContainer);

    MyLogger.info(this, "Moon Canvas added");

    BaseFrame.getInstance().getDesktop().add(this);
    this.getLayers().add(this.getRendLayer());
    this.getLayers().add(this.annotationLayer);
    // TODO make this optional
    this.getLayers().add(new CustomLayerManager(this.getWwGLCanvas()));
    this.getWwGLCanvas().redrawNow();

    try {
      this.setMaximum(true);
    } catch (PropertyVetoException e) {
      // TODO Add your own exception handling here, consider logging
      MyLogger.info(this, "Moon Workspace could not be maximized");
      e.printStackTrace();
    }
    this.setVisible(true);

    new ShapeListener(this);
    this.getRendLayer().setName("Renderable layer");
    this.annotationLayer.setName("Anotation Layer");
  }

  /**
   * Retrives the layer list from the currently loaded model.
   * 
   * @return A layer list of all curently loaded layers in the model
   */
  public LayerList getLayers() {
    LayerList layers = new LayerList();
    layers = this.getWwGLCanvas().getModel().getLayers();
    return layers;

  }

  /**
   * Set wwGLCanvas.
   * 
   * @param wwGLCanvas
   */
  public void setWwGLCanvas(WorldWindowGLCanvas wwGLCanvas) {
    this.wwGLCanvas = wwGLCanvas;
  }

  /**
   * Get wwGLCanvas.
   * 
   * @return wwGLCanvas
   */
  public WorldWindowGLCanvas getWwGLCanvas() {
    return this.wwGLCanvas;
  }

  /**
   * Set rendLayer.
   * 
   * @param rendLayer
   */
  public void setRendLayer(RenderableLayer rendLayer) {
    this.rendLayer = rendLayer;
  }

  /**
   * Get rendLayer.
   * 
   * @return rendLayer
   */
  public RenderableLayer getRendLayer() {
    return rendLayer;
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
   * TODO DESCRIPTION
   */
  public void showHideLayerTree() {
    if (this.layerTreeStatus == false) {
      MyLogger.getLogger().info("Adding LayerTree");
      this.canvasLT = new CanvasLayerTree(this);
      this.layerTreeStatus = true;
      System.gc();
    } else if (this.layerTreeStatus == true) {
      MyLogger.getLogger().info("Removing LayerTree");
      this.getLayers().remove(this.canvasLT);
      this.canvasLT = null;
      this.layerTreeStatus = false;
      System.gc();
    }
  }


  /**
   * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
   */
  @Override
  public void internalFrameActivated(InternalFrameEvent e) {
    MoonWorkspaceFactory.getInstance().setLastSelectedIntFr(this);
  }

  /**
   * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
   */
  @Override
  public void internalFrameClosed(InternalFrameEvent e) {
    this.del_This_If_LastSelected();
  }

  /**
   * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
   */
  @Override
  public void internalFrameClosing(InternalFrameEvent e) {
    this.del_This_If_LastSelected();
  }

  /**
   * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
   */
  @Override
  public void internalFrameDeactivated(InternalFrameEvent e) {
    // TODO unimplemented yet
  }

  /**
   * @see javax.swing.event.InternalFrameListener#internalFrameDeiconified(javax.swing.event.InternalFrameEvent)
   */
  @Override
  public void internalFrameDeiconified(InternalFrameEvent e) {
    MoonWorkspaceFactory.getInstance().setLastSelectedIntFr(this);
  }

  /**
   * @see javax.swing.event.InternalFrameListener#internalFrameIconified(javax.swing.event.InternalFrameEvent)
   */
  @Override
  public void internalFrameIconified(InternalFrameEvent e) {
    this.del_This_If_LastSelected();
  }

  /**
   * @see javax.swing.event.InternalFrameListener#internalFrameOpened(javax.swing.event.InternalFrameEvent)
   */
  @Override
  public void internalFrameOpened(InternalFrameEvent e) {
    MoonWorkspaceFactory.getInstance().setLastSelectedIntFr(this);

  }

  /**
   * In case the current frame is the last selected, it is removed 
   */
  private void del_This_If_LastSelected() {
    if (MoonWorkspaceFactory.getInstance().getLastSelectedIntFr() == this) {
      MoonWorkspaceFactory.getInstance().setLastSelectedIntFr(null);
    }

  }

}
