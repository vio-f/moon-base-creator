package gui.actions.menubar;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Restorable;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.render.airspaces.AirspaceAttributes;
import gui.BaseFrame;
import gui.ProgressDialog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import _workspace.MoonWorkspaceFactory;
import _workspace.shapes.DomeShape;
import _workspace.shapes.IShape;
import _workspace.shapes.ShapesPool;

import utility.MyLogger;

/**
 * @author Viorel Florian
 *         <p>
 *         Defines the action for File->Save item
 */
public class FileSaveAct extends AbstractAction {
  private JFrame f;

  private JFileChooser fileChooser;

  /**
   * Constructs a new instance.
   * 
   * @param Parent JFrame
   */
  public FileSaveAct(JFrame f) {
    super();
    this.f = f;
    setDefaultPropreties();
  }

  /**
   * Sets the default properties of the button
   */
  private void setDefaultPropreties() {
    putValue(Action.NAME, "Save...");
    KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
    putValue(Action.ACCELERATOR_KEY, key);

  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0) {
    if (this.fileChooser == null) {
      this.fileChooser = new JFileChooser();
      this.fileChooser.setCurrentDirectory(new File(Configuration.getCurrentWorkingDirectory()));
    }

    this.fileChooser.setDialogTitle("Choose Directory to Place components");
    this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    this.fileChooser.setMultiSelectionEnabled(false);
    int status = this.fileChooser.showSaveDialog(null);
    if (status != JFileChooser.APPROVE_OPTION)
      return;

    final File dir = this.fileChooser.getSelectedFile();
    if (dir == null)
      return;

    if (!dir.exists()) {
      // noinspection ResultOfMethodCallIgnored
      dir.mkdirs();
    }

    Set<Entry<String, IShape>> allShapes = ShapesPool.getInstance().getAllShapesEntries();

    for (Entry<String, IShape> entry : allShapes) {
      DomeShape dome = (DomeShape) entry.getValue();
      
      File newFile = new File(dir.getAbsolutePath(), dome.getIdentifier() + "." + "mbc");
      dome.saveMe(newFile, dome.getIdentifier());
      
    }
      
  }

}// EOF
