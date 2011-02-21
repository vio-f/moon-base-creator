package gui;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;


public class StartMe {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("App started");
		//setarile necesare ptr vizualizarea Lunii
		
		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, gov.nasa.worldwind.globes.Moon.class.getName());// seteaza tipul astrului care urmeaza a fi vizualiza
        Configuration.setValue(AVKey.MOON_ELEVATION_MODEL_CONFIG_FILE, "config/Moon/MoonElevationModel.xml");//seteaza  ce fisier de elevatie (inaltimi) sa se foloseasca
        Configuration.setValue(AVKey.LAYERS_CLASS_NAMES, BaseFrame.LAYERS);// LAYERS a fost definit mai sus
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 600e3);  // 6000km
        System.out.println("Settings made");
		new BaseFrame();

	}

}
