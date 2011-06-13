package gui;

import gui.actions.menubar.EditResizeAct;
import gui.actions.menubar.EditShowHideLayerTreeAct;
import gui.actions.menubar.FileExitAct;
import gui.actions.menubar.FileNewAct;

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
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class BaseFrame extends JFrame {

  private static BaseFrame INSTANCE = null;

  /*********** Mai jos sunt toate variabilele folosite ************************/
  /******************** Unele sunt initializate cu valori, altele nu **********/
  /**************************************************************************/
  /********** Astfel componentele sunt accesibile la nevoie, ******************/
  /****** in afara acestei clase **********************************************/

  public static JMenuBar menuBar = new JMenuBar();

  // meniul file
  public static JMenu fileMenu = new JMenu("File");

  // file menu items
  public static JMenuItem fileNewItem = new JMenuItem();

  public static JMenuItem fileOpenItem = new JMenuItem("Open...");

  public static JMenuItem fileSaveItem = new JMenuItem("Save...");

  public static JMenuItem fileExitItem = new JMenuItem("Exit...");

  // meniul Edit..
  JMenu editMenu = new JMenu("Edit");

  // edit menu items
  public static JMenuItem editShowHideLayerTreeItem = new JMenuItem();

  public static JMenuItem editMoveItem = new JMenuItem("MoveIt");

  public static JMenuItem editResize = new JMenuItem();

  // meniul Help
  JMenu helpMenu = new JMenu("Help");

  // help menu items
  public static JMenuItem helpHelpcontentsItem = new JMenuItem("Help contents");

  public static JMenuItem helpAboutItem = new JMenuItem("About");

  // TODO: Add more menu items

  public static JDesktopPane desktop = new JDesktopPane();

  // cream o intstanta al clasei de actiuni
  FileNewAct fileNewAction = new FileNewAct(this);

  Action fileExitAction = new FileExitAct();

  Action editShowHideAction = new EditShowHideLayerTreeAct();

  Action editResizeAction = new EditResizeAct();

  public static InfoPanel iPanel = new InfoPanel();

  public static ToolWindowsPanel tPanel = new ToolWindowsPanel();

  /**********************************************************************/
  /**********************************************************************/
  /**********************************************************************/

  // the almighty constructor
  private BaseFrame() {
    super("That's right!!! Its a \"Moon Base creator\""); // TODO think of a decent title
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(480, 320));
    this.setSize(getMaximumSize()); // TODO remove this latter
    JPopupMenu.setDefaultLightWeightPopupEnabled(false); // without this the canvas will drawn over
                                                         // the menu

    // seteaza frame LookandFeel dupa sistem
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      // TODO adauga rasp ptr exceptie
    }

    /**********************************************************************/
    // definim listener ptr obiecte
    fileNewItem.setAction(fileNewAction);
    fileExitItem.setAction(fileExitAction);

    editShowHideLayerTreeItem.setAction(editShowHideAction);
    editResize.setAction(editResizeAction);
    // TODO add more action listeners

    /**********************************************************************/

    menuBar.add(fileMenu);

    fileMenu.add(fileNewItem);
    fileMenu.addSeparator();// adaugam un separator
    fileMenu.add(fileOpenItem);
    fileMenu.add(fileSaveItem);
    fileMenu.addSeparator();// adaugam un separator
    fileMenu.add(fileExitItem);

    menuBar.add(editMenu);
    editShowHideLayerTreeItem.setEnabled(false);
    editMenu.add(editShowHideLayerTreeItem);
    editMenu.add(editMoveItem);
    editMenu.add(editResize);

    menuBar.add(helpMenu);
    helpMenu.add(helpHelpcontentsItem);
    helpMenu.add(helpAboutItem);

    // adaugam menuBar la frame
    this.setJMenuBar(menuBar);

    /**********************************************************************/
    desktop.setBackground(Color.LIGHT_GRAY);
    desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(desktop, BorderLayout.CENTER);
    this.getContentPane().add(new InternalPalleteToobar(), BorderLayout.WEST);// TODO optimize for
                                                                              // add/remove
    this.getContentPane().add(iPanel, BorderLayout.SOUTH);// TODO optimize for add/remove
    this.getContentPane().add(tPanel, BorderLayout.EAST);// TODO optimize for add/remove

    /**********************************************************************/
    // pornim
    this.setVisible(true);
    // pack();
  }
  
  /**********************************************************************/
  /**********************************************************************/
  /**********************************************************************/

  
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
}
