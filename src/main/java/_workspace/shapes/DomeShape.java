/**
 * 
 */
package _workspace.shapes;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.util.ShapeUtils;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;
import gui.MoonWorkspaceFactory;
import gui.MoonWorkspaceInternalFrame;

/**
 * @author Viorel Florian
 * Creates and manages "Dome" objects
 */
public class DomeShape extends Ellipsoid implements IShape {
	MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
	Ellipsoid ellips = null;
	// static ArrayList<DomeShape> domes = new ArrayList<DomeShape>();
	private String domeName = "";
	static int nextID = 0;
	DomeActions d = new DomeActions();

	
	/**
	 * @param
	 * Creates a "Dome" in the center of the viewport, relative to the altitude 
	 * the camera is at the time of call, with basic attributes
	 */
	public DomeShape(WorldWindow wwd) throws Exception {
		Position position = ShapeUtils.getNewShapePosition(wwd);
        double sizeInMeters = ShapeUtils.getViewportScaleFactor(wwd) ;
        double diam =sizeInMeters / 2.0;
        ellips = new Ellipsoid(position, diam, diam/2, diam);
		ellips.setAttributes(new BasicShapeAttributes());
		domeName = generateName();
		// domes.add(this);
		selectedIntFr.rendLayer.addRenderable(ellips);
		selectedIntFr.wwGLCanvas.redrawNow();
		this.addToPool();
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
			double eastWestRadius) throws Exception {
		
		ellips = new Ellipsoid(pos, northSouthRadius, verticalRadius, eastWestRadius);
		ellips.setAttributes(new BasicShapeAttributes());
		domeName = generateName();
		// domes.add(this);
		
		this.addToPool();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void addToPool() throws Exception {
	  ShapesPool.getInstance().addShape(this);
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
	public void setIdentifier(String name){
		this.domeName = name;
	}
	


  /**
   * @return Current name of the Dome
   * @see _workspace.shapes.IShape#getIdentifier()
   */
  public String getIdentifier() {
    return this.domeName;
  }

/* (non-Javadoc)
 * @see _workspace.shapes.IShape#getAltitudes()
 */

	

	

}
