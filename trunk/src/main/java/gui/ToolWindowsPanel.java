/**
 * 
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
		this.setPreferredSize(new Dimension(200, 800));// height does not matter
														// cause of the layout
		twpDesk.setBackground(Color.WHITE);
		twpDesk.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		this.add(twpDesk);
	}

	/**
	 * Adds an internal frame to his panels desktop
	 */
	public static void addCompToThisPanel(Component cmp) {
	  //TODO - add validation here to avoid adding the same component more then once 
		twpDesk.add(cmp);
	}
}
