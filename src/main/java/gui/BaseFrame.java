package gui;

import java.awt.Color;

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
	
	//meniul Edit..
	JMenu editMenu = new JMenu("Edit");
	
	static JMenuItem editShowHideLayerTreeItem = new JMenuItem("Show/Hide LayerTree");
	static JMenuItem editMoveItem = new JMenuItem("MoveIt");
	
	//meniul About
	JMenu helpMenu = new JMenu("Help");
	
	static JMenuItem helpHelpcontentsItem = new JMenuItem("Help contents");
	static JMenuItem helpAboutItem = new JMenuItem("About");
	//TODO: Add more menu items
	
	JToolBar leftTools = new JToolBar(SwingConstants.VERTICAL);

	public static JDesktopPane desktop = new JDesktopPane();




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
	    catch (Exception e) {
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
		
		menuBar.add(editMenu);
		editShowHideLayerTreeItem.setEnabled(false);
		editMenu.add(editShowHideLayerTreeItem);
		editMenu.add(editMoveItem);
		
		menuBar.add(helpMenu);
		helpMenu.add(helpHelpcontentsItem);
		helpMenu.add(helpAboutItem);
		
		// adaugam menuBar la frame
		this.setJMenuBar(menuBar);
		/**********************************************************************/
		//definim listener ptr obiecte
		fileExitItem.addActionListener(theActions);
		fileNewItem.addActionListener(theActions);
		fileOpenItem.addActionListener(theActions);
		
		editShowHideLayerTreeItem.addActionListener(theActions);
		editMoveItem.addActionListener(theActions);
		//TODO add more action listeners
		
		MyAction a = new MyAction();
		fileOpenItem.setAction(a);
		fileSaveItem.setAction(a);
		
		/**********************************************************************/
/*		leftTools.setSize(50, 300);
		leftTools.setBorder(BorderFactory.createLineBorder(Color.black));
		leftTools.setFloatable(true);
		
		// TODO butoane ptr leftTools
		// adaugam leftTools la frame
		this.getRootPane().add(leftTools);*/
		
		desktop.setBackground(Color.LIGHT_GRAY);
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		this.add(desktop);
		desktop.add(new InternalPalleteToobar());
		/**********************************************************************/
		
		
		/**********************************************************************/
		//pornim
		this.setVisible(true);
		//pack();	
		
	}



	
	

	
		
}




