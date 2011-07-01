/**
 * 
 */
package _workspace.shapes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.util.ShapeUtils;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;

/**
 * @author Viorel Florian Creates and manages "Dome" objects
 */
public class DomeShape extends Ellipsoid implements IShape {
  MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
  // static ArrayList<DomeShape> domes = new ArrayList<DomeShape>();
  /** domeName */
  private String domeName = "";

  /** nextID */
  static int nextID = 0;

  /**
   * @param wwd 
   * Creates a "Dome" in the center of the viewport, relative to the altitude the camera is
   *        at the time of call, with basic attributes
   * @throws Exception 
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

    this.domeName = generateName();
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
   *        <p>
   *        Creates a "Dome" at the provided position, with the provided sizes in meters, , with
   *        basic attributes
   * @throws Exception 
   */
  public DomeShape(WorldWindow wwd, Position pos, double northSouthRadius, double verticalRadius,
      double eastWestRadius) throws Exception {
    super(pos, northSouthRadius, verticalRadius, eastWestRadius);
    this.setAttributes(new BasicShapeAttributes());
    this.domeName = generateName();
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
    this.selectedIntFr.rendLayer.removeRenderable(this);
    this.selectedIntFr.wwGLCanvas.redraw();
    ShapeListener.lastSelectedObj = null;
  }

  
  public void saveMe(File file, String name){
    
    Properties p = new Properties();

    p.setProperty("shape.name", name);

    p.setProperty("shape.centerPosition", String.valueOf(this.getCenterPosition()));

    


    try {

    p.store(new FileOutputStream(file), "My Shape's properties");

    } catch (FileNotFoundException e) {

    e.printStackTrace();

    } catch (IOException e) {

    e.printStackTrace();

    }


    
    
  }
  
  

}
