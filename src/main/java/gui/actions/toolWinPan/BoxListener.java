/*
 * Copyright (C) TBA BV
 * All rights reserved.
 * www.tba.nl
 */
package gui.actions.toolWinPan;

import gov.nasa.worldwind.render.Ellipsoid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utility.MyLogger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;
import _workspace.shapes.ShapeListener;

import gui.ResizeComponent;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
public class BoxListener implements ActionListener {


    /**
     * Constructs a new instance.
     */
    public BoxListener() {
      super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
        ResizeComponent.setSizeBoxValue((Integer)ResizeComponent.getSizeBox().getValue());
        ResizeComponent.getSlider().setValue(ResizeComponent.getSizeBoxValue());
        ResizeComponent.resizeTo(ResizeComponent.getSizeBoxValue());
        selectedIntFr.wwGLCanvas.redraw();
    }
}




