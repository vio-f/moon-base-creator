package _workspace;

import java.util.ArrayList;

public class MoonWorkspaceFactory implements MoonWorkspace {
	private static MoonWorkspaceFactory instance = null;
	static ArrayList<MoonWorkspaceInternalFrame> internalFrames = new ArrayList<MoonWorkspaceInternalFrame>();
	private MoonWorkspaceInternalFrame lastSelectedIntFr = null;
	private MoonWorkspaceFactory() {

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

	public void newMoonWorkspace() {
		internalFrames.add(new MoonWorkspaceInternalFrame());
	}

	public static MoonWorkspaceFactory getInstance() {

		if (instance == null) {
			instance = new MoonWorkspaceFactory();
		}
		return instance;
	}


}
