/*
 * Copyright (C) 2001, 2011 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.geom.*;

/**
 * A BrowserBalloon attached to a position on the globe.
 *
 * @author pabercrombie
 * @version $Id $
 */
public class GlobeBrowserBalloon extends AbstractBrowserBalloon implements GlobeBalloon
{
    protected Position position;
    protected int altitudeMode;

    /** The Cartesian point corresponding to the balloon position. May be {@code null}. */
    protected Vec4 placePoint;
    /** The location of the balloon's placePoint in the viewport (on the screen). May be {@code null}. */
    protected Vec4 screenPlacePoint;

    /**
     * Create the balloon.
     *
     * @param text     Text to display in the balloon.
     * @param position The balloon's initial position.
     */
    public GlobeBrowserBalloon(String text, Position position)
    {
        super(text);

        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.position = position;
    }

    /** {@inheritDoc} */
    public Position getPosition()
    {
        return this.position;
    }

    /** {@inheritDoc} */
    public void setPosition(Position position)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.position = position;
    }

    /** {@inheritDoc} */
    public int getAltitudeMode()
    {
        return altitudeMode;
    }

    /** {@inheritDoc} */
    public void setAltitudeMode(int altitudeMode)
    {
        this.altitudeMode = altitudeMode;
    }

    /**
     * Computes and stores the balloon's model-coordinate point in {@link #placePoint}, the screen-space projection of
     * the model-coordinate point in {@link #screenPlacePoint}, the balloon's screen-coordinate content frame in {@link
     * #screenRect}, the WebView's screen-coordinate content frame in {@link #webViewRect}, the screen-coordinate
     * geometry extent in {@link #screenExtent}, the screen-coordinate offset in {@link #screenOffset}, and the distance
     * between the model-coordinate point and the View's eye point in {@link #eyeDistance}. This applies the balloon's
     * altitude mode when computing the model-coordinate point.
     *
     * @param dc the current draw context.
     */
    protected void computeBalloonPoints(DrawContext dc)
    {
        this.placePoint = null;
        this.screenPlacePoint = null;
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

        if (this.altitudeMode == WorldWind.CLAMP_TO_GROUND)
        {
            this.placePoint = dc.computeTerrainPoint(
                this.position.getLatitude(), this.position.getLongitude(), 0);
        }
        else if (this.altitudeMode == WorldWind.RELATIVE_TO_GROUND)
        {
            this.placePoint = dc.computeTerrainPoint(
                this.position.getLatitude(), this.position.getLongitude(), this.position.getAltitude());
        }
        else // ABSOLUTE
        {
            double height = this.position.getElevation() * dc.getVerticalExaggeration();
            this.placePoint = dc.getGlobe().computePointFromPosition(
                this.position.getLatitude(), this.position.getLongitude(), height);
        }

        // Exit immediately if the place point is null. In this case we cannot compute the data that depends on the
        // place point: screen place point, screen rectangle, WebView rectangle, and eye distance.
        if (this.placePoint == null)
            return;

        // Compute the screen place point as the projection of the place point into screen coordinates.
        this.screenPlacePoint = dc.getView().project(this.placePoint);
        // Cache the screen offset computed from the active attributes.
        this.screenOffset = new Point((int) offset.getX(), (int) offset.getY());
        // Compute the screen rectangle given the screen projection of the place point, the current screen offset, and
        // the current screen size. Note: The screen offset denotes how to place the screen reference point relative to
        // the frame. For example, an offset of (-10, -10) in pixels places the reference point below and to the left
        // of the frame. Since the screen reference point is fixed, the frame appears to move relative to the reference
        // point.
        this.screenRect = new Rectangle((int) (this.screenPlacePoint.x - this.screenOffset.x),
            (int) (this.screenPlacePoint.y - this.screenOffset.y),
            size.width, size.height);
        // Compute the screen extent as the rectangle containing the balloon's screen rectangle and its place point.
        this.screenExtent = new Rectangle(this.screenRect);
        this.screenExtent.add(this.screenPlacePoint.x, this.screenPlacePoint.y);

        // Compute the WebView rectangle as an inset of the screen rectangle, given the current inset values.
        this.webViewRect = new Rectangle(
            this.screenRect.x + insets.left,
            this.screenRect.y + insets.bottom,
            this.screenRect.width - (insets.left + insets.right),
            this.screenRect.height - (insets.bottom + insets.top));

        // Compute the eye distance as the distance from the place point to the View's eye point.
        this.eyeDistance = this.isAlwaysOnTop() ? 0 : dc.getView().getEyePoint().distanceTo3(this.placePoint);
    }

    /** {@inheritDoc} */
    protected void setupDepthTest(DrawContext dc)
    {
        GL gl = dc.getGL();

        if (!this.isAlwaysOnTop() && this.screenPlacePoint != null
            && dc.getView().getEyePosition().getElevation() < (dc.getGlobe().getMaxElevation()
            * dc.getVerticalExaggeration()))
        {
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthMask(false);

            // Adjust depth of image to bring it slightly forward
            double depth = this.screenPlacePoint.z - (8d * 0.00048875809d);
            depth = depth < 0d ? 0d : (depth > 1d ? 1d : depth);
            gl.glDepthFunc(GL.GL_LESS);
            gl.glDepthRange(depth, depth);
        }
        else
        {
            gl.glDisable(GL.GL_DEPTH_TEST);
        }
    }

    /**
     * Determines whether the balloon intersects the view frustum.
     *
     * @param dc the current draw context.
     *
     * @return {@code true} If the balloon intersects the frustum, otherwise {@code false}.
     */
    @Override
    protected boolean intersectsFrustum(DrawContext dc)
    {
        View view = dc.getView();

        // Test the balloon against the near and far clipping planes.
        Frustum frustum = view.getFrustumInModelCoordinates();
        //noinspection SimplifiableIfStatement
        if (this.placePoint != null
            && (frustum.getNear().distanceTo(this.placePoint) < 0
            || frustum.getFar().distanceTo(this.placePoint) < 0))
        {
            return false;
        }

        return super.intersectsFrustum(dc);
    }

    @Override
    protected PickedObject createPickedObject(DrawContext dc, Color pickColor)
    {
        PickedObject po = super.createPickedObject(dc, pickColor);

        // Set the picked object's position to the balloon's position.
        po.setPosition(this.position);
        return po;
    }
}
