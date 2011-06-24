package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;

import utility.MyLogger;

/**
 * A JPanel which implements an accordion like system to manage shape properties
 * 
 * @author viorel.florian
 */
@SuppressWarnings("serial")
public class JAccordionMenu extends JPanel implements ActionListener {
  /** instance */
  public static JAccordionMenu instance = new JAccordionMenu();

  /** superLayout - the layout set for the whole JPanel */
  private BorderLayout superLayout = new BorderLayout();

  /** northLayout - the layout set for the unopened components on the top side */
  private GridLayout northLayout = new GridLayout(5, 1);

  /** centerLayout - the layout set for the unopened components on the top side */
  private GridLayout centerLayout = new GridLayout(5, 1);

  /** southLayout - the layout set for the unopened components on the bottom side */
  private GridLayout southLayout = new GridLayout(5, 1);

  /** topPanel */
  private JPanel topPanel = new JPanel();

  /** centerPanel */
  private JPanel centerPanel = new JPanel();

  /** bottomPanel */
  private JPanel bottomPanel = new JPanel();

  /** visibleBar */
  private String visibleBarName = null;

  /**
   * <b>allBars </b> <br>
   * <br>
   * <b>Key</b> - bar name <br>
   * <b>Value</b> - the JPanel to be displayed
   **/
  private Map<String, JAcordionBar> allBars = new LinkedHashMap<String, JAcordionBar>();

  /** topList List of names added to the topPanel */
  private List<String> topList = new LinkedList<String>();

  /** topList List of names added to the bottomPanel */
  private List<String> bottomList = new LinkedList<String>();

  /**
   * Constructs a new instance. And sets the layout of the three major zones.
   */
  public JAccordionMenu() {
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
   * It serves only for testing purposes
   * 
   */

  public void addDummyContent() {
    for (int i = 0; i < 4; i++) {
      String barName = "name " + i;
      JAcordionBar jac = new JAcordionBar(barName);
      jac.setAsocPanel(getDummyPanel(barName));
      jac.addActionListener(this);
      addBar(barName, jac);

    }
  }

  /**
   * Meant only for debugging
   * 
   * @param name
   * @return
   */
  public static JPanel getDummyPanel(String name) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new JLabel(name, JLabel.CENTER));
    return panel;
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
   * Set centerPanel.
   * 
   * @param centerPanel
   */
  public void setCenterPanel(JPanel centerPanel) {
    this.centerPanel = centerPanel;
  }

  /**
   * Get centerPanel.
   * 
   * @return centerPanel
   */
  public JPanel getCenterPanel() {
    return centerPanel;
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
   * Set allBars.
   * 
   * @param allBars
   */
  public void setAllBars(Map<String, JAcordionBar> allBars) {
    this.allBars = allBars;
  }

  /**
   * Get allBars.
   * 
   * @return allBars
   */
  public Map<String, JAcordionBar> getAllBars() {
    return allBars;
  }

  /**
   * Set topList.
   * 
   * @param topList
   */
  public void setTopList(ArrayList<String> topList) {
    this.topList = topList;
  }

  /**
   * Get topList.
   * 
   * @return topList
   */
  public List<String> getTopList() {
    return topList;
  }

  /**
   * Set bottomList.
   * 
   * @param bottomList
   */
  public void setBottomList(ArrayList<String> bottomList) {
    this.bottomList = bottomList;
  }

  /**
   * Get bottomList.
   * 
   * @return bottomList
   */
  public List<String> getBottomList() {
    return bottomList;
  }

  /**
   * Used to add a bar to the display
   * 
   * @param name Bar identifier
   * @param panel
   */
  public void addBar(String name, JAcordionBar bar) {
    getAllBars().put(name, bar);
    revertAll();
  }

  /**
   * TODO Removes a bar from the Properties Panel
   * 
   * @param name
   */
  public void removeBar(String name) {
    getAllBars().remove(name);
    revertAll();

  }

  /**
   * TODO DESCRIPTION
   * 
   * @param name
   */
  private void sortAll(String name) {
    getTopList().clear();
    getBottomList().clear();
    getTopPanel().removeAll();
    getBottomPanel().removeAll();

    boolean placeTop = true;

    for (Entry<String, JAcordionBar> entry : this.allBars.entrySet()) {
      if (entry.getKey().equals(name)) {
        // index = entry.;
        placeTop = false;
        getTopList().add(entry.getKey());
      } else {
        if (placeTop) {
          getTopList().add(entry.getKey());
        } else {
          getBottomList().add(entry.getKey());
        }
      }// end of first else

      refreshTop();
      refreshBottom();

      this.validate();

    }

  }

  /**
   * Re-initializes everything
   */
  private void revertAll() {
    // remove everything
    getTopList().clear();
    getBottomList().clear();
    getTopPanel().removeAll();
    getBottomPanel().removeAll();

    // add everything
    for (Entry<String, JAcordionBar> entry : this.allBars.entrySet()) {
      System.out.println(entry.getKey());
      getTopList().add(entry.getKey());
      getTopPanel().add(entry.getValue());
    }

    this.validate();

  }

  /**
   * TODO DESCRIPTION
   */
  private void refreshTop() {
    getNorthLayout().setRows(getTopList().size());
    for (String x : getTopList()) {
      getTopPanel().add(this.allBars.get(x));
    }
    getTopPanel().repaint();
  }

  /**
   * TODO DESCRIPTION
   */
  private void refreshBottom() {
    if (getBottomList().size() == 0) {
      getSouthLayout().setRows(1);
    }
    getSouthLayout().setRows(getBottomList().size());
    for (String x : getBottomList()) {
      getBottomPanel().add(this.allBars.get(x));
    }
    getBottomPanel().repaint();
  }

  /**
   * 
   * @param name The name associated to a component
   * @return The current parent of this component
   */
  public JPanel getPosition(String name) {

    Component comp = null;
    if (comp.getParent().equals(getTopPanel())) {
      return getTopPanel();
    }
    if (comp.getParent().equals(getBottomPanel())) {
      return getBottomPanel();
    }
    return null;
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {

    if (e.getSource() instanceof JAcordionBar) {
      JAcordionBar jac = (JAcordionBar) e.getSource();
      String jacName = jac.getName();
      MyLogger.info(this, "Source of the event is " + jacName);
      sortAll(jacName);

    }

  }

}// EOF
