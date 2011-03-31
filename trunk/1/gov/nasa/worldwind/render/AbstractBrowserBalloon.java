/*
Copyright (C) 2001, 2010 United States Government as represented by 
the Administrator of the National Aeronautics and Space Administration. 
All Rights Reserved. 
*/
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.util.webview.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.nio.DoubleBuffer;

/**
 * A {@link Balloon} that renders HTML.
 * <p/>
 * The browser balloon may optionally include user interface controls to navigate the browser back and forward, and to
 * close the balloon. When the user selects one of these controls, a {@link SelectEvent} is generated with the {@link
 * PickedObject}'s {@link AVKey#ACTION} value set to one of {@link AVKey#CLOSE}, {@link AVKey#BACK}, or {@link
 * AVKey#FORWARD}.
 * <p/>
 * The browser balloon can be resized by dragging the mouse on the balloon border. When the user selects the border, a
 * SelectEvent is generated with the PickedObject's AVKey.ACTION value set to {@link AVKey#RESIZE}. The PickedObject's
 * {@link AVKey#BOUNDS} value holds the Balloon's screen bounds in AWT coordinates (origin at the upper left corner) as
 * a java.awt.Rectangle.
 * <p/>
 * <b>Note on BrowserBalloon size:</b> The balloon size is specified as a {@link Size} object in the {@link
 * BalloonAttributes}. However, BrowserBalloon does not have a native size, so a {@link Size} with size mode {@link
 * Size#NATIVE_DIMENSION} or {@link Size#MAINTAIN_ASPECT_RATIO} is <b>invalid</b>, and will result in a
 * IllegalStateException during rendering. The size attribute must explicitly specify the size of the balloon using
 * {@link Size#EXPLICIT_DIMENSION}.
 *
 * @author dcollins
 * @version $Id: AbstractBrowserBalloon.java 14581 2011-01-27 22:30:47Z dcollins $
 */
public abstract class AbstractBrowserBalloon extends AbstractBalloon implements OrderedRenderable, HotSpot, Disposable
{
    /**
     * Holds the vertex data and the defining properties of the balloon's frame geometry. The {@link #vertexBuffer}
     * defines the screen-coordinate vertices of the balloon's frame. The {@link #size}, {@link #offset}, {@link
     * #balloonShape}, {@link #leaderShape}, {@link #leaderWidth}, and {@link #cornerRadius} are the frame geometry's
     * defining properties. These are used to determine when the frame geometry is invalid and must be recomputed.
     */
    protected static class FrameGeometryInfo
    {
        protected Dimension size;
        protected Point offset;
        protected String balloonShape;
        protected String leaderShape;
        protected int leaderWidth;
        protected int cornerRadius;
        protected DoubleBuffer vertexBuffer;

        public FrameGeometryInfo()
        {
        }
    }

    /**
     * The class name of the default {@link gov.nasa.worldwind.util.webview.WebViewFactory} used to create the balloon's
     * internal {@link gov.nasa.worldwind.util.webview.WebView}. This factory is used when the configuration does not
     * specify a WebView factory.
     */
    protected static final String DEFAULT_WEB_VIEW_FACTORY = BasicWebViewFactory.class.getName();
    /** The default outline pick width in pixels. The default is 10 pixels. */
    protected static final int DEFAULT_OUTLINE_PICK_WIDTH = 10;
    /** The default close control width and height in pixels. The default is 15x15 pixels. */
    protected static final Dimension DEFAULT_CLOSE_CONTROL_SIZE = new Dimension(15, 15);
    /**
     * The default close control's location within the balloon's screen rectangle. The default location is (30, 25)
     * pixels inset from the balloon' upper right corner.
     */
    protected static final Offset DEFAULT_CLOSE_CONTROL_OFFSET = new Offset(30.0, 25.0, AVKey.INSET_PIXELS,
        AVKey.INSET_PIXELS);
    /** The default back navigation control width and height in pixels. The default is 15x15 pixels. */
    protected static final Dimension DEFAULT_BACK_CONTROL_SIZE = new Dimension(15, 15);
    /**
     * The default back navigation control's location within the balloon's screen rectangle. The default location is
     * (15, 25) pixels inset from the balloon' upper left corner.
     */
    protected static final Offset DEFAULT_BACK_CONTROL_OFFSET = new Offset(15.0, 25.0, AVKey.PIXELS,
        AVKey.INSET_PIXELS);
    /** The default forward navigation control width and height in pixels. The default is 15x15 pixels. */
    protected static final Dimension DEFAULT_FORWARD_CONTROL_SIZE = new Dimension(15, 15);
    /**
     * The default forward navigation control's location within the balloon's screen rectangle. The default location is
     * (35, 25) pixels inset from the balloon' upper left corner.
     */
    protected static final Offset DEFAULT_FORWARD_CONTROL_OFFSET = new Offset(35.0, 25.0, AVKey.PIXELS,
        AVKey.INSET_PIXELS);

    /** Denotes whether or not the balloon's resize control is enabled. Initially {@code true}. */
    protected boolean enableResizeControl = true;
    /** Denotes whether or not the balloon's close control is enabled. Initially {@code true}. */
    protected boolean enableCloseControl = true;
    /** Denotes whether or not the balloon's navigation controls are enabled. Initially {@code true}. */
    protected boolean enableNavigationControls = true;
    /**
     * The line width used to draw the the balloon's outline during picking. Initially set to {@link
     * #DEFAULT_OUTLINE_PICK_WIDTH}.
     */
    protected int outlinePickWidth = DEFAULT_OUTLINE_PICK_WIDTH;
    /** Size of the close control in pixels. Initially set to {@link #DEFAULT_CLOSE_CONTROL_SIZE}. */
    protected Dimension closeControlSize = DEFAULT_CLOSE_CONTROL_SIZE;
    /**
     * Location within the balloon's screen rectangle where the close control is drawn. Initially set to {@link
     * #DEFAULT_CLOSE_CONTROL_OFFSET}.
     */
    protected Offset closeControlOffset = DEFAULT_CLOSE_CONTROL_OFFSET;
    /** Size of the back control in pixels. Initially set to {@link #DEFAULT_BACK_CONTROL_SIZE}. */
    protected Dimension backControlSize = DEFAULT_BACK_CONTROL_SIZE;
    /**
     * Location within the balloon's screen rectangle where the back control is drawn.  Initially set to {@link
     * #DEFAULT_BACK_CONTROL_OFFSET}.
     */
    protected Offset backControlOffset = DEFAULT_BACK_CONTROL_OFFSET;
    /** Size of the forward control in pixels.  Initially set to {@link #DEFAULT_FORWARD_CONTROL_SIZE}. */
    protected Dimension forwardControlSize = DEFAULT_FORWARD_CONTROL_SIZE;
    /**
     * Location within the balloon's screen rectangle where the forward control is drawn.  Initially set to {@link
     * #DEFAULT_FORWARD_CONTROL_OFFSET}.
     */
    protected Offset forwardControlOffset = DEFAULT_FORWARD_CONTROL_OFFSET;
    /**
     * The URL used to resolve relative URLs in the text content. {@code null} indicates the current working directory.
     * Initially {@code null}.
     */
    protected URL baseURL;

    /** Identifies the time when the balloon text was updated. */
    protected long textUpdateTime = -1;
    /** Denotes whether or not the balloon's text is valid. Initially {@code true}. */
    protected boolean textValid = true;
    /** Interface for interacting with the operating system's web browser control. Initially {@code null}. */
    protected WebView webView;
    /**
     * Denotes whether or not an attempt at WebView creation failed. When {@code true} the balloon does not perform
     * subsequent attempts to create the WebView. Initially {@code false}.
     */
    protected boolean webViewCreationFailed;
    /**
     * Listener for forwarding WebView SelectEvents to the BrowserBalloon. We use a separate SelectListener for the
     * WebView to disambiguate WebView SelectEvents from HotSpot SelectEvents.
     */
    protected SelectListener webViewSelectListener = new SelectListener()
    {
        public void selected(SelectEvent event)
        {
            handleWebViewSelectEvent(event);
        }
    };

