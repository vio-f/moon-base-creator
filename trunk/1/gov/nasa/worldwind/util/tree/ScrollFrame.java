/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.tree;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.TextureCoords;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.net.URL;
import java.nio.DoubleBuffer;

/**
 * A frame that can scroll its contents.
 *
 * @author pabercrombie
 * @version $Id: ScrollFrame.java 14748 2011-02-17 08:51:48Z pabercrombie $
 */
public class ScrollFrame extends DragControl implements Renderable
{
    protected FrameAttributes normalAttributes;
    protected FrameAttributes highlightAttributes;
    protected FrameAttributes activeAttributes = new BasicFrameAttributes(); // re-determined each frame

    protected String frameTitle;

    protected Scrollable contents;

    protected Offset screenLocation;

    protected Insets insets = new Insets(0, 2, 2, 2);
    protected int titleBarHeight = 25;
    protected int scrollBarSize = 15;
    protected int frameBorder = 3;
    protected int frameLineWidth = 1;
    /** Space in pixels between the title bar icon and the left edge of the frame. */
    protected int iconInset = 5;

    protected OGLStackHandler BEogsh = new OGLStackHandler();

    protected ScrollBar verticalScrollBar;
    protected ScrollBar horizontalScrollBar;

    protected PickSupport pickSupport = new PickSupport();

    protected boolean minimized = false;

    protected Size size = Size.fromPixels(250, 300);

    /** Image source for the icon drawn in the upper left corner of the frame. */
    protected Object iconImageSource;
    /** Texture loaded from {@link #iconImageSource}. */
    protected BasicWWTexture texture;

    /** An animation to play when the frame is minimized or maximized. */
    protected Animation minimizeAnimation;
    /** The active animation that is currently playing. */
    protected Animation animation;
    /** Delay in milliseconds between frames of an animation. */
    protected int animationDelay = 5;

    // UI controls
    protected HotSpot minimizeButton;
    protected FrameResizeControl frameResizeControl;
    protected int resizePickWidth = 20;

    /** The balloon geometry vertices passed to OpenGL. */
    protected DoubleBuffer vertexBuffer;

    // Computed each frame
    protected long frameNumber = -1;
    protected boolean mustRecomputeFrameGeometry = true;
    protected Point2D screenPoint;
    protected Rectangle frameBounds;     // Bounds of the full frame
    protected Rectangle innerBounds;     // Bounds of the frame inside the frame border
    protected Rectangle contentBounds;   // Bounds of the visible portion of the tree
    protected Rectangle treeContentBounds;
    protected Dimension contentSize;     // Size of the scroll frame contents
    protected Dimension frameSize;
    protected boolean highlighted;
    protected boolean showVerticalScrollbar;
    protected boolean showHorizontalScrollbar;

    /** The attributes used if attributes are not specified. */
    protected static final FrameAttributes defaultAttributes;

    static
    {
        defaultAttributes = new BasicFrameAttributes();
    }

    public ScrollFrame()
    {
        super(null);
        this.initializeUIControls();
    }

    public ScrollFrame(int x, int y)
    {
        this(new Offset((double) x, (double) y, AVKey.PIXELS, AVKey.PIXELS));
    }

    public ScrollFrame(Offset screenLocation)
    {
        super(null);
        this.setScreenLocation(screenLocation);
        this.initializeUIControls();
    }

    public Scrollable getContents()
    {
        return contents;
    }

    public void setContents(Scrollable contents)
    {
        this.contents = contents;
    }

    protected void initializeUIControls()
    {
        this.minimizeAnimation = new WindowShadeAnimation(this);
        this.frameResizeControl = new FrameResizeControl(this);

        this.minimizeButton = new TreeHotSpot(this)
        {
            public void selected(SelectEvent event)
            {
                if (!event.isConsumed() && event.isLeftClick())
                {
                    ScrollFrame.this.setMinimized(!ScrollFrame.this.isMinimized());
                    event.consume();
                }
            }
        };

        this.verticalScrollBar = new ScrollBar(this, AVKey.VERTICAL);
        this.horizontalScrollBar = new ScrollBar(this, AVKey.HORIZONTAL);
    }

    /**
     * Get the bounds of the tree frame.
     *
     * @param dc Draw context
     *
     * @return The bounds of the tree frame on screen, in screen coordinates (origin at upper left).
     */
    public Rectangle getBounds(DrawContext dc)
    {
        this.computeBounds(dc);

        return new Rectangle((int) this.screenPoint.getX(), (int) this.screenPoint.getY(), this.frameSize.width,
            this.frameSize.height);
    }

