package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;

public class InternalPalleteToobar extends JInternalFrame {
	//TODO make JButton Array
	static JButton sphereButt = new JButton("");
	Icon sphereicon = new ImageIcon(getClass().getResource("/res/sphere.png"));

	
	
	public InternalPalleteToobar() {
		super("Tools", //title
			  false,//resizable
			  false,//closeble
			  false,//maximizable
			  false);//iconifiable
		this.setSize(80, 300);
		
		this.setLayout(new FlowLayout());
		//TODO set mnemonic (which key activates this button)
		sphereButt.setPreferredSize(new Dimension(sphereicon.getIconWidth()+4 ,sphereicon.getIconHeight()+6));
		sphereButt.setIcon(sphereicon);
		sphereButt.setAlignmentY(BOTTOM_ALIGNMENT);
		sphereButt.addActionListener(ToolbarActions.getInstance());
		//make button size relative to icon
		//sphereButt.setSize(sphereicon.getIconWidth(),sphereicon.getIconHeight()); 
		this.add(sphereButt);
		this.setVisible(true);
	}

	
	 

}