    /** Identifies the frame used to calculate the balloon's active attributes and points. */
    protected long frameTimeStamp = -1;
    /** Identifies the frame used to calculate the balloon's geometry. */
    protected long geomTimeStamp = -1;
    /** The location and size of the balloon's content frame in the viewport (on the screen). */
    protected Rectangle screenRect;
    /** The location and size of the WebView's content frame in the viewport (on the screen). */
    protected Rectangle webViewRect;
    /** The extent of the balloon's geometry in the viewport (on the screen). */
    protected Rectangle screenExtent;
    /** The location of the balloon's content frame relative to the balloon's screen point in the viewport. */
    protected Point screenOffset;
    /** Used to order the balloon as an ordered renderable. */
    protected double eyeDistance;
    /** The balloon geometry vertices passed to OpenGL. */
    protected FrameGeometryInfo frameInfo;
    /** The layer active during the most recent pick pass. */
    protected Layer pickLayer;

    /** Support for setting up and restoring picking state, and resolving the picked object. */
    protected PickSupport pickSupport = new PickSupport();
    /** Support for setting up and restoring OpenGL state during rendering. */
    protected OGLStackHandler osh = new OGLStackHandler();

    protected AbstractBrowserBalloon(String text)
    {
        super(text);
    }

    /**
     * Computes and stores the balloon's model-coordinate and screen-coordinate points.
     *
     * @param dc the current draw context.
     */
    protected abstract void computeBalloonPoints(DrawContext dc);

    protected abstract void setupDepthTest(DrawContext dc);

    /**
     * Disposes the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView}. This does nothing if the balloon
     * is already disposed.
     */
    public void dispose()
    {
        this.disposeWebView();
    }

    /**
     * Indicates whether the balloon's resize control is enabled.
     *
     * @return {@code true} if the balloon's resize control is enabled, and {@code false} otherwise.
     *
     * @see #setEnableResizeControl(boolean)
     */
    public boolean isEnableResizeControl()
    {
        return this.enableResizeControl;
    }

    /**
     * Specifies whether the balloon's resize control is enabled. Enabled by default. The default resize control enables
     * the user to resize the balloon by dragging any part of the balloon's outline. The resize control's line width can
     * be specified by calling {@link #setOutlinePickWidth(int)}.
     *
     * @param enable {@code true} to enable the balloon's resize control, and {@code false} to disable it.
     *
     * @see #isEnableResizeControl()
     */
    public void setEnableResizeControl(boolean enable)
    {
        this.enableResizeControl = enable;
    }

    /**
     * Indicates whether the balloon's close control is enabled.
     *
     * @return {@code true} if the balloon's close control is enabled, and {@code false} otherwise.
     *
     * @see #setEnableCloseControl(boolean) (boolean)
     */
    public boolean isEnableCloseControl()
    {
        return this.enableCloseControl;
    }

    /**
     * Specifies whether the balloon's close control is enabled. Enabled by default. The default close control is drawn
     * in the balloon's upper right corner, and enables the user to close the balloon by clicking on it. The close
     * control's size and offset within the balloon can be specified by calling {@link
     * #setCloseControlSize(java.awt.Dimension)} and {@link #setCloseControlOffset(Offset)}, respectively.
     *
     * @param enable {@code true} to enable the balloon's close control, and {@code false} to disable it.
     *
     * @see #isEnableCloseControl()
     */
    public void setEnableCloseControl(boolean enable)
    {
        this.enableCloseControl = enable;
    }

    /**
     * Indicates whether the balloon's navigation controls are enabled.
     *
     * @return {@code true} if the balloon's navigation controls is enabled, and {@code false} otherwise.
     *
     * @see #setEnableNavigationControls(boolean)
     */
    public boolean isEnableNavigationControls()
    {
        return enableNavigationControls;
    }

    /**
     * Specifies whether the balloon's navigation controls are enabled. Enabled by default. The default navigation
     * controls are drawn in the balloon's upper left corner, and enable the user to go back and go forward in the
     * balloon's history by clicking on the controls. The back and forward control's size and offset within the balloon
     * can be specified by calling {@link #setBackControlSize(java.awt.Dimension)}, {@link
     * #setBackControlOffset(Offset)}, {@link #setForwardControlSize(java.awt.Dimension)}, and {@link
     * #setForwardControlOffset(Offset)}.
     *
     * @param enable {@code true} to enable the balloon's navigation controls, and {@code false} to disable them.
     *
     * @see #isEnableNavigationControls()
     */
    public void setEnableNavigationControls(boolean enable)
    {
        this.enableNavigationControls = enable;
    }

    /**
     * Indicates the outline line width (in pixels) used during picking.  A larger width than normal typically makes the
     * outline easier to pick.
     *
     * @return the outline line width (in pixels) used during picking.
     *
     * @see #setOutlinePickWidth(int)
     */
    public int getOutlinePickWidth()
    {
        return this.outlinePickWidth;
    }

