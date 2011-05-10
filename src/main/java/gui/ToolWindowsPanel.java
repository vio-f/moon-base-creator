/**
 * 
 */
package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;

/**
 * @author Viorel Florian
 * 
 */
public class ToolWindowsPanel extends JPanel {
	private static JDesktopPane twpDesk = new JDesktopPane();

	/**
	 * 
	 */
	public ToolWindowsPanel() {
		super();
		this.setLayout(new GridLayout());
		this.setPreferredSize(new Dimension(200, 800));// heigth does not matter
														// cause of the layout
		twpDesk.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		this.add(twpDesk);
	}

	/**
	 * Adds an internal frame to his panels desktop
	 */
	public static void addCompToThisPanel(Component cmp) {
		twpDesk.add(cmp);
	}
}
