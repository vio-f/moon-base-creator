/**
 * 
 */
package _workspace.shapes;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.AbstractShape;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.Ellipsoid;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.util.BasicDragger;
import _workspace.MoonWorkspaceFactory;
import _workspace.MoonWorkspaceInternalFrame;

/**
 * TODO DESCRIPTION
 * 
 * @author viorel.florian
 */
public class ShapeListener {
  /** wwd */
  private WorldWindowGLCanvas wwd;

  // protected IShape aglAirspaces;

  // protected IShape lastHighlit;
  // protected IShape lastToolTip;
  // protected ShapeAttributes lastAttrs;
  // protected Annotation lastAnnotation;
  /** dragger */
  protected BasicDragger dragger = null;

  /** currentWorkspace */
  MoonWorkspaceInternalFrame currentWorkspace = null;

  /** lastSelectedObj */
  public static Object lastSelectedObj = null;

  /**
   * Constructs a new instance.
   * 
   * @param mwif
   */
  public ShapeListener(MoonWorkspaceInternalFrame mwif) {
    this.currentWorkspace = mwif;
    this.wwd = mwif.getWwGLCanvas();
    this.dragger = new BasicDragger(this.wwd);
    this.initializeSelectionMonitoring();
  }

  /**
   * TODO DESCRIPTION
   */
  public void initializeSelectionMonitoring() {

    this.wwd.addSelectListener(new SelectListener() {
      public void selected(SelectEvent event) {
        if (event.getTopObject() instanceof Ellipsoid) {
          // TODO works only on mouse over
          lastSelectedObj = event.getTopObject();

          // Have rollover events highlight the rolled-over object.
          if (event.getEventAction().equals(SelectEvent.ROLLOVER)
              && !ShapeListener.this.dragger.isDragging()) {
            // if (highlight(event.getTopObject()))
            ShapeListener.this.wwd.redraw();
          }
          // Have hover events popup an annotation about the hovered-over object.
          else if (event.getEventAction().equals(SelectEvent.HOVER)
              && !ShapeListener.this.dragger.isDragging()) {
            // if (showToolTip(event.getTopObject(), event))

            Object obj = event.getTopObject();
            Annotation ann = new GlobeAnnotation(obj.getClass().getName(),
                ((Ellipsoid) obj).getCenterPosition());
            currentWorkspace.getAnnotationLayer().addAnnotation(ann);

            ShapeListener.this.wwd.redraw();
          }

          // Have drag events drag the selected object.
          else if (event.getEventAction().equals(SelectEvent.DRAG_END)
              || event.getEventAction().equals(SelectEvent.DRAG)) {
            // Delegate dragging computations to a dragger.
            ShapeListener.this.dragger.selected(event);

            // We missed any roll-over events while dragging, so highlight any under the cursor now,
            // or de-highlight the dragged shape if it's no longer under the cursor.
            if (event.getEventAction().equals(SelectEvent.DRAG_END)) {
              PickedObjectList pol = ShapeListener.this.wwd.getObjectsAtCurrentPosition();
              if (pol != null) {
                // highlight(pol.getTopObject());
                ShapeListener.this.wwd.repaint();
              }

            }
          }
        }
      }
    });
  }

  /*
   * protected boolean highlight(Object o) { if (this.lastHighlit == o) return false; // Same thing
   * selected
   * 
   * // Turn off highlight if on. if (this.lastHighlit != null) {
   * this.lastHighlit.setAttributes(this.lastAttrs); this.lastHighlit = null; this.lastAttrs = null;
   * }
   * 
   * // Turn on highlight if selected object is a SurfaceImage. if (o instanceof IShape) {
   * this.lastHighlit = (IShape) o; this.lastAttrs = this.lastHighlit.getAttributes();
   * ShapeAttributes highlitAttrs = new BasicShapeAttributes(); highlitAttrs = this.lastAttrs;
   * highlitAttrs.setInteriorMaterial(Material.WHITE); this.lastHighlit.setAttributes(highlitAttrs);
   * }
   * 
   * return true; }
   * 
   * protected boolean showToolTip(Object o, SelectEvent e) { if (this.lastToolTip == o) return
   * false; // Same thing selected
   * 
   * if (this.lastToolTip != null && (e.getTopObject() == null ||
   * !e.getTopObject().equals(this.lastToolTip))) {
   * currentWorkspace.annotationLayer.removeAnnotation(this.lastAnnotation); this.lastToolTip =
   * null; }
   * 
   * if (this.lastToolTip == null) { this.lastToolTip = (IShape) o; this.lastAnnotation =
   * this.createToolTip((IShape) o, e);
   * currentWorkspace.annotationLayer.addAnnotation(this.lastAnnotation); }
   * 
   * return true; }
   * 
   * 
   * 
   * protected Annotation createToolTip(IShape a, SelectEvent e) { Object o = a.getIdentifier(); if
   * (o == null) o = a.getClass().getName();
   * 
   * java.awt.Point point = e.getPickPoint();
   * 
   * Annotation annotation;
   * 
   * Position pos = wwd.getView().computePositionFromScreenPoint(point.x, point.y); if (pos != null)
   * { //double[] altitudes = a.getAltitudes(); pos = new Position(pos.getLatitude(),
   * pos.getLongitude(), 0); annotation = new GlobeAnnotation(a.getIdentifier(), pos); } else {
   * annotation = new ScreenAnnotation(a.getIdentifier(), point); }
   * 
   * annotation.setAlwaysOnTop(true); annotation.setPickEnabled(false);
   * 
   * return annotation; }
   */

  // end
}
