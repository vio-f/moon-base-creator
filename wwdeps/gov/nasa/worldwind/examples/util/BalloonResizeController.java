package gov.nasa.worldwind.examples.util;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.AbstractResizeHotSpot;

import java.awt.*;

/**
 * Create a controller to resize a {@link Balloon} by dragging the mouse. This class should usually not be instantiated
 * directly. Instead, {@link BalloonController} will instantiate it when a balloon needs to be resized.
 *
 * @author pabercrombie
 * @version $Id: BalloonResizeController.java 14494 2011-01-18 20:39:13Z pabercrombie $
 *
 * @see BalloonController
 */
public class BalloonResizeController extends AbstractResizeHotSpot
{
    protected WorldWindow wwd;
    protected Rectangle bounds;
    protected Balloon balloon;

    protected static final Dimension DEFAULT_MIN_SIZE = new Dimension(50, 50);

    /**
     * Create a resize controller. This constructor does not register the controller as a listener.
     *
     * @param wwd     WorldWindow to interact with.
     * @param balloon Balloon to resize.
     * @param bounds  Bounds of the balloon in AWT coordinates.
     */
    public BalloonResizeController(WorldWindow wwd, Balloon balloon, Rectangle bounds)
    {
        this.wwd = wwd;
        this.balloon = balloon;
        this.bounds = bounds;
    }

    @Override
    public void selected(SelectEvent event)
    {
        // Update the World Window's cursor to the resize cursor.
        if (this.wwd instanceof Component)
        {
            ((Component) this.wwd).setCursor(this.getCursor());
        }
        super.selected(event);
    }

    /**
     * Is the controller currently resizing a balloon?
     *
     * @return True if the controller is currently resizing the balloon.
     */
    public boolean isResizing()
    {
        return this.isDragging();
    }

    /** {@inheritDoc} */
    protected Dimension getSize()
    {
        return bounds.getSize();
    }

    /** {@inheritDoc} */
    protected void setSize(Dimension newSize)
    {
        Size size = Size.fromPixels(newSize.width, newSize.height);

        BalloonAttributes attributes = this.balloon.getAttributes();

        // If the balloon is using default attributes, create a new set of attributes that we can customize
        if (attributes == null)
        {
            attributes = new BasicBalloonAttributes();
            this.balloon.setAttributes(attributes);
        }

        attributes.setSize(size);

        // If the balloon also has highlight attributes, change the highlight size as well.
        BalloonAttributes highlightAattributes = this.balloon.getHighlightAttributes();
        if (highlightAattributes != null)
            highlightAattributes.setSize(size);
    }

    /** {@inheritDoc} */
    protected Point getScreenPoint()
    {
        return this.bounds.getLocation();
    }

    /** {@inheritDoc} */
    protected void setScreenPoint(Point newPoint)
    {
        // Do not set the screen point. The balloon is attached to a particular screen point, and we do not want to
        // change it. When the balloon is resized, the attachment point should remain constant, and the balloon should
        // move.
    }

    /** {@inheritDoc} */
    @Override
    protected Dimension getMinimumSize()
    {
        return DEFAULT_MIN_SIZE;
    }
}
