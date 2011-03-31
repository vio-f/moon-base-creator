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
 * Basic implementation of {@link FrameAttributes} set.
 *
 * @author pabercrombie
 * @version $Id: BasicFrameAttributes.java 14425 2011-01-09 02:15:52Z pabercrombie $
 */
public class BasicFrameAttributes implements FrameAttributes
{
    protected double frameOpacity;
    protected Color frameColor1;
    protected Color frameColor2;

    protected boolean drawTitleBar;
    protected boolean enableResize;
    protected boolean enableMove;

    protected Color titleBarColor1;
    protected Color titleBarColor2;

    protected Color minimizeButtonColor;

    protected double textOpacity;

    protected Color textColor;
    protected Font font;

    protected Dimension iconSize;
    protected int iconSpace;

    protected int cornerRadius;

    public BasicFrameAttributes()
    {
        this.frameOpacity = 0.8;
        this.frameColor1 = Color.WHITE;
        this.frameColor2 = new Color(0xC8D2DE);

        this.drawTitleBar = true;
        this.enableResize = true;
        this.enableMove = true;

        this.titleBarColor1 = new Color(29, 78, 169);
        this.titleBarColor2 = new Color(93, 158, 223);

        this.minimizeButtonColor = new Color(0xEB9BA4);
        this.textOpacity = 1.0;
        this.textColor = Color.BLACK;
        this.font = Font.decode("Arial-BOLD-14");

        this.iconSize = new Dimension(16, 16);
        this.iconSpace = 5;

        this.cornerRadius = 5;
    }

    /**
     * Create a new attributes object with the same configuration as an existing attributes object.
     *
     * @param attributes Object to copy configuration from.
     */
    public BasicFrameAttributes(BasicFrameAttributes attributes)
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
    public Color getForegroundColor()
    {
        return this.textColor;
    }

    /** {@inheritDoc} */
    public void setForegroundColor(Color textColor)
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
    public double getForegroundOpacity()
    {
        return textOpacity;
    }

    /** {@inheritDoc} */
    public void setForegroundOpacity(double textOpacity)
    {
        this.textOpacity = textOpacity;
    }

    /** {@inheritDoc} */
    public double getBackgroundOpacity()
    {
        return this.frameOpacity;
    }

    /** {@inheritDoc} */
    public void setBackgroundOpacity(double frameOpacity)
    {
        this.frameOpacity = frameOpacity;
    }

    /** {@inheritDoc} */
    public Color[] getBackgroundColor()
    {
        return new Color[] {this.frameColor1, this.frameColor2};
    }

    /** {@inheritDoc} */
    public void setTitleBarColor(Color color1, Color color2)
    {
        if (frameColor1 == null || frameColor2 == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.titleBarColor1 = color1;
        this.titleBarColor2 = color2;
    }

    /** {@inheritDoc} */
    public Color[] getTitleBarColor()
    {
        return new Color[] {this.titleBarColor1, this.titleBarColor2};
    }

    /** {@inheritDoc} */
    public Color getMinimizeButtonColor()
    {
        return minimizeButtonColor;
    }

    /** {@inheritDoc} */
    public void setMinimizeButtonColor(Color minimizeButtonColor)
    {
        if (frameColor1 == null || frameColor2 == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.minimizeButtonColor = minimizeButtonColor;
    }

    /** {@inheritDoc} */
    public void setBackgroundColor(Color frameColor1, Color frameColor2)
    {
        if (frameColor1 == null || frameColor2 == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.frameColor1 = frameColor1;
        this.frameColor2 = frameColor2;
    }

    /** {@inheritDoc} */
    public boolean isDrawTitleBar()
    {
        return this.drawTitleBar;
    }

    /** {@inheritDoc} */
    public void setDrawTitleBar(boolean drawTitleBar)
    {
        this.drawTitleBar = drawTitleBar;
    }

    /** {@inheritDoc} */
    public boolean isEnableResizeControl()
    {
        return this.enableResize;
    }

    /** {@inheritDoc} */
    public void setEnableResizeControl(boolean enable)
    {
        this.enableResize = enable;
    }

    /** {@inheritDoc} */
    public boolean isEnableMove()
    {
        return this.enableMove;
    }

    /** {@inheritDoc} */
    public void setEnableMove(boolean enable)
    {
        this.enableMove = enable;
    }

    /** {@inheritDoc} */
    public int getCornerRadius()
    {
        return this.cornerRadius;
    }

    /** {@inheritDoc} */
    public void setCornerRadius(int cornerRadius)
    {
        this.cornerRadius = cornerRadius;
    }

    /** {@inheritDoc} */
    public BasicFrameAttributes copy()
    {
        return new BasicFrameAttributes(this);
    }

    /** {@inheritDoc} */
    public void copy(FrameAttributes attributes)
    {
        if (attributes != null)
        {
            this.frameOpacity = attributes.getBackgroundOpacity();
            Color[] colorArray = attributes.getBackgroundColor();
            this.frameColor1 = colorArray[0];
            this.frameColor2 = colorArray[1];

            this.drawTitleBar = attributes.isDrawTitleBar();
            this.enableResize = attributes.isEnableResizeControl();
            this.enableMove = attributes.isEnableMove();

            colorArray = attributes.getTitleBarColor();
            this.titleBarColor1 = colorArray[0];
            this.titleBarColor2 = colorArray[1];

            this.minimizeButtonColor = attributes.getMinimizeButtonColor();
            this.textOpacity = attributes.getForegroundOpacity();
            this.textColor = attributes.getForegroundColor();
            this.font = attributes.getFont();

            this.iconSize = attributes.getIconSize();
            this.iconSpace = attributes.getIconSpace();

            this.cornerRadius = attributes.getCornerRadius();
        }
    }
}
