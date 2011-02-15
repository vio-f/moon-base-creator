package gui;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class BaseFrame extends JFrame {


	
	
	
	//the almighty constructor
	public BaseFrame() {
		super ("That's right!!! Its a \"Moon Base creator\""); //TODO think of a decent title
		
		
		//setarile necesare ptr vizualizarea Lunii
		
		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, gov.nasa.worldwind.globes.Moon.class.getName());// seteaza tipul astrului care urmeaza a fi vizualiza
        Configuration.setValue(AVKey.MOON_ELEVATION_MODEL_CONFIG_FILE, "config/Moon/MoonElevationModel.xml");//seteaza  ce fisier de elevatie (inaltimi) sa se foloseasca
        Configuration.setValue(AVKey.LAYERS_CLASS_NAMES, LAYERS);// LAYERS a fost dfinit mai sus
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 60000e3);  // 6000km
        
        // se creaza canvasul ptr luna
    	gov.nasa.worldwind.awt.WorldWindowGLCanvas worldWindowGLCanvas;
    	Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
    	
		//initializam Canvasul ptr Luna
		worldWindowGLCanvas = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();
	    worldWindowGLCanvas.setModel(m); //adauga model-ul la canvas
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

		
		/**********************************************************************/
		leftTools.setSize(50, this.getHeight()-50);
		leftTools.setBorder(BorderFactory.createLineBorder(Color.black));
		leftTools.setFloatable(false);
		
		// TODO butoane ptr leftTools
		
		
		// adaugam leftTools la frame
		this.add(leftTools);
		/**********************************************************************/
		
		
		/**********************************************************************/
		// adaugam un container ptr WW model 
		canvasPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		//canvasPanel.setVisible(true);
		canvasPanel.add(worldWindowGLCanvas);

		
		//adaugam canvasPanel la frame
		this.add(canvasPanel);
		/**********************************************************************/

	

		
		/**********************************************************************/
		//pornim
		this.setVisible(true);
		System.out.println("Frame visible"); //TODO remove this latter
		pack();	
		
	}
	
	

	
	/***********Mai jos sunt toate variabilele folosite************************/
	/********************Unele sunt initializate cu valori, altele nu**********/
	/**************************************************************************/
	/**********Astfel componentele sunt accesibile la nevoie,******************/
	/******in afara acestei clase**********************************************/
	


    
	//try {
    	GuiActions theActions = new GuiActions(this); //cream o intstanta al clasei de actiuni
   // } catch(StackOverflowError t) {
   //     // more general: catch(Error t)
        // anything: catch(Throwable t)
    //    System.out.println("Caught "+t);
  //      t.printStackTrace();
  //  };
	
	
	
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

	JPanel canvasPanel = new JPanel(new java.awt.BorderLayout()); 

	//definim layer-ele care vor fi incarcate
	final String LAYERS = "gov.nasa.worldwind.layers.StarsLayer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine40BaseLayer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine40Layer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine30Layer"
        //+ ",gov.nasa.worldwind.layers.Moon.ShadedElevationLayer"
        + ",gov.nasa.worldwind.layers.ScalebarLayer"
        + ",gov.nasa.worldwind.layers.CompassLayer";
	//TODO add more layers	
}




