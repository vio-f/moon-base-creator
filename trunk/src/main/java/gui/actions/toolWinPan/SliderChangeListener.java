/*
 * Copyright (C) TBA BV
 * All rights reserved.
 * www.tba.nl
 */
package gui.actions.toolWinPan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import gui.ResizeComponent;

import utility.MyLogger;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */

public class SliderChangeListener implements ChangeListener {

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent )
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

    ResizeComponent.setSizeBoxValue(ResizeComponent.getSlider().getValue());
    // sizeBox.setValue(sizeBoxValue);
    ResizeComponent.getSizeBox().setValue(ResizeComponent.getSizeBoxValue());
    ResizeComponent.resizeTo(ResizeComponent.getSizeBoxValue());
    try {
      selectedIntFr.wwGLCanvas.redraw();
    } catch (Exception ee) {
      MyLogger.getLogger().error("No wwd present");
    }
  }
}
