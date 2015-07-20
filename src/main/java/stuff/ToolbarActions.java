package stuff;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

//TODO explore EVENTHANDLER
/**
 * 
 */
public class ToolbarActions extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	static ToolbarActions instance = null;


	private DomeShape lastDome = null;
	
	ToolbarActions() {
		
	}
*/
	 @Override
	public void actionPerformed(ActionEvent e) {
	 }
	 /*
		MoonWorkspaceInternalFrame selectedIntFr = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr();

		try {
		if (e.getSource().equals(InternalPalleteToobar.toolButtons.get(0))) {
			MyLogger.info(this, "New sphere pressed");
			if (selectedIntFr != null) {
				DomeShape d = new DomeShape(selectedIntFr.wwGLCanvas);
				this.lastDome = d;
				MyLogger.info(this, "" + d.getIdentifier() + " added");
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
		
		
		if (e.getSource().equals(InternalPalleteToobar.toolButtons.get(2))) {
          MyLogger.info(this, "get current domeName pressed");
          if (selectedIntFr != null) {
              
            String lastDomeIdentifier = this.lastDome.getIdentifier();
              MyLogger.info(this, "Expected last dome: " + lastDomeIdentifier + " VS found last dome: " + ShapesPool.getInstance().getShape(lastDomeIdentifier).getIdentifier());
              selectedIntFr.wwGLCanvas.redrawNow();
              MyLogger.info(this, "CompoundConnector added");
          } else
              MyLogger.error(this, "No suitable workspace was found"); //new NullPointerException() can be added
      }
		} catch(Exception e1) {
		  MyLogger.error(this, e1); //new NullPointerException() can be added
		}
		
		
	}

	public static ToolbarActions getInstance() {
		if (instance == null) {
			instance = new ToolbarActions();
		}
		return instance;

	}*/
}
