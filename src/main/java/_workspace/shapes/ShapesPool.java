package _workspace.shapes;

import java.util.HashMap;
import java.util.Map;

/**
 * Pool of shapes
 * 
 * @author viorel.florian
 */
public final class ShapesPool {

  /** pool instance */
  private final static ShapesPool poolInstance = new ShapesPool();
  
  /**
   * Get instance
   * 
   * @return pool singleton instance
   */
  public static ShapesPool getInstance() {
    return poolInstance;
  }
  
  /**
   * 
   * Constructs a new instance.
   */
  private ShapesPool() {
    super();
  }
  
  /** pool */
  private final Map<String, IShape> pool = new HashMap<String, IShape>();
  
  
  /**
   * Validate identifier
   *  
   * @param identifier
   * 
   * @return validated identifier
   * 
   * @throws Exception if identifier is not empty
   */
  public static String validateIdentifier(String identifier) throws Exception {
    String _identifier = ("" + identifier).toUpperCase();
    
    if(_identifier.equals("")) {
      throw new Exception("Empty identifier.");
    }
    
    return _identifier; 
  }
  
  /**
   * 
   * Get shape by identifier
   * 
   * @param identifier
   * 
   * @return
   * 
   * @throws Exception 
   */
  public IShape getShape(String identifier) throws Exception {
    
    synchronized (this.pool) {      
    
      return this.pool.get(validateIdentifier(identifier));
    }
  }
  
  /**
   * Add shape with original identifier
   * 
   * @see ShapesPool#addShape(String, IShape)
   * 
   * @param shape
   * @throws Exception 
   */
  public void addShape(IShape shape) throws Exception {
    
    this.addShape(shape.getIdentifier(), shape);
  }
  
  /**
   * Add shape with specific identifier
   * @param identifier
   * @param shape
   * @throws Exception 
   */
  public void addShape(String identifier, IShape shape) throws Exception {
        
    String _identifier = validateIdentifier(identifier);
    
    synchronized (this.pool) {
            
      if (this.pool.get(_identifier) != null) {
        throw new Exception("Invalid (already exist) identifier: " + identifier);
      }
      
      this.pool.put(_identifier, shape);
    }
  }
  
  /**
   * Remove shape from list by identifier
   * 
   * @param identifier
   * 
   * @return
   * 
   * @throws Exception 
   */
  public IShape removeShape(String identifier) throws Exception {
    
    synchronized (this.pool) {
      
      return this.pool.remove(validateIdentifier(identifier));
    }
  }
  
  /**
   * Update shape identifier
   * 
   * @param oldIdentifier
   * @param newIdentifier
   * 
   * @throws Exception
   */
  public void updateIdentifier(String oldIdentifier, String newIdentifier) throws Exception {
    
    String _oldIdentifier = validateIdentifier(oldIdentifier);
    String _newIdentifier = validateIdentifier(newIdentifier);
    
    synchronized (this.pool) {  //make this thread safe. make sure no concurrent identifier updates for the same shape
      
      if (this.pool.get(_oldIdentifier) == null) {
        throw new Exception("Invalid (does NOT exist) OLD identifier: " + _oldIdentifier);
      }

      if (this.pool.get(_newIdentifier) != null) {
        throw new Exception("Invalid (already exist) NEW identifier: " + _newIdentifier);
      }

      IShape shape = this.removeShape(_oldIdentifier);
      this.addShape(_newIdentifier, shape);
    }
  }
  
}
