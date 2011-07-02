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
public class TiltBoxListener implements ActionListener {


    /**
     * Constructs a new instance.
     */
    public TiltBoxListener() {
      super();
    }
    

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
        ChangeTiltPanel.setSizeBoxValue((Integer)ChangeTiltPanel.getSizeBox().getValue());
        ChangeTiltPanel.getSlider().setValue(ChangeTiltPanel.getSizeBoxValue());
        ChangeTiltPanel.tiltTo(ChangeTiltPanel.getSizeBoxValue());
        try {
          selectedIntFr.getWwGLCanvas().redraw();
        } catch (Exception ee) {
          MyLogger.error(this, "No component present", ee);
        }
    }
}




