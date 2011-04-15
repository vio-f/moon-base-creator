/*
 * Copyright (C) TBA BV
 * All rights reserved.
 * www.tba.nl
 */
package gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
public class MyAction extends AbstractAction {

  /**
   * Constructs a new instance.
   */
  public MyAction() {
    super();
    putValue(Action.NAME, "Aaaaa");
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    System.out.println("MyAction.actionPerformed()");
  }

}
