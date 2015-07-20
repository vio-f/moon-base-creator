/**
 * 
 */
package gui.jaccordian.jAccordionPanels;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.render.Ellipsoid;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;

import utility.MyLogger;
import _workspace.shapes.ShapeListener;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
@SuppressWarnings("serial")
public class ChangeRollPanel extends JPanel {

  /** INSTANCE */
  private static ChangeRollPanel INSTANCE = null;

  /** selectedIntFr */
//  private static MoonWorkspaceInternalFrame selectedIntFr = null;

  /** slider */
  private static JSlider slider = new JSlider(0, 360, 15);

  /** sizeBoxValue */
  private static int sizeBoxValue = 0;

  /**
   * sizeBox defines the format of the JFormattedTextField
   * */
  private static JFormattedTextField sizeBox = new JFormattedTextField(getSizeBoxValue());

  /**
   * Constructs a new instance.
   */
  private ChangeRollPanel() {
//    selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
    getSlider().setPreferredSize(new Dimension(110, 20));
    this.setLayout(new FlowLayout());
    getSizeBox().setColumns(6);
    getSlider().addChangeListener(new RollSliderChangeListener());
    getSizeBox().addActionListener(new RollBoxListener());
    this.add(getSlider());
    this.add(getSizeBox());

  }

  // TODO works only on mouse over
  /**
   * @param tilt new tilt
   */
  public static void rollTo(double tilt) {
    Object obj = null;

    obj = ShapeListener.lastSelectedObj;
    // String s = ShapeListener.lastSelectedObj.toString();
    //MyLogger.getLogger().error(obj);

    if (obj == null) {
      MyLogger.error(getInstance(), "No component selected");
      return;

    }
    if (obj instanceof Ellipsoid) {
      //((Ellipsoid) obj).setTilt(Angle.fromDegrees(tilt));
      ((Ellipsoid) obj).setRoll(Angle.fromDegrees(tilt));
      
    }

  }

  /**
   * Set slider.
   * 
   * @param slider
   */
  public static void setSlider(JSlider slider) {
    ChangeRollPanel.slider = slider;
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
    ChangeRollPanel.sizeBoxValue = sizeBoxValue;
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
    ChangeRollPanel.sizeBox = sizeBox;
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
  public static ChangeRollPanel getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ChangeRollPanel();
    }
    return INSTANCE;
  }

}