    public Rectangle getVisibleBounds()
    {
        return this.contentBounds;
    }

    public void render(DrawContext dc)
    {
        Offset screenLocation = this.getScreenLocation();
        if (screenLocation == null)
            return;

        if (dc.getFrameTimeStamp() != this.frameNumber)
        {
            this.determineActiveAttributes();
            this.computeBounds(dc);

            Point pickPoint = dc.getPickPoint();
            if (pickPoint != null)
                this.setHighlighted(this.getBounds(dc).contains(pickPoint));

            this.frameNumber = dc.getFrameTimeStamp();
        }

        if (this.mustRecomputeFrameGeometry)
        {
            this.computeFrameGeometry();
            this.mustRecomputeFrameGeometry = false;
        }

        if (this.intersectsFrustum(dc))
        {
            try
            {
                this.beginDrawing(dc);

                // While the tree is animated toward a minimized state, draw it as if it were maximized,
                // with the contents and scrollbars
                if (this.isDrawMinimized())
                    this.drawMinimized(dc);
                else
                    this.drawMaximized(dc);
            }
            finally
            {
                this.endDrawing(dc);
            }
        }
    }

    /**
     * Determines whether the frame intersects the view frustum.
     *
     * @param dc the current draw context.
     *
     * @return {@code true} If the frame intersects the frustum, otherwise {@code false}.
     */
    protected boolean intersectsFrustum(DrawContext dc)
    {
        if (dc.isPickingMode())
            return dc.getPickFrustums().intersectsAny(this.frameBounds);
        else
            return dc.getView().getViewport().intersects(this.frameBounds);
    }

    protected void stepAnimation(DrawContext dc)
    {
        if (this.isAnimating())
        {
            this.animation.step();

            if (this.animation.hasNext())
                dc.setRedrawRequested(this.animationDelay);
            else
                this.animation = null;
        }
    }

    /**
     * Compute the bounds of the content frame, if the bounds have not already been computed in this frame.
     *
     * @param dc Draw context.
     */
    protected void computeBounds(DrawContext dc)
    {
        if (dc.getFrameTimeStamp() == this.frameNumber)
            return;

        this.stepAnimation(dc);

        Rectangle viewport = dc.getView().getViewport();

        this.screenPoint = this.screenLocation.computeOffset(viewport.width, viewport.height, 1.0, 1.0);

        this.contentSize = this.contents.getSize(dc);

        // Compute point in OpenGL coordinates
        Point upperLeft = new Point((int) this.screenPoint.getX(), (int) (viewport.height - this.screenPoint.getY()));

        this.frameSize = this.getSize().compute(viewport.width, viewport.height, this.contentSize.width,
            this.contentSize.height);

        this.frameBounds = new Rectangle(upperLeft.x, upperLeft.y - frameSize.height, frameSize.width,
            frameSize.height);
        this.innerBounds = new Rectangle(upperLeft.x + this.frameBorder,
            upperLeft.y - frameSize.height + this.frameBorder, frameSize.width - this.frameBorder * 2,
            frameSize.height - this.frameBorder * 2);

        // Try laying out the frame without scroll bars
        this.contentBounds = this.computeBounds(false, false);

        // Add a little extra space to the scroll bar max value to allow the tree to be scrolled a little
        // bit further than its height. This avoids chopping off the bottom of descending characters because the
        // text is too close to the scissor box.
        final int scrollPadding = 10;

        this.showVerticalScrollbar = this.mustShowVerticalScrollbar();
        this.showHorizontalScrollbar = this.mustShowHorizontalScrollbar();

        // If we need a scroll bar, compute the bounds again to take into account the space occupied by the scroll bar.
        if (this.showHorizontalScrollbar || this.showVerticalScrollbar)
            this.contentBounds = this.computeBounds(this.showVerticalScrollbar, this.showHorizontalScrollbar);

        this.verticalScrollBar.setMaxValue(this.contentSize.height + scrollPadding);
        this.verticalScrollBar.setExtent(this.contentBounds.height);

        this.horizontalScrollBar.setMaxValue(this.contentSize.width + scrollPadding);
        this.horizontalScrollBar.setExtent(this.contentBounds.width);

        this.treeContentBounds = new Rectangle(this.contentBounds);
        this.treeContentBounds.x -= this.horizontalScrollBar.getValue();
        this.treeContentBounds.y += this.verticalScrollBar.getValue();
    }

