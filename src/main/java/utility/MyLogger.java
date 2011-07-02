/**
 * 
 */
package utility;

import gui.InfoPanel;

import java.net.URL;

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
      URL logURL = MyLogger.class.getResource(LOG_FILENAME);
      PropertyConfigurator.configure(logURL);
    }
    return log;
  }

  /**
   * 
   * @param obj
   * @param message 
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
    String t = getFormatedMessage(source, text);
    getLogger().info(t);
    appendToMbcOutput(t);
  }

  /**
   * 
   * @param source
   * @param text
   */
  public static void error(Object source, String text) {
    String t = getFormatedMessage(source, text);
    getLogger().error(t);
    appendToMbcOutput(t);
  }

  /**
   * 
   * @param source
   * @param text
   * @param t 
   */
  public static void error(Object source, String text, Throwable trw) {
    String t = getFormatedMessage(source, text);

    getLogger().error(t, trw);
    appendToMbcOutput(t);

  }

  /**
   * 
   * @param source
   * @param t 
   */
  public static void error(Object source, Throwable trw) {
    String t = getFormatedMessage(source, trw.getMessage());
    getLogger().error(getFormatedMessage(source, trw.getMessage()), trw);
    appendToMbcOutput(t);
  }

  /**
   * appends text to the info panel
   * @param newTxt 
   */
  private static void appendToMbcOutput(String newTxt) {
    String oldtxt = InfoPanel.getMbcOutput().getText();
    InfoPanel.getMbcOutput().setText(oldtxt + "\n" + newTxt);

  }

  // EOF
}
