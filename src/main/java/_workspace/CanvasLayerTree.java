/**
 * 
 */
package _workspace;

//import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.LayerTree;
import gov.nasa.worldwind.examples.util.HotSpotController;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.examples.ApplicationTemplate;

/**
 * @author Vio This class has been adapted to fit the needs of this project
 */
public class CanvasLayerTree extends RenderableLayer {
	

	protected LayerTree layerTree;
	protected HotSpotController controller;
	protected RenderableLayer hiddenLayer; 
	public MoonWorkspaceInternalFrame parantframe = null;

	public CanvasLayerTree(MoonWorkspaceInternalFrame f) {
		this.parantframe = f;

		this.layerTree = new LayerTree();
		this.layerTree.getModel().refresh(parantframe.getLayers());


		/*this.hiddenLayer = new RenderableLayer();
		this.hiddenLayer.addRenderable(this.layerTree);
		selectedIntFr.getStuff().add(this.hiddenLayer);*/
		
		this.addRenderable(this.layerTree);
		insertBeforeCompass(this);
		//parantframe.getStuff().add(this);;
		

		// Add a controller to handle input events on the layer tree.
		this.controller = new HotSpotController(parantframe.getWwGLCanvas());

	}
	
    public void insertBeforeCompass(Layer layer)
    {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = parantframe.getLayers();
        for (Layer l : layers)
        {
            if (l instanceof CompassLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

}
