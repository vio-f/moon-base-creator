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

		if (e.getSource().equals(InternalPalleteToobar.sphereButt)) {
			MyLogger.info(this, "New sphere pressed");
			if (selectedIntFr != null) {
				//TODO  this is NOT acceptable!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				Ellipsoid elip = EllipsoidFactory.getInstance().makeNewEllipsoid(selectedIntFr.wwGLCanvas);
				RenderableLayer layer = new RenderableLayer();
				layer.addRenderable(elip);
				selectedIntFr.getStuff().add(layer);
				MyLogger.info(this, "Sphere painted");
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