    /**
     * Specifies the outline line width (in pixels) to use during picking. The specified <code>width</code> must be zero
     * or a positive integer. Specifying a pick width of zero effectively disables the picking of the balloon's outline
     * and its resize control. A larger width than normal typically makes the outline easier to pick.
     * <p/>
     * When the the balloon's resize control is enabled, the outline becomes the resize control and is drawn in the
     * specified <code>width</code>. Therefore this value also control's the balloon's resize control width. If the
     * resize control is disabled by calling {@link #setEnableResizeControl(boolean)} with a value of
     * <code>false</code>, this has no effect on the balloon's resize control until it's enabled.
     *
     * @param width the outline line width (in pixels) to use during picking.
     *
     * @throws IllegalArgumentException if <code>width</code> is less than zero.
     * @see #getOutlinePickWidth()
     * @see #setEnableResizeControl(boolean)
     */
    public void setOutlinePickWidth(int width)
    {
        if (width < 0)
        {
            String message = Logging.getMessage("Geom.WidthIsNegative", width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.outlinePickWidth = width;
    }

    /**
     * Indicates the close control's width and height in pixels.
     *
     * @return the close control's width and height in pixels.
     *
     * @see #setCloseControlSize(java.awt.Dimension)
     */
    public Dimension getCloseControlSize()
    {
        return this.closeControlSize;
    }

    /**
     * Specifies the close control's width and height in pixels. The specified width and height must be zero or a
     * positive integer.
     * <p/>
     * If the control intersects part of the balloon's content, the control is given rendering and picking priority over
     * the content. If the control is placed outside the balloon, the control is clipped by the balloon's outline.
     * <p/>
     * If the close control is disabled, this has no effect until it's enabled.
     *
     * @param size the close control's width and height in pixels.
     *
     * @throws IllegalArgumentException if <code>size</code> is null, if <code>size.getWidth()</code> is less than zero,
     *                                  or if <code>size.getHeight()</code> is less than zero.
     * @see #getCloseControlSize()
     * @see #setCloseControlOffset(Offset)
     */
    public void setCloseControlSize(Dimension size)
    {
        if (size == null)
        {
            String message = Logging.getMessage("nullValue.SizeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (size.width < 0)
        {
            String message = Logging.getMessage("Geom.WidthIsNegative", size.width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (size.height < 0)
        {
            String message = Logging.getMessage("Geom.HeightIsNegative", size.height);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.closeControlSize = size;
    }

    /**
     * Indicates the location of the close control's lower left corner within the balloon's screen rectangle. The offset
     * is computed with a rectangle width and height equal to the balloon's screen rectangle, and with its origin in the
     * balloon's lower left corner.
     *
     * @return the close control's location within the balloon's screen rectangle.
     *
     * @see #setCloseControlOffset(Offset)
     */
    public Offset getCloseControlOffset()
    {
        return this.closeControlOffset;
    }

    /**
     * Specifies the location of the close control's lower left corner within the balloon's screen rectangle. The offset
     * is computed with a rectangle width and height equal to the balloon's screen rectangle, and with its origin in the
     * balloon's lower left corner.
     * <p/>
     * If the control intersects part of the balloon's content, the control is given rendering and picking priority over
     * the content. If the control is placed outside the balloon, the control extends beyond balloon's geometry.
     * <p/>
     * If the close control is disabled, this has no effect until it's enabled.
     *
     * @param offset the close control's location within the balloon's screen rectangle.
     *
     * @throws IllegalArgumentException if <code>offset</code> is null.
     * @see #getCloseControlOffset()
     * @see #setCloseControlSize(java.awt.Dimension)
     */
    public void setCloseControlOffset(Offset offset)
    {
        if (offset == null)
        {
            String message = Logging.getMessage("nullValue.OffsetIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.closeControlOffset = offset;
    }

    /**
     * Indicates the back navigation control's width and height in pixels.
     *
     * @return the back navigation control's width and height in pixels.
     *
     * @see #setBackControlSize(java.awt.Dimension)
     */
    public Dimension getBackControlSize()
    {
        return this.backControlSize;
    }

    /**
     * Specifies the back navigation control's width and height in pixels. The specified width and height must be zero
     * or a positive integer.
     * <p/>
     * If the control intersects part of the balloon's content, the control is given rendering and picking priority over
     * the content. If the control is placed outside the balloon, the control extends beyond the balloon's geometry.
     * <p/>
     * If the navigation controls are disabled, this has no effect until they are enabled.
     *
     * @param size the back navigation control's width and height in pixels.
     *
     * @throws IllegalArgumentException if <code>size</code> is null, if <code>size.getWidth()</code> is less than zero,
     *                                  or if <code>size.getHeight()</code> is less than zero.
     * @see #getBackControlSize()
     * @see #setBackControlOffset(Offset)
     */
    public void setBackControlSize(Dimension size)
    {
        if (size == null)
        {
            String message = Logging.getMessage("nullValue.SizeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.backControlSize = size;
    }

    /**
     * Indicates the location of the back navigation control's lower left corner within the balloon's screen rectangle.
     * The offset is computed with a rectangle width and height equal to the balloon's screen rectangle, and with its
     * origin in the balloon's lower left corner.
     *
     * @return the back navigation control's location within the balloon's screen rectangle.
     *
     * @see #setBackControlOffset(Offset)
     */
    public Offset getBackControlOffset()
    {
        return this.backControlOffset;
    }

    /**
     * Specifies the location of the back navigation control's lower left corner within the balloon's screen rectangle.
     * The offset is computed with a rectangle width and height equal to the balloon's screen rectangle, and with its
     * origin in the balloon's lower left corner.
     * <p/>
     * If the control intersects part of the balloon's content, the control is given rendering and picking priority over
     * the content. If the control is placed outside the balloon, the control extends beyond the balloon's geometry.
     * <p/>
     * If the navigation controls are disabled, this has no effect until they are enabled.
     *
     * @param offset the close control's location within the balloon's screen rectangle.
     *
     * @throws IllegalArgumentException if <code>offset</code> is null.
     * @see #getBackControlOffset()
     * @see #setBackControlSize(java.awt.Dimension)
     */
    public void setBackControlOffset(Offset offset)
    {
        if (offset == null)
        {
            String message = Logging.getMessage("nullValue.OffsetIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.backControlOffset = offset;
    }

    /**
     * Indicates the forward navigation control's width and height in pixels.
     *
     * @return the forward navigation control's width and height in pixels.
     *
     * @see #setForwardControlSize(java.awt.Dimension)
     */
    public Dimension getForwardControlSize()
    {
        return this.forwardControlSize;
    }

    /**
     * Specifies the forward navigation control's width and height in pixels. The specified width and height must be
     * zero or a positive integer.
     * <p/>
     * If the control intersects part of the balloon's content, the control is given rendering and picking priority over
     * the content. If the control is placed outside the balloon, the control extends beyond the balloon's geometry.
     * <p/>
     * If the navigation controls are disabled, this has no effect until they are are enabled.
     *
     * @param size the forward navigation control's width and height in pixels.
     *
     * @throws IllegalArgumentException if <code>size</code> is null, if <code>size.getWidth()</code> is less than zero,
     *                                  or if <code>size.getHeight()</code> is less than zero.
     * @see #getForwardControlSize()
     * @see #setForwardControlOffset(Offset)
     */
    public void setForwardControlSize(Dimension size)
    {
        if (size == null)
        {
            String message = Logging.getMessage("nullValue.SizeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.forwardControlSize = size;
    }

    /**
     * Indicates the location of the forward navigation control's lower left corner within the balloon's screen
     * rectangle. The offset is computed with a rectangle width and height equal to the balloon's screen rectangle, and
     * with its origin in the balloon's lower left corner.
     *
     * @return the forward navigation control's location within the balloon's screen rectangle.
     *
     * @see #setForwardControlOffset(Offset)
     */
    public Offset getForwardControlOffset()
    {
        return this.forwardControlOffset;
    }

    /**
     * Specifies the location of the forward navigation control's lower left corner within the balloon's screen
     * rectangle. The offset is computed with a rectangle width and height equal to the balloon's screen rectangle, and
     * with its origin in the balloon's lower left corner.
     * <p/>
     * If the control intersects part of the balloon's content, the control is given rendering and picking priority over
     * the content. If the control is placed outside the balloon, the control extends beyond the balloon's geometry.
     * <p/>
     * If the navigation controls are disabled, this has no effect until they are are enabled.
     *
     * @param offset the close control's location within the balloon's screen rectangle.
     *
     * @throws IllegalArgumentException if <code>offset</code> is null.
     * @see #getForwardControlOffset()
     * @see #setForwardControlSize(java.awt.Dimension)
     */
    public void setForwardControlOffset(Offset offset)
    {
        if (offset == null)
        {
            String message = Logging.getMessage("nullValue.OffsetIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.forwardControlOffset = offset;
    }

    public URL getBaseURL()
    {
        return this.baseURL;
    }

    public void setBaseURL(URL baseURL)
    {
        this.baseURL = baseURL;
    }

    /** Navigate the browser to the previous page in the browsing history. Has no effect if there is previous page. */
    public void goBack()
    {
        if (this.webView != null)
            this.webView.goBack();
    }

    /** Navigate the browser to the next page in the browsing history. Has no effect if there is no next page. */
    public void goForward()
    {
        if (this.webView != null)
            this.webView.goForward();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overridden to mark the balloon's text as valid. The balloon's text is marked as invalid if the user navigates to
     * a link embedded in the balloon text.
     */
    @Override
    public void setText(String text)
    {
        super.setText(text);
        this.textValid = true;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overridden to apply the balloon's delegate owner to its internal {@link gov.nasa.worldwind.util.webview.WebView}.
     * This ensures that SelectEvents generated by the balloon or its WebView contain the same picked object.
     */
    @Override
    public void setDelegateOwner(Object delegateOwner)
    {
        super.setDelegateOwner(delegateOwner);

        // Configure the WebView with the appropriate delegate owner to use for generated select events. If the delegate
        // owner is set before the WebView is created, it's applied to the WebView just after it's created.
        if (this.webView != null)
            this.webView.setDelegateOwner(this.getDelegateOwner() != null ? this.getDelegateOwner() : this);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overridden to suppress {@link gov.nasa.worldwind.avlist.AVKey#REPAINT} property change events sent by the
     * balloon's internal {@link gov.nasa.worldwind.util.webview.WebView} when {@link #isVisible()} returns {@code
     * false}.
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent)
    {
        if (!this.isVisible() && propertyChangeEvent != null
            && AVKey.REPAINT.equals(propertyChangeEvent.getPropertyName()))
        {
            return;
        }

        super.propertyChange(propertyChangeEvent);
    }

    /** {@inheritDoc} */
    public Rectangle getBounds(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Update the balloon's active attributes and points if that hasn't already been done this frame.
        this.updateRenderStateIfNeeded(dc);

        // Return the balloon's screen extent computed in updateRenderStateIfNeeded. This may be null.
        return this.screenExtent;
    }

    public double getDistanceFromEye()
    {
        return this.eyeDistance;
    }

    public void pick(DrawContext dc, Point pickPoint)
    {
        // This method is called only when ordered renderables are being drawn.
        // Arg checked within call to render.

        if (!this.isPickEnabled())
            return;

        this.pickSupport.clearPickList();
        try
        {
            this.pickSupport.beginPicking(dc);
            this.render(dc);
        }
        finally
        {
            this.pickSupport.endPicking(dc);
            this.pickSupport.resolvePick(dc, pickPoint, this.pickLayer);
        }
    }

    public void render(DrawContext dc)
    {
        // This render method is called three times during frame generation. It's first called as a Renderable during
        // picking. It's called again during normal rendering. And it's called a third time as an OrderedRenderable. The
        // first two calls determine whether to add the placemark  and its optional line to the ordered renderable list
        // during pick and render. The third call just draws the ordered renderable.

        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!this.isVisible())
            return;

        if (dc.isOrderedRenderingMode())
            this.drawOrderedRenderable(dc);
        else
            this.makeOrderedRenderable(dc);
    }

    /**
     * Updates the balloon's per-frame rendering state, and determines whether to queue an ordered renderable for the
     * balloon. This queues an ordered renderable if the balloon intersects the current viewing frustum, and if the
     * balloon's internal rendering state can be computed. This updates the balloon's rendering state by calling {@link
     * #updateRenderStateIfNeeded(DrawContext)}, and updates its geometry by calling {@link #computeGeometry()}.
     * <p/>
     * BrowserBalloon separates render state updates from geometry updates for two reasons: <ul> <li>Geometry may be
     * updated based on different conditions.</li> <li>Rendering state potentially needs to be updated in
     * getBounds.</li> </ul>
     *
     * @param dc the current draw context.
     */
    protected void makeOrderedRenderable(DrawContext dc)
    {
        // Update the balloon's active attributes and points if that hasn't already been done this frame.
        this.updateRenderStateIfNeeded(dc);

        // Exit immediately if either the balloon's active attributes or its screen rectangle are null. In either case
        // we cannot compute the balloon's geometry nor can we determine where to render the balloon.
        if (this.getActiveAttributes() == null || this.screenRect == null)
            return;

        // Re-use geometry already calculated this frame.
        if (dc.getFrameTimeStamp() != this.geomTimeStamp)
        {
            // Recompute this balloon's geometry only when an attribute change requires us to.
            if (this.mustRegenerateGeometry())
                this.computeGeometry();
            this.geomTimeStamp = dc.getFrameTimeStamp();
        }

        if (this.intersectsFrustum(dc))
            dc.addOrderedRenderable(this);

        if (dc.isPickingMode())
            this.pickLayer = dc.getCurrentLayer();
    }

    /**
     * Update the balloon's active attributes and points, if that hasn't already been done this frame. This updates the
     * balloon's rendering state as follows: <ul> <li>Computes the balloon's active attributes by calling {@link
     * #determineActiveAttributes()} and stores the result in {@link #activeAttributes}.</li> <li>Computes the balloon's
     * model-coordinate and screen-coordinate points by calling {@link #computeBalloonPoints(DrawContext)}.</li> </ul>
     *
     * @param dc the current draw context.
     */
    protected void updateRenderStateIfNeeded(DrawContext dc)
    {
        // Re-use rendering state values already calculated this frame.
        if (dc.getFrameTimeStamp() != this.frameTimeStamp)
        {
            this.updateRenderState(dc);
            this.frameTimeStamp = dc.getFrameTimeStamp();
        }
    }

    protected void updateRenderState(DrawContext dc)
    {
        this.determineActiveAttributes();
        if (this.getActiveAttributes() == null)
            return;

        this.computeBalloonPoints(dc);
    }

    /**
     * Determines which attributes -- normal, highlight or default -- to use each frame.
     *
     * @throws IllegalStateException if the size attribute uses a size mode that is not valid for a BrowserBalloon. See
     *                               BrowserBalloon class documentation for details.
     */
    @Override
    protected void determineActiveAttributes()
    {
        super.determineActiveAttributes();

        // The balloon does not have a native size, so the size must be specified explicitly as a pixel dimension,
        // or as a fraction of the screen. The MAINTAIN_ASPECT_RATIO and NATIVE_DIMENSION size modes are not valid
        // for BrowserBalloon.
        Size balloonSize = this.activeAttributes.getSize();
        if (balloonSize.getHeightMode() == Size.NATIVE_DIMENSION
            || balloonSize.getHeightMode() == Size.MAINTAIN_ASPECT_RATIO
            || balloonSize.getWidthMode() == Size.NATIVE_DIMENSION
            || balloonSize.getWidthMode() == Size.MAINTAIN_ASPECT_RATIO)
        {
            String message = Logging.getMessage("Geom.RequireExplicitSize");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }
    }

    /**
     * Indicates whether this balloon's screen-coordinate geometry must be recomputed as a result of a balloon attribute
     * changing.
     *
     * @return <code>true</code> if this balloon's geometry must be recomputed, otherwise <code>false</code>.
     */
    protected boolean mustRegenerateGeometry()
    {
        if (this.frameInfo == null)
            return true;

        if (!this.screenRect.getSize().equals(this.frameInfo.size) || !this.screenOffset.equals(this.frameInfo.offset))
            return true;

        BalloonAttributes activeAttrs = this.getActiveAttributes();
        return !activeAttrs.getBalloonShape().equals(this.frameInfo.balloonShape)
            || !activeAttrs.getLeaderShape().equals(this.frameInfo.leaderShape)
            || activeAttrs.getLeaderWidth() != this.frameInfo.leaderWidth
            || activeAttrs.getCornerRadius() != this.frameInfo.cornerRadius;
    }

    /**
     * Updates the balloon's screen-coordinate geometry in {@link #frameInfo} according to the current screen bounds,
     * screen offset, and active attributes.
     */
    protected void computeGeometry()
    {
        if (this.screenRect == null)
            return;

        BalloonAttributes activeAttrs = this.getActiveAttributes();

        if (this.frameInfo == null)
            this.frameInfo = new FrameGeometryInfo();

        if (FrameFactory.LEADER_TRIANGLE.equals(activeAttrs.getLeaderShape()))
        {
            // Note: the balloon leader offset is equivalent to its screen offset, because the screen offset denotes
            // how to place the screen reference point relative to the frame. For example, an offset of (-10, -10) in
            // pixels places the reference point below and to the left of the frame. Since the leader points from the
            // frame to the reference point, its size is determined by the balloon's screen offset.
            this.frameInfo.vertexBuffer = FrameFactory.createShapeWithLeaderBuffer(activeAttrs.getBalloonShape(),
                this.screenRect.width, this.screenRect.height, this.screenOffset, activeAttrs.getLeaderWidth(),
                activeAttrs.getCornerRadius(), this.frameInfo.vertexBuffer);
        }
        else // FrameFactory.LEADER_NONE
        {
            this.frameInfo.vertexBuffer = FrameFactory.createShapeBuffer(activeAttrs.getBalloonShape(),
                this.screenRect.width, this.screenRect.height, activeAttrs.getCornerRadius(),
                this.frameInfo.vertexBuffer);
        }

        // Update the current attributes associated with FrameInfo's vertex buffer.
        this.frameInfo.size = this.screenRect.getSize();
        this.frameInfo.offset = this.screenOffset;
        this.frameInfo.balloonShape = activeAttrs.getBalloonShape();
        this.frameInfo.leaderShape = activeAttrs.getLeaderShape();
        this.frameInfo.leaderWidth = activeAttrs.getLeaderWidth();
        this.frameInfo.cornerRadius = activeAttrs.getCornerRadius();
    }

    /**
     * Determines whether the balloon intersects the view frustum.
     *
     * @param dc the current draw context.
     *
     * @return {@code true} If the balloon intersects the frustum, otherwise {@code false}.
     */
    protected boolean intersectsFrustum(DrawContext dc)
    {
        if (dc.isPickingMode())
            return dc.getPickFrustums().intersectsAny(this.screenRect);
        else
            return dc.getView().getViewport().intersects(this.screenRect);
    }

    protected void drawOrderedRenderable(DrawContext dc)
    {
        this.beginDrawing(dc);
        try
        {
            this.doDrawOrderedRenderable(dc);
        }
        finally
        {
            this.endDrawing(dc);
        }
    }

    protected void beginDrawing(DrawContext dc)
    {
        GL gl = dc.getGL();

        int attrMask =
            GL.GL_COLOR_BUFFER_BIT // For alpha enable, blend enable, alpha func, blend func.
                | GL.GL_CURRENT_BIT // For current color
                | GL.GL_DEPTH_BUFFER_BIT // For depth test enable/disable, depth func, depth mask.
                | GL.GL_LINE_BIT // For line smooth enable, line stipple enable, line width, line stipple factor,
                // line stipple pattern.
                | GL.GL_POLYGON_BIT // For polygon mode.
                | GL.GL_VIEWPORT_BIT; // For depth range.

        this.osh.clear(); // Reset the stack handler's internal state.
        this.osh.pushAttrib(gl, attrMask);
        this.osh.pushClientAttrib(gl, GL.GL_CLIENT_VERTEX_ARRAY_BIT); // For vertex array enable, vertex array pointers.
        this.osh.pushProjectionIdentity(gl);
        // The browser balloon is drawn using a parallel projection sized to fit the viewport.
        gl.glOrtho(0d, dc.getView().getViewport().width, 0d, dc.getView().getViewport().height, -1d, 1d);
        this.osh.pushTextureIdentity(gl);
        this.osh.pushModelviewIdentity(gl);

        gl.glEnableClientState(GL.GL_VERTEX_ARRAY); // All drawing uses vertex arrays.

        if (!dc.isPickingMode())
        {
            gl.glEnable(GL.GL_BLEND); // Enable interior and outline alpha blending when not picking.
            OGLUtil.applyBlending(gl, false);
        }
    }

    protected void endDrawing(DrawContext dc)
    {
        this.osh.pop(dc.getGL());
    }

    protected void doDrawOrderedRenderable(DrawContext dc)
    {
        GL gl = dc.getGL();

        if (dc.isPickingMode())
        {
            Color pickColor = dc.getUniquePickColor();
            this.pickSupport.addPickableObject(this.createPickedObject(dc, pickColor));
            gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
        }

        // Translate to the balloon's screen origin. Use integer coordinates to ensure that the WebView texels are
        // aligned exactly with screen pixels.
        gl.glTranslatef(this.screenRect.x, this.screenRect.y, 0);

        if (!dc.isDeepPickingEnabled())
            this.setupDepthTest(dc);

        if (this.isDrawInterior(dc))
            this.drawInterior(dc);

        if (this.isDrawOutline(dc))
            this.drawOutline(dc);

        if (this.isEnableResizeControl())
            this.drawResizeControl(dc);

        if (this.isEnableCloseControl())
            this.drawCloseControl(dc);

        if (this.isEnableNavigationControls())
            this.drawNavigationControls(dc);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected PickedObject createPickedObject(DrawContext dc, Color pickColor)
    {
        PickedObject po = new PickedObject(pickColor.getRGB(),
            this.getDelegateOwner() != null ? this.getDelegateOwner() : this);

        // Attach the balloon to the picked object's AVList under the key HOT_SPOT. The application can then find that
        // the balloon is a HotSpot by looking in the picked object's AVList. This is critical when the delegate owner
        // is not null because the balloon is no longer the picked object. This would otherwise prevent the application
        // from interacting with the balloon via the HotSpot interface.
        po.setValue(AVKey.HOT_SPOT, this);

        return po;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected boolean isDrawInterior(DrawContext dc)
    {
        return this.getActiveAttributes().isDrawInterior() && this.getActiveAttributes().getInteriorOpacity() > 0;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected boolean isDrawOutline(DrawContext dc)
    {
        return this.getActiveAttributes().isDrawOutline() && this.getActiveAttributes().getOutlineOpacity() > 0;
    }

    protected void drawInterior(DrawContext dc)
    {
        GL gl = dc.getGL();

        boolean textureApplied = false;
        try
        {
            if (!dc.isPickingMode())
            {
                // Apply the balloon's background color and opacity if we're in normal rendering mode.
                Color color = this.getActiveAttributes().getInteriorMaterial().getDiffuse();
                double opacity = this.getActiveAttributes().getInteriorOpacity();
                gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(),
                    (byte) (opacity < 1 ? (int) (opacity * 255 + 0.5) : 255));

                // Bind the WebView's texture representation as the current texture source if we're in normal rendering
                // mode. This also configures the texture matrix to transform texture coordinates from the balloon's vertex
                // coordinates to the WebView's screen rectangle. For this reason we use the balloon's vertex coordinates as
                // its texture coordinates.
                if (this.bindWebView(dc))
                {
                    // Denote that the texture has been applied and that we need to restore the default texture state.
                    textureApplied = true;

                    // The WebView's texture is successfully bound. Enable GL texturing and set up the texture environment
                    // to apply the texture in decal mode. Decal mode uses the texture color where the texture's alpha is 1,
                    // and uses the balloon's background color where it's 0. The texture's internal format must be RGBA to
                    // work correctly, and we assume that the WebView's texture format is RGBA.
                    gl.glEnable(GL.GL_TEXTURE_2D);
                    gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
                    gl.glTexCoordPointer(2, GL.GL_DOUBLE, 0, this.frameInfo.vertexBuffer);
                }
            }

            // Bind the balloon's vertex buffer as source of GL vertex data and draw the balloon's geometry as a triangle
            // fan to display the interior. The balloon's vertices are in screen coordinates.
            gl.glVertexPointer(2, GL.GL_DOUBLE, 0, this.frameInfo.vertexBuffer);
            gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, this.frameInfo.vertexBuffer.remaining() / 2);
        }
        finally
        {
            // Restore the previous texture state. We do this to avoid pushing and popping the texture attribute bit,
            // which is expensive. We disable textures, disable client texture coord arrays, set the default texture
            // environment mode, and bind texture id 0. We don't set the texture coord pointer to 0 because vertex array
            // state is pushed and popped in beginDrawing and endDrawing, respectively.
            if (textureApplied)
            {
                gl.glDisable(GL.GL_TEXTURE_2D);
                gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
                gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            }
        }
    }

    protected void drawOutline(DrawContext dc)
    {
        GL gl = dc.getGL();

        if (!dc.isPickingMode())
        {
            // Apply the balloon's outline color and opacity and apply the balloon's normal outline width if we're in
            // normal rendering mode.
            Color color = this.getActiveAttributes().getOutlineMaterial().getDiffuse();
            double opacity = this.getActiveAttributes().getOutlineOpacity();
            gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(),
                (byte) (opacity < 1 ? (int) (opacity * 255 + 0.5) : 255));

            // Apply line smoothing if we're in normal rendering mode.
            if (this.getActiveAttributes().isEnableAntialiasing())
            {
                gl.glEnable(GL.GL_LINE_SMOOTH);
            }

            if (this.getActiveAttributes().getOutlineStippleFactor() > 0)
            {
                gl.glEnable(GL.GL_LINE_STIPPLE);
                gl.glLineStipple(this.getActiveAttributes().getOutlineStippleFactor(),
                    this.getActiveAttributes().getOutlineStipplePattern());
            }
        }

        // Apply the balloon's outline width. Use the outline pick width if we're in picking mode and the pick width is
        // greater than the normal line width. Otherwise use the normal line width.
        if (dc.isPickingMode() && this.getActiveAttributes().getOutlineWidth() < this.getOutlinePickWidth())
            gl.glLineWidth(this.getOutlinePickWidth());
        else
            gl.glLineWidth((float) this.getActiveAttributes().getOutlineWidth());

        // Bind the balloon's vertex buffer as source of GL vertex data and draw the balloon's geometry as a line strip
        // to display the outline.. The balloon's vertices are in screen coordinates.
        gl.glVertexPointer(2, GL.GL_DOUBLE, 0, this.frameInfo.vertexBuffer);
        gl.glDrawArrays(GL.GL_LINE_STRIP, 0, this.frameInfo.vertexBuffer.remaining() / 2);
    }

    protected boolean bindWebView(DrawContext dc)
    {
        // Attempt to create the balloon's WebView.
        if (this.webView == null)
        {
            this.makeWebView(dc, this.webViewRect.getSize());

            // Exit immediately if WebView creation failed.
            if (this.webView == null)
                return false;
        }

        // The WebView's screen size can change each frame. Synchronize the WebView's frame size with the desired size
        // before attempting to use the WebView's texture. The WebView avoids doing unnecessary work when the same frame
        // size is specified.
        this.webView.setFrameSize(this.webViewRect.getSize());

        // If the balloon's text is valid, update the WebView's text content each time the balloon's decoded string
        // changes. Otherwise, the WebView has navigated away from the balloon's text content. Since the balloon's text
        // no longer represents the WebView's content we ignore changes to the text until the application explicitly
        // sets the balloon's text.
        if (this.textValid)
        {
            String text = this.getTextDecoder().getDecodedText();
            if (this.getTextDecoder().getLastUpdateTime() != this.textUpdateTime)
            {
                this.webView.setHTMLString(text, this.getBaseURL());
                this.textUpdateTime = this.getTextDecoder().getLastUpdateTime();
            }
        }

        // Attempt to get the WebView's texture representation. Exit immediately if this fails or if the texture cannot
        // be bound.
        WWTexture texture = this.webView.getTextureRepresentation(dc);
        if (texture == null)
            return false;

        if (!texture.bind(dc))
            return false;

        GL gl = dc.getGL();

        // Set up the texture matrix to transform texture coordinates from the balloon's screen space vertex coordinates
        // into WebView texture space. This places the WebView's texture in the WebView's screen rectangle. Use integer
        // coordinates when possible to ensure that the image texels are aligned exactly with screen pixels. This
        // transforms texture coordinates such that (webViewRect.getMinX(), webViewRect.getMinY()) maps to (0, 0) - the
        // texture's lower left corner, and (webViewRect.getMaxX(), webViewRect.getMaxY()) maps to (1, 1) - the
        // texture's upper right corner. Since texture coordinates are generated relative to the screenRect origin and
        // webViewRect is in screen coordinates, we translate the texture coordinates by the offset from the screenRect
        // origin to the webViewRect origin.
        texture.applyInternalTransform(dc);
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glScalef(1f / this.webViewRect.width, 1f / this.webViewRect.height, 1f);
        gl.glTranslatef(this.screenRect.x - this.webViewRect.x, this.screenRect.y - this.webViewRect.y, 0f);

        // Restore the matrix mode.
        gl.glMatrixMode(GL.GL_MODELVIEW);

        return true;
    }

    protected void makeWebView(DrawContext dc, Dimension frameSize)
    {
        if (this.webView != null || this.webViewCreationFailed)
            return;

        try
        {
            // Attempt to get the WebViewFactory class name from configuration. Fall back on the BrowserBalloon's
            // default factory if the configuration does not specify a one.
            String className = Configuration.getStringValue(AVKey.WEB_VIEW_FACTORY, DEFAULT_WEB_VIEW_FACTORY);
            WebViewFactory factory = (WebViewFactory) WorldWind.createComponent(className);
            this.webView = factory.createWebView(frameSize);
        }
        catch (Throwable t)
        {
            String message = Logging.getMessage("WebView.ExceptionCreatingWebView", t);
            Logging.logger().severe(message);

            dc.addRenderingException(t);

            // Set flag to prevent retrying the web view creation. We assume that if this fails once it will continue to
            // fail.
            this.webViewCreationFailed = true;
        }

        // Configure the balloon to forward the WebView's property change events to its listeners, and to forward the
        // WebView's select events to the World Window's input handler. Configure the WebView with the appropriate
        // delegate owner to use for generated select events. We configure the delegate owner here to handle the case
        // when the balloon's delegate owner is set before the WebView is created.
        if (this.webView != null)
        {
            this.webView.addPropertyChangeListener(this);
            this.webView.addSelectListener(this.webViewSelectListener);
            this.webView.setDelegateOwner(this.getDelegateOwner() != null ? this.getDelegateOwner() : this);
        }
    }

    protected void disposeWebView()
    {
        if (this.webView == null)
            return;

        this.webView.removePropertyChangeListener(this);
        this.webView.removeSelectListener(this.webViewSelectListener);
        this.webView.dispose();
        this.webView = null;
    }

    /**
     * Draw pickable regions for the resize controls. A pickable region is drawn along the frame outline.
     *
     * @param dc Draw context.
     */
    protected void drawResizeControl(DrawContext dc)
    {
        // There is no visible control so only proceed in picking mode. We draw a pickable area on the balloon's frame.
        if (!dc.isPickingMode())
            return;

        GL gl = dc.getGL();

        Color color = dc.getUniquePickColor();
        gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

        // Compute the screen bounds in AWT coordinates (origin top left).
        Rectangle awtScreenRect = new Rectangle(this.screenRect.x,
            dc.getView().getViewport().height - this.screenRect.y - this.screenRect.height,
            this.screenRect.width, this.screenRect.height);

        // Set ACTION of PickedObject to RESIZE. Attach current bounds to the picked object so that the resize
        // controller will have enough information to interpret mouse drag events.
        PickedObject po = new PickedObject(color.getRGB(), this);
        po.setValue(AVKey.ACTION, AVKey.RESIZE);
        po.setValue(AVKey.BOUNDS, awtScreenRect);
        this.pickSupport.addPickableObject(po);

        gl.glLineWidth((float) this.getOutlinePickWidth());
        gl.glDrawArrays(GL.GL_LINE_STRIP, 0, this.frameInfo.vertexBuffer.remaining() / 2);
    }

    /**
     * Draw the control that closes the balloon. The picked object attached to the close control will have the key
     * {@link AVKey#ACTION} set to {@link AVKey#CLOSE}.
     *
     * @param dc Draw context.
     */
    protected void drawCloseControl(DrawContext dc)
    {
        GL gl = dc.getGL();
        try
        {
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPushMatrix();

            // Translate to the control offset
            Point2D offset = this.getCloseControlOffset().computeOffset(this.screenRect.width, this.screenRect.height,
                1d, 1d);
            gl.glTranslated(offset.getX(), offset.getY(), 0);

            // Draw the button outline
            this.drawControlButton(dc, this.getCloseControlSize(), AVKey.CLOSE);

            // Draw the X inside of the frame
            if (!dc.isPickingMode())
            {
                gl.glLineWidth(2f);

                int buttonWidth = this.getCloseControlSize().width;
                int buttonHeight = this.getCloseControlSize().height;

                float insetX = buttonWidth / 4f;
                float insetY = buttonHeight / 4f;

                gl.glBegin(GL.GL_LINES);
                gl.glVertex2f(insetX, buttonHeight - insetY);
                gl.glVertex2f(buttonWidth - insetX, insetY);
                gl.glVertex2f(insetX, insetY);
                gl.glVertex2f(buttonWidth - insetX, buttonHeight - insetY);
                gl.glEnd();
            }
        }
        finally
        {
            gl.glPopMatrix();
        }
    }

    /**
     * Draw the controls that navigates the browser back and forward. The picked objects attached to the back and
     * forward control will have the key {@link AVKey#ACTION} set to {@link AVKey#BACK} and {@link AVKey#FORWARD},
     * respectively.
     *
     * @param dc Draw context.
     */
    protected void drawNavigationControls(DrawContext dc)
    {
        this.drawBackControl(dc);
        this.drawForwardControl(dc);
    }

    /**
     * Draw the control that navigates the browser back. The picked object attached to the close control will have the
     * key {@link AVKey#ACTION} set to {@link AVKey#BACK}.
     *
     * @param dc Draw context.
     */
    protected void drawBackControl(DrawContext dc)
    {
        GL gl = dc.getGL();

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        try
        {
            // Translate to the the lower left corner of the control.
            Point2D offset = this.getBackControlOffset().computeOffset(this.screenRect.width, this.screenRect.height,
                1d, 1d);
            gl.glTranslated(offset.getX(), offset.getY(), 0);

            // Draw the control button's frame.
            this.drawControlButton(dc, this.getBackControlSize(), AVKey.BACK);

            // Draw a left pointing triangle for the back control.
            if (!dc.isPickingMode())
            {
                // Compute dimension for a triangle that is twice as tall as it is wide. This gives the triangle two
                // forty five degree angles and one ninety degree angle, and results in a nice looking triangle that can
                // be drawn without jagged edges. Leave 10% of the control size as an empty border around the triangle.
                float halfHeight = this.getBackControlSize().height * 0.7f / 2.0f;
                float width = this.getBackControlSize().width * 0.7f / 2.0f;

                // Translate to the midpoint of the triangle's base
                float adjustX = (this.getBackControlSize().width - width) / 2.0f;
                float adjustY = this.getBackControlSize().height / 2.0f;
                gl.glTranslated(this.getBackControlSize().width - adjustX, adjustY, 1.0);

                gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
                gl.glBegin(GL.GL_TRIANGLES);
                gl.glVertex2f(0, halfHeight);
                gl.glVertex2f(-width, 0);
                gl.glVertex2f(0, -halfHeight);
                gl.glEnd();
            }
        }
        finally
        {
            gl.glPopMatrix();
        }
    }

    /**
     * Draw the control that navigates the browser forward. The picked object attached to the close control will have
     * the key {@link AVKey#ACTION} set to {@link AVKey#FORWARD}.
     *
     * @param dc Draw context.
     */
    protected void drawForwardControl(DrawContext dc)
    {
        GL gl = dc.getGL();

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        try
        {
            // Translate to the the lower left corner of the control.
            Point2D offset = this.getForwardControlOffset().computeOffset(this.screenRect.width, this.screenRect.height,
                1d, 1d);
            gl.glTranslated(offset.getX(), offset.getY(), 0);

            // Draw the control button's frame.
            this.drawControlButton(dc, this.getForwardControlSize(), AVKey.FORWARD);

            // Draw a right pointing triangle for the forward control.
            if (!dc.isPickingMode())
            {
                // Compute dimension for a triangle that is twice as tall as it is wide. This gives the triangle two
                // forty five degree angles and one ninety degree angle, and results in a nice looking triangle that can
                // be drawn without jagged edges. Leave 10% of the control size as an empty border around the triangle.
                float halfHeight = this.getForwardControlSize().height * 0.7f / 2.0f;
                float width = this.getForwardControlSize().width * 0.7f / 2.0f;

                // Translate to the midpoint of the triangle's base
                float adjustX = (this.getForwardControlSize().width - width) / 2.0f;
                float adjustY = this.getForwardControlSize().height / 2.0f;
                gl.glTranslated(adjustX, adjustY, 1.0);

                gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
                gl.glBegin(GL.GL_TRIANGLES);
                gl.glVertex2f(0, halfHeight);
                gl.glVertex2f(0, -halfHeight);
                gl.glVertex2f(width, 0);
                gl.glEnd();
            }
        }
        finally
        {
            gl.glPopMatrix();
        }
    }

    /**
     * Draw the background for a button control. The button is drawn at the current origin.
     *
     * @param dc     Draw context.
     * @param size   Size of button to draw.
     * @param action Action to associate with the picked object for the button. This value will be attached to the
     *               picked object under the key {@link AVKey#ACTION}.
     */
    protected void drawControlButton(DrawContext dc, Dimension size, String action)
    {
        GL gl = dc.getGL();

        if (dc.isPickingMode())
        {
            Color color = dc.getUniquePickColor();
            gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

            // Set the ACTION key to represent the action associated with this control. Attach the balloon as the user
            // object instead of the delegate owner. This picked object is for use by the BalloonController, and the
            // controller needs the balloon. Set the HOT_SPOT key to specify that the standard button cursor should
            // display when the cursor is placed over the control button.
            PickedObject po = new PickedObject(color.getRGB(), this);
            po.setValue(AVKey.ACTION, action);
            po.setValue(AVKey.HOT_SPOT, new AbstractHotSpot()
            {
                @Override
                public Cursor getCursor()
                {
                    return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                }
            });
            this.pickSupport.addPickableObject(po);
        }

        // In picking mode, draw the button as a filled rectangle. Otherwise, just draw the outline.
        int mode = dc.isPickingMode() ? GL.GL_FILL : GL.GL_LINE;
        gl.glPolygonMode(GL.GL_FRONT, mode);
        gl.glLineWidth(1f);
        gl.glRecti(0, 0, size.width, size.height);
    }

    /** {@inheritDoc} */
    public void setActive(boolean active)
    {
        if (this.webView != null)
            this.webView.setActive(active);
    }

    /** {@inheritDoc} */
    public boolean isActive()
    {
        return (this.webView != null) && this.webView.isActive();
    }

    /**
     * Does nothing; BrowserBalloon does not handle select events.
     *
     * @param event The event to handle.
     */
    public void selected(SelectEvent event)
    {
    }

    /**
     * Forwards the key typed event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView} and
     * consumes the event. This consumes the event so the {@link gov.nasa.worldwind.View} doesn't respond to it.
     *
     * @param event The event to forward.
     */
    public void keyTyped(KeyEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
        event.consume(); // Consume the event so the View doesn't respond to it.
    }

    /**
     * Forwards the key pressed event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView} and
     * consumes the event. This consumes the event so the {@link gov.nasa.worldwind.View} doesn't respond to it. The
     *
     * @param event The event to forward.
     */
    public void keyPressed(KeyEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
        event.consume(); // Consume the event so the View doesn't respond to it.
    }

    /**
     * Forwards the key released event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView} and
     * consumes the event. This consumes the event so the {@link gov.nasa.worldwind.View} doesn't respond to it.
     *
     * @param event The event to forward.
     */
    public void keyReleased(KeyEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
        event.consume(); // Consume the event so the View doesn't respond to it.
    }

    /**
     * Forwards the mouse clicked event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView}. This
     * does not consume the event, because the {@link gov.nasa.worldwind.event.InputHandler} implements the policy for
     * consuming or forwarding mouse clicked events to other objects.
     *
     * @param event The event to forward.
     */
    public void mouseClicked(MouseEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
    }

    /**
     * Forwards the mouse pressed event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView}. This
     * does not consume the event, because the {@link gov.nasa.worldwind.event.InputHandler} implements the policy for
     * consuming or forwarding mouse pressed events to other objects.
     *
     * @param event The event to forward.
     */
    public void mousePressed(MouseEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
    }

    /**
     * Forwards the mouse released event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView}. This
     * does not consume the event, because the {@link gov.nasa.worldwind.event.InputHandler} implements the policy for
     * consuming or forwarding mouse released events to other objects.
     *
     * @param event The event to forward.
     */
    public void mouseReleased(MouseEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
    }

    /**
     * Does nothing; BrowserBalloon does not handle mouse entered events.
     *
     * @param event The event to handle.
     */
    public void mouseEntered(MouseEvent event)
    {
    }

    /**
     * Does nothing; BrowserBalloon does not handle mouse exited events.
     *
     * @param event The event to handle.
     */
    public void mouseExited(MouseEvent event)
    {
    }

    /**
     * Forwards the mouse dragged event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView}. This
     * does not consume the event, because the {@link gov.nasa.worldwind.event.InputHandler} implements the policy for
     * consuming or forwarding mouse dragged events to other objects.
     *
     * @param event The event to forward.
     */
    public void mouseDragged(MouseEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
    }

    /**
     * Forwards the mouse moved event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView}. This
     * does not consume the event, because the {@link gov.nasa.worldwind.event.InputHandler} implements the policy for
     * consuming or forwarding mouse moved events to other objects.
     *
     * @param event The event to forward.
     */
    public void mouseMoved(MouseEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
    }

    /**
     * Forwards the mouse wheel event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView} and
     * consumes the event. This consumes the event so the {@link gov.nasa.worldwind.View} doesn't respond to it.
     *
     * @param event The event to forward.
     */
    public void mouseWheelMoved(MouseWheelEvent event)
    {
        if (event == null)
            return;

        this.handleInputEvent(event);
        event.consume(); // Consume the event so the View doesn't respond to it.
    }

    /**
     * Returns a {@code null} Cursor, indicating the default cursor should be used when the BrowserBalloon is active.
     * The Cursor is set by the {@link gov.nasa.worldwind.util.webview.WebView} in response to mouse moved events.
     *
     * @return A {@code null} Cursor.
     */
    public Cursor getCursor()
    {
        return null;
    }

    /**
     * Sends the specified event to the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView}. If the event
     * is a {@link java.awt.event.MouseEvent}, the cursor point is converted from AWT coordinates to the WebView's local
     * coordinate system, and a copy of the event with the new point is sent to the WebView.
     * <p/>
     * If the event causes the WebView to navigate to another page, the WebView sends a SelectEvent to its listeners.
     * These events are then forwarded to {@link #handleWebViewSelectEvent(gov.nasa.worldwind.event.SelectEvent)}.
     * <p/>
     * This does nothing if the event is {@code null} or if the balloon's WebView is uninitialized.
     *
     * @param event The event to send.
     */
    protected void handleInputEvent(InputEvent event)
    {
        if (event == null)
            return;

        if (this.webView == null)
            return;

        if (event instanceof MouseEvent)
        {
            if (!this.intersectsWebView((MouseEvent) event))
                return;

            event = this.convertToWebView((MouseEvent) event);
        }

        this.webView.sendEvent(event);
    }

    /**
     * Forwards select events from the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView} to the {@link
     * gov.nasa.worldwind.event.InputHandler} associated with the {@link gov.nasa.worldwind.WorldWindow} that generated
     * the event. This logs a warning if the event did not originate from a WorldWindow.
     * <p/>
     * Consuming the specified event suppresses the WebView's default navigation behavior. If the event is not consumed
     * the WebView has navigated to the selected link and balloon's text is marked as invalid. The text is marked as
     * valid by a call to {@link #setText(String)}.
     *
     * @param event The event to handle.
     */
    protected void handleWebViewSelectEvent(SelectEvent event)
    {
        if (event == null)
            return;

        // Attach the balloon's context to the picked object to provide context for hyperlink events.
        if (event.getTopPickedObject() != null && this.hasKey(AVKey.CONTEXT))
        {
            event.getTopPickedObject().setValue(AVKey.CONTEXT, this.getValue(AVKey.CONTEXT));
        }

        if (event.getSource() instanceof WorldWindow && ((WorldWindow) event.getSource()).getInputHandler() != null)
        {
            ((WorldWindow) event.getSource()).getInputHandler().selected(event);
        }
        else
        {
            Logging.logger().warning(Logging.getMessage("generic.UnrecognizedEventSource"));
        }

        // If the event is not consumed then the WebView will navigate to the selected link. We mark the text as invalid
        // to denote that it does not represent the balloon's current content.
        if (!event.isConsumed())
        {
            this.textValid = false;
        }
    }

    /**
     * Determines whether the balloon's internal {@link gov.nasa.worldwind.util.webview.WebView} intersects the mouse
     * event's screen point.
     *
     * @param e The event to test.
     *
     * @return {@code true} if the WebView intersects the mouse event's point, otherwise {@code false}.
     */
    protected boolean intersectsWebView(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();

        // Translate AWT coordinates to OpenGL screen coordinates by moving the Y origin from the upper left corner to
        // the lower left corner and flipping the direction of the Y axis.
        if (e.getSource() instanceof Component)
        {
            y = ((Component) e.getSource()).getHeight() - e.getY();
        }

        return this.webViewRect.contains(x, y);
    }

    /**
     * Converts the specified mouse event's screen point from AWT coordinates to local WebView coordinates, and returns
     * a new event who's screen point is in WebView local coordinates.
     *
     * @param e The event to convert.
     *
     * @return A new mouse event in the WebView's local coordinate system.
     */
    protected MouseEvent convertToWebView(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();

        // Translate AWT coordinates to OpenGL screen coordinates by moving the Y origin from the upper left corner to
        // the lower left corner and flipping the direction of the Y axis.
        if (e.getSource() instanceof Component)
        {
            y = ((Component) e.getSource()).getHeight() - e.getY();
        }

        x -= this.webViewRect.x;
        y -= this.webViewRect.y;

        if (e instanceof MouseWheelEvent)
        {
            return new MouseWheelEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), x, y,
                e.getClickCount(), e.isPopupTrigger(), ((MouseWheelEvent) e).getScrollType(),
                ((MouseWheelEvent) e).getScrollAmount(), ((MouseWheelEvent) e).getWheelRotation());
        }
        else
        {
            return new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), x, y,
                e.getClickCount(), e.isPopupTrigger(), e.getButton());
        }
    }
}