    /**
     * Determine if the vertical scrollbar should be displayed.
     *
     * @return {@code true} if the vertical scrollbar should be displayed, otherwise {@code false}.
     */
    protected boolean mustShowVerticalScrollbar()
    {
        // If the frame is not minimized or in the middle of an animation, compare the content size to the visible
        // bounds.
        if ((!this.isMinimized() && !this.isAnimating()))
        {
            return this.contentSize.height > this.contentBounds.height;
        }
        else
        {
            // Otherwise, return the previous scrollbar setting, do not recompute it. While the frame is animating, we want
            // the scrollbar decision to be based on its maximized size. If the frame would have scrollbars when maximized will
            // have scrollbars while it animates, but a frame that would not have scrollbars when maximized will not have
            // scrollbars while animating.
            return this.showVerticalScrollbar;
        }
    }

    /**
     * Determine if the horizontal scrollbar should be displayed.
     *
     * @return {@code true} if the horizontal scrollbar should be displayed, otherwise {@code false}.
     */
    protected boolean mustShowHorizontalScrollbar()
    {
        return this.contentSize.width > this.contentBounds.width;
    }

    /**
     * Determines if the frame is currently animating.
     *
     * @return {@code true} if an animation is in progress, otherwise {@code false}.
     */
    protected boolean isAnimating()
    {
        return this.animation != null;
    }

    /**
     * Compute the content bounds, taking into account the frame size and the presence of scroll bars.
     *
     * @param showVerticalScrollBar   True if the frame will have a vertical scroll bar. A vertical scroll bar will make
     *                                the content frame narrower.
     * @param showHorizontalScrollBar True if the frame will have a horizontal scroll bar. A horizontal scroll bar will
     *                                make the content frame shorter.
     *
     * @return The bounds of the content frame.
     */
    protected Rectangle computeBounds(boolean showVerticalScrollBar, boolean showHorizontalScrollBar)
    {
        int hScrollBarSize = (showHorizontalScrollBar ? this.scrollBarSize : 0);
        int vScrollBarSize = (showVerticalScrollBar ? this.scrollBarSize : 0);

        int titleBarHeight = this.getActiveAttributes().isDrawTitleBar() ? this.titleBarHeight : 0;

        return new Rectangle(this.innerBounds.x + this.insets.left,
            this.innerBounds.y + this.insets.bottom + hScrollBarSize,
            this.innerBounds.width - this.insets.right - this.insets.left - vScrollBarSize,
            this.innerBounds.height - this.insets.bottom - this.insets.top - titleBarHeight - hScrollBarSize);
    }

    /** Updates the frame's screen-coordinate geometry in {@link #vertexBuffer} according to the current screen bounds. */
    protected void computeFrameGeometry()
    {
        if (this.frameBounds == null)
            return;

        FrameAttributes attributes = this.getActiveAttributes();

        this.vertexBuffer = FrameFactory.createShapeBuffer(FrameFactory.SHAPE_RECTANGLE, this.frameBounds.width,
            this.frameBounds.height, attributes.getCornerRadius(), this.vertexBuffer);
    }

    /**
     * Get the smallest dimension that the frame can draw itself. This user is not allowed to resize the frame to be
     * smaller than this dimension.
     *
     * @return The frame's minimum size.
     */
    protected Dimension getMinimumSize()
    {
        // Reserve enough space to draw the border, both scroll bars, and the title bar
        int minWidth = this.frameBorder * 2 + this.scrollBarSize * 3; // left scroll arrow + right + vertical scroll bar
        int minHeight = this.frameBorder * 2 + this.scrollBarSize * 3
            + this.titleBarHeight; // Up arrow + down arrow + horizontal scroll bar
        return new Dimension(minWidth, minHeight);
    }

    /**
     * Determines if the frame should draw in its minimized form.
     *
     * @return {@code true} if the frame should draw minimized, otherwise {@code false}.
     */
    protected boolean isDrawMinimized()
    {
        // Draw minimized when the frame is minimized, but not while animating toward the minimized state
        return this.isMinimized() && !this.isAnimating();
    }

