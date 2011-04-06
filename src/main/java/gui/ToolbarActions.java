package gui;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Ellipsoid;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.event.AncestorListener;

import utility.MyLogger;

//TODO explore EVENTHANDLER
/**
 * 
 */
public class ToolbarActions extends AbstractAction {
	static ToolbarActions instance = null;


	ToolbarActions() {
		
	}

	// @Override
	public void actionPerformed(ActionEvent e) {
		MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

		if (e.getSource().equals(InternalPalleteToobar.toolButtons.get(0))) {
			MyLogger.info(this, "New sphere pressed");
			if (selectedIntFr != null) {
				DomeShape d = new DomeShape(selectedIntFr.wwGLCanvas);
				MyLogger.info(this, "" + d.getName() + " added");
			} else
				MyLogger.error(this, "No suitable workspace was found"); //new NullPointerException() can be added
		}
		
		
		if (e.getSource().equals(InternalPalleteToobar.toolButtons.get(1))) {
			MyLogger.info(this, "New CompoundConnector pressed");
			if (selectedIntFr != null) {
				new CompoundConnector();
				

				selectedIntFr.wwGLCanvas.redrawNow();
				MyLogger.info(this, "CompoundConnector added");
			} else
				MyLogger.error(this, "No suitable workspace was found"); //new NullPointerException() can be added
		}
		
		
		
	}

	public static ToolbarActions getInstance() {
		if (instance == null) {
			instance = new ToolbarActions();
		}
		return instance;

	}
}
