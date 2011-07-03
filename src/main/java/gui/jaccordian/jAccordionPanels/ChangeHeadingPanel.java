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
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import _workspace.shapes.ShapeListener;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
@SuppressWarnings("serial")
public class ChangeHeadingPanel extends JPanel {

  /** INSTANCE */
  private static ChangeHeadingPanel INSTANCE = null;

  /** selectedIntFr */
  private static MoonWorkspaceInternalFrame selectedIntFr = null;

  /** slider */
  private JSlider slider = new JSlider(0, 360, 15);

  /** sizeBoxValue */
  private int sizeBoxValue = 0;

  /**
   * sizeBox defines the format of the JFormattedTextField
   * */
  private JFormattedTextField sizeBox = new JFormattedTextField(getSizeBoxValue());

  /**
   * Constructs a new instance.
   */
  private ChangeHeadingPanel() {
    setSelectedIntFr(MoonWorkspaceFactory.getInstance().getLastSelectedIntFr());
    getSlider().setPreferredSize(new Dimension(110, 20));
    this.setLayout(new FlowLayout());
    getSizeBox().setColumns(6);
    getSlider().addChangeListener(new HeadingSliderChangeListener());
    getSizeBox().addActionListener(new HeadingBoxListener());
    this.add(getSlider());
    this.add(getSizeBox());

  }

  // TODO works only on mouse over
  /**
   * @param newHeading new tilt
   */
  public static void headingTo(double newHeading) {
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
      ((Ellipsoid) obj).setHeading(Angle.fromDegrees(newHeading));
      
    }

  }

  /**
   * Set slider.
   * 
   * @param slider
   */
  public void setSlider(JSlider slider) {
    this.slider = slider;
  }

  /**
   * Get slider.
   * 
   * @return slider
   */
  public JSlider getSlider() {
    return slider;
  }

  /**
   * Set sizeBoxValue.
   * 
   * @param sizeBoxValue
   */
  public void setSizeBoxValue(int sizeBoxValue) {
    this.sizeBoxValue = sizeBoxValue;
  }

  /**
   * Get sizeBoxValue.
   * 
   * @return sizeBoxValue
   */
  public int getSizeBoxValue() {
    return this.sizeBoxValue;
  }

  /**
   * Set sizeBox.
   * 
   * @param sizeBox
   */
  public void setSizeBox(JFormattedTextField sizeBox) {
    this.sizeBox = sizeBox;
  }

  /**
   * Get sizeBox.
   * 
   * @return sizeBox
   */
  public JFormattedTextField getSizeBox() {
    return this.sizeBox;
  }

  /**
   * Get instance.
   * 
   * @return instance
   */
  public static ChangeHeadingPanel getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ChangeHeadingPanel();
    }
    return INSTANCE;
  }

public static void setSelectedIntFr(MoonWorkspaceInternalFrame selectedIntFr) {
	ChangeHeadingPanel.selectedIntFr = selectedIntFr;
}

public static MoonWorkspaceInternalFrame getSelectedIntFr() {
	return selectedIntFr;
}

}
