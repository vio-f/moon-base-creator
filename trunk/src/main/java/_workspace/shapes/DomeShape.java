/**
 * 
 */
package _workspace.shapes;

import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.util.ShapeUtils;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;

/**
 * @author Viorel Florian Creates and manages "Dome" objects
 */
public class DomeShape extends Ellipsoid implements IShape {
	MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory
			.getInstance().getLastSelectedIntFr();
	// static ArrayList<DomeShape> domes = new ArrayList<DomeShape>();
	private String domeName = "";
	static int nextID = 0;

	/**
	 * @param Creates
	 *            a "Dome" in the center of the viewport, relative to the
	 *            altitude the camera is at the time of call, with basic
	 *            attributes
	 */
	public DomeShape(WorldWindow wwd) throws Exception {
		super();
		Position position = ShapeUtils.getNewShapePosition(wwd);
		double sizeInMeters = ShapeUtils.getViewportScaleFactor(wwd);
		double diam = sizeInMeters / 2.0;
		this.setCenterPosition(position);
		this.setNorthSouthRadius(diam);
		this.setVerticalRadius(diam);
		this.setEastWestRadius(diam);
		this.setAttributes(new BasicShapeAttributes());

		domeName = generateName();
		// domes.add(this);
		selectedIntFr.rendLayer.addRenderable(this);
		selectedIntFr.wwGLCanvas.redraw();
		this.addToPool();
		ShapeListener.lastSelectedObj = this;
	}

	/**
	 * @param wwd
	 * @param pos
	 * @param northSouthRadius
	 * @param verticalRadius
	 * @param eastWestRadius
	 * 
	 *            <p>
	 *            Creates a "Dome" at the provided position, with the provided
	 *            sizes in meters, , with basic attributes
	 */
	public DomeShape(WorldWindow wwd, Position pos, double northSouthRadius,
			double verticalRadius, double eastWestRadius) throws Exception {
		super(pos, northSouthRadius, verticalRadius, eastWestRadius);
		this.setAttributes(new BasicShapeAttributes());
		domeName = generateName();
		this.addToPool();
	}

	/**
	 * 
	 * @throws Exception
	 */

	private void addToPool() throws Exception {
		ShapesPool.getInstance().addShape(this);
	}

	/**
	 * 
	 * @throws Exception
	 */

	private void removeFromPool() throws Exception {
		ShapesPool.getInstance().removeShape(getIdentifier());
	}

	/**
	 * 
	 * @return ID in a predetermined format (can be changed)
	 * @see _workspace.shapes.IShape#setIdentifier()
	 * @see _workspace.shapes.IShape#getIdentifier()
	 */

	private String generateName() {
		nextID++;
		if (nextID < 10) {
			return "Dome " + "0" + nextID;
		}
		return "Dome " + nextID;
	}

	/**
	 * 
	 * @param new DomeName
	 * @see _workspace.shapes.IShape#setIdentifier()
	 */
	@Override
	public void setIdentifier(String name) {
		this.domeName = name;
	}

	/**
	 * @return Current name of the Dome
	 * @see _workspace.shapes.IShape#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return this.domeName;
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Override
	public void removeMe() throws Exception {
		this.removeFromPool();
		selectedIntFr.rendLayer.removeRenderable(this);
		selectedIntFr.wwGLCanvas.redraw();
		ShapeListener.lastSelectedObj = null;
	}

}
