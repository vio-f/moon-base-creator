/**
 * 
 */
package _workspace.shapes;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.util.ShapeUtils;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;

/**
 * @author Viorel Florian Creates and manages "Dome" objects
 */
public class FlatDomeShape extends Ellipsoid implements IShape {
  /** selectedIntFr */
  MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

  // static ArrayList<DomeShape> domes = new ArrayList<DomeShape>();
  /** domeName */
  private String domeName = "";

  /** nextID */
  static int nextID = 0;

  /**
   * @param wwd Creates a "Dome" in the center of the viewport, relative to the altitude the camera
   *        is at the time of call, with basic attributes
   * @throws Exception
   */
  public FlatDomeShape(WorldWindow wwd) throws Exception {
    super();
    Position position = ShapeUtils.getNewShapePosition(wwd);
    double sizeInMeters = ShapeUtils.getViewportScaleFactor(wwd);
    double diam = sizeInMeters / 2.0;

    this.setCenterPosition(position);
    this.setNorthSouthRadius(diam);
    this.setVerticalRadius(diam/2);
    this.setEastWestRadius(diam);
    this.setAttributes(new BasicShapeAttributes());

    this.domeName = this.generateName();
    // domes.add(this);
    this.selectedIntFr.getRendLayer().addRenderable(this);
    this.selectedIntFr.getWwGLCanvas().redraw();
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
   *        <p>
   *        Creates a "Dome" at the provided position, with the provided sizes in meters, , with
   *        basic attributes
   * @throws Exception
   */
  public FlatDomeShape(WorldWindow wwd, Position pos, double northSouthRadius, double verticalRadius,
      double eastWestRadius) throws Exception {
    super(pos, northSouthRadius, verticalRadius, eastWestRadius);
    this.setAttributes(new BasicShapeAttributes());
    this.domeName = this.generateName();
    this.selectedIntFr.getRendLayer().addRenderable(this);
    this.selectedIntFr.getWwGLCanvas().redraw();
    this.addToPool();
    ShapeListener.lastSelectedObj = this;
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
    ShapesPool.getInstance().removeShape(this.getIdentifier());
  }

  /**
   * TODO DESCRIPTION
   * 
   * @return
   */
  private String generateName() {
    nextID++;
    if (nextID < 10) {
      return "Flat dome " + "0" + nextID;
    }
    return "Flat dome " + nextID;
  }

  /**
   * @see _workspace.shapes.IShape#setIdentifier(java.lang.String)
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
    this.selectedIntFr.getRendLayer().removeRenderable(this);
    this.selectedIntFr.getWwGLCanvas().redraw();
    ShapeListener.lastSelectedObj = null;
  }

  /**
   * saves this object to file
   * 
   * @param file - file object
   * @param name - object identifier
   */
  public void saveMe(File file, String name) {

    Properties p = new Properties();

    p.setProperty("dome.name", name);

    Position pos = this.getCenterPosition();

    p.setProperty("dome.centerPosition.latitude", String.valueOf(pos.getLatitude().getDegrees()));
    p.setProperty("dome.centerPosition.longitude", String.valueOf(pos.getLongitude().getDegrees()));
    p.setProperty("dome.centerPosition.elevation", String.valueOf(pos.getElevation()));

    p.setProperty("dome.nsRadius", String.valueOf(this.getNorthSouthRadius()));
    p.setProperty("dome.evRadius", String.valueOf(this.getEastWestRadius()));
    p.setProperty("dome.vertRadius", String.valueOf(this.getVerticalRadius()));

    p.setProperty("dome.tilt", String.valueOf(this.getTilt()));
    p.setProperty("dome.roll", String.valueOf(this.getRoll()));

    try {

      p.store(new FileOutputStream(file), "My Shape's properties");

    } catch (FileNotFoundException e) {

      e.printStackTrace();

    } catch (IOException e) {

      e.printStackTrace();

    }

  }

}// EOF
