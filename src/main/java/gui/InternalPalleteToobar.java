package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;

public class InternalPalleteToobar extends JInternalFrame {
	//TODO make JButton Array
	static ArrayList<JButton> toolButtons = new ArrayList<JButton>();
	Icon sphereicon = new ImageIcon(getClass().getResource("/res/sphere.png"));

	
	
	public InternalPalleteToobar() {
		super("Tools", //title
			  true,//resizable
			  false,//closeble
			  false,//maximizable
			  false);//iconifiable
		this.setSize(67, 300);
		
		this.setLayout(new FlowLayout());
		// index 0 - sphere button + icon
		toolButtons.add(new JButton(""));
		//make button size relative to icon
		toolButtons.get(0).setPreferredSize(new Dimension(sphereicon.getIconWidth()+4 ,sphereicon.getIconHeight()+6));
		toolButtons.get(0).setIcon(sphereicon);
		toolButtons.get(0).setAlignmentY(BOTTOM_ALIGNMENT);
		toolButtons.get(0).setToolTipText("Creates a Dome relative to current " +
										"altitude in the center of the workspace");
		toolButtons.get(0).addActionListener(ToolbarActions.getInstance());

		toolButtons.add(new JButton(""));
		//make button size relative to icon
		toolButtons.get(1).setPreferredSize(new Dimension(sphereicon.getIconWidth()+4 ,sphereicon.getIconHeight()+6));
		toolButtons.get(1).setAlignmentY(BOTTOM_ALIGNMENT);
		toolButtons.get(1).setToolTipText("Creates a Dome relative to current " +
										"altitude in the center of the workspace");
		toolButtons.get(1).addActionListener(ToolbarActions.getInstance());
		
		toolButtons.add(new JButton(""));
		//make button size relative to icon
		toolButtons.get(2).setPreferredSize(new Dimension(sphereicon.getIconWidth()+4 ,sphereicon.getIconHeight()+6));
		toolButtons.get(2).setAlignmentY(BOTTOM_ALIGNMENT);
		toolButtons.get(2).setToolTipText("Creates a Dome relative to current " +
										"altitude in the center of the workspace");
		toolButtons.get(2).addActionListener(ToolbarActions.getInstance());
		
		
		toolButtons.add(new JButton(""));
		//make button size relative to icon
		toolButtons.get(3).setPreferredSize(new Dimension(sphereicon.getIconWidth()+4 ,sphereicon.getIconHeight()+6));
		toolButtons.get(3).setAlignmentY(BOTTOM_ALIGNMENT);
		toolButtons.get(3).setToolTipText("Creates a Dome relative to current " +
										"altitude in the center of the workspace");
		toolButtons.get(3).addActionListener(ToolbarActions.getInstance());
		
		//TODO replace with bulk add 
		this.add(toolButtons.get(0));
		this.add(toolButtons.get(1));
		this.add(toolButtons.get(2));
		this.add(toolButtons.get(3));
		this.setVisible(true);
	}

	
	

}
