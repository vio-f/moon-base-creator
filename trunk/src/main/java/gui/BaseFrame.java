package gui;

import gui.actions.menubar.EditGoToAct;
import gui.actions.menubar.EditHeadingAct;
import gui.actions.menubar.EditResizeAct;
import gui.actions.menubar.EditRollAct;
import gui.actions.menubar.EditTiltAct;
import gui.actions.menubar.FileExitAct;
import gui.actions.menubar.FileLoadAct;
import gui.actions.menubar.FileNewAct;
import gui.actions.menubar.FileSaveAct;
import gui.jaccordian.JAccordionMenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import utility.MyLogger;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
public class BaseFrame extends JFrame {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /** INSTANCE */
  private static BaseFrame INSTANCE;

  /*********** Mai jos sunt toate variabilele folosite ************************/
  /******************** Unele sunt initializate cu valori, altele nu **********/
  /**************************************************************************/
  /********** Astfel componentele sunt accesibile la nevoie, ******************/
  /****** in afara acestei clase **********************************************/

  public JMenuBar menuBar = new JMenuBar();

  /** fileMenu */
  // meniul file
  public JMenu fileMenu = new JMenu("File");

  /** fileNewItem */
  // file menu items
  private JMenuItem fileNewItem = new JMenuItem();

  /** fileLoadItem */
  private JMenuItem fileLoadItem = new JMenuItem();

  /** fileSaveItem */
  private JMenuItem fileSaveItem = new JMenuItem();

  /** fileExitItem */
  private JMenuItem fileExitItem = new JMenuItem();

  /** editMenu */
  private JMenu editMenu = new JMenu("Edit");

  /** editResizeItem */
  private JMenuItem editResizeItem = new JMenuItem();

  /** editTiltItem */
  private JMenuItem editTiltItem = new JMenuItem();

  /** editTiltItem */
  private JMenuItem editRollItem = new JMenuItem();
  private JMenuItem editHeadingItem = new JMenuItem();
  /** editGoToItem */
  private JMenuItem editGoToItem = new JMenuItem();

  // meniul Help
  /** helpMenu */
  private JMenu helpMenu = new JMenu("Help");

  // help menu items
  /** helpHelpcontentsItem */
  private JMenuItem helpHelpcontentsItem = new JMenuItem("Help contents");

  /** helpAboutItem */
  private JMenuItem helpAboutItem = new JMenuItem("About");

  // TODO: Add more menu items

  /** desktop */
  private JDesktopPane desktop = new JDesktopPane();

  /**
   * ACTIONS instances
   * 
   * 
   */
  /** fileNewAction */
  FileNewAct fileNewAction = new FileNewAct(this);

  /** fileLoadAction */
  FileLoadAct fileLoadAction = new FileLoadAct(this);

  /** fileSaveAction */
  FileSaveAct fileSaveAction = new FileSaveAct(this);

  /** fileExitAction */
  Action fileExitAction = new FileExitAct();

  /** editResizeAction */
  Action editResizeAction = new EditResizeAct();

  /** editTiltAction */
  Action editTiltAction = new EditTiltAct();

  /** editTiltAction */
  Action editRollAction = new EditRollAct();
  /** editHeadingAction */
  Action editHeadingAction = new EditHeadingAct();

  /** editGoToAction */
  Action editGoToAction = new EditGoToAct();

  /** infoPanel */
  private InfoPanel infoPanel = new InfoPanel();

  // TODO remove this if other is finished >>>>> public ToolWindowsPanel propertiesPanel =
  // new ToolWindowsPanel();
  /** propertiesPanel */
  private JAccordionMenu propertiesPanel = new JAccordionMenu();

