
package _workspace.shapes;

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

/**
 * @return
 */
public double[] getAltitudes();
}
