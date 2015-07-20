/*
 * Copyright (C) TBA BV
 * All rights reserved.
 * www.tba.nl
 */
package gui.jaccordian.jAccordionPanels;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Position;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import _workspace.MoonWorkspaceFactory;

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
public class GoToPositionPanel extends JPanel {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /** INSTANCE */
  private static GoToPositionPanel INSTANCE = null;

  /** latitude */
  private double latitude;

  /** longitude */
  private double longitude;

  /** elevation */
  private double elevation;

  /** latBox */
  private JFormattedTextField latBox = new JFormattedTextField(0);

  /** longBox */
  private JFormattedTextField longBox = new JFormattedTextField(0);

  /** elevation */
  private JFormattedTextField elevationBox = new JFormattedTextField(1000);
  
  
  private JButton goButt = new JButton("GO...");
  /**
   * Constructs a new instance.
   */
  public GoToPositionPanel() {
    super();
    this.getLatBox().setColumns(6);
    this.getLongBox().setColumns(6);
    this.getElevationBox().setColumns(6);
    
    getGoButt().addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        setLatitude((Integer)getLatBox().getValue() + 0.0);
        setLongitude((Integer) getLongBox().getValue() + 0.0);
        setElevation((Integer) getElevationBox().getValue() + 0.0);
        goTo(Position.fromDegrees(getLatitude(), getLongitude(), getElevation()));
      }
    });
    
    
    
    
    this.setLayout(new FlowLayout());
    this.add(getLatBox());
    this.add(getLongBox());
    this.add(getElevationBox());
    this.add(getGoButt());

  }

  /**
   * TODO DESCRIPTION
   * 
   * @return
   */
  public static GoToPositionPanel getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new GoToPositionPanel();
    }
    return INSTANCE;
  }

  /**
   * TODO DESCRIPTION
   * 
   * @param pos
   */
  public void goTo(Position pos) {
    View v = MoonWorkspaceFactory.getInstance().getLastSelectedIntFr().getWwGLCanvas().getView();
    // v.setEyePosition(pos);
    v.goTo(pos, 6000e3);
    MoonWorkspaceFactory.getInstance().getLastSelectedIntFr().getWwGLCanvas().setView(v);
    MoonWorkspaceFactory.getInstance().getLastSelectedIntFr().getWwGLCanvas().redraw();

  }

  /**
   * Set latBox.
   * 
   * @param latBox
   */
  public void setLatBox(JFormattedTextField latBox) {
    this.latBox = latBox;
  }

  /**
   * Get latBox.
   * 
   * @return latBox
   */
  public JFormattedTextField getLatBox() {
    return this.latBox;
  }

  /**
   * Set longBox.
   * 
   * @param longBox
   */
  public void setLongBox(JFormattedTextField longBox) {
    this.longBox = longBox;
  }

  /**
   * Get longBox.
   * 
   * @return longBox
   */
  public JFormattedTextField getLongBox() {
    return this.longBox;
  }

  /**
   * Set elevationBox.
   * 
   * @param elevationBox
   */
  public void setElevationBox(JFormattedTextField elevationBox) {
    this.elevationBox = elevationBox;
  }

  /**
   * Get elevationBox.
   * 
   * @return elevationBox
   */
  public JFormattedTextField getElevationBox() {
    return this.elevationBox;
  }

  /**
   * Set latitude.
   * 
   * @param latitude
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  /**
   * Get latitude.
   * 
   * @return latitude
   */
  public double getLatitude() {
    return this.latitude;
  }

  /**
   * Set longitude.
   * 
   * @param longitude
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  /**
   * Get longitude.
   * 
   * @return longitude
   */
  public double getLongitude() {
    return this.longitude;
  }

  /**
   * Set elevation.
   * 
   * @param elevation
   */
  public void setElevation(double elevation) {
    this.elevation = elevation;
  }

  /**
   * Get elevation.
   * 
   * @return elevation
   */
  public double getElevation() {
    return this.elevation;
  }

  /**
   * Set goButt.
   * 
   * @param goButt
   */
  public void setGoButt(JButton goButt) {
    this.goButt = goButt;
  }

  /**
   * Get goButt.
   * 
   * @return goButt
   */
  public JButton getGoButt() {
    return goButt;
  }

}// EOF
