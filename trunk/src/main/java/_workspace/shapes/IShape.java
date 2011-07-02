
package _workspace.shapes;

import java.io.File;

import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;

/**
 * Shape interface
 * 
 * @author viorel.florian
 */
public interface IShape extends Renderable{
	
	
	/**
	 * 
	 * @param new DomeName
	 */
	public void setIdentifier(String name);
	

  /**
   * Get identifier
   * 
   * @return
   */
  public String getIdentifier();

/**
 * @param lastAttrs
 */
public void setAttributes(ShapeAttributes lastAttrs);

/**
 * @return
 */
public ShapeAttributes getAttributes();



void removeMe() throws Exception;


/**
 * TODO DESCRIPTION
 * @param newFile
 * @param identifier
 */
public void saveMe(File newFile, String identifier);
}