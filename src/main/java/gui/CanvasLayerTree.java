/**
 * 
 */
package gui;

//import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.examples.LayerTree;
import gov.nasa.worldwind.examples.util.HotSpotController;
import gov.nasa.worldwind.layers.RenderableLayer;
//import gov.nasa.worldwind.util.WWUtil;


/**
 * @author Vio
 * This class has been adapted to fit the needs of this project
 */
public class CanvasLayerTree {

	
	 protected LayerTree layerTree;
     protected static RenderableLayer hiddenLayer;

     protected HotSpotController controller;

     public CanvasLayerTree()
     {



         // Add the on-screen layer tree, refreshing model with the WorldWindow's current layer list. We
         // intentionally refresh the tree's model before adding the layer that contains the tree itself. This
         // prevents the tree's layer from being displayed in the tree itself.
         this.layerTree = new LayerTree();
         this.layerTree.getModel().refresh(MoonWorkspaceInternalFrame.getStuff());
         

         // Set up a layer to display the on-screen layer tree in the WorldWindow. This layer is not displayed in
         // the layer tree's model. Doing so would enable the user to hide the layer tree display with no way of
         // bringing it back.
         hiddenLayer = new RenderableLayer();
         hiddenLayer.addRenderable(this.layerTree);
         MoonWorkspaceInternalFrame.getStuff().add(hiddenLayer);

         // Add a controller to handle input events on the layer tree.
         this.controller = new HotSpotController(MoonWorkspaceInternalFrame.wwGLCanvas);


         //WWUtil.alignComponent(null, this, AVKey.CENTER);
     }

}
