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
public class HeadingBoxListener implements ActionListener {


    /**
     * Constructs a new instance.
     */
    public HeadingBoxListener() {
      super();
    }
    

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
        ChangeHeadingPanel.setSizeBoxValue((Integer)ChangeHeadingPanel.getSizeBox().getValue());
        ChangeHeadingPanel.getSlider().setValue(ChangeHeadingPanel.getSizeBoxValue());
        ChangeHeadingPanel.headingTo(ChangeHeadingPanel.getSizeBoxValue());
        try {
          selectedIntFr.getWwGLCanvas().redraw();
        } catch (Exception ee) {
          MyLogger.error(this, "No component present", ee);
        }
    }
}




