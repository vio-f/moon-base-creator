package gui;

import gui.actions.menubar.EditResizeAct;
import gui.actions.menubar.FileExitAct;
import gui.actions.menubar.FileNewAct;
import gui.actions.menubar.EditShowHideLayerTreeAct;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import stuff.MyAction;

public class BaseFrame extends JFrame {
	/***********Mai jos sunt toate variabilele folosite************************/
	/********************Unele sunt initializate cu valori, altele nu**********/
	/**************************************************************************/
	/**********Astfel componentele sunt accesibile la nevoie,******************/
	/******in afara acestei clase**********************************************/
	
	
	public static JMenuBar menuBar = new JMenuBar();
	
	//meniul file
	public static JMenu fileMenu = new JMenu("File");
	
	public static JMenuItem fileNewItem = new JMenuItem();
	public static JMenuItem fileOpenItem = new JMenuItem("Open...");
	public static JMenuItem fileSaveItem = new JMenuItem("Save...");
	public static JMenuItem fileExitItem = new JMenuItem("Exit...");
	
	//meniul Edit..
	JMenu editMenu = new JMenu("Edit");
	
	public static JMenuItem editShowHideLayerTreeItem = new JMenuItem();
	public static JMenuItem editMoveItem = new JMenuItem("MoveIt");
	public static JMenuItem editResize = new JMenuItem();
	
	//meniul About
	JMenu helpMenu = new JMenu("Help");
	
	public static JMenuItem helpHelpcontentsItem = new JMenuItem("Help contents");
	public static JMenuItem helpAboutItem = new JMenuItem("About");
	//TODO: Add more menu items
	
	public static JToolBar leftTools = new JToolBar(SwingConstants.VERTICAL);

	public static JDesktopPane desktop = new JDesktopPane();


	//public GuiActions theActions = new GuiActions(this); //cream o intstanta al clasei de actiuni
	FileNewAct fileNewAction = new FileNewAct(this);
	Action fileExitAction = new FileExitAct();
	Action editShowHideAction = new EditShowHideLayerTreeAct();
	Action editResizeAction = new EditResizeAct();
	

	/**********************************************************************/
	/**********************************************************************/
	
	
	//the almighty constructor
	public BaseFrame() {
		super ("That's right!!! Its a \"Moon Base creator\""); //TODO think of a decent title
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
		//definim listener ptr obiecte
		fileNewItem.setAction(fileNewAction);
		fileExitItem.setAction(fileExitAction);
		
		editShowHideLayerTreeItem.setAction(editShowHideAction);
		editResize.setAction(editResizeAction);
		//TODO add more action listeners

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
		editMenu.add(editResize);
		
		menuBar.add(helpMenu);
		helpMenu.add(helpHelpcontentsItem);
		helpMenu.add(helpAboutItem);
		
		// adaugam menuBar la frame
		this.setJMenuBar(menuBar);
		/**********************************************************************/

/*		leftTools.setSize(50, 300);
		leftTools.setBorder(BorderFactory.createLineBorder(Color.black));
		leftTools.setFloatable(true);
		
		// TODO butoane ptr leftTools
		// adaugam leftTools la frame
		this.getRootPane().add(leftTools);*/
		/**********************************************************************/
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




