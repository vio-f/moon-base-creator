package gui;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.cache.AbstractFileStore;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class BaseFrame extends JFrame {

	//definim layer-ele care vor fi incarcate
	final String LAYERS = "gov.nasa.worldwind.layers.StarsLayer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine40BaseLayer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine40Layer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine30Layer"
        //+ ",gov.nasa.worldwind.layers.Moon.ShadedElevationLayer"
        + ",gov.nasa.worldwind.layers.ScalebarLayer"
        + ",gov.nasa.worldwind.layers.CompassLayer";
	//TODO add more layers	
	
	
	
	//the almighty constructor
	public BaseFrame() {
		 
		

		/**********************************************************************/
		//setarile necesare ptr vizualizarea Lunii
		
		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, gov.nasa.worldwind.globes.Moon.class.getName());// seteaza tipul astrului care urmeaza a fi vizualiza
        Configuration.setValue(AVKey.MOON_ELEVATION_MODEL_CONFIG_FILE, "config/Moon/MoonElevationModel.xml");//seteaza  ce fisier de elevatie (inaltimi) sa se foloseasca
        Configuration.setValue(AVKey.LAYERS_CLASS_NAMES, LAYERS);// LAYERS a fost dfinit mai sus
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 60000e3);  // 6000km
       

		//initializam Canvasul ptr Luna
		gov.nasa.worldwind.awt.WorldWindowGLCanvas worldWindowGLCanvas;
		worldWindowGLCanvas = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();
		
		Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME); // se initializeaza modelul
	    worldWindowGLCanvas.setModel(m); //adauga model-ul la canvas
		/**********************************************************************/
	    
	    
		/**********************************************************************/
	    //creem containerul principal
		final JFrame theFrame = new JFrame("That's right!!! Its a \"Moon Base creator\""); //TODO think of decent title
		theFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		theFrame.setSize(800, 600); //TODO remove this latter    
			
		
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
	    //un menubar nu strica
		JMenuBar menuBar = new JMenuBar();
		
		//meniul File
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem fileNewItem = new JMenuItem("New...");
		JMenuItem fileOpenItem = new JMenuItem("Open...");
		JMenuItem fileSaveItem = new JMenuItem("Save...");
		JMenuItem fileExitItem = new JMenuItem("Exit...");
		
		GuiActions theActions = new GuiActions(this);
		fileExitItem.addActionListener(theActions);
		
		
		
		menuBar.add(fileMenu);
		
		fileMenu.add(fileNewItem);
		fileMenu.addSeparator();// adaugam un separator
		fileMenu.add(fileOpenItem);
		fileMenu.add(fileSaveItem);
		fileMenu.addSeparator();// adaugam un separator
		fileMenu.add(fileExitItem);
		

		//meniul About
		JMenu helpMenu = new JMenu("Help");
		
		JMenuItem helpHelpcontentsItem = new JMenuItem("Help contents");
		JMenuItem helpAboutItem = new JMenuItem("About");

		
		menuBar.add(helpMenu);
		helpMenu.add(helpHelpcontentsItem);
		helpMenu.add(helpAboutItem);
		
		
		//TODO: Add more menu items
		
		// adaugam menuBar la frame
		theFrame.setJMenuBar(menuBar);
		/**********************************************************************/

		
		/**********************************************************************/
		//urmeaza un toolbar 
		JToolBar leftTools = new JToolBar(SwingConstants.VERTICAL);
		leftTools.setSize(50, theFrame.getHeight()-50);
		leftTools.setBorder(BorderFactory.createLineBorder(Color.black));
		leftTools.setFloatable(false);
		
		// TODO butoane ptr leftTools
		
		
		// adaugam leftTools la frame
		theFrame.add(leftTools);
		/**********************************************************************/
		
		
		/**********************************************************************/
		// adaugam un container ptr model 
		JPanel canvasPanel = new JPanel(new java.awt.BorderLayout()); 
		canvasPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		//canvasPanel.setVisible(true);
		canvasPanel.add(worldWindowGLCanvas);

		
		//adaugam canvasPanel la frame
		theFrame.add(canvasPanel);
		/**********************************************************************/

	

		
		/**********************************************************************/
		//pornim
		theFrame.setVisible(true);
		System.out.println("Frame visible"); //remove this latter
		//pack();
		
	}

}

