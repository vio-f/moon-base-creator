package gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class BaseFrame extends JFrame {
	/***********Mai jos sunt toate variabilele folosite************************/
	/********************Unele sunt initializate cu valori, altele nu**********/
	/**************************************************************************/
	/**********Astfel componentele sunt accesibile la nevoie,******************/
	/******in afara acestei clase**********************************************/
	
    GuiActions theActions = new GuiActions(this); //cream o intstanta al clasei de actiuni

	
	
	JMenuBar menuBar = new JMenuBar();
	
	//meniul file
	JMenu fileMenu = new JMenu("File");
	
	static JMenuItem fileNewItem = new JMenuItem("New...");
	static JMenuItem fileOpenItem = new JMenuItem("Open...");
	static JMenuItem fileSaveItem = new JMenuItem("Save...");
	static JMenuItem fileExitItem = new JMenuItem("Exit...");
	
	//meniul About
	JMenu helpMenu = new JMenu("Help");
	
	static JMenuItem helpHelpcontentsItem = new JMenuItem("Help contents");
	static JMenuItem helpAboutItem = new JMenuItem("About");
	//TODO: Add more menu items
	
	JToolBar leftTools = new JToolBar(SwingConstants.VERTICAL);

	static JDesktopPane desktop = new JDesktopPane();

	//definim layer-ele care vor fi incarcate
	final static String LAYERS = "gov.nasa.worldwind.layers.StarsLayer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine40BaseLayer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine40Layer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine30Layer"
        //+ ",gov.nasa.worldwind.layers.Moon.ShadedElevationLayer"
        + ",gov.nasa.worldwind.layers.ScalebarLayer"
        + ",gov.nasa.worldwind.layers.CompassLayer";
	//TODO add more layers


	/**********************************************************************/
	/**********************************************************************/
	
	
	//the almighty constructor
	public BaseFrame() {
		super ("That's right!!! Its a \"Moon Base creator\""); //TODO think of a decent title
		
		

		/**********************************************************************/
	    
	    
		/**********************************************************************/
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800, 600); //TODO remove this latter    
			
		
		//seteaza frame LookandFeel dupa sistem
		try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	    	// TODO adauga rasp ptr exceptie
	    }
	    catch (ClassNotFoundException e) {
	    	// TODO adauga rasp ptr exceptie
	    }
	    catch (InstantiationException e) {
	    	// TODO adauga rasp ptr exceptie
	    }
	    catch (IllegalAccessException e) {
	    	// TODO adauga rasp ptr exceptie
	    }
		/**********************************************************************/
	    
	    
		/**********************************************************************/

		
		
		
		menuBar.add(fileMenu);
		
		fileMenu.add(fileNewItem);
		fileMenu.addSeparator();// adaugam un separator
		fileMenu.add(fileOpenItem);
		fileMenu.add(fileSaveItem);
		fileMenu.addSeparator();// adaugam un separator
		fileMenu.add(fileExitItem);
		
		menuBar.add(helpMenu);
		helpMenu.add(helpHelpcontentsItem);
		helpMenu.add(helpAboutItem);
		
		// adaugam menuBar la frame
		this.setJMenuBar(menuBar);
		/**********************************************************************/
		fileExitItem.addActionListener(theActions);
		fileNewItem.addActionListener(theActions);
		//TODO add more action listeners
		
		/**********************************************************************/
		leftTools.setSize(50, 300);
		leftTools.setBorder(BorderFactory.createLineBorder(Color.black));
		leftTools.setFloatable(true);
		
		// TODO butoane ptr leftTools
		
		
		// adaugam leftTools la frame
		desktop.add(leftTools);
		desktop.setBackground(Color.LIGHT_GRAY);
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		this.add(desktop);
		
		/**********************************************************************/
		
		
		/**********************************************************************/
		//pornim
		this.setVisible(true);
		//pack();	
		
	}



	
	

	
		
}




