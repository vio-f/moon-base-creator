package gui;

import javax.swing.SwingWorker;

import _workspace.MoonWorkspaceFactory;
import utility.MyLogger;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;


public class StartMe {
	//definim layer-ele care vor fi incarcate
	final static String LAYERS = "gov.nasa.worldwind.layers.StarsLayer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine40BaseLayer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine40Layer"
        + ",gov.nasa.worldwind.layers.Moon.Clementine30Layer"
        //+ ",gov.nasa.worldwind.layers.Moon.ShadedElevationLayer"
        + ",gov.nasa.worldwind.layers.ScalebarLayer"
        + ",gov.nasa.worldwind.layers.CompassLayer";
	//TODO add more layers
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MyLogger.getLogger().info("App started");
		//setarile necesare ptr vizualizarea Lunii
		
		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, gov.nasa.worldwind.globes.Moon.class.getName());// seteaza tipul astrului care urmeaza a fi vizualiza
        Configuration.setValue(AVKey.MOON_ELEVATION_MODEL_CONFIG_FILE, "config/Moon/MoonElevationModel.xml");//seteaza  ce fisier de elevatie (inaltimi) sa se foloseasca
        Configuration.setValue(AVKey.LAYERS_CLASS_NAMES, LAYERS);// LAYERS a fost definit mai sus
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 5000e3);  // 50000km
        MyLogger.getLogger().info("Settings made");
        
        final ProgressDialog progressDialog = new ProgressDialog(null);
        
        @SuppressWarnings("rawtypes")
        SwingWorker sw = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    progressDialog.start();
                    
                    BaseFrame.getInstance();
                    progressDialog.stop();
                } catch (Exception e) {
                    //Exception e1 = new Exception("Error while workspace creation.", e);
                    MyLogger.error(this, "Error while workspace creation.",e);
                    
                }
                return null;
            }
        };
        
        sw.execute();
        
		

	}

}
