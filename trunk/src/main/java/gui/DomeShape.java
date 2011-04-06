/**
 * 
 */
package gui;

import java.util.ArrayList;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.util.ShapeUtils;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;

/**
 * @author Viorel Florian
 * Creates and manages "Dome" objects
 */
public class DomeShape extends Ellipsoid{
	MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
	Ellipsoid ellips = null;
	static ArrayList<DomeShape> domes = new ArrayList<DomeShape>();
	private String DOME_NAME = "";
	static int nextID = 0;

	/**
	 * @param
	 * Creates a "Dome" in the center of the viewport, relative to the altitude 
	 * the camera is at the time of call, with basic attributes
	 */
	public DomeShape(WorldWindow wwd) {
		Position position = ShapeUtils.getNewShapePosition(wwd);
        double sizeInMeters = ShapeUtils.getViewportScaleFactor(wwd) ;
        double diam =sizeInMeters / 2.0;
        ellips = new Ellipsoid(position, diam, diam, diam);
		ellips.setAttributes(new BasicShapeAttributes());
		DOME_NAME = generateName();
		domes.add(this);
		selectedIntFr.rendLayer.addRenderable(ellips);
		selectedIntFr.wwGLCanvas.redrawNow();
	}	
	
	/**
	 * @param wwd
	 * @param pos
	 * @param northSouthRadius
	 * @param verticalRadius
	 * @param eastWestRadius
	 * 
	 * <p>Creates a "Dome" at the provided position, with the provided sizes in meters,
	 * , with basic attributes
	 */
	public DomeShape(WorldWindow wwd, Position pos, 
			double northSouthRadius, 
			double verticalRadius, 
			double eastWestRadius){
		
		ellips = new Ellipsoid(pos, northSouthRadius, verticalRadius, eastWestRadius);
		ellips.setAttributes(new BasicShapeAttributes());
		DOME_NAME = generateName();
		domes.add(this);
	}
	
	
	private String generateName(){
		nextID++;
		if (nextID < 10){
			return "Dome " + "0" + nextID;
		}
		return "Dome " + nextID; 
		
	}
	
	
	
	/**
	 * 
	 * @param new DomeName
	 */
	public void setName(String name){
		this.DOME_NAME = name;
	}
	
	/**
	 * 
	 * @return Current name of the Dome
	 */
	public String getName(){
		return this.DOME_NAME;
	}
	
	

	

	

}
