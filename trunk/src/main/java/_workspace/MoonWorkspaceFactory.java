package _workspace;

import java.util.ArrayList;

/**
 * 
 * @author viorel.florian
 */
public class MoonWorkspaceFactory implements MoonWorkspace {
	/** instance */
	private static MoonWorkspaceFactory instance = null;
	/** internalFrames */
	static ArrayList<MoonWorkspaceInternalFrame> internalFrames = new ArrayList<MoonWorkspaceInternalFrame>();
	/** lastSelectedIntFr */
	private MoonWorkspaceInternalFrame lastSelectedIntFr = null;
	
	
	/**
	 * Constructs a new instance.
	 */
	private MoonWorkspaceFactory() {
	  super();
	}

	/**
	 * @param f the lastSelectedIntFr to set
	 */
	public void setLastSelectedIntFr(MoonWorkspaceInternalFrame f) {
		this.lastSelectedIntFr = f;
	}

	/**
	 * @return the lastSelectedIntFr
	 */
	public MoonWorkspaceInternalFrame getLastSelectedIntFr() {
		return this.lastSelectedIntFr;
	}

	/**
	 * @see _workspace.MoonWorkspace#newMoonWorkspace()
	 */
	public void newMoonWorkspace() {
		internalFrames.add(new MoonWorkspaceInternalFrame());
	}

	/**
	 * 
	 * @return   One and only one instance of this factory class.
	 */
	public static MoonWorkspaceFactory getInstance() {

		if (instance == null) {
			instance = new MoonWorkspaceFactory();
		}
		return instance;
	}


}
