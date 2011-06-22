package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A JPanel which implements an accordion like system to manage shape properties 
 * 
 * @author viorel.florian
 */
@SuppressWarnings("serial")
public class JPropertiesPanel extends JPanel implements ActionListener {
  /** instance */
  public static JPropertiesPanel instance = new JPropertiesPanel();
  /** superLayout - the layout set for the whole JPanel*/
  private BorderLayout superLayout = new BorderLayout();
  /** northLayout - the layout set for the unopened components on the top side */
  private GridLayout northLayout = new GridLayout(5, 1);
  /** southLayout - the layout set for the unopened components on the bottom side */
  private GridLayout southLayout = new GridLayout(5, 1);
  /** topPanel */
  private JPanel topPanel = new JPanel();
  /** activePanel */
  private JPanel activePanel = new JPanel();
  /** bottomPanel */
  private JPanel bottomPanel = new JPanel();
  
  /** propertyBarList - dynamically managed list of property bars*/
  private ArrayList<JButton> propertyBarList = new ArrayList<JButton>(5);
  /** visibleBar */
  private String visibleBarName = null;
  /** <b>barToPanel </b>
   * <br><br>
   * <b>Key</b> - bar name
   * <br>
   * <b>Value</b> - the JButton instance itself
   **/
  private Map<String, JButton> barToPanel = new HashMap<String, JButton>();  

  
  
  /**
   * Constructs a new instance. And sets the layout of the three major zones.
   */
  public JPropertiesPanel() {
  super();
  this.setLayout(this.superLayout);
  this.setPreferredSize(new Dimension(300, 800));
  
  this.topPanel.setLayout(this.northLayout);
  this.bottomPanel.setLayout(this.southLayout);
  
  this.add(this.topPanel, BorderLayout.NORTH);
  this.add(this.bottomPanel, BorderLayout.SOUTH);
  
  addDummyContent();
  
  }

  
  
  
  
  /**
   * 
   *  It serves only for testing purposes 
   *
   */
  
  public void addDummyContent(){
    for (int i = 0; i < 4; i++) {
      JButton obj =  new JButton("Name " + i);
      getPropertyBarList().add(obj);
      getTopPanel().add(obj);
    }
  }
  
  /**
   * Set superLayout.
   * 
   * @param superLayout
   */
  public void setSuperLayout(BorderLayout superLayout) {
    this.superLayout = superLayout;
  }

  /**
   * Get superLayout.
   * 
   * @return superLayout
   */
  public BorderLayout getSuperLayout() {
    return this.superLayout;
  }

  /**
   * Set northLayout.
   * 
   * @param northLayout
   */
  public void setNorthLayout(GridLayout northLayout) {
    this.northLayout = northLayout;
  }

  /**
   * Get northLayout.
   * 
   * @return northLayout
   */
  public GridLayout getNorthLayout() {
    return this.northLayout;
  }

  /**
   * Set southLayout.
   * 
   * @param southLayout
   */
  public void setSouthLayout(GridLayout southLayout) {
    this.southLayout = southLayout;
  }

  /**
   * Get southLayout.
   * 
   * @return southLayout
   */
  public GridLayout getSouthLayout() {
    return this.southLayout;
  }

  /**
   * Set propertyList.
   * 
   * @param propertyList
   */
  public void setPropertyBarList(ArrayList<JButton> propertyList) {
    this.propertyBarList = propertyList;
  }

  /**
   * Get propertyList.
   * 
   * @return propertyList
   */
  public ArrayList<JButton> getPropertyBarList() {
    return this.propertyBarList;
  }






  /**
   * Set topPanel.
   * 
   * @param topPanel
   */
  public void setTopPanel(JPanel topPanel) {
    this.topPanel = topPanel;
  }






  /**
   * Get topPanel.
   * 
   * @return topPanel
   */
  public JPanel getTopPanel() {
    return this.topPanel;
  }






  /**
   * Set activePanel.
   * 
   * @param activePanel
   */
  public void setActivePanel(JPanel activePanel) {
    this.activePanel = activePanel;
  }






  /**
   * Get activePanel.
   * 
   * @return activePanel
   */
  public JPanel getActivePanel() {
    return this.activePanel;
  }






  /**
   * Set bottomPanel.
   * 
   * @param bottomPanel
   */
  public void setBottomPanel(JPanel bottomPanel) {
    this.bottomPanel = bottomPanel;
  }






  /**
   * Get bottomPanel.
   * 
   * @return bottomPanel
   */
  public JPanel getBottomPanel() {
    return this.bottomPanel;
  }





  /**
   * Set visibleBar.
   * 
   * @param visibleBar
   */
  public void setVisibleBarName(String visibleBar) {
    this.visibleBarName = visibleBar;
  }





  /**
   * Get visibleBar.
   * 
   * @return visibleBar
   */
  public String getVisibleBarName() {
    return this.visibleBarName;
  }





  /**
   * Set barToPanel.
   * 
   * @param barToPanel
   */
  public void setBarToPanel(Map<String, JButton> barToPanel) {
    this.barToPanel = barToPanel;
  }





  /**
   * Get barToPanel.
   * 
   * @return barToPanel
   */
  public Map<String, JButton> getBarToPanel() {
    return this.barToPanel;
  }

  
  
  /**
   * TODO DESCRIPTION
   */
  private void addBar(String name, JButton panel) {
    getBarToPanel().put(name, panel);
  }




  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
  }

}