    protected void drawMaximized(DrawContext dc)
    {
        this.drawFrame(dc);

        GL gl = dc.getGL();
        gl.glEnable(GL.GL_SCISSOR_TEST);
        gl.glScissor(this.contentBounds.x, this.contentBounds.y, this.contentBounds.width, this.contentBounds.height);

        this.contents.renderScrollable(dc, this.treeContentBounds);
    }

    protected void drawMinimized(DrawContext dc)
    {
        GL gl = dc.getGL();

        OGLStackHandler oglStack = new OGLStackHandler();
        try
        {
            oglStack.pushModelviewIdentity(gl);

            FrameAttributes attributes = this.getActiveAttributes();

            gl.glTranslated(this.frameBounds.x, this.frameBounds.y, 0.0);

            if (!dc.isPickingMode())
            {
                Color[] color = attributes.getBackgroundColor();

                try
                {
                    gl.glEnable(GL.GL_LINE_SMOOTH);

                    OGLUtil.applyColor(gl, color[0], 1.0, false);
                    FrameFactory.drawBuffer(dc, GL.GL_LINE_STRIP, this.vertexBuffer);
                }
                finally
                {
                    gl.glDisable(GL.GL_LINE_SMOOTH);
                }

                gl.glLoadIdentity();
                gl.glTranslated(this.innerBounds.x, this.innerBounds.y, 0.0); // Translate back to inner frame

                TreeUtil.drawRectWithGradient(gl, new Rectangle(0, 0, this.innerBounds.width, this.innerBounds.height),
                    color[0], color[1], attributes.getBackgroundOpacity(), AVKey.VERTICAL);
            }
            else
            {
                Color color = dc.getUniquePickColor();
                int colorCode = color.getRGB();
                this.pickSupport.addPickableObject(colorCode, this, null, false);
                gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

                gl.glScaled(this.frameSize.width, this.frameSize.height, 1d);
                dc.drawUnitQuad();
            }

            // Draw title bar
            if (attributes.isDrawTitleBar())
            {
                gl.glLoadIdentity();
                gl.glTranslated(this.innerBounds.x, this.innerBounds.y + this.innerBounds.height - this.titleBarHeight,
                    0);
                this.drawTitleBar(dc);
            }
        }
        finally
        {
            oglStack.pop(gl);
        }
    }

    protected void drawFrame(DrawContext dc)
    {
        GL gl = dc.getGL();

        OGLStackHandler oglStack = new OGLStackHandler();
        try
        {
            oglStack.pushModelviewIdentity(gl);

            FrameAttributes attributes = this.getActiveAttributes();

            gl.glTranslated(this.frameBounds.x, this.frameBounds.y, 0.0);

            if (!dc.isPickingMode())
            {
                Color[] color = attributes.getBackgroundColor();

                try
                {
                    gl.glEnable(GL.GL_LINE_SMOOTH);

                    OGLUtil.applyColor(gl, color[0], 1.0, false);
                    gl.glLineWidth(this.frameLineWidth);
                    FrameFactory.drawBuffer(dc, GL.GL_LINE_STRIP, this.vertexBuffer);
                }
                finally {
                    gl.glDisable(GL.GL_LINE_SMOOTH);
                }

                gl.glLoadIdentity();
                gl.glTranslated(this.innerBounds.x, this.innerBounds.y, 0.0); // Translate back inner frame

                TreeUtil.drawRectWithGradient(gl, new Rectangle(0, 0, this.innerBounds.width, this.innerBounds.height),
                    color[0], color[1], attributes.getBackgroundOpacity(), AVKey.VERTICAL);
            }
            else
            {
                int frameHeight = this.frameBounds.height;
                int frameWidth = this.frameBounds.width;

                // Draw draggable frame
                TreeUtil.drawPickableRect(dc, this.pickSupport, this, new Rectangle(0, 0, frameWidth, frameHeight));

                if (attributes.isEnableResizeControl())
                {
                    Color color = dc.getUniquePickColor();
                    int colorCode = color.getRGB();
                    this.pickSupport.addPickableObject(colorCode, this.frameResizeControl);
                    gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

                    // Draw the resize control as a pickable line on the frame border
                    gl.glLineWidth(this.resizePickWidth);
                    FrameFactory.drawBuffer(dc, GL.GL_LINE_STRIP, this.vertexBuffer);
                }
            }

            // Draw a vertical scroll bar if the tree extends beyond the visible bounds
            if (this.showVerticalScrollbar)
            {
                int x1 = this.innerBounds.width - this.insets.right - this.scrollBarSize;
                int y1 = this.insets.bottom;
                if (this.showHorizontalScrollbar)
                    y1 += this.scrollBarSize;

                Rectangle scrollBarBounds = new Rectangle(x1, y1, this.scrollBarSize, this.contentBounds.height);

                this.verticalScrollBar.setBounds(scrollBarBounds);
                this.verticalScrollBar.render(dc);
            }

            // Draw a horizontal scroll bar if the tree extends beyond the visible bounds
            if (this.showHorizontalScrollbar)
            {
                int x1 = this.insets.right;
                int y1 = this.insets.bottom;

                Rectangle scrollBarBounds = new Rectangle(x1, y1, this.contentBounds.width, this.scrollBarSize);

                this.horizontalScrollBar.setBounds(scrollBarBounds);
                this.horizontalScrollBar.render(dc);
            }

            // Draw title bar
            if (attributes.isDrawTitleBar())
            {
                gl.glTranslated(0, this.innerBounds.height - this.titleBarHeight, 0);
                this.drawTitleBar(dc);
            }
        }
        finally
        {
            oglStack.pop(gl);
        }
    }

