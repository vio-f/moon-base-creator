package _workspace.shapes;

import gov.nasa.worldwind.geom.Position;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;

import utility.MyLogger;

/**
 * Provides a static way to load shapes from a file
 * 
 * @author viorel.florian
 */
public class ShapeLoaderFromFile {
  /** selectedIntFr */
  static MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
  
  /**
   * Constructs a new instance.
   */
  
  
  
  public static void loadShape(File file){
    

      
      Properties property = new Properties();

      try {

      property.load(new FileInputStream(file));

      } catch (Exception e){
        MyLogger.error(null, "Error while trying to load component from file", e);
        e.printStackTrace();
        return;
      }


      String shapeName = property.getProperty("dome.name");
      
      
      double latitude = Double.parseDouble(property.getProperty("dome.centerPosition.latitude"));
      double longitude = Double.parseDouble(property.getProperty("dome.centerPosition.longitude"));
      double elevation = Double.parseDouble(property.getProperty("dome.centerPosition.elevation"));
      
      double nsRadius = Double.parseDouble(property.getProperty("dome.nsRadius"));
      double evRadius = Double.parseDouble(property.getProperty("dome.evRadius"));
      double vertRadius = Double.parseDouble(property.getProperty("dome.vertRadius"));
      
      double tilt = Double.parseDouble(property.getProperty("dome.tilt"));
      double roll = Double.parseDouble(property.getProperty("dome.roll"));
      double heading = Double.parseDouble(property.getProperty("dome.heading"));
      
      //TODO implement distinction between dome.name values
      try {
    	  if(shapeName.startsWith("Dome")){
    		  new DomeShape(selectedIntFr.getWwGLCanvas(), Position.fromDegrees(latitude, longitude, elevation), nsRadius, vertRadius, evRadius);
    	  }
    	  if(shapeName.startsWith("Flat Dome")){
    		  new DomeShape(selectedIntFr.getWwGLCanvas(), Position.fromDegrees(latitude, longitude, elevation), nsRadius, vertRadius, evRadius);
    	  }
    	  if(shapeName.startsWith("Connector Dome")){
    		  new DomeShape(selectedIntFr.getWwGLCanvas(), Position.fromDegrees(latitude, longitude, elevation), nsRadius, vertRadius, evRadius);
    	  }
    	  
    	  
    	  
        
      } catch (Exception e) {
        MyLogger.error(null, "Error while trying to create component", e);
        e.printStackTrace();
        return;
      }
      
      
    }
}//EOF