  /**
   * 
   * Constructs a new instance.
   */
  private BaseFrame() {
    super("Moon Base creator - the future is ours");
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(480, 320));
    this.setSize(this.getMaximumSize()); // TODO remove this latter
    JPopupMenu.setDefaultLightWeightPopupEnabled(false); // without this the canvas will drawn over
                                                         // the menu
    /*
     * sets LOOk and Feel after the OS default
     */
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      MyLogger.error(BaseFrame.getInstance(), "OS look and feel could not be set", e);
    }

    /**
     * Set menu item action listeners
     */
    this.getFileNewItem().setAction(this.fileNewAction);
    this.fileSaveItem.setAction(this.fileSaveAction);
    this.getFileExitItem().setAction(this.fileExitAction);
    this.fileLoadItem.setAction(this.fileLoadAction);

    this.editResizeItem.setAction(this.editResizeAction);
    this.editTiltItem.setAction(this.editTiltAction);
    this.editRollItem.setAction(this.editRollAction);
    this.editHeadingItem.setAction(this.editHeadingAction);
    this.editGoToItem.setAction(this.editGoToAction);
    // TODO add more action listeners

    /**********************************************************************/

    this.menuBar.add(this.fileMenu);

    this.fileMenu.add(this.getFileNewItem());
    this.fileMenu.addSeparator();// adaugam un separator
    this.fileMenu.add(this.fileLoadItem);
    this.fileMenu.add(this.fileSaveItem);
    this.fileMenu.addSeparator();// adaugam un separator
    this.fileMenu.add(this.fileExitItem);

    this.menuBar.add(this.editMenu);
    this.editMenu.add(this.editResizeItem);
    this.editMenu.add(this.editTiltItem);
    this.editMenu.add(this.editRollItem);
    this.editMenu.add(this.editHeadingAction);
    this.fileMenu.addSeparator();// adaugam un separator
    this.editMenu.add(this.editGoToItem);

    this.menuBar.add(this.helpMenu);
    this.helpMenu.add(this.helpHelpcontentsItem);
    this.helpMenu.add(this.helpAboutItem);

    // adaugam menuBar la frame
    this.setJMenuBar(this.menuBar);

    /**********************************************************************/
    this.getDesktop().setBackground(Color.LIGHT_GRAY);
    this.getDesktop().setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(this.getDesktop(), BorderLayout.CENTER);
    this.getContentPane().add(new InternalPalleteToobar(), BorderLayout.WEST);// TODO optimize for
                                                                              // add/remove
    this.getContentPane().add(this.infoPanel, BorderLayout.SOUTH);// TODO optimize for add/remove
    this.getContentPane().add(this.getPropertiesPanel(), BorderLayout.EAST);// TODO optimize for
    // add/remove

    /**********************************************************************/
    // pornim
    this.setVisible(true);
    // pack();
  }

  /**********************************************************************/
  /**********************************************************************/
  /**********************************************************************/

  /**
   * Set desktop.
   * 
   * @param desktop
   */
  public void setDesktop(JDesktopPane desktop) {
    this.desktop = desktop;
  }

  /**
   * Get desktop.
   * 
   * @return desktop
   */
  public JDesktopPane getDesktop() {
    return desktop;
  }

  /**
   * Set fileNewItem.
   * 
   * @param fileNewItem
   */
  public void setFileNewItem(JMenuItem fileNewItem) {
    this.fileNewItem = fileNewItem;
  }

  /**
   * Get fileNewItem.
   * 
   * @return fileNewItem
   */
  public JMenuItem getFileNewItem() {
    return fileNewItem;
  }

  /**
   * Get instance.
   * 
   * @return instance
   */
  public static BaseFrame getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new BaseFrame();
    }

    return INSTANCE;

  }

  // EOF

  /**
   * Set fileExitItem.
   * 
   * @param fileExitItem
   */
  public void setFileExitItem(JMenuItem fileExitItem) {
    this.fileExitItem = fileExitItem;
  }

  /**
   * Get fileExitItem.
   * 
   * @return fileExitItem
   */
  public JMenuItem getFileExitItem() {
    return this.fileExitItem;
  }

  /**
   * Set propertiesPanel.
   * 
   * @param propertiesPanel
   */
  public void setPropertiesPanel(JAccordionMenu propertiesPanel) {
    this.propertiesPanel = propertiesPanel;
  }

  /**
   * Get propertiesPanel.
   * 
   * @return propertiesPanel
   */
  public JAccordionMenu getPropertiesPanel() {
    return propertiesPanel;
  }
}