    /**
     * Draw the title bar.
     *
     * @param dc Draw context
     */
    protected void drawTitleBar(DrawContext dc)
    {
        GL gl = dc.getGL();

        FrameAttributes attributes = this.getActiveAttributes();

        if (!dc.isPickingMode())
        {
            // Draw title bar as a rectangle with gradient
            Color[] color = attributes.getTitleBarColor();
            TreeUtil.drawRectWithGradient(gl, new Rectangle(0, 0, this.innerBounds.width, this.getTitleBarHeight()),
                color[0], color[1], attributes.getBackgroundOpacity(), AVKey.VERTICAL);

            OGLUtil.applyColor(gl, attributes.getForegroundColor(), 1.0, false);

            if (!this.isDrawMinimized())
            {
                // Draw a line to separate the title bar from the frame
                gl.glBegin(GL.GL_LINES);
                gl.glVertex2f(0, 0);
                gl.glVertex2f(this.innerBounds.width, 0);
                gl.glEnd();
            }

            Point drawPoint = new Point(0, 0);

            this.drawIcon(dc, drawPoint);
            this.drawTitleText(dc, drawPoint);
        }

        this.drawMinimizeButton(dc);
    }

    /**
     * Draw an icon in the upper left corner of the title bar. This method takes a point relative to lower left corner
     * of the title bar. This point is modified to indicate how much horizontal space is consumed by the icon.
     *
     * @param dc        Draw context
     * @param drawPoint Point at which to draw the icon. This point is relative to the lower left corner of the title
     *                  bar. This point will be modified to indicate how much horizontal space was consumed by drawing
     *                  the icon. After drawing the icon, the x value with point to the first available space to the
     *                  right of the icon.
     */
    protected void drawIcon(DrawContext dc, Point drawPoint)
    {
        // This method is never called during picked, so picking mode is not handled here

        GL gl = dc.getGL();
        FrameAttributes attributes = this.getActiveAttributes();

        // Draw icon in upper left corner
        BasicWWTexture texture = this.getTexture();
        if (texture == null)
        {
            drawPoint.x += this.iconInset;
            return;
        }

        OGLStackHandler oglStack = new OGLStackHandler();
        try
        {
            if (texture.bind(dc))
            {
                gl.glEnable(GL.GL_TEXTURE_2D);

                Dimension iconSize = attributes.getIconSize();

                oglStack.pushModelview(gl);

                gl.glColor4d(1.0, 1.0, 1.0, 1.0);

                double vertAdjust = (this.titleBarHeight - iconSize.height) / 2;
                TextureCoords texCoords = texture.getTexCoords();
                gl.glTranslated(drawPoint.x + this.iconInset, drawPoint.y + vertAdjust + 1, 1.0);
                gl.glScaled((double) iconSize.width, (double) iconSize.height, 1d);
                dc.drawUnitQuad(texCoords);

                drawPoint.x += iconSize.getWidth() + attributes.getIconSpace() + this.iconInset;
            }
        }
        finally
        {
            gl.glDisable(GL.GL_TEXTURE_2D);
            oglStack.pop(gl);
        }
    }

