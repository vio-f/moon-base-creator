package gui;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.util.ShapeUtils;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;

public interface Movable3dShape {
	Ellipsoid makeNewEllipsoid(WorldWindow wwd);

}

class EllipsoidFactory implements Movable3dShape {
	private static EllipsoidFactory instance = null;
	
	
	public static Movable3dShape getInstance() {
		if (instance == null) {
			instance = new EllipsoidFactory();
		}
		return instance;

	}

	private EllipsoidFactory() {

	}

	@Override
	public Ellipsoid makeNewEllipsoid(WorldWindow wwd) {

		
        //ellips.setValue(AVKey.DISPLAY_NAME, getNextName(toString()));
        // Creates a sphere in the center of the viewport. Attempts to guess at a reasonable size and height.
        Position position = ShapeUtils.getNewShapePosition(wwd);
        double sizeInMeters = ShapeUtils.getViewportScaleFactor(wwd) ;
        double diam =sizeInMeters / 2.0;
        Ellipsoid ellips = new Ellipsoid(position, diam, diam, diam);
		ellips.setAttributes(new BasicShapeAttributes());
        
       

        return ellips;
	}

}
