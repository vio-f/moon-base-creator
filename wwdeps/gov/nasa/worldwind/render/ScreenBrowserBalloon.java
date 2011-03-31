/*
 * Copyright (C) 2001, 2011 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.geom.*;

/**
 * A BrowserBalloon attached to a point on the screen.
 *
 * @author pabercrombie
 * @version $Id: ScreenBrowserBalloon.java 14555 2011-01-24 23:17:03Z dcollins $
 */
public class ScreenBrowserBalloon extends AbstractBrowserBalloon implements ScreenBalloon
{
    protected Point screenLocation;

    /**
     * Create the balloon.
     *
     * @param text  Text to display in the balloon.
     * @param point The balloon's initial position, in AWT coordinates (origin at upper left corner of the window).
     */
    public ScreenBrowserBalloon(String text, Point point)
    {
        super(text);

        if (point == null)
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.screenLocation = point;
    }

    /** {@inheritDoc} */
    public Point getScreenLocation()
    {
        return this.screenLocation;
    }

    /** {@inheritDoc} */
    public void setScreenLocation(Point point)
    {
        if (point == null)
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.screenLocation = point;
    }

    /**
     * Computes and stores the balloon's screen-coordinate content frame in {@link #screenRect}, the WebView's
     * screen-coordinate content frame in {@link #webViewRect}, the screen-coordinate geometry extent in {@link
     * #screenExtent}, the screen-coordinate offset in {@link #screenOffset}, and assigns {@link #eyeDistance} to 0.
     *
     * @param dc the current draw context.
     */
    protected void computeBalloonPoints(DrawContext dc)
    {
        this.screenRect = null;
        this.screenExtent = null;
        this.webViewRect = null;
        this.screenOffset = null;
        this.eyeDistance = 0;

        BalloonAttributes activeAttrs = this.getActiveAttributes();
        Dimension size = activeAttrs.getSize().compute(1, 1, dc.getView().getViewport().width,
            dc.getView().getViewport().height);
        Point2D offset = activeAttrs.getOffset().computeOffset(size.width, size.height, 1d, 1d);
        Insets insets = activeAttrs.getInsets();

        // Cache the screen offset computed from the active attributes.
        this.screenOffset = new Point((int) offset.getX(), (int) offset.getY());
        // Compute the screen rectangle given the current screen point, the current screen offset, and the current
        // screen size. Translate the screen y from AWT coordinates (origin at upper left) to GL coordinates (origin at
        // bottom left). Note: The screen offset denotes how to place the screen reference point relative to the frame.
        // For example, an offset of (-10, -10) in pixels places the reference point below and to the left of the frame.
        // Since the screen reference point is fixed, the frame appears to move relative to the reference point.
        int y = dc.getView().getViewport().height - this.screenLocation.y;
        this.screenRect = new Rectangle(this.screenLocation.x - this.screenOffset.x, y - this.screenOffset.y,
            size.width, size.height);
        // Compute the screen extent as the rectangle containing the balloon's screen rectangle and its screen point.
        this.screenExtent = new Rectangle(this.screenRect);
        this.screenExtent.add(this.screenLocation.x, y);

        // Compute the WebView rectangle as an inset of the screen rectangle, given the current inset values.
        this.webViewRect = new Rectangle(
            this.screenRect.x + insets.left,
            this.screenRect.y + insets.bottom,
            this.screenRect.width - (insets.left + insets.right),
            this.screenRect.height - (insets.bottom + insets.top));
        // The screen balloon has no eye distance; assign it to zero.
        this.eyeDistance = 0;
    }

    /** {@inheritDoc} */
    protected void setupDepthTest(DrawContext dc)
    {
        dc.getGL().glDisable(GL.GL_DEPTH_TEST);
    }
}