    /**
     * Draw text in the frame title.
     *
     * @param dc        Draw context
     * @param drawPoint Point at which to draw text. This point is relative to the lower left corner of the title bar.
     */
    protected void drawTitleText(DrawContext dc, Point drawPoint)
    {
        // This method is never called during picked, so picking mode is not handled here

        GL gl = dc.getGL();
        FrameAttributes attributes = this.getActiveAttributes();

        // Draw text title
        String frameTitle = this.getFrameTitle();
        if (frameTitle == null)
            return;

        TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(),
            attributes.getFont());

        Rectangle2D textSize = textRenderer.getBounds(frameTitle);

        try
        {
            textRenderer.begin3DRendering();
            OGLUtil.applyColor(gl, Color.WHITE, 1.0, false);

            double vertAdjust = (this.titleBarHeight - Math.abs(textSize.getY())) / 2;
            textRenderer.draw(frameTitle, drawPoint.x, (int) (drawPoint.y + vertAdjust) + 1);
        }
        finally
        {
            textRenderer.end3DRendering();
        }
    }

    protected void drawMinimizeButton(DrawContext dc)
    {
        GL gl = dc.getGL();

        OGLStackHandler oglStack = new OGLStackHandler();
        try
        {
            oglStack.pushModelviewIdentity(gl);

            float buttonSize = this.scrollBarSize;

            gl.glTranslated(this.innerBounds.x + this.innerBounds.width - this.insets.left - buttonSize,
                this.innerBounds.y + this.innerBounds.height - (this.titleBarHeight - buttonSize) / 2 - buttonSize,
                1.0);

            if (!dc.isPickingMode())
            {
                Color color = this.getActiveAttributes().getMinimizeButtonColor();

                FrameAttributes attributes = this.getActiveAttributes();
                gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
                OGLUtil.applyColor(gl, color, attributes.getForegroundOpacity(), false);
                gl.glRectf(0, 0, buttonSize, buttonSize);

                gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
                OGLUtil.applyColor(gl, attributes.getForegroundColor(), false);

                // Adjust by half a pixel to color the center of each pixel
                gl.glRectf(0f, 0f, buttonSize - 0.5f, buttonSize - 0.5f);

                gl.glBegin(GL.GL_LINES);
                gl.glVertex2f(buttonSize / 4 - 0.5f, buttonSize / 2 - 0.5f);
                gl.glVertex2f(buttonSize - buttonSize / 4 - 0.5f, buttonSize / 2 - 0.5f);
                gl.glEnd();
            }
            else
            {
                Color color = dc.getUniquePickColor();
                int colorCode = color.getRGB();

                this.pickSupport.addPickableObject(colorCode, this.minimizeButton, null, false);
                gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

                gl.glScaled(buttonSize, buttonSize, 1d);
                dc.drawUnitQuad();
            }
        }
        finally
        {
            oglStack.pop(gl);
        }
    }

    protected void beginDrawing(DrawContext dc)
    {
        GL gl = dc.getGL();
        GLU glu = dc.getGLU();

        this.BEogsh.pushAttrib(gl, GL.GL_DEPTH_BUFFER_BIT
            | GL.GL_COLOR_BUFFER_BIT
            | GL.GL_ENABLE_BIT
            | GL.GL_CURRENT_BIT
            | GL.GL_POLYGON_BIT
            | GL.GL_LINE_BIT        // For line width
            | GL.GL_TRANSFORM_BIT
            | GL.GL_SCISSOR_BIT);

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL.GL_DEPTH_TEST);

        // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
        // into the GL projection matrix.
        this.BEogsh.pushProjectionIdentity(gl);

        java.awt.Rectangle viewport = dc.getView().getViewport();

        glu.gluOrtho2D(0d, viewport.width, 0d, viewport.height);
        this.BEogsh.pushModelviewIdentity(gl);

        if (dc.isPickingMode())
        {
            this.pickSupport.clearPickList();
            this.pickSupport.beginPicking(dc);
        }
    }

    protected void endDrawing(DrawContext dc)
    {
        if (dc.isPickingMode())
        {
            this.pickSupport.endPicking(dc);
            this.pickSupport.resolvePick(dc, dc.getPickPoint(), dc.getCurrentLayer());
        }

        GL gl = dc.getGL();
        this.BEogsh.pop(gl);
    }

    /**
     * Get the location of the upper left corner of the tree, measured in screen coordinates with the origin at the
     * upper left corner of the screen.
     *
     * @return Screen location, measured in pixels from the upper left corner of the screen.
     */
    public Offset getScreenLocation()
    {
        return this.screenLocation;
    }

    /**
     * Set the location of the upper left corner of the tree, measured in screen coordinates with the origin at the
     * upper left corner of the screen.
     *
     * @param screenLocation New screen location.
     */
    public void setScreenLocation(Offset screenLocation)
    {
        this.screenLocation = screenLocation;
    }

    /**
     * Get the location of the upper left corner of the frame, measured from the upper left corner of the screen.
     *
     * @return The location of the upper left corner of the frame. This method will return null until the has been
     *         rendered.
     */
    protected Point2D getScreenPoint()
    {
        return this.screenPoint;
    }

    public FrameAttributes getAttributes()
    {
        return this.normalAttributes;
    }

    public void setAttributes(FrameAttributes attributes)
    {
        if (attributes == null)
        {
            String msg = Logging.getMessage("nullValue.AttributesIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.normalAttributes = attributes;
    }

    public FrameAttributes getHighlightAttributes()
    {
        return this.highlightAttributes;
    }

    public void setHighlightAttributes(FrameAttributes attributes)
    {
        if (attributes == null)
        {
            String msg = Logging.getMessage("nullValue.AttributesIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.highlightAttributes = attributes;
    }

    protected FrameAttributes getActiveAttributes()
    {
        return this.activeAttributes;
    }

    /** Determines which attributes -- normal, highlight or default -- to use each frame. */
    protected void determineActiveAttributes()
    {
        if (this.isHighlighted())
        {
            if (this.getHighlightAttributes() != null)
                this.activeAttributes.copy(this.getHighlightAttributes());
            else
            {
                // If no highlight attributes have been specified we will use the normal attributes.
                if (this.getAttributes() != null)
                    this.activeAttributes.copy(this.getAttributes());
                else
                    this.activeAttributes.copy(defaultAttributes);
            }
        }
        else if (this.getAttributes() != null)
        {
            this.activeAttributes.copy(this.getAttributes());
        }
        else
        {
            this.activeAttributes.copy(defaultAttributes);
        }
    }

    public boolean isMinimized()
    {
        return this.minimized;
    }

    public void setMinimized(boolean minimized)
    {
        if (minimized != this.isMinimized())
        {
            this.minimized = minimized;
            if (this.minimizeAnimation != null)
            {
                this.animation = this.minimizeAnimation;
                this.animation.reset();
            }
        }
    }

    public boolean isHighlighted()
    {
        return this.highlighted;
    }

    public void setHighlighted(boolean highlighted)
    {
        if (this.highlighted != highlighted)
        {
            this.highlighted = highlighted;

            this.contents.setHighlighted(highlighted);

            FrameAttributes attrs = this.getActiveAttributes();
            this.verticalScrollBar.setLineColor(attrs.getForegroundColor());
            this.verticalScrollBar.setOpacity(attrs.getForegroundOpacity());
            this.horizontalScrollBar.setLineColor(attrs.getForegroundColor());
            this.horizontalScrollBar.setOpacity(attrs.getForegroundOpacity());
        }
    }

    /**
     * Get the title of the tree frame.
     *
     * @return The frame title.
     *
     * @see #setFrameTitle(String)
     */
    public String getFrameTitle()
    {
        return this.frameTitle;
    }

    /**
     * Set the title of the tree frame.
     *
     * @param frameTitle New frame title.
     *
     * @see #getFrameTitle()
     */
    public void setFrameTitle(String frameTitle)
    {
        this.frameTitle = frameTitle;
    }

    /**
     * Get the current size of the tree frame. This size may be different than the normal size of the frame returned by
     * {!link #getSize()} if the frame is minimized or animating between maximized and minimized states.
     *
     * @return the
     *
     * @see #setSize(gov.nasa.worldwind.render.Size)
     */
    public Size getSize()
    {
        return this.size;
    }

    /**
     * Set the size of the frame.
     *
     * @param size New size.
     */
    public void setSize(Size size)
    {
        if (size == null)
        {
            String message = Logging.getMessage("nullValue.SizeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.size = size;
        this.mustRecomputeFrameGeometry = true;
    }

    /**
     * Return the amount of screen that space that the frame is currently using. The frame size may change due to a
     * window resize, or an animation.
     *
     * @return The size of the frame on screen, in pixels. This method will return null until the frame has been
     *         rendered at least once.
     */
    public Dimension getCurrentSize()
    {
        return this.frameSize;
    }

    public int getTitleBarHeight()
    {
        return this.titleBarHeight;
    }

    public void setTitleBarHeight(int titleBarHeight)
    {
        this.titleBarHeight = titleBarHeight;
    }

    /**
     * Get the insets that seperate the frame contents from the frame.
     *
     * @return Active insets.
     */
    public Insets getInsets()
    {
        return this.insets;
    }

    /**
     * Set the frame insets. This is the amount of space between the frame and its contents.
     *
     * @param insets New insets.
     */
    public void setInsets(Insets insets)
    {
        if (insets == null)
        {
            String message = Logging.getMessage("nullValue.InsetsIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        this.insets = insets;
    }

    /**
     * Get the animation that is played when the tree frame is minimized.
     *
     * @return Animation played when the frame is minimized.
     *
     * @see #setMinimizeAnimation(Animation)
     */
    public Animation getMinimizeAnimation()
    {
        return minimizeAnimation;
    }

    /**
     * Set the animation that is played when the tree frame is minimized.
     *
     * @param minimizeAnimation New minimize animation.
     *
     * @see #getMinimizeAnimation()
     */
    public void setMinimizeAnimation(Animation minimizeAnimation)
    {
        this.minimizeAnimation = minimizeAnimation;
    }

    /**
     * Get the image source for the frame icon.
     *
     * @return The icon image source, or null if no image source has been set.
     *
     * @see #setIconImageSource(Object)
     */
    public Object getIconImageSource()
    {
        return this.iconImageSource;
    }

    /**
     * Set the image source of the frame icon. This icon is drawn in the upper right hand corner of the tree frame.
     *
     * @param imageSource New image source. May be a String, URL, or BufferedImage.
     */
    public void setIconImageSource(Object imageSource)
    {
        this.iconImageSource = imageSource;
    }

    /**
     * Get the texture loaded for the icon image source. If the texture has not been loaded, this method will attempt to
     * load it in the background.
     *
     * @return The icon texture, or no image source has been set, or if the icon has not been loaded yet.
     */
    protected BasicWWTexture getTexture()
    {
        if (this.texture != null)
            return this.texture;
        else
            return this.initializeTexture();
    }

    /**
     * Create and initialize the texture from the image source. If the image is not in memory this method will request
     * that it be loaded and return null.
     *
     * @return The texture, or null if the texture is not yet available.
     */
    protected BasicWWTexture initializeTexture()
    {
        Object imageSource = this.getIconImageSource();
        if (imageSource instanceof String || imageSource instanceof URL)
        {
            URL imageURL = WorldWind.getDataFileStore().requestFile(imageSource.toString());
            if (imageURL != null)
            {
                this.texture = new BasicWWTexture(imageURL, true);
            }
        }
        else if (imageSource != null)
        {
            this.texture = new BasicWWTexture(imageSource, true);
            return this.texture;
        }

        return null;
    }

    /**
     * Get a reference to one of the frame's scroll bars.
     *
     * @param direction Determines which scroll bar to get. Either {@link AVKey#VERTICAL} or {@link AVKey#HORIZONTAL}.
     *
     * @return The horizontal or vertical scroll bar.
     */
    public ScrollBar getScrollBar(String direction)
    {
        if (AVKey.VERTICAL.equals(direction))
            return this.verticalScrollBar;
        else
            return this.horizontalScrollBar;
    }

    @Override
    protected void beginDrag(Point point)
    {
        if (this.getActiveAttributes().isEnableMove())
        {
            Point2D location = this.screenPoint;
            this.dragRefPoint = new Point((int) location.getX() - point.x, (int) location.getY() - point.y);
        }
    }

    public void drag(Point point)
    {
        if (this.getActiveAttributes().isEnableMove())
        {
            double x = point.x + this.dragRefPoint.x;
            double y = point.y + this.dragRefPoint.y;
            this.setScreenLocation(new Offset(x, y, AVKey.PIXELS, AVKey.PIXELS));
        }
    }

    @Override
    public void selected(SelectEvent event)
    {
        if (event.isConsumed())
            return;

        super.selected(event);

        if (event.isLeftDoubleClick())
        {
            this.setMinimized(!this.isMinimized());
            event.consume();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (!e.isConsumed())
        {
            this.verticalScrollBar.scroll(e.getUnitsToScroll());
            e.consume();

            // Fire a property change to trigger a repaint
            this.firePropertyChange(AVKey.REPAINT, null, this);
        }
    }
}
