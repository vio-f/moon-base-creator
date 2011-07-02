/*
 * Copyright (C) TBA BV
 * All rights reserved.
 * www.tba.nl
 */
package gui.jaccordian.jAccordionPanels;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */

public class ResizeSliderChangeListener implements ChangeListener {

  /**
   * Constructs a new instance.
   */
  public ResizeSliderChangeListener() {
    super();
  }

  /**
   * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

    ResizePanel.setSizeBoxValue(ResizePanel.getSlider().getValue());
    // sizeBox.setValue(sizeBoxValue);
    ResizePanel.getInstance().getSizeBox().setValue(ResizePanel.getSizeBoxValue());
    ResizePanel.getInstance().resizeTo(ResizePanel.getSizeBoxValue());
    try {
      selectedIntFr.getWwGLCanvas().redraw();
    } catch (Exception ee) {
      MyLogger.error(this, "No component present", ee);
    }
  }
}
