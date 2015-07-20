package _workspace.shapes;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;

import java.util.ArrayList;

import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;

public class MyShapesExample{
	ExtrudedPolygon pgon;
	MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

	
	
	/**
	 * 
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	public MyShapesExample(){
		MyLogger.info(this, "Instacing RenderableLayer");
		 RenderableLayer layer = new RenderableLayer();

         // Create and set an attribute bundle.
		 MyLogger.info(this, "Creating sideAttributes.");
         ShapeAttributes sideAttributes = new BasicShapeAttributes();
         sideAttributes.setInteriorMaterial(Material.MAGENTA);
         sideAttributes.setOutlineOpacity(0.5);
         sideAttributes.setInteriorOpacity(0.5);
         sideAttributes.setOutlineMaterial(Material.GREEN);
         sideAttributes.setOutlineWidth(2);
         sideAttributes.setDrawOutline(true);
         sideAttributes.setDrawInterior(true);
         sideAttributes.setEnableLighting(true);

         MyLogger.info(this, "Creating \"sideHighlightAttributes\".");
         ShapeAttributes sideHighlightAttributes = new BasicShapeAttributes(sideAttributes);
         sideHighlightAttributes.setOutlineMaterial(Material.WHITE);
         sideHighlightAttributes.setOutlineOpacity(1);

         MyLogger.info(this, "Creating \"capAttributes\".");
         ShapeAttributes capAttributes = new BasicShapeAttributes(sideAttributes);
         capAttributes.setInteriorMaterial(Material.YELLOW);
         capAttributes.setInteriorOpacity(0.8);
         capAttributes.setDrawInterior(true);
         capAttributes.setEnableLighting(true);

         // Create a path, set some of its properties and set its attributes.
         MyLogger.info(this, "Creating the letter  \"V\".");
         ArrayList<Position> pathPositions = new ArrayList<Position>();
         pathPositions.add(Position.fromDegrees(0, -9, 3e5));
         pathPositions.add(Position.fromDegrees(8, -12, 3e5));
         pathPositions.add(Position.fromDegrees(8, -10, 3e5));
         pathPositions.add(Position.fromDegrees(3, -8, 3e5));
         pathPositions.add(Position.fromDegrees(8, -6, 3e5));
         pathPositions.add(Position.fromDegrees(8, -4, 3e5));
         pathPositions.add(Position.fromDegrees(0, -7, 3e5));
         
         
         pgon = new ExtrudedPolygon(pathPositions);
         pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
         pgon.setSideAttributes(sideAttributes);
         pgon.setSideHighlightAttributes(sideHighlightAttributes);
         pgon.setCapAttributes(capAttributes);
         layer.addRenderable(pgon);
                  
         
         
         MyLogger.info(this, "Creating the letter  \"I\".");
         ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
         pathLocations.add(LatLon.fromDegrees(0, -1));
         pathLocations.add(LatLon.fromDegrees(8, -1));
         pathLocations.add(LatLon.fromDegrees(8, 1));
         pathLocations.add(LatLon.fromDegrees(0, 1));
         pgon = new ExtrudedPolygon(pathLocations, 3e5);
         pgon.setSideAttributes(sideAttributes);
         pgon.setSideHighlightAttributes(sideHighlightAttributes);
         pgon.setCapAttributes(capAttributes);
         layer.addRenderable(pgon);
         
         MyLogger.info(this, "Creating the letter  \"O\".");
         ArrayList<LatLon> pathLocations1 = new ArrayList<LatLon>();
         pathLocations1.add(LatLon.fromDegrees(0, 6));
         pathLocations1.add(LatLon.fromDegrees(2, 4));
         pathLocations1.add(LatLon.fromDegrees(6, 4));
         pathLocations1.add(LatLon.fromDegrees(8, 6));
         pathLocations1.add(LatLon.fromDegrees(8, 8));
         pathLocations1.add(LatLon.fromDegrees(6, 10));
         pathLocations1.add(LatLon.fromDegrees(2, 10));
         pathLocations1.add(LatLon.fromDegrees(0, 8));
         pgon = new ExtrudedPolygon(pathLocations1, 3e5);
         
         pathLocations1.clear();
         pathLocations1.add(LatLon.fromDegrees(1, 6));
         pathLocations1.add(LatLon.fromDegrees(2, 5));
         pathLocations1.add(LatLon.fromDegrees(6, 5));
         pathLocations1.add(LatLon.fromDegrees(7, 6));
         pathLocations1.add(LatLon.fromDegrees(7, 8));
         pathLocations1.add(LatLon.fromDegrees(6, 9));
         pathLocations1.add(LatLon.fromDegrees(2, 9));
         pathLocations1.add(LatLon.fromDegrees(1, 8));
         
        pgon.addInnerBoundary(pathLocations1);
         
         
         
         pgon.setSideAttributes(sideAttributes);
         pgon.setSideHighlightAttributes(sideHighlightAttributes);
         pgon.setCapAttributes(capAttributes);
         layer.addRenderable(pgon);

         
         
         //TODO finish this quik
         
         MyLogger.info(this, "Adding shapes to layers");
         LayerList layers = new LayerList(selectedIntFr.getLayers());
        	 
         layer.setName("Vio");
         layers.add(layer);
         // System.out.println("Shape added");
         MyLogger.info(this, "Shapes added");
         
         
        
     }

	public void moveMe(Position pos){
		pgon.move(pos);
		
	}


	

}
