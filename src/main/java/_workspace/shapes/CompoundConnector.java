/**
 * 
 */
package _workspace.shapes;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;

import java.io.File;
import java.util.ArrayList;

import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;

/**
 * @author Viorel Florian
 * 
 */
public class CompoundConnector /* extends ExtrudedPolygon */implements IShape {
  /** selectedIntFr */
  MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

  /** sideAttributes */
  ShapeAttributes sideAttributes = new BasicShapeAttributes();

  /** capAttributes */
  ShapeAttributes capAttributes = new BasicShapeAttributes(this.sideAttributes);

  ExtrudedPolygon pgon = null;

  /**
   * Constructs a new instance.
   */
  public CompoundConnector() {
    this.sideAttributes.setInteriorMaterial(Material.MAGENTA);
    this.sideAttributes.setOutlineOpacity(0.5);
    this.sideAttributes.setInteriorOpacity(0.5);
    this.sideAttributes.setOutlineMaterial(Material.GREEN);
    this.sideAttributes.setOutlineWidth(2);
    this.sideAttributes.setDrawOutline(true);
    this.sideAttributes.setDrawInterior(true);
    this.sideAttributes.setEnableLighting(true);

    this.capAttributes.setInteriorMaterial(Material.YELLOW);
    this.capAttributes.setInteriorOpacity(0.8);
    this.capAttributes.setDrawInterior(true);
    this.capAttributes.setEnableLighting(true);

    ShapeAttributes sideHighlightAttributes = new BasicShapeAttributes(this.sideAttributes);
    sideHighlightAttributes.setOutlineMaterial(Material.WHITE);
    sideHighlightAttributes.setOutlineOpacity(1);

    // Create a path, set some of its properties and set its attributes.
    ArrayList<Position> pathPositions = new ArrayList<Position>();

    pathPositions.add(Position.fromDegrees(28, -106, 3e4));// 28, -106, 3e4));
    pathPositions.add(Position.fromDegrees(35, -104, 3e4));// (35, -104, 3e4));
    pathPositions.add(Position.fromDegrees(35, -107, 9e4));// (35, -107, 9e4));
    pathPositions.add(Position.fromDegrees(28, -107, 9e4));// (28, -107, 9e4));
    pathPositions.add(Position.fromDegrees(28, -106, 3e4));// (28, -106, 3e4));

    this.pgon = new ExtrudedPolygon(pathPositions);

    this.pgon.setSideAttributes(this.sideAttributes);
    this.pgon.setSideHighlightAttributes(sideHighlightAttributes);
    this.pgon.setCapAttributes(this.capAttributes);

    this.selectedIntFr.getRendLayer().addRenderable(this.pgon);

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

  /*
   * (non-Javadoc)
   * 
   * @see _workspace.shapes.IShape#setAttributes(gov.nasa.worldwind.render.ShapeAttributes)
   */
  @Override
  public void setAttributes(ShapeAttributes lastAttrs) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see _workspace.shapes.IShape#getAttributes()
   */
  @Override
  public ShapeAttributes getAttributes() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.worldwind.render.Renderable#render(gov.nasa.worldwind.render.DrawContext)
   */
  @Override
  public void render(DrawContext dc) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see _workspace.shapes.IShape#setIdentifier(java.lang.String)
   */
  @Override
  public void setIdentifier(String name) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see _workspace.shapes.IShape#removeMe()
   */
  public void removeMe() throws Exception {
  }

  /*
   * (non-Javadoc)
   * 
   * @see _workspace.shapes.IShape#generateName()
   */
  protected String generateName() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see _workspace.shapes.IShape#removeFromPool()
   */
  protected void removeFromPool() throws Exception {
  }

  /*
   * (non-Javadoc)
   * 
   * @see _workspace.shapes.IShape#addToPool()
   */
  protected void addToPool() throws Exception {
  }

  /**
   * @see _workspace.shapes.IShape#saveMe(java.io.File, java.lang.String)
   */
  public void saveMe(File newFile, String identifier) {
  }

}
