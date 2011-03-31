/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.tree;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.TextureCoords;
import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.geom.*;
import java.beans.*;
import java.util.*;

/**
 * Layout that draws a {@link Tree} similar to a file browser tree.
 *
 * @author pabercrombie
 * @version $Id: BasicTreeLayout.java 14702 2011-02-14 18:37:09Z pabercrombie $
 */
public class BasicTreeLayout extends WWObjectImpl implements TreeLayout, Scrollable
{
    protected Tree tree;

    protected ScrollFrame frame;

    protected TreeAttributes normalAttributes = new BasicTreeAttributes();
    protected TreeAttributes highlightAttributes = new BasicTreeAttributes();
    protected TreeAttributes activeAttributes = new BasicTreeAttributes();

    protected boolean highlighted;

    protected PickSupport pickSupport = new PickSupport();

    /**
     * This field is set by {@link #makeVisible(TreePath)}, and read by {@link #scrollToNode(gov.nasa.worldwind.render.DrawContext,
     * java.awt.Point)} during rendering.
     */
    protected TreeNode scrollToNode;

    /** Cache the rendered size of the tree and recompute when the tree changes. */
    protected Dimension size;
    protected boolean mustRecomputeSize = true;
    protected boolean mustRecomputeLayout = true;

    /** The attributes used if attributes are not specified. */
    protected static final TreeAttributes defaultAttributes;

    protected boolean showDescription = true;

    protected boolean drawNodeStateSymbol = true;

    protected boolean drawSelectedSymbol = true;

    /** Cache of computed text bounds. */
    protected BoundedHashMap<TextCacheKey, Rectangle2D> textCache = new BoundedHashMap<TextCacheKey, Rectangle2D>();

    // Computed each frame
    protected long frameNumber = -1L;
    protected Rectangle previousBounds;
    protected int lineHeight;
    /** Number of nodes in the tree, used to set a bound on the text cache. */
    protected int nodeCount;
    protected java.util.List<NodeLayout> visibleNodes = new ArrayList<NodeLayout>();

    /** Indentation in pixels applied to each new level of the tree. */
    protected int indent;

    static
    {
        defaultAttributes = new BasicTreeAttributes();
    }

    /**
     * Create a layout for a tree.
     *
     * @param tree Tree to create layout for.
     */
    public BasicTreeLayout(Tree tree)
    {
        this(tree, null);
    }

    /**
     * Create a layout for a tree, at a screen location.
     *
     * @param tree Tree to create layout for.
     * @param x    X coordinate of the upper left corner of the tree frame.
     * @param y    Y coordinate of the upper left corner of the tree frame, measured from the top of the screen.
     */
    public BasicTreeLayout(Tree tree, int x, int y)
    {
        this(tree, new Offset((double) x, (double) y, AVKey.PIXELS, AVKey.PIXELS));
    }

