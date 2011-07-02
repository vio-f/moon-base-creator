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
public class RollBoxListener implements ActionListener {


    /**
     * Constructs a new instance.
     */
    public RollBoxListener() {
      super();
    }
    

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();
        ChangeRollPanel.setSizeBoxValue((Integer)ChangeRollPanel.getSizeBox().getValue());
        ChangeRollPanel.getSlider().setValue(ChangeRollPanel.getSizeBoxValue());
        ChangeRollPanel.rollTo(ChangeRollPanel.getSizeBoxValue());
        try {
          selectedIntFr.getWwGLCanvas().redraw();
        } catch (Exception ee) {
          MyLogger.error(this, "No component present", ee);
        }
    }
}




