package gui.actions.menubar;

import gov.nasa.worldwind.Configuration;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import _workspace.shapes.ShapeLoaderFromFile;

/**
 * @author Viorel Florian
 *         <p>
 *         Defines the action for File->Save item
 */
public class FileLoadAct extends AbstractAction {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  private JFileChooser fileChooser;

  /**
   * Constructs a new instance.
   */
  public FileLoadAct() {
    super();
    setDefaultPropreties();
  }

  /**
   * Sets the default properties of the button
   */
  private void setDefaultPropreties() {
    putValue(Action.NAME, "Open...");
    KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
    putValue(Action.ACCELERATOR_KEY, key);

  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0) {
    if (this.fileChooser == null)
    {
        this.fileChooser = new JFileChooser();
        this.fileChooser.setCurrentDirectory(new File(Configuration.getCurrentWorkingDirectory()));
    }

    this.fileChooser.setDialogTitle("Choose Airspace File Directory");
    this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    this.fileChooser.setMultiSelectionEnabled(false);
    int status = this.fileChooser.showOpenDialog(null);
    if (status != JFileChooser.APPROVE_OPTION)
        return;

    final File dir = this.fileChooser.getSelectedFile();
    if (dir == null)
        return;
    
    
    File[] files = dir.listFiles(new FilenameFilter()
    {
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".mbc");
        }
    });
    
    for (File file : files) {
      ShapeLoaderFromFile.loadShape(file);
    }
  }

}// EOF
