/**
 * 
 */
package utility;

import java.net.URL;

import javax.sound.midi.MidiDevice.Info;

import org.apache.log4j.PropertyConfigurator;

/**
 * @author Administrator
 * 
 */
public class MyLogger {

	private static final String LOG_FILENAME = "log.properties";

	/** */
	private static org.apache.log4j.Logger log;

	/**
	 * 
	 * @return
	 */
	public static org.apache.log4j.Logger getLogger() {

		if (log == null) {
			log = org.apache.log4j.Logger.getLogger(MyLogger.class);
			URL logURL = MyLogger.class.getResource("log.properties");
			PropertyConfigurator.configure(logURL);
		}
		return log;
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	private static String getFormatedMessage(Object obj, Object message) {
		return " [" + obj.getClass().getSimpleName().trim() + "] - " + message;
	}

	/**
	 * 
	 * @param source
	 * @param text
	 */
	public static void info(Object source, String text) {
		getLogger().info(getFormatedMessage(source, text));
	}
	
	/**
	 * 
	 * @param source
	 * @param text
	 */
	public static void error(Object source, String text) {
		getLogger().error(getFormatedMessage(source, text));
	}
	
	/**
	 * 
	 * @param source
	 * @param text
	 */
	public static void error(Object source, String text, Throwable t) {
		getLogger().error(getFormatedMessage(source, text), t);
	}
	
	
}
