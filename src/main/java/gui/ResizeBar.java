
package gui;

import gov.nasa.worldwind.render.Ellipsoid;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utility.MyLogger;

import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import _workspace.shapes.IShape;
import _workspace.shapes.ShapeListener;

import gui.actions.toolWinPan.*;

/**
 * @author Viorel Florian
 * 
 */
@SuppressWarnings("serial")
public class ResizeBar extends JPanel {
  
    private static ResizeBar INSTANCE = null;
    private static JSlider slider = new JSlider(10, 1000, 100);
	private static int sizeBoxValue = 100;
	private static JFormattedTextField sizeBox = new JFormattedTextField(
			getSizeBoxValue()); // defines the format of the JFormattedTextField
	private static MoonWorkspaceInternalFrame selectedIntFr = null;
	
	private static SliderChangeListener scl = new SliderChangeListener();
	private static BoxListener bl = new BoxListener();

	/**
	 * 
	 */
	public ResizeBar() {
        selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
		getSlider().setPreferredSize(new Dimension(110, 20));
		this.setLayout(new FlowLayout());
		getSizeBox().setColumns(6);
		
		
		getSlider().addChangeListener(scl);
		getSizeBox().addActionListener(bl);

		this.add(getSlider());
		this.add(getSizeBox());

	}

	//TODO works only on mouse over
	public static void resizeTo(double ns){
	    Object obj = null;

	    obj = ShapeListener.lastSelectedObj;
	    //String s = ShapeListener.lastSelectedObj.toString();
	    MyLogger.getLogger().error(obj);
	    
	    if (obj == null) {
	        MyLogger.getLogger().error("obj=null");
	        return;
	        
	    }
	    if (obj instanceof Ellipsoid){
	        ((Ellipsoid) obj).setEastWestRadius(ns*100);
	        ((Ellipsoid) obj).setNorthSouthRadius(ns*100);
	        ((Ellipsoid) obj).setVerticalRadius(ns*100);
	    }
	    
	}

  /**
   * Set slider.
   * 
   * @param slider
   */
  public static void setSlider(JSlider slider) {
    ResizeBar.slider = slider;
  }

  /**
   * Get slider.
   * 
   * @return slider
   */
  public static JSlider getSlider() {
    return slider;
  }

  /**
   * Set sizeBoxValue.
   * 
   * @param sizeBoxValue
   */
  public static void setSizeBoxValue(int sizeBoxValue) {
    ResizeBar.sizeBoxValue = sizeBoxValue;
  }

  /**
   * Get sizeBoxValue.
   * 
   * @return sizeBoxValue
   */
  public static int getSizeBoxValue() {
    return sizeBoxValue;
  }

  /**
   * Set sizeBox.
   * 
   * @param sizeBox
   */
  public static void setSizeBox(JFormattedTextField sizeBox) {
    ResizeBar.sizeBox = sizeBox;
  }

  /**
   * Get sizeBox.
   * 
   * @return sizeBox
   */
  public static JFormattedTextField getSizeBox() {
    return sizeBox;
  }

  /**
   * Get instance.
   * 
   * @return instance
   */
  public static ResizeBar getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ResizeBar();
    }

    return INSTANCE;

  }


	
	
	
	//EOF
}

