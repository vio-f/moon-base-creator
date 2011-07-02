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

public class RollSliderChangeListener implements ChangeListener {

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent )
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

    ChangeRollPanel.setSizeBoxValue(ChangeRollPanel.getSlider().getValue());
    // sizeBox.setValue(sizeBoxValue);
    ChangeRollPanel.getSizeBox().setValue(ChangeRollPanel.getSizeBoxValue());
    ChangeRollPanel.rollTo(ChangeRollPanel.getSizeBoxValue());
    try {
      selectedIntFr.getWwGLCanvas().redraw();
    } catch (Exception ee) {
      MyLogger.error(this, "No component present", ee);
    }
  }
}
