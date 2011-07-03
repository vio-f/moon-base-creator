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
        ChangeHeadingPanel.getInstance().setSizeBoxValue((Integer)ChangeHeadingPanel.getInstance().getSizeBox().getValue());
        ChangeHeadingPanel.getInstance().getSlider().setValue(ChangeHeadingPanel.getInstance().getSizeBoxValue());
        ChangeHeadingPanel.headingTo(ChangeHeadingPanel.getInstance().getSizeBoxValue());
        try {
          selectedIntFr.getWwGLCanvas().redraw();
        } catch (Exception ee) {
          MyLogger.error(this, "No component present", ee);
        }
    }
}




