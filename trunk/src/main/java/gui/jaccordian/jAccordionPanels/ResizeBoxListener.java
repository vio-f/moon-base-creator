/*
 * Copyright (C) TBA BV
 * All rights reserved.
 * www.tba.nl
 */
package gui.jaccordian.jAccordionPanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
public class ResizeBoxListener implements ActionListener {


    /**
     * Constructs a new instance.
     */
    public ResizeBoxListener() {
      super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
        ResizePanel.setSizeBoxValue((Integer)ResizePanel.getInstance().getSizeBox().getValue());
        ResizePanel.getSlider().setValue(ResizePanel.getSizeBoxValue());
        ResizePanel.getInstance().resizeTo(ResizePanel.getSizeBoxValue());
        try{
        selectedIntFr.getWwGLCanvas().redraw();
        } catch (Exception e) {
          MyLogger.error(this, "No world wind window/ or component", e);
        }
    }
}




