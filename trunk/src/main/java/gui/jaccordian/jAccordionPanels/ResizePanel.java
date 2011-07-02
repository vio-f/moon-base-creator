package gui.jaccordian.jAccordionPanels;

import gov.nasa.worldwind.render.Ellipsoid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;

import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import _workspace.shapes.ShapeListener;

/**
 * @author Viorel Florian
 * 
 */
@SuppressWarnings("serial")
public class ResizePanel extends JPanel {

  /** INSTANCE */
  private static ResizePanel INSTANCE = null;

  /** slider */
  private static JSlider slider = new JSlider(10, 1000, 100);

  /** sizeBoxValue */
  private static int sizeBoxValue = 100;

  /**
   * sizeBox defines the format of the JFormattedTextField
   * */
  private static JFormattedTextField sizeBox = new JFormattedTextField(getSizeBoxValue());

  /** selectedIntFr */
  private static MoonWorkspaceInternalFrame selectedIntFr = null;

  /** sliderListener */
  private static ResizeSliderChangeListener sliderListener = new ResizeSliderChangeListener();

  /** boxListener */
  private static ResizeBoxListener boxListener = new ResizeBoxListener();


  /**
   * Constructs a new instance.
   */
  public ResizePanel() {
    selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
    getSlider().setPreferredSize(new Dimension(110, 20));
    this.setLayout(new FlowLayout());
    getSizeBox().setColumns(6);

    getSlider().addChangeListener(sliderListener);
    getSizeBox().addActionListener(boxListener);

    this.add(getSlider());
    this.add(getSizeBox());

  }

  // TODO works only on mouse over
  /**
   * @param ns - new size
   */
  public void resizeTo(double ns) {
    Object obj = null;

    obj = ShapeListener.lastSelectedObj;
    // String s = ShapeListener.lastSelectedObj.toString();
    MyLogger.getLogger().error(obj);

    if (obj == null) {
      MyLogger.error(this, "No component selected");
      return;

    }
    if (obj instanceof Ellipsoid) {
      ((Ellipsoid) obj).setEastWestRadius(ns * 100);
      ((Ellipsoid) obj).setNorthSouthRadius(ns * 100);
      ((Ellipsoid) obj).setVerticalRadius(ns * 100);
    }

  }

  /**
   * Set slider.
   * 
   * @param slider
   */
  public static void setSlider(JSlider slider) {
    ResizePanel.slider = slider;
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
    ResizePanel.sizeBoxValue = sizeBoxValue;
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
    ResizePanel.sizeBox = sizeBox;
  }

  /**
   * Get sizeBox.
   * 
   * @return sizeBox
   */
  public JFormattedTextField getSizeBox() {
    return sizeBox;
  }

  /**
   * Get instance.
   * 
   * @return instance
   */
  public static ResizePanel getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ResizePanel();
    }
    return INSTANCE;
  }

}// EOF
