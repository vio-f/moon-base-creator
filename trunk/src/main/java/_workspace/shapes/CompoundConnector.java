/**
 * 
 */
package _workspace.shapes;

import java.util.ArrayList;


import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gui.MoonWorkspaceFactory;
import gui.MoonWorkspaceInternalFrame;

/**
 * @author Viorel Florian
 *
 */
public class CompoundConnector /*extends ExtrudedPolygon*/ implements IShape {
	MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

    ShapeAttributes sideAttributes = new BasicShapeAttributes();
    ShapeAttributes capAttributes = new BasicShapeAttributes(sideAttributes);
    ExtrudedPolygon pgon = null;



public CompoundConnector(){
	sideAttributes.setInteriorMaterial(Material.MAGENTA);
    sideAttributes.setOutlineOpacity(0.5);
    sideAttributes.setInteriorOpacity(0.5);
    sideAttributes.setOutlineMaterial(Material.GREEN);
    sideAttributes.setOutlineWidth(2);
    sideAttributes.setDrawOutline(true);
    sideAttributes.setDrawInterior(true);
    sideAttributes.setEnableLighting(true);
    
    capAttributes.setInteriorMaterial(Material.YELLOW);
    capAttributes.setInteriorOpacity(0.8);
    capAttributes.setDrawInterior(true);
    capAttributes.setEnableLighting(true);
    
    ShapeAttributes sideHighlightAttributes = new BasicShapeAttributes(sideAttributes);
    sideHighlightAttributes.setOutlineMaterial(Material.WHITE);
    sideHighlightAttributes.setOutlineOpacity(1);
    
 // Create a path, set some of its properties and set its attributes.
    ArrayList<Position> pathPositions = new ArrayList<Position>();
    
    pathPositions.add(Position.fromDegrees(28, -106, 3e4));//28, -106, 3e4));
    pathPositions.add(Position.fromDegrees(35, -104, 3e4));//(35, -104, 3e4));
    pathPositions.add(Position.fromDegrees(35, -107, 9e4));//(35, -107, 9e4));
    pathPositions.add(Position.fromDegrees(28, -107, 9e4));//(28, -107, 9e4));
    pathPositions.add(Position.fromDegrees(28, -106, 3e4));//(28, -106, 3e4));
    
    pgon = new ExtrudedPolygon(pathPositions);
    
    pgon.setSideAttributes(sideAttributes);
    pgon.setSideHighlightAttributes(sideHighlightAttributes);
    pgon.setCapAttributes(capAttributes);
    
    
    selectedIntFr.rendLayer.addRenderable(pgon);
   
}



/**
 * @see _workspace.shapes.IShape#getIdentifier()
 */
public String getIdentifier() {
  /**
   * TODO add identifier / name
   */
  return null;
}



/* (non-Javadoc)
 * @see _workspace.shapes.IShape#setAttributes(gov.nasa.worldwind.render.ShapeAttributes)
 */
@Override
public void setAttributes(ShapeAttributes lastAttrs) {
	// TODO Auto-generated method stub
	
}



/* (non-Javadoc)
 * @see _workspace.shapes.IShape#getAttributes()
 */
@Override
public ShapeAttributes getAttributes() {
	// TODO Auto-generated method stub
	return null;
}



/* (non-Javadoc)
 * @see gov.nasa.worldwind.render.Renderable#render(gov.nasa.worldwind.render.DrawContext)
 */
@Override
public void render(DrawContext dc) {
	// TODO Auto-generated method stub
	
}



/* (non-Javadoc)
 * @see _workspace.shapes.IShape#setIdentifier(java.lang.String)
 */
@Override
public void setIdentifier(String name) {
	// TODO Auto-generated method stub
	
}
	

}