    /**
     * Create a layout for a tree, at a screen location.
     *
     * @param tree           Tree to create layout for.
     * @param screenLocation The location of the upper left corner of the tree frame. The point is in screen
     *                       coordinates, measured from the upper left corner of the screen.
     */
    public BasicTreeLayout(Tree tree, Offset screenLocation)
    {
        this.tree = tree;
        this.frame = this.createFrame();
        this.frame.setContents(this);

        // Listen for property changes in the frame. These will be forwarded to the layout listeners. This is necessary
        // to pass AVKey.REPAINT events up the layer.
        this.frame.addPropertyChangeListener(this);

        // Add listener for tree events so that we can recompute the tree size when things change. Because TreeLayout
        // is a WWObject, it sends property change events to its listeners. Since Tree is likely to listen for property
        // change events on TreeLayout, we add an anonymous listener to avoid an infinite cycle of property change
        // events between TreeLayout and Tree.
        this.tree.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent)
            {
                // Ignore events originated by this TreeLayout.
                if (propertyChangeEvent.getSource() != BasicTreeLayout.this)
                    BasicTreeLayout.this.invalidate();
            }
        });

        if (screenLocation != null)
            this.setScreenLocation(screenLocation);
    }

    /** Force the layout to recompute the size of the tree. */
    public void invalidate()
    {
        this.mustRecomputeSize = true;
        this.mustRecomputeLayout = true;
    }

    /**
     * Create the frame that the tree will be rendered inside.
     *
     * @return A new frame.
     */
    protected ScrollFrame createFrame()
    {
        return new ScrollFrame();
    }

    /**
     * Get the size of the entire tree, including the part that is not visible in the scroll pane.
     *
     * @param dc Draw context.
     *
     * @return Size of the rendered tree.
     */
    public Dimension getSize(DrawContext dc)
    {
        // Computing the size of rendered text is expensive, so only recompute the tree size when necessary.
        if (this.mustRecomputeSize)
        {
            TreeModel model = this.tree.getModel();
            TreeNode root = model.getRoot();

            this.size = new Dimension();
            this.computeSize(this.tree, root, dc, size, 1);
            this.textCache.setCapacity(this.nodeCount * 2);

            this.mustRecomputeSize = false;
        }
        return this.size;
    }

    /**
     * Compute the size of a tree. This method invokes itself recursively to calculate the size of the tree, taking into
     * account which nodes are expanded and which are not. This computed size will be stored in the {@code size}
     * parameter.
     *
     * @param tree  Tree that contains the root node.
     * @param root  Root node of the subtree to find the size of. This does not need to be the root node of the tree.
     * @param dc    Draw context.
     * @param size  Size object to modify. This method will change the width and height fields of {@code size} to hold
     *              the new size of the tree.
     * @param level Level of this node. Tree root node is level 1, children of the root are level 2, etc.
     */
    protected void computeSize(Tree tree, TreeNode root, DrawContext dc, Dimension size, int level)
    {
        this.nodeCount++;

        TreeAttributes attributes = this.getActiveAttributes();

        Dimension thisSize = this.getNodeSize(dc, root, attributes);

        if (this.mustDisplayNode(root, level))
        {
            int indent = this.indent * level;
            int thisWidth = thisSize.width + indent;
            if (thisWidth > size.width)
                size.width = thisWidth;

            size.height += thisSize.height;
            size.height += attributes.getRowSpacing();
        }

        if (tree.isNodeExpanded(root))
        {
            for (TreeNode child : root.getChildren())
            {
                this.computeSize(tree, child, dc, size, level + 1);
            }
        }
    }

    /**
     * Determine if a node needs to be displayed. This method examines only one node at a time. It does not take into
     * account that the node's parent may be in the collapsed state, in which the children are not rendered.
     *
     * @param node  Node to test.
     * @param level Level of the node in the tree. The root node is level 1, its children are level 2, etc.
     *
     * @return True if the node must be displayed.
     */
    protected boolean mustDisplayNode(TreeNode node, int level)
    {
        return node.isVisible() && (level > 1 || this.getActiveAttributes().isRootVisible());
    }

    /** {@inheritDoc} */
    public void render(DrawContext dc)
    {
        this.frame.render(dc);
    }

    /**
     * Scroll the frame to make a the node set in {@link #scrollToNode} node visible. Does nothing if {@link
     * #scrollToNode} is null.
     *
     * @param dc        Draw context.
     * @param upperLeft The upper left corner of the tree, in OpenGL coordinates.
     */
    protected synchronized void scrollToNode(DrawContext dc, Point upperLeft)
    {
        if (this.scrollToNode != null)
        {
            Point drawPoint = new Point(upperLeft);
            Rectangle bounds = this.findNodeBounds(this.scrollToNode, this.tree.getModel().getRoot(), dc, drawPoint, 1);

            Rectangle visibleBounds = this.frame.getVisibleBounds();

            if (bounds.getMaxY() > visibleBounds.getMaxY())
                this.frame.getScrollBar(AVKey.VERTICAL).setValue((int) (bounds.getMaxY() - visibleBounds.getMaxY()));
            else if (bounds.getMinY() < visibleBounds.getMinY())
                this.frame.getScrollBar(AVKey.VERTICAL).setValue((int) (visibleBounds.getMinY() - bounds.getMinY()));

            this.scrollToNode = null;
        }
    }

    /**
     * This method is called by {@link ScrollFrame} to render the contents of the tree at the appropriate position.
     *
     * @param dc     Draw context.
     * @param bounds Rectangle in which to render scrollable content. The rectangle is specified in OpenGL coordinates,
     *               with the origin at the low left corner of the screen.
     */
    public void renderScrollable(DrawContext dc, Rectangle bounds)
    {
        TreeModel model = this.tree.getModel();
        TreeNode root = model.getRoot();

        if (this.frameNumber != dc.getFrameTimeStamp())
        {
            this.determineActiveAttributes();

            Point drawPoint = new Point(bounds.x, bounds.y + bounds.height);

            this.scrollToNode(dc, drawPoint);

            if (this.mustRecomputeTreeLayout(bounds))
            {
                this.visibleNodes.clear();
                this.computeTreeLayout(root, dc, drawPoint, 1, visibleNodes);
                this.previousBounds = bounds;
                this.mustRecomputeLayout = false;
            }

            this.computeIndentation();
            this.computeMaxTextHeight(dc);

            this.frameNumber = dc.getFrameTimeStamp();
        }

        try
        {
            if (dc.isPickingMode())
            {
                this.pickSupport.clearPickList();
                this.pickSupport.beginPicking(dc);
            }

            this.renderNodes(dc, visibleNodes);
        }
        finally
        {
            if (dc.isPickingMode())
            {
                this.pickSupport.endPicking(dc);
                this.pickSupport.resolvePick(dc, dc.getPickPoint(), dc.getCurrentLayer());
            }
        }
    }

    /**
     * Indicates whether or not the tree layout needs to be recomputed.
     *
     * @param bounds Tree bounds.
     *
     * @return {@code true} if the layout needs to be recomputed, otherwise {@code false}.
     */
    protected boolean mustRecomputeTreeLayout(Rectangle bounds)
    {
        return this.mustRecomputeLayout || this.previousBounds == null || !this.previousBounds.equals(bounds);
    }

    /** Compute the indentation, in pixels, applied to each new level of the tree. */
    protected void computeIndentation()
    {
        this.indent = 0;

        int iconWidth = this.getActiveAttributes().getIconSize().width;
        int iconSpacing = this.getActiveAttributes().getIconSpace();
        int checkboxWidth = this.getSelectedSymbolSize().width;

        // Compute the indentation to make the checkbox of the child level line up the icon of the parent level
        this.indent = (checkboxWidth + iconSpacing) + ((iconWidth - checkboxWidth) / 2);
    }

    protected void computeMaxTextHeight(DrawContext dc)
    {
        TreeAttributes attributes = this.getActiveAttributes();

        // Use underscore + capital E with acute accent as max height
        Rectangle2D bounds = this.getTextBounds(dc, "_\u00c9", attributes.getFont());

        double lineHeight = Math.abs(bounds.getY());
        this.lineHeight = (int) Math.max(lineHeight, attributes.getIconSize().height);
    }

    protected void renderNodes(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        for (NodeLayout layout : nodes)
        {
            layout.reset();
        }

        if (this.isDrawNodeStateSymbol())
            this.drawTriangles(dc, nodes);

        if (this.isDrawSelectedSymbol())
            this.drawCheckboxes(dc, nodes);

        // If not picking, draw text and icons. Otherwise just draw pickable rectangles tagged with the node. Unlike
        // the toggle and select controls, selecting the node does not mean anything to the tree, but it may mean
        // something to an application controller.
        if (!dc.isPickingMode())
        {
            this.drawIcons(dc, nodes);
            this.drawText(dc, nodes);

            if (this.isShowDescription())
                this.drawDescriptionText(dc, nodes);
        }
        else
        {
            this.pickTextAndIcon(dc, nodes);
        }
    }

    /**
     * Draw pick rectangles over the icon and text areas the visible nodes.
     *
     * @param dc    Current draw context.
     * @param nodes Visible nodes.
     */
    protected void pickTextAndIcon(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        GL gl = dc.getGL();

        try
        {
            gl.glBegin(GL.GL_QUADS);

            for (NodeLayout layout : nodes)
            {
                Color color = dc.getUniquePickColor();
                PickedObject pickedObject = new PickedObject(color.getRGB(), layout.node);
                pickedObject.setValue(AVKey.HOT_SPOT, this.getFrame());
                this.pickSupport.addPickableObject(pickedObject);
                gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

                float minX = (float) layout.drawPoint.x;
                float minY = (float) layout.drawPoint.y;
                float maxX = (float) layout.bounds.getMaxX();
                float maxY = (float) layout.bounds.getMaxY();

                gl.glVertex2f(minX, maxY);
                gl.glVertex2f(maxX, maxY);
                gl.glVertex2f(maxX, minY);
                gl.glVertex2f(minX, minY);
            }
        }
        finally
        {
            gl.glEnd(); // Quads
        }
    }

    protected void drawText(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        GL gl = dc.getGL();

        TreeAttributes attributes = this.getActiveAttributes();
        Color color = attributes.getColor();
        float[] colorRGB = color.getRGBColorComponents(null);

        TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(),
            attributes.getFont(), true, false, false);

        gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);

        try
        {
            textRenderer.begin3DRendering();
            textRenderer.setColor(colorRGB[0], colorRGB[1], colorRGB[2], (float) attributes.getOpacity());

            for (NodeLayout layout : nodes)
            {
                String text = this.getText(layout.node);
                Rectangle2D textBounds = this.getTextBounds(dc, text, attributes.getFont());

                // Calculate height of text from baseline to top of text. Note that this does not include descenders below the
                // baseline.
                int textHeight = (int) Math.abs(textBounds.getY());

                int vertAdjust = layout.bounds.height - textHeight - (this.lineHeight - textHeight) / 2;

                textRenderer.draw(text, layout.drawPoint.x, layout.drawPoint.y + vertAdjust);
            }
        }
        finally
        {
            textRenderer.end3DRendering();
        }
    }

    protected void drawDescriptionText(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        TreeAttributes attributes = this.getActiveAttributes();
        Color color = attributes.getColor();
        float[] colorRGB = color.getRGBColorComponents(null);

        TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(),
            attributes.getDescriptionFont(), true, false, false);
        MultiLineTextRenderer mltr = new MultiLineTextRenderer(textRenderer);

        try
        {
            textRenderer.begin3DRendering();
            textRenderer.setColor(colorRGB[0], colorRGB[1], colorRGB[2], (float) attributes.getOpacity());

            for (NodeLayout layout : nodes)
            {
                String description = layout.node.getDescription();

                if (description != null)
                {
                    int vertAdjust = layout.bounds.height - this.lineHeight;
                    mltr.draw(description, layout.drawPoint.x, layout.drawPoint.y + vertAdjust);
                }
            }
        }
        finally
        {
            textRenderer.end3DRendering();
        }
    }

    protected void drawIcons(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        GL gl = dc.getGL();

        try
        {
            gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
            gl.glEnable(GL.GL_TEXTURE_2D);

            TreeAttributes attributes = this.getActiveAttributes();
            Dimension iconSize = attributes.getIconSize();

            gl.glColor4d(1d, 1d, 1d, attributes.getOpacity());

            WWTexture activeTexture = null;

            for (NodeLayout layout : nodes)
            {
                WWTexture texture = layout.node.getTexture();
                if (texture == null)
                    continue;

                // Check to see if this node's icon is the same as the previous node. If so, there's no need to rebind
                // the texture.
                boolean textureBound;
                // noinspection SimplifiableIfStatement
                if ((activeTexture != null) && (texture.getImageSource() == activeTexture.getImageSource()))
                {
                    textureBound = true;
                }
                else
                {
                    textureBound = texture.bind(dc);
                    if (textureBound)
                        activeTexture = texture;
                }

                if (textureBound)
                {
                    // If the total node height is greater than the image height, vertically center the image
                    int vertAdjustment = 0;
                    if (iconSize.height < layout.bounds.height)
                    {
                        vertAdjustment = layout.bounds.height - iconSize.height
                            - (this.lineHeight - iconSize.height) / 2;
                    }

                    try
                    {
                        gl.glPushMatrix();

                        TextureCoords texCoords = activeTexture.getTexCoords();
                        gl.glTranslated(layout.drawPoint.x, layout.drawPoint.y + vertAdjustment, 1.0);
                        gl.glScaled((double) iconSize.width, (double) iconSize.width, 1d);
                        dc.drawUnitQuad(texCoords);
                    }
                    finally
                    {
                        gl.glPopMatrix();
                    }

                    layout.drawPoint.x += attributes.getIconSize().width + attributes.getIconSpace();
                }
            }
        }
        finally
        {
            gl.glDisable(GL.GL_TEXTURE_2D);
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        }
    }

    /**
     * Draw check boxes. Each box includes a check mark is the node is selected, or is filled with a gradient if the
     * node is partially selected.
     *
     * @param dc    Current draw context.
     * @param nodes List of visible nodes.
     */
    protected void drawCheckboxes(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        // The check boxes are drawn in three passes:
        // 1) Draw filled background for partially selected nodes
        // 2) Draw check marks for selected nodes
        // 3) Draw checkbox outlines

        GL gl = dc.getGL();

        Dimension selectedSymbolSize = this.getSelectedSymbolSize();

        if (!dc.isPickingMode())
        {
            this.drawFilledCheckboxes(dc, nodes); // Draw filled boxes for partially selected nodes
            this.drawCheckmarks(dc, nodes); // Draw check marks for selected nodes
        }

        // Draw checkbox outlines
        try
        {
            if (!dc.isPickingMode())
                gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
            else
                gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);

            gl.glBegin(GL.GL_QUADS);

            for (NodeLayout layout : nodes)
            {
                int vertAdjust = layout.bounds.height - selectedSymbolSize.height
                    - (this.lineHeight - selectedSymbolSize.height) / 2;

                int x = layout.drawPoint.x;
                int y = layout.drawPoint.y + vertAdjust;

                if (dc.isPickingMode())
                {
                    Color color = dc.getUniquePickColor();
                    int colorCode = color.getRGB();
                    this.pickSupport.addPickableObject(colorCode, this.createSelectControl(layout.node));
                    gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
                }

                // Shift coordinates by half a pixel to position the lines on the center of the pixels
                gl.glVertex2f(x + selectedSymbolSize.width + 0.5f, y + selectedSymbolSize.height + 0.5f);
                gl.glVertex2f(x + 0.5f, y + selectedSymbolSize.height + 0.5f);
                gl.glVertex2f(x, y);
                gl.glVertex2f(x + selectedSymbolSize.width, y);

                layout.drawPoint.x += selectedSymbolSize.width + this.getActiveAttributes().getIconSpace();
            }
        }
        finally
        {
            gl.glEnd(); // Quads
        }
    }

    /**
     * Draw squares filled with a gradient for partially selected checkboxes.
     *
     * @param dc    Current draw context.
     * @param nodes List of visible nodes.
     */
    protected void drawFilledCheckboxes(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        Dimension selectedSymbolSize = this.getSelectedSymbolSize();
        TreeAttributes attributes = this.getActiveAttributes();

        double opacity = attributes.getOpacity();

        GL gl = dc.getGL();

        Color color1 = new Color(29, 78, 169);
        Color color2 = new Color(93, 158, 223);

        try
        {
            gl.glLineWidth(1f);
            gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
            // Fill box with a diagonal gradient
            gl.glBegin(GL.GL_QUADS);

            for (NodeLayout layout : nodes)
            {
                int vertAdjust = layout.bounds.height - selectedSymbolSize.height
                    - (this.lineHeight - selectedSymbolSize.height) / 2;

                int x = layout.drawPoint.x;
                int y = layout.drawPoint.y + vertAdjust;

                String selected = layout.node.isTreeSelected();
                boolean filled = TreeNode.PARTIALLY_SELECTED.equals(selected);

                if (filled)
                {
                    OGLUtil.applyColor(gl, color1, opacity, false);
                    gl.glVertex2f(x + selectedSymbolSize.width, y + selectedSymbolSize.height);
                    gl.glVertex2f(x, y + selectedSymbolSize.height);
                    gl.glVertex2f(x, y);

                    OGLUtil.applyColor(gl, color2, opacity, false);
                    gl.glVertex2f(x + selectedSymbolSize.width, y);
                }
            }
        }
        finally
        {
            gl.glEnd(); // Quads
        }
    }

    /**
     * Draw checkmark symbols in the selected checkboxes.
     *
     * @param dc    Current draw context.
     * @param nodes List of visible nodes.
     */
    protected void drawCheckmarks(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        Dimension selectedSymbolSize = this.getSelectedSymbolSize();
        TreeAttributes attributes = this.getActiveAttributes();

        Color color = attributes.getColor();
        double opacity = attributes.getOpacity();

        GL gl = dc.getGL();

        // Draw checkmarks for selected nodes
        OGLUtil.applyColor(gl, color, opacity, false);
        try
        {
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glBegin(GL.GL_LINES);

            for (NodeLayout layout : nodes)
            {
                int vertAdjust = layout.bounds.height - selectedSymbolSize.height
                    - (this.lineHeight - selectedSymbolSize.height) / 2;

                String selected = layout.node.isTreeSelected();
                boolean checked = TreeNode.SELECTED.equals(selected);
                if (checked)
                {
                    int x = layout.drawPoint.x;
                    int y = layout.drawPoint.y + vertAdjust;

                    gl.glVertex2f(x + selectedSymbolSize.width * 0.3f, y + selectedSymbolSize.height * 0.6f);
                    gl.glVertex2f(x + selectedSymbolSize.width * 0.3f, y + selectedSymbolSize.height * 0.2f);

                    gl.glVertex2f(x + selectedSymbolSize.width * 0.3f, y + selectedSymbolSize.height * 0.2f);
                    gl.glVertex2f(x + selectedSymbolSize.width * 0.8f, y + selectedSymbolSize.height * 0.8f);
                }
            }
        }
        finally
        {
            gl.glEnd(); // Lines
            gl.glDisable(GL.GL_LINE_SMOOTH);
        }
    }

    /**
     * Draw triangles to indicate that the nodes are expanded or collapsed.
     *
     * @param dc    Current draw context.
     * @param nodes Visible nodes.
     */
    protected void drawTriangles(DrawContext dc, Iterable<NodeLayout> nodes)
    {
        GL gl = dc.getGL();

        Dimension symbolSize = this.getNodeStateSymbolSize();

        int halfHeight = symbolSize.height / 2;
        int halfWidth = symbolSize.width / 2;

        int iconSpace = this.getActiveAttributes().getIconSpace();
        int pickWidth = symbolSize.width + iconSpace;

        if (!dc.isPickingMode())
        {
            TreeAttributes attributes = this.getActiveAttributes();

            Color color = attributes.getColor();
            double opacity = attributes.getOpacity();

            gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
            gl.glLineWidth(1f);
            OGLUtil.applyColor(gl, color, opacity, false);

            gl.glBegin(GL.GL_TRIANGLES);
        }
        else
        {
            gl.glBegin(GL.GL_QUADS); // Draw pick areas as rectangles, not triangles
        }

        try
        {
            for (NodeLayout layout : nodes)
            {
                int vertAdjust = layout.bounds.height - symbolSize.height - (this.lineHeight - symbolSize.height) / 2;

                // If the node is not a leaf, draw a symbol to indicate if it is expanded or collapsed
                if (!layout.node.isLeaf())
                {
                    int x = layout.drawPoint.x;
                    int y = layout.drawPoint.y + vertAdjust;

                    if (!dc.isPickingMode())
                    {
                        x += halfWidth;
                        y += halfHeight;

                        if (this.tree.isNodeExpanded(layout.node))
                        {
                            // Draw triangle pointing down
                            gl.glVertex2f(x - halfHeight, y);
                            gl.glVertex2f(x, -halfWidth + y);
                            gl.glVertex2f(x + halfHeight, y);
                        }
                        else
                        {
                            // Draw triangle pointing right
                            gl.glVertex2f(x, -0.5f + halfHeight + y);
                            gl.glVertex2f(x + halfWidth, y);
                            gl.glVertex2f(x, -0.5f - halfHeight + y);
                        }
                    }
                    else
                    {
                        Color color = dc.getUniquePickColor();
                        int colorCode = color.getRGB();
                        this.pickSupport.addPickableObject(colorCode,
                            this.createTogglePathControl(this.tree, layout.node));
                        gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

                        gl.glVertex2f(x, y);
                        gl.glVertex2f(x, y + this.lineHeight);
                        gl.glVertex2f(x + pickWidth, y + this.lineHeight);
                        gl.glVertex2f(x + pickWidth, y);
                    }
                }

                if (this.isDrawNodeStateSymbol())
                    layout.drawPoint.x += this.getNodeStateSymbolSize().width
                        + this.getActiveAttributes().getIconSpace();
            }
        }
        finally
        {
            gl.glEnd(); // Triangles if drawing, quads if picking
        }
    }

    /**
     * Determine the tree layout. This method determines which nodes are visible, and where they will be drawn.
     *
     * @param root         Root node of the subtree to render.
     * @param dc           Draw context.
     * @param location     Location at which to draw the node. The location specifies the upper left corner of the
     *                     subtree.
     * @param level        The level of this node in the tree. The root node is at level 1, its child nodes are at level
     *                     2, etc.
     * @param visibleNodes List to collect nodes that are currently visible. This method adds nodes to this list.
     *
     * @return True if all nodes were rendered. False if any node was not rendered because it was entirely out of the
     *         scrollable region.
     */
    protected boolean computeTreeLayout(TreeNode root, DrawContext dc, Point location, int level,
        java.util.List<NodeLayout> visibleNodes)
    {
        TreeAttributes attributes = this.getActiveAttributes();

        int oldX = location.x;

        if (this.mustDisplayNode(root, level))
        {
            Dimension size = this.getNodeSize(dc, root, attributes);

            // Adjust y to the bottom of the node area
            location.y -= (size.height + this.getActiveAttributes().getRowSpacing());

            Rectangle nodeBounds = new Rectangle(location.x, location.y, size.width, size.height);
            if (nodeBounds.intersects(this.frame.getVisibleBounds()))
            {
                visibleNodes.add(new NodeLayout(root, nodeBounds));
            }
            else if (nodeBounds.getMinY() < this.frame.getVisibleBounds().getMinY())
            {
                // If the lower edge of this node is out of the frame, all the nodes below it will be out of the frame
                // as well.
                return false;
            }
            location.x += this.indent;
        }

        // Draw child nodes if the root node is expanded.
        if (this.tree.isNodeExpanded(root))
        {
            for (TreeNode child : root.getChildren())
            {
                boolean rendered = this.computeTreeLayout(child, dc, location, level + 1, visibleNodes);

                // If the child node did not render don't attempt to render the other children.
                if (!rendered)
                    return false;
            }
        }
        location.x = oldX; // Restore previous indent level
        return true;
    }

    /**
     * Find the bounds of a node in the tree.
     *
     * @param needle   The node to find.
     * @param haystack Root node of the subtree to search.
     * @param dc       Draw context.
     * @param location Point in OpenGL screen coordinates (origin lower left corner) that defines the upper left corner
     *                 of the subtree.
     * @param level    Level of this subtree in the tree. The root node is level 1, its children are level 2, etc.
     *
     * @return Bounds of the node {@code needle}.
     */
    protected Rectangle findNodeBounds(TreeNode needle, TreeNode haystack, DrawContext dc, Point location, int level)
    {
        TreeAttributes attributes = this.getActiveAttributes();

        int oldX = location.x;

        if (level > 1 || attributes.isRootVisible())
        {
            Dimension size = this.getNodeSize(dc, haystack, attributes);

            // Adjust y to the bottom of the node area
            location.y -= (size.height + this.getActiveAttributes().getRowSpacing());

            Rectangle nodeBounds = new Rectangle(location.x, location.y, size.width, size.height);

            if (haystack.getPath().equals(needle.getPath()))
                return nodeBounds;

            location.x += level * this.indent;
        }

        // Draw child nodes if the root node is expanded
        if (this.tree.isNodeExpanded(haystack))
        {
            for (TreeNode child : haystack.getChildren())
            {
                Rectangle bounds = this.findNodeBounds(needle, child, dc, location, level + 1);
                if (bounds != null)
                    return bounds;
            }
        }
        location.x = oldX; // Restore previous indent level

        return null;
    }

    /** {@inheritDoc} */
    public synchronized void makeVisible(TreePath path)
    {
        TreeNode node = this.tree.getNode(path);
        if (node == null)
            return;

        TreeNode parent = node.getParent();
        while (parent != null)
        {
            this.tree.expandPath(parent.getPath());
            parent = parent.getParent();
        }

        // Set the scrollToNode field. This field will be read during rendering, and the frame will be
        // scrolled appropriately.
        this.scrollToNode = node;
    }

    /**
     * Get the location of the upper left corner of the tree, measured in screen coordinates with the origin at the
     * upper left corner of the screen.
     *
     * @return Screen location, measured in pixels from the upper left corner of the screen.
     */
    public Offset getScreenLocation()
    {
        return this.frame.getScreenLocation();
    }

    /**
     * Set the location of the upper left corner of the tree, measured in screen coordinates with the origin at the
     * upper left corner of the screen.
     *
     * @param screenLocation New screen location.
     */
    public void setScreenLocation(Offset screenLocation)
    {
        frame.setScreenLocation(screenLocation);
    }

    /** {@inheritDoc} */
    public TreeAttributes getAttributes()
    {
        return this.normalAttributes;
    }

    /** {@inheritDoc} */
    public void setAttributes(TreeAttributes attributes)
    {
        if (attributes == null)
        {
            String msg = Logging.getMessage("nullValue.AttributesIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.normalAttributes = attributes;
    }

    /**
     * Get the attributes to apply when the tree is highlighted.
     *
     * @return Attributes to use when tree is highlighted.
     */
    public TreeAttributes getHighlightAttributes()
    {
        return this.highlightAttributes;
    }

    /**
     * Set the attributes to use when the tree is highlighted.
     *
     * @param attributes New highlight attributes.
     */
    public void setHighlightAttributes(TreeAttributes attributes)
    {
        if (attributes == null)
        {
            String msg = Logging.getMessage("nullValue.AttributesIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.highlightAttributes = attributes;
    }

    /**
     * Get the active attributes, based on the highlight state.
     *
     * @return Highlight attributes if the tree is highlighted. Otherwise, the normal attributes.
     */
    protected TreeAttributes getActiveAttributes()
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

    /**
     * Is the tree highlighted? The tree is highlighted when the mouse is within the bounds of the containing frame.
     *
     * @return True if the tree is highlighted.
     */
    public boolean isHighlighted()
    {
        return this.highlighted;
    }

    /**
     * Set the tree layout to highlighted or not highlighted.
     *
     * @param highlighted True if the tree should be highlighted.
     */
    public void setHighlighted(boolean highlighted)
    {
        this.highlighted = highlighted;
    }

    /**
     * Get the frame that surrounds the tree.
     *
     * @return The frame that the tree is drawn on.
     */
    public ScrollFrame getFrame()
    {
        return this.frame;
    }

    public Dimension getNodeSize(DrawContext dc, TreeNode node, TreeAttributes attributes)
    {
        Dimension size = new Dimension();
        Rectangle2D textBounds = this.getTextBounds(dc, this.getText(node), attributes.getFont());
        size.width = (int) textBounds.getWidth();
        size.height = (int) textBounds.getHeight();

        String description = this.getDescriptionText(node);
        if (description != null)
        {
            Rectangle2D descriptionBounds = this.getMultilineTextBounds(dc, description,
                attributes.getDescriptionFont());
            size.width = (int) Math.max(size.width, descriptionBounds.getWidth());
            size.height += (int) descriptionBounds.getHeight();
        }

        if (node.hasImage())
        {
            Dimension iconSize = attributes.getIconSize();
            if (iconSize.height > size.height)
                size.height = iconSize.height;

            size.width += (iconSize.width + attributes.getIconSpace());
        }

        if (node.isLeaf())
            size.width += (this.getSelectedSymbolSize().width + attributes.getIconSpace());
        else
            size.width += (this.getNodeStateSymbolSize().width + attributes.getIconSpace());

        return size;
    }

    /**
     * Create a pickable object to represent a toggle control in the tree. The toggle control will expand or collapse a
     * node in response to user input.
     *
     * @param tree Tree that contains the node.
     * @param node The node to expand or collapse.
     *
     * @return A {@link TreeHotSpot} that will be added as a pickable object to the screen area occupied by the toggle
     *         control.
     */
    protected HotSpot createTogglePathControl(final Tree tree, final TreeNode node)
    {
        return new TreeHotSpot(this.getFrame())
        {
            @Override
            public void selected(SelectEvent event)
            {
                if (!event.isConsumed() && (event.isLeftClick() || event.isLeftDoubleClick()))
                {
                    tree.togglePath(node.getPath());
                    event.consume();
                }
            }
        };
    }

    /**
     * Create a pickable object to represent selection control in the tree. The selection control will select or
     * deselect a node in response to user input. The returned <code>HotSpot</code> calls <code>{@link
     * #toggleNodeSelection(TreeNode)}</code> upon a left-click select event.
     *
     * @param node The node to expand or collapse.
     *
     * @return A {@link TreeHotSpot} that will be added as a pickable object to the screen area occupied by the toggle
     *         control.
     */
    protected HotSpot createSelectControl(final TreeNode node)
    {
        return new TreeHotSpot(this.getFrame())
        {
            @Override
            public void selected(SelectEvent event)
            {
                if (!event.isConsumed() && (event.isLeftClick() || event.isLeftDoubleClick()))
                {
                    toggleNodeSelection(node);
                    event.consume();
                }
            }
        };
    }

    /**
     * Get the bounds of a text string.
     *
     * @param dc   Draw context.
     * @param text Text to get bounds of.
     * @param font Font applied to the text.
     *
     * @return A rectangle that describes the node bounds. See {@link com.sun.opengl.util.j2d.TextRenderer#getBounds}
     *         for information on how this rectangle should be interpreted.
     */
    protected Rectangle2D getTextBounds(DrawContext dc, String text, Font font)
    {
        TextCacheKey cacheKey = new TextCacheKey(text, font);
        Rectangle2D bounds = this.textCache.get(cacheKey);

        if (bounds == null)
        {
            TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), font);
            bounds = textRenderer.getBounds(text);

            this.textCache.put(cacheKey, bounds);
        }

        return bounds;
    }

    protected Rectangle2D getMultilineTextBounds(DrawContext dc, String text, Font font)
    {
        int width = 0;
        int maxLineHeight = 0;
        String[] lines = text.split("\n");

        for (String line : lines)
        {
            Rectangle2D lineBounds = this.getTextBounds(dc, line, font);
            width = (int) Math.max(lineBounds.getWidth(), width);
            maxLineHeight = (int) Math.max(lineBounds.getHeight(), lineHeight);
        }

        int lineSpacing = 0;

        // Compute final height using maxLineHeight and number of lines
        return new Rectangle(lines.length, lineHeight, width,
            lines.length * maxLineHeight + (lines.length - 1) * lineSpacing);
    }

    /**
     * Toggles the selection state of the specified <code>node</code>. In order to provide an intuitive tree selection
     * model to the application, this changes the selection state of the <code>node</code>'s ancestors and descendants
     * as follows:
     * <p/>
     * <ul> <li>The branch beneath the node it also set to the node's new selection state. Toggling an interior node's
     * selection state causes that entire branch to toggle.</li> <li>The node's ancestors are set to match the node's
     * new selection state. If the new state is <code>false</code>, this stops at the first ancestor with another branch
     * that has a selected node. When an interior or leaf node is toggled, the path to that node is also toggled, except
     * when doing so would clear a selected path to another interior or leaf node.</li> </ul>
     * <p/>
     *
     * @param node the <code>TreeNode</code> who's selection state should be toggled.
     */
    protected void toggleNodeSelection(TreeNode node)
    {
        boolean selected = !node.isSelected();
        node.setSelected(selected);

        // Change the selection state of the node's descendants to match. Toggling an interior node's selection state
        // causes that entire branch to toggle.
        if (!node.isLeaf())
            this.setDescendantsSelected(node, selected);

        // Change the selection state of the node's ancestors to match. If the node's new selection state is true, then
        // mark its ancestors as selected. When an interior or leaf node is selected, the path to that node is also
        // selected. If the node's new selection state is false, then mark its ancestors as not selected, stopping at
        // the first ancestor with a selected child. This avoids clearing a selected path to another interior or leaf
        // node.
        TreeNode parent = node.getParent();
        while (parent != null)
        {
            boolean prevSelected = parent.isSelected();
            parent.setSelected(selected);

            if (!selected && !TreeNode.NOT_SELECTED.equals(parent.isTreeSelected()))
            {
                parent.setSelected(prevSelected);
                break;
            }

            parent = parent.getParent();
        }
    }

    /**
     * Sets the selection state of the branch beneath the specified <code>node</code>.
     *
     * @param node     the <code>TreeNode</code> who descendants selection should be set.
     * @param selected <code>true</code> to mark the descendants and selected, otherwise <code>false</code>.
     */
    protected void setDescendantsSelected(TreeNode node, boolean selected)
    {
        for (TreeNode child : node.getChildren())
        {
            child.setSelected(selected);

            if (!child.isLeaf())
                this.setDescendantsSelected(child, selected);
        }
    }

    /**
     * Get the text for a node.
     *
     * @param node Node to get text for.
     *
     * @return Text for node.
     */
    protected String getText(TreeNode node)
    {
        return node.getText();
    }

    /**
     * Get the description text for a node.
     *
     * @param node Node to get text for.
     *
     * @return Description text for {@code node}. May return null if there is no description.
     */
    protected String getDescriptionText(TreeNode node)
    {
        return node.getDescription();
    }

    /**
     * Get the size of the symbol that indicates that a node is expanded or collapsed.
     *
     * @return The size of the node state symbol.
     */
    protected Dimension getNodeStateSymbolSize()
    {
        return new Dimension(12, 12);
    }

    /**
     * Get the size of the symbol that indicates that a node is selected or not selected.
     *
     * @return The size of the node selection symbol.
     */
    protected Dimension getSelectedSymbolSize()
    {
        return new Dimension(12, 12);
    }

    /**
     * Should the node renderer include node descriptions?
     *
     * @return True if the renderer should renderer node descriptions.
     */
    public boolean isShowDescription()
    {
        return this.showDescription;
    }

    /**
     * Set the renderer to renderer node descriptions (additional text rendered under the node title).
     *
     * @param showDescription True if the description should be rendered. False if only the icon and title should be
     *                        rendered.
     */
    public void setShowDescription(boolean showDescription)
    {
        this.showDescription = showDescription;
    }

    /**
     * Will the renderer draw a symbol to indicate that the node is selected? The default symbol is a checkbox.
     *
     * @return True if the node selected symbol (a checkbox by default) will be drawn.
     */
    public boolean isDrawSelectedSymbol()
    {
        return this.drawSelectedSymbol;
    }

    /**
     * Set whether or not the renderer will draw a symbol to indicate that the node is selected. The default symbol is a
     * checkbox.
     *
     * @param drawSelectedSymbol True if the node selected symbol (a checkbox by default) will be drawn.
     */
    public void setDrawSelectedSymbol(boolean drawSelectedSymbol)
    {
        this.drawSelectedSymbol = drawSelectedSymbol;
    }

    /**
     * Will the renderer draw a symbol to indicate that the node is expanded or collapsed (applies only to non-leaf
     * nodes). The default symbol is a triangle pointing to the right, for collapsed nodes, or down for expanded nodes.
     *
     * @return True if the node state symbol (default is a triangle pointing either to the right or down) will be
     *         drawn.
     */
    public boolean isDrawNodeStateSymbol()
    {
        return this.drawNodeStateSymbol;
    }

    /**
     * Set whether or not the renderer will draw a symbol to indicate that the node is expanded or collapsed (applies
     * only to non-leaf nodes). The default symbol is a triangle pointing to the right, for collapsed nodes, or down for
     * expanded nodes.
     *
     * @param drawNodeStateSymbol True if the node state symbol (default is a triangle pointing either to the right or
     *                            down) will be drawn.
     */
    public void setDrawNodeStateSymbol(boolean drawNodeStateSymbol)
    {
        this.drawNodeStateSymbol = drawNodeStateSymbol;
    }

    /** Cache key for cache text bound cache. */
    protected static class TextCacheKey
    {
        protected String text;
        protected Font font;
        protected int hash = 0;

        /**
         * Create a cache key for a string rendered in a font.
         *
         * @param text String for which to cache bounds.
         * @param font Font of the rendered string.
         */
        public TextCacheKey(String text, Font font)
        {
            if (text == null)
            {
                String message = Logging.getMessage("nullValue.StringIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }
            if (font == null)
            {
                String message = Logging.getMessage("nullValue.FontIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            this.text = text;
            this.font = font;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || this.getClass() != o.getClass())
                return false;

            TextCacheKey cacheKey = (TextCacheKey) o;

            return this.text.equals(cacheKey.text) && this.font.equals(cacheKey.font);
        }

        @Override
        public int hashCode()
        {
            if (this.hash == 0)
            {
                int result;
                result = this.text.hashCode();
                result = 31 * result + this.font.hashCode();
                this.hash = result;
            }
            return this.hash;
        }
    }

    /** Class to hold information about how a tree node is layed out. */
    protected static class NodeLayout
    {
        /** Node that this layout applies to. */
        protected TreeNode node;
        /** Node bounds. */
        protected Rectangle bounds;

        /**
         * Point at which the next component should be drawn. Nodes are drawn left to right, and the draw point is as
         * parts of the node are rendered. For example, the toggle triangle is drawn first at the draw point, and then
         * the draw point is moved to the right by the width of the triangle, to the next component will draw at the
         * correct point. The draw point is reset to the lower left corner of the node bounds before each render cycle.
         */
        protected Point drawPoint;

        /**
         * Create a new node layout.
         *
         * @param node   Node that is being laid out.
         * @param bounds Node bounds.
         */
        protected NodeLayout(TreeNode node, Rectangle bounds)
        {
            if (node == null)
            {
                String message = Logging.getMessage("nullValue.TreeNodeIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }
            if (bounds == null)
            {
                String message = Logging.getMessage("nullValue.RectangleIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            this.node = node;
            this.bounds = bounds;
            this.drawPoint = new Point(bounds.x, bounds.y);
        }

        /** Reset the draw point to the lower left corner of the node bounds. */
        protected void reset()
        {
            this.drawPoint.x = this.bounds.x;
            this.drawPoint.y = this.bounds.y;
        }
    }
}
