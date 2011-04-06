package stuff;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gui.MoonWorkspaceFactory;
import gui.MoonWorkspaceInternalFrame;

import java.util.ArrayList;

public class ShapesExample{
	MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

	
	ShapesExample(){
		 RenderableLayer layer = new RenderableLayer();

         // Create and set an attribute bundle.
         ShapeAttributes sideAttributes = new BasicShapeAttributes();
         sideAttributes.setInteriorMaterial(Material.MAGENTA);
         sideAttributes.setOutlineOpacity(0.5);
         sideAttributes.setInteriorOpacity(0.5);
         sideAttributes.setOutlineMaterial(Material.GREEN);
         sideAttributes.setOutlineWidth(2);
         sideAttributes.setDrawOutline(true);
         sideAttributes.setDrawInterior(true);
         sideAttributes.setEnableLighting(true);

         ShapeAttributes sideHighlightAttributes = new BasicShapeAttributes(sideAttributes);
         sideHighlightAttributes.setOutlineMaterial(Material.WHITE);
         sideHighlightAttributes.setOutlineOpacity(1);

         ShapeAttributes capAttributes = new BasicShapeAttributes(sideAttributes);
         capAttributes.setInteriorMaterial(Material.YELLOW);
         capAttributes.setInteriorOpacity(0.8);
         capAttributes.setDrawInterior(true);
         capAttributes.setEnableLighting(true);

         // Create a path, set some of its properties and set its attributes.
         ArrayList<Position> pathPositions = new ArrayList<Position>();
         pathPositions.add(Position.fromDegrees(28, -106, 3e4));//28, -106, 3e4));
         pathPositions.add(Position.fromDegrees(35, -104, 3e4));//(35, -104, 3e4));
         pathPositions.add(Position.fromDegrees(35, -107, 9e4));//(35, -107, 9e4));
         pathPositions.add(Position.fromDegrees(28, -107, 9e4));//(28, -107, 9e4));
         pathPositions.add(Position.fromDegrees(28, -106, 3e4));//(28, -106, 3e4));
         ExtrudedPolygon pgon = new ExtrudedPolygon(pathPositions);

         pathPositions.clear();
         pathPositions.add(Position.fromDegrees(29, -106.4, 4e4));//(29, -106.4, 4e4));
         pathPositions.add(Position.fromDegrees(30, -106.4, 4e4));//(30, -106.4, 4e4));
         pathPositions.add(Position.fromDegrees(29, -106.8, 7e4));//(29, -106.8, 7e4));
         pathPositions.add(Position.fromDegrees(29, -106.4, 4e4));//(29, -106.4, 4e4));
        pgon.addInnerBoundary(pathPositions);
         pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
         pgon.setSideAttributes(sideAttributes);
         pgon.setSideHighlightAttributes(sideHighlightAttributes);
         pgon.setCapAttributes(capAttributes);
         layer.addRenderable(pgon);

         ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
         pathLocations.add(LatLon.fromDegrees(28, -110));
         pathLocations.add(LatLon.fromDegrees(35, -108));
         pathLocations.add(LatLon.fromDegrees(35, -111));
         pathLocations.add(LatLon.fromDegrees(28, -111));
         pathLocations.add(LatLon.fromDegrees(28, -110));
         pgon = new ExtrudedPolygon(pathLocations, 6e4);
         pgon.setSideAttributes(sideAttributes);
         pgon.setSideHighlightAttributes(sideHighlightAttributes);
         pgon.setCapAttributes(capAttributes);
         layer.addRenderable(pgon);

         //LayerList layers = BaseFrame.desktop.getSelectedFrame().getContentPane().getjPanel.worldWindowGLCanvas.getModel().getLayers();
         
         
         //TODO finish this quik
         
         
         LayerList layers = selectedIntFr.getLayers();
         layers.add(layer);
         System.out.println("Shape added");
         
         
         // Add the layer to the model.
        // insertBeforeCompass(getWwd(), layer);

         // Update layer panel
         //this.getLayerPanel().update(this.getWwd());
     }


	

}
