/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.tree;

import gov.nasa.worldwind.util.Logging;

import java.awt.*;

/**
 * Basic implementation of {@link TreeAttributes} set.
 *
 * @author pabercrombie
 * @version $Id: BasicTreeAttributes.java 14665 2011-02-10 19:46:31Z pabercrombie $
 */
public class BasicTreeAttributes implements TreeAttributes
{
    protected boolean rootVisible;

    protected double opacity;

    protected Color textColor;
    protected Font font;
    protected Font descriptionFont;
    protected int rowSpacing; // Spacing between rows in the tree

    protected Dimension iconSize;
    protected int iconSpace;

    public BasicTreeAttributes()
    {
        this.rootVisible = true;
        this.opacity = 1.0;
        this.textColor = Color.BLACK;
        this.font = Font.decode("Arial-BOLD-14");
        this.descriptionFont = Font.decode("Arial-12");
        this.rowSpacing = 8; // Spacing between rows in the tree

        this.iconSize = new Dimension(16, 16);
        this.iconSpace = 5;        
    }

    /**
     * Create a new attributes object with the same configuration as an existing attributes object.
     *
     * @param attributes Object to copy configuration from.
     */
    public BasicTreeAttributes(TreeAttributes attributes)
    {
        if (attributes == null)
        {
            String message = Logging.getMessage("nullValue.AttributesIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.copy(attributes);
    }

    /** {@inheritDoc} */
    public boolean isRootVisible()
    {
        return this.rootVisible;
    }

    /** {@inheritDoc} */
    public void setRootVisible(boolean visible)
    {
        this.rootVisible = visible;
    }

    /** {@inheritDoc} */
    public Color getColor()
    {
        return this.textColor;
    }

    /** {@inheritDoc} */
    public void setColor(Color textColor)
    {
        if (textColor == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.textColor = textColor;
    }

    /** {@inheritDoc} */
    public Font getFont()
    {
        return this.font;
    }

    /** {@inheritDoc} */
    public Font getDescriptionFont()
    {
        return this.descriptionFont;
    }

    /** {@inheritDoc} */
    public void setDescriptionFont(Font font)
    {
        if (font == null)
        {
            String msg = Logging.getMessage("nullValue.FontIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.descriptionFont = font;
    }

    /** {@inheritDoc} */
    public void setFont(Font font)
    {
        if (font == null)
        {
            String msg = Logging.getMessage("nullValue.FontIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.font = font;
    }

    /** {@inheritDoc} */
    public int getRowSpacing()
    {
        return this.rowSpacing;
    }

    /** {@inheritDoc} */
    public void setRowSpacing(int spacing)
    {
        this.rowSpacing = spacing;
    }

    /** {@inheritDoc} */
    public Dimension getIconSize()
    {
        return this.iconSize;
    }

    /** {@inheritDoc} */
    public void setIconSize(Dimension iconSize)
    {
        if (iconSize == null)
        {
            String message = Logging.getMessage("nullValue.SizeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        this.iconSize = iconSize;
    }

    /** {@inheritDoc} */
    public int getIconSpace()
    {
        return this.iconSpace;
    }

    /** {@inheritDoc} */
    public void setIconSpace(int iconSpace)
    {
        this.iconSpace = iconSpace;
    }

    /** {@inheritDoc} */
    public double getOpacity()
    {
        return opacity;
    }

    /** {@inheritDoc} */
    public void setOpacity(double textOpacity)
    {
        this.opacity = textOpacity;
    }

    /** {@inheritDoc} */
    public BasicTreeAttributes copy()
    {
        return new BasicTreeAttributes(this);
    }

    /** {@inheritDoc} */
    public void copy(TreeAttributes attributes)
    {
        if (attributes != null)
        {
            this.rootVisible = attributes.isRootVisible();
            this.opacity = attributes.getOpacity();
            this.textColor = attributes.getColor();
            this.font = attributes.getFont();
            this.descriptionFont = attributes.getDescriptionFont();
//            this.indent = attributes.getIndent();
            this.rowSpacing = attributes.getRowSpacing();
            this.iconSize = attributes.getIconSize();
            this.iconSpace = attributes.getIconSpace();
        }
    }
}
