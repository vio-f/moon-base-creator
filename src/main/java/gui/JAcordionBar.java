/*
 * Copyright (C) TBA BV
 * All rights reserved.
 * www.tba.nl
 */
package gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
@SuppressWarnings("serial")
public class JAcordionBar extends JButton {
  /** identifier and text displayed on the bar */
  private String name;
  /** asocPanel - associated JPanel to be displayed when this bar is clicked*/
  private JPanel asocPanel = null;
  
  
  


  /**
   * Constructs a new instance.
   * @param name 
   */
  public JAcordionBar(String name) {
    super (name);
    this.setName(name);
    this.name = name;
  }
  /**
   * Constructs a new instance.
   * @param name 
   * @param jp 
   */
  public JAcordionBar(String name, JPanel jp) {
    super (name);
    this.name = name;
    this.setName(name);
    this.asocPanel = jp;
    
  }
  
  
  /**
   * Set asocPanel.
   * 
   * @param asocPanel
   */
  public void setAsocPanel(JPanel asocPanel) {
    this.asocPanel = asocPanel;
  }

  /**
   * Get asocPanel.
   * 
   * @return asocPanel
   */
  public JPanel getAsocPanel() {
    return asocPanel;
  }

}
