/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.tree;

import java.awt.*;

/**
 * Attributes to control how a {@link ScrollFrame} is rendered.
 *
 * @author pabercrombie
 * @version $Id: FrameAttributes.java 14425 2011-01-09 02:15:52Z pabercrombie $
 * @see ScrollFrame
 */
public interface FrameAttributes
{
    /**
     * Returns a new FrameAttributes instance of the same type as this FrameAttributes, who's properties are
     * configured exactly as this FrameAttributes.
     *
     * @return a copy of this FrameAttributes.
     */
    FrameAttributes copy();

    /**
     * Copies the specified FrameAttributes' properties into this object's properties. This does nothing if the
     * specified attributes is null.
     *
     * @param attributes the attributes to copy.
     */
    void copy(FrameAttributes attributes);

    /**
     * Get the color of the text in the frame.
     *
     * @return Text color.
     *
     * @see #setForegroundColor(java.awt.Color)
     */
    Color getForegroundColor();

    /**
     * Set the color of the text in the frame.
     *
     * @param textColor New text color.
     *
     * @see #getForegroundColor()
     */
    void setForegroundColor(Color textColor);

    /**
     * Get the font used to render text.
     *
     * @return frame font.
     *
     * @see #setFont(java.awt.Font)
     */
    Font getFont();

    /**
     * Set the font used to render text.
     *
     * @param font New frame font.
     *
     * @see #getFont()
     */
    void setFont(Font font);

    /**
     * Get the size of the icon in the frame title bar.
     *
     * @return Icon size.
     *
     * @see #setIconSize(java.awt.Dimension)
     */
    Dimension getIconSize();

    /**
     * Set the size of each icon in the frame title bar.
     *
     * @param size New size.
     *
     * @see #getIconSize()
     */
    void setIconSize(Dimension size);

    /**
     * Get the amount of space, in pixels, to leave between an icon in the frame and surrounding text and shapes.
     *
     * @return Icon space in pixels.
     *
     * @see #setIconSpace(int)
     */
    int getIconSpace();

    /**
     * Set the amount of space, in pixels, to leave between an icon in the frame and surrounding text and shapes.
     *
     * @param iconSpace Icon space in pixels.
     *
     * @see #getIconSpace()
     */
    void setIconSpace(int iconSpace);

    /**
     * Get the opacity of the text and images in the frame.
     *
     * @return Opacity of text and images.
     *
     * @see #setForegroundOpacity(double)
     * @see #getBackgroundOpacity()
     */
    double getForegroundOpacity();

    /**
     * Set the opacity of the frame text and images.
     *
     * @param textOpacity New opacity.
     *
     * @see #getForegroundOpacity()
     * @see #setBackgroundOpacity(double)
     */
    void setForegroundOpacity(double textOpacity);

    /**
     * Get the opacity of the frame.
     *
     * @return Frame opacity.
     *
     * @see #setBackgroundOpacity(double)
     */
    double getBackgroundOpacity();

    /**
     * Set the opacity of the frame.
     *
     * @param frameOpacity New frame opacity.
     *
     * @see #getBackgroundOpacity()
     */
    void setBackgroundOpacity(double frameOpacity);

    /**
     * Get the colors that make up the frame's background gradient.
     *
     * @return Two element array containing the colors in the background gradient.
     *
     * @see #setBackgroundColor(java.awt.Color, java.awt.Color)
     */
    Color[] getBackgroundColor();

    /**
     * Set the colors in the background gradient of the frame.
     *
     * @param frameColor1 First color in frame gradient.
     * @param frameColor2 Second color in frame gradient.
     *
     * @see #getBackgroundColor()
     */
    void setBackgroundColor(Color frameColor1, Color frameColor2);

    /**
     * Get the colors that make up the frame's title bar gradient.
     *
     * @return Two element array containing the colors in the title bar gradient.
     *
     * @see #setTitleBarColor(java.awt.Color, java.awt.Color)
     */
    Color[] getTitleBarColor();

    /**
     * Set the colors in the title bar gradient.
     *
     * @param color1 First color in the title bar gradient.
     * @param color2 Second color in the title bar gradient.
     *
     * @see #getBackgroundColor()
     */
    void setTitleBarColor(Color color1, Color color2);

    /**
     * Get the color of the minimize button drawn in the upper right corner of the frame.
     *
     * @return Color of the minimize button.
     */
    Color getMinimizeButtonColor();

    /**
     * Set the color of the minimize button drawn in the upper right corner of the frame.
     *
     * @param color Color of the minimize button.
     */
    void setMinimizeButtonColor(Color color);

    /**
     * Does the frame have a title bar?
     *
     * @return True if the frame will draw a title bar.
     */
    boolean isDrawTitleBar();

    /**
     * Set whether the frame has a title bar.
     *
     * @param drawTitleBar True if the frame will draw a title bar.
     */
    void setDrawTitleBar(boolean drawTitleBar);

    /**
     * Indicates whether or not the user can resize the frame by dragging the frame border.
     *
     * @return {@code true} if the user can resize the frame by dragging.
     */
    boolean isEnableResizeControl();

    /**
     * Specifies whether the user can resize the frame by dragging the border.
     *
     * @param enable {@code true} to allow the user to resize the frame by dragging the border.
     */
    void setEnableResizeControl(boolean enable);

    /**
     * Specifies whether the user can move the frame by dragging the title bar.
     *
     * @return {@code true} if the user can allowed to move the frame by dragging the title bar.
     */
    boolean isEnableMove();

    /**
     * Specifies whether the user can move the frame by dragging the title bar.
     *
     * @param enable {@code true} if the user is allowed to move the frame by dragging.
     */
    void setEnableMove(boolean enable);

    /**
     * Get the radius of the frame corners. A value of {@code zero} means square corners.
     *
     * @return The radius of the frame corners, in pixels.
     */
    public int getCornerRadius();

    /**
     * Set the radius of the frame corners.
     *
     * @param cornerRadius New radius, in pixels. A value of {@code zero} means square corners.
     */
    public void setCornerRadius(int cornerRadius);
}
