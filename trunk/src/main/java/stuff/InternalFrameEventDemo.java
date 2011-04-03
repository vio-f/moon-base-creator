package stuff;

/*
 * 1.1+Swing code.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class InternalFrameEventDemo 
                     extends JFrame
                     implements InternalFrameListener,
                                ActionListener {
    JTextArea display;
    JDesktopPane desktop;
    JInternalFrame displayWindow;
    JInternalFrame listenedToWindow;
    static final String SHOW = "show";
    static final String CLEAR = "clear";
    String newline = "\n";
    static final int desktopWidth = 500;
    static final int desktopHeight = 300;

    public InternalFrameEventDemo() {
        super("InternalFrameEventDemo");

        //Quit this app when the big window closes.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Set up the GUI.
        desktop = new JDesktopPane();
        //XXX: It's NOT enough to call setSize.  Must set the preferred size.
        //XXX: (Common Problem?)
        desktop.setPreferredSize(new Dimension(desktopWidth, desktopHeight));
        setContentPane(desktop);

        createDisplayWindow();
        desktop.add(displayWindow); //DON'T FORGET THIS!!!
        Dimension displaySize = displayWindow.getSize();
        displayWindow.setSize(desktopWidth, displaySize.height);

        //The following probably would save significant time if we reused 
        //the internal frame.  We can't reuse it, due to bug #4128975.
        //createListenedToWindow();
    }

    //Create the window that displays event information.
    protected void createDisplayWindow() {
        JButton b1 = new JButton("Show internal frame");
        b1.setActionCommand(SHOW);
        b1.addActionListener(this);

        JButton b2 = new JButton("Clear event info");
        b2.setActionCommand(CLEAR);
        b2.addActionListener(this);

        display = new JTextArea(5, 40);
        display.setEditable(false);
        JScrollPane textScroller = new JScrollPane(display);

        displayWindow = new JInternalFrame("Event Watcher",
                                           true,  //resizable
                                           false, //not closable
                                           false, //not maximizable
                                           true); //iconifiable
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        contentPane.setLayout(new BoxLayout(contentPane,
                                            BoxLayout.Y_AXIS));
        b1.setAlignmentX(CENTER_ALIGNMENT);
        contentPane.add(b1);
        contentPane.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPane.add(textScroller);
        contentPane.add(Box.createRigidArea(new Dimension(0, 5)));
        b2.setAlignmentX(CENTER_ALIGNMENT);
        contentPane.add(b2);

        displayWindow.setContentPane(contentPane);
        displayWindow.pack();
    }

    //Create the listened-to window.
    protected void createListenedToWindow() {
        listenedToWindow = new JInternalFrame("Event Generator",
                                              true,  //resizable
                                              true,  //closable
                                              true,  //maximizable
                                              true); //iconifiable
        //The next statement is necessary to work around bug 4128975.
        listenedToWindow.setDefaultCloseOperation(
                                WindowConstants.DISPOSE_ON_CLOSE);
        listenedToWindow.setSize(300, 100);
    }

    public void internalFrameClosing(InternalFrameEvent e) {
        displayMessage("Internal frame closing", e);
    }

    public void internalFrameClosed(InternalFrameEvent e) {
        displayMessage("Internal frame closed", e);
        listenedToWindow = null;
    }

    public void internalFrameOpened(InternalFrameEvent e) {
        displayMessage("Internal frame opened", e);
        //XXX: Why do we get one of these when we dispose of a window?
        //XXX: And not when you first show the window?
    }

    public void internalFrameIconified(InternalFrameEvent e) {
        displayMessage("Internal frame iconified", e);
    }

    public void internalFrameDeiconified(InternalFrameEvent e) {
        displayMessage("Internal frame deiconified", e);
    }

    public void internalFrameActivated(InternalFrameEvent e) {
        displayMessage("Internal frame activated", e);
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {
        displayMessage("Internal frame deactivated", e);
    }

    void displayMessage(String prefix, InternalFrameEvent e) {
        String s = prefix + ": " + e.getSource(); 
        display.append(s + newline);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(SHOW)) {
            //Can't show/setVisible, due to bug 4128975.
            //listenedToWindow.setVisible(true);

            //Instead, create a new internal frame.
            if (listenedToWindow == null) {
                createListenedToWindow();
                listenedToWindow.addInternalFrameListener(this);
                desktop.add(listenedToWindow);
                Dimension size = listenedToWindow.getSize();
                listenedToWindow.setLocation(desktopWidth/2 - size.width/2,
                                             desktopHeight - size.height);
            }
        } else {
            display.setText("");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new InternalFrameEventDemo();

        frame.pack();
        frame.setVisible(true);
    }
}
