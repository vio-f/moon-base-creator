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
public class ChangeTiltPanel extends JPanel {

  /** INSTANCE */
  private static ChangeTiltPanel INSTANCE = null;

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
  public ChangeTiltPanel() {
//    selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
    getSlider().setPreferredSize(new Dimension(110, 20));
    this.setLayout(new FlowLayout());
    getSizeBox().setColumns(6);
    getSlider().addChangeListener(new TiltSliderChangeListener());
    getSizeBox().addActionListener(new TiltBoxListener());
    this.add(getSlider());
    this.add(getSizeBox());

  }

  // TODO works only on mouse over
  /**
   * @param tilt new tilt
   */
  public static void tiltTo(double tilt) {
    Object obj = null;

    obj = ShapeListener.lastSelectedObj;
    // String s = ShapeListener.lastSelectedObj.toString();
    MyLogger.getLogger().error(obj);

    if (obj == null) {
      MyLogger.error(getInstance(), "No component selected");
      return;

    }
    if (obj instanceof Ellipsoid) {
      ((Ellipsoid) obj).setTilt(Angle.fromDegrees(tilt));
      
    }

  }

  /**
   * Set slider.
   * 
   * @param slider
   */
  public static void setSlider(JSlider slider) {
    ChangeTiltPanel.slider = slider;
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
    ChangeTiltPanel.sizeBoxValue = sizeBoxValue;
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
    ChangeTiltPanel.sizeBox = sizeBox;
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
  public static ChangeTiltPanel getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ChangeTiltPanel();
    }
    return INSTANCE;
  }

}
