package gui;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.cache.AbstractFileStore;

import java.awt.Color;
import java.awt.event.ComponentEvent;

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
		//let us initialize  all
		final JFrame theFrame = new JFrame("That's right its a \"Moon Base creator\"");
		
		//definim layer-ele care vor fi incarcate
		final String LAYERS = "gov.nasa.worldwind.layers.StarsLayer"
            + ",gov.nasa.worldwind.layers.Moon.Clementine40BaseLayer"
            + ",gov.nasa.worldwind.layers.Moon.Clementine40Layer"
            + ",gov.nasa.worldwind.layers.Moon.Clementine30Layer"
            //+ ",gov.nasa.worldwind.layers.Moon.ShadedElevationLayer"
            + ",gov.nasa.worldwind.layers.ScalebarLayer"
            + ",gov.nasa.worldwind.layers.CompassLayer";
		//TODO add more layers
		
		
		
		//setarile necesare ptr vizualizare
		
		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, gov.nasa.worldwind.globes.Moon.class.getName());
        Configuration.setValue(AVKey.MOON_ELEVATION_MODEL_CONFIG_FILE, "config/Moon/MoonElevationModel.xml");
        Configuration.setValue(AVKey.LAYERS_CLASS_NAMES, LAYERS);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 60000e3);  // 6000km
       

		
		gov.nasa.worldwind.awt.WorldWindowGLCanvas worldWindowGLCanvas;
		worldWindowGLCanvas = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();
		
		Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		//Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
	    worldWindowGLCanvas.setModel(m); //adauga model-ul la canvas
	        
			
		
		/************seteaza frame LookandFeel dupa sistem************/
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
	    /*************************************************************/
		
	    theFrame.addComponentListener(new java.awt.event.ComponentAdapter() 
		{
			public void componentResized(ComponentEvent e)
			{
				//TODO on window resize repaint
				
			}
		});

	    
	    
		theFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		theFrame.setSize(800, 600); //TODO remove this latter
		
		
		
		
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem fileExitItem = new JMenuItem("Exit");
		menuBar.add(fileMenu);
		fileMenu.add(fileExitItem);
		//TODO: Add more menu items
		
		//**********************************************************************//
		
		JToolBar leftTools = new JToolBar(SwingConstants.VERTICAL);
		leftTools.setSize(50, theFrame.getHeight()-50);
		leftTools.setBorder(BorderFactory.createLineBorder(Color.black));
		leftTools.setFloatable(false);
		
		// butoane ptr leftTools
		
		
		
		JPanel canvasPanel = new JPanel(new java.awt.BorderLayout()); 
		canvasPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		//canvasPanel.setVisible(true);
		canvasPanel.add(worldWindowGLCanvas);

		
		//add panels
		theFrame.add(leftTools);
		theFrame.add(canvasPanel);		
		
		
		
		// adaugam componentele
		theFrame.setJMenuBar(menuBar);
		
		//setam proprietatile frame-ului

		
		//aici se adauga componentele
		
		
		
		//pornim
		theFrame.setVisible(true);
		//pack();
		
	}

}
