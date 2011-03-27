/**
 * 
 */
package gui;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.examples.LayerTree;
import gov.nasa.worldwind.examples.util.HotSpotController;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * @author Vio
 *
 */
public class CanvasLayerTree {

	
	 protected LayerTree layerTree;
     protected RenderableLayer hiddenLayer;

     protected HotSpotController controller;

     public CanvasLayerTree(MoonWorkspaceInternalFrame f)
     {



         // Add the on-screen layer tree, refreshing model with the WorldWindow's current layer list. We
         // intentionally refresh the tree's model before adding the layer that contains the tree itself. This
         // prevents the tree's layer from being displayed in the tree itself.
         this.layerTree = new LayerTree();
         this.layerTree.getModel().refresh(MoonWorkspaceInternalFrame.getStuff());
         

         // Set up a layer to display the on-screen layer tree in the WorldWindow. This layer is not displayed in
         // the layer tree's model. Doing so would enable the user to hide the layer tree display with no way of
         // bringing it back.
         this.hiddenLayer = new RenderableLayer();
         this.hiddenLayer.addRenderable(this.layerTree);
         MoonWorkspaceInternalFrame.getStuff().add(this.hiddenLayer);

         // Add a controller to handle input events on the layer tree.
         this.controller = new HotSpotController(MoonWorkspaceInternalFrame.wwGLCanvas);


         //WWUtil.alignComponent(null, this, AVKey.CENTER);
     }

}
