package gui;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.examples.ApplicationTemplate;

public class MyFirstExtrudedShape {
	
	public MyFirstExtrudedShape() {
		RenderableLayer extrlayer = new RenderableLayer();
 		ExtrudedPolygon extrp = new ExtrudedPolygon(30000000.0);
 		extrp.setReferenceLocation(Position.fromDegrees(0, -9, 3e5));
 		System.out.println(extrp.getHeight());
 		System.out.println(extrp.isVisible());
 		
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

        
        extrp.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
 		extrp.setSideAttributes(sideAttributes);
        extrp.setSideHighlightAttributes(sideHighlightAttributes);
        extrp.setCapAttributes(capAttributes);
 		
 		extrlayer.addRenderable(extrp); 
 		ApplicationTemplate.insertBeforeCompass(MoonWorkspaceInternalFrame.wwGLCanvas, extrlayer);
 		MoonWorkspaceInternalFrame.getStuff().add(extrlayer);
	}

}
