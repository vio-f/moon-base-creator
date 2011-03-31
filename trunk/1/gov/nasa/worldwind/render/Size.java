/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.util.*;

import java.awt.*;

/**
 * Defines the dimensions of an image, label or other screen-space item relative to a container (for example, the
 * viewport). A size contains a width, a height, a width size mode, a height size mode, and for each of these a "units"
 * string indicating the coordinate units.
 * <p/>
 * The possible size modes are: <ul> <li> {@link #NATIVE_DIMENSION} - Maintain the native dimensions.</li> <li> {@link
 * #MAINTAIN_ASPECT_RATIO} - Maintain the aspect ratio of the image when one dimension is specified and the other is
 * not.</li> <li> {@link #EXPLICIT_DIMENSION} - Use an explicit dimension. This dimension may be either an absolute
 * pixel value, or a fraction of the container.</li>
 * <p/>
 * Recognized "units" values are {@link AVKey#PIXELS}, which indicates pixel units relative to the lower left corner of
 * the image, {@link AVKey#FRACTION}, which indicates the units are fractions of the image width and height.
 * <p/>
 * Examples:
 * <pre>
 * Width mode      Height mode      Width (Units)      Height (Units)        Result
 * --------------------------------------------------------------------------------------------------------------------
 * Native          Native           N/A                N/A                   Keep native dimensions
 * Aspect ratio    Explicit         N/A                100 (pix)             Scale image so that height is 100 pixels,
 *                                                                           but maintain aspect ratio
 * Explicit        Aspect ratio     0.5 (fraction)     N/A                   Make the width half of the container, and
 *                                                                           scale height to maintain aspect ratio
 * Explicit        Native           1.0 (fraction)     N/A                   Stretch the image to fill the width of the
 *                                                                           container, but do not scale the height.
 * <p/>
 * This class implements the functionality of a KML <i>size</i>.
 *
 * @author pabercrombie
 * @version $Id: Size.java 14527 2011-01-20 01:32:39Z pabercrombie $
 */
public class Size
{
    /** Size value that KML uses to indicate that the image's native dimension should be used. */
    public static final int NATIVE_DIMENSION = 0;

    /** Size value that KML uses to indicate that the image aspect ration should be maintained. */
    public static final int MAINTAIN_ASPECT_RATIO = 1;

    /**
     * Constant to indicate that the size parameter from the KML file indicates the image dimension, not {@link
     * #NATIVE_DIMENSION} or {@link #MAINTAIN_ASPECT_RATIO}.
     */
    public static final int EXPLICIT_DIMENSION = 2;

    protected double widthParam;
    protected double heightParam;
    protected String widthUnits = AVKey.PIXELS;
    protected String heightUnits = AVKey.PIXELS;
    protected int widthMode = NATIVE_DIMENSION;
    protected int heightMode = NATIVE_DIMENSION;

    /** Create a Size object that will preserve native dimensions. */
    public Size()
    {
    }

    /**
     * Create a Size with specified dimensions.
     *
     * @param widthMode   Width mode, one of {@link #NATIVE_DIMENSION}, {@link #MAINTAIN_ASPECT_RATIO}, or {@link
     *                    #EXPLICIT_DIMENSION}.
     * @param widthParam  The width (applies only to {@link #EXPLICIT_DIMENSION} mode).
     * @param widthUnits  Units of {@code width}. Either {@link AVKey#PIXELS} or {@link AVKey#PIXELS}.
     * @param heightMode  height mode, one of {@link #NATIVE_DIMENSION}, {@link #MAINTAIN_ASPECT_RATIO}, or {@link
     *                    #EXPLICIT_DIMENSION}.
     * @param heightParam The height (applies only to {@link #EXPLICIT_DIMENSION} mode).
     * @param heightUnits Units of {@code height}. Either {@link AVKey#PIXELS} or {@link AVKey#PIXELS}.
     *
     * @see #setWidth(int, double, String)
     * @see #setHeight(int, double, String)
     */
    public Size(int widthMode, double widthParam, String widthUnits, int heightMode, double heightParam,
        String heightUnits)
    {
        this.setWidth(widthMode, widthParam, widthUnits);
        this.setHeight(heightMode, heightParam, heightUnits);
    }

    /**
     * Create a size from explicit pixel dimensions.
     *
     * @param widthInPixels  Width of rectangle in pixels.
     * @param heightInPixels Height of rectangle in pixels.
     *
     * @return New size object.
     */
    public static Size fromPixels(int widthInPixels, int heightInPixels)
    {
        return new Size(EXPLICIT_DIMENSION, widthInPixels, AVKey.PIXELS,
            EXPLICIT_DIMENSION, heightInPixels, AVKey.PIXELS);
    }

    /**
     * Set the width.
     *
     * @param mode  Width mode, one of {@link #NATIVE_DIMENSION}, {@link #MAINTAIN_ASPECT_RATIO}, or {@link
     *              #EXPLICIT_DIMENSION}.
     * @param width The width (applies only to {@link #EXPLICIT_DIMENSION} mode).
     * @param units Units of {@code width}. Either {@link AVKey#PIXELS} or {@link AVKey#PIXELS}.
     */
    public void setWidth(int mode, double width, String units)
    {
        this.widthMode = mode;
        this.widthParam = width;
        this.widthUnits = units;
    }

    /**
     * Set the height.
     *
     * @param mode   Width mode, one of {@link #NATIVE_DIMENSION}, {@link #MAINTAIN_ASPECT_RATIO}, or {@link
     *               #EXPLICIT_DIMENSION}.
     * @param height The width (applies only to {@link #EXPLICIT_DIMENSION} mode).
     * @param units  Units of {@code width}. Either {@link AVKey#PIXELS} or {@link AVKey#FRACTION}.
     */
    public void setHeight(int mode, double height, String units)
    {
        this.heightMode = mode;
        this.heightParam = height;
        this.heightUnits = units;
    }

    /**
     * Returns the units of the offset X value. See {@link #setWidth(int, double, String)} for a description of the
     * recognized values.
     *
     * @return the units of the offset X value, or null.
     */
    public String getWidthUnits()
    {
        return widthUnits;
    }

    /**
     * Returns the units of the offset Y value. See {@link #setHeight(int, double, String)} for a description of the
     * recognized values.
     *
     * @return the units of the offset Y value, or null.
     */
    public String getHeightUnits()
    {
        return heightUnits;
    }

    /**
     * Get the mode of the width dimension.
     *
     * @return Width mode, one of {@link #NATIVE_DIMENSION}, {@link #MAINTAIN_ASPECT_RATIO}, or {@link
     *         #EXPLICIT_DIMENSION}.
     */
    public int getWidthMode()
    {
        return this.widthMode;
    }

    /**
     * Get the mode of the height dimension.
     *
     * @return Height mode, one of {@link #NATIVE_DIMENSION}, {@link #MAINTAIN_ASPECT_RATIO}, or {@link
     *         #EXPLICIT_DIMENSION}.
     */
    public int getHeightMode()
    {
        return this.heightMode;
    }

    /**
     * Get the unscaled width.
     *
     * @return Unscaled width. The units of this value depend on the current height units.
     *
     * @see #getWidthMode()
     * @see #getWidthUnits()
     */
    public double getWidth()
    {
        return widthParam;
    }

    /**
     * Get the unscaled height.
     *
     * @return Unscaled height. The units of this value depend on the current height units.
     *
     * @see #getHeightMode()
     * @see #getHeightUnits()
     */
    public double getHeight()
    {
        return heightParam;
    }

    /**
     * Computes the width and height of a rectangle within a container rectangle.
     *
     * @param rectWidth       The width of the rectangle to size.
     * @param rectHeight      The height of the rectangle to size.
     * @param containerWidth  The width of the container.
     * @param containerHeight The height of the container.
     *
     * @return The desired image dimensions.
     */
    public Dimension compute(int rectWidth, int rectHeight, int containerWidth, int containerHeight)
    {
        if (rectWidth < 0)
        {
            String message = Logging.getMessage("generic.InvalidWidth", rectWidth);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (rectHeight < 0)
        {
            String message = Logging.getMessage("generic.InvalidHeight", rectHeight);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (containerWidth < 0)
        {
            String message = Logging.getMessage("generic.InvalidWidth", containerWidth);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (containerHeight < 0)
        {
            String message = Logging.getMessage("generic.InvalidHeight", containerHeight);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double aspectRatio;
        if (rectHeight != 0)
            aspectRatio = (double) rectWidth / rectHeight;
        else
            aspectRatio = 0;

        int xMode = this.getWidthMode();
        int yMode = this.getHeightMode();

        double width, height;

        if (xMode == NATIVE_DIMENSION && yMode == NATIVE_DIMENSION
            || xMode == NATIVE_DIMENSION && yMode == MAINTAIN_ASPECT_RATIO
            || xMode == MAINTAIN_ASPECT_RATIO && yMode == NATIVE_DIMENSION
            || xMode == MAINTAIN_ASPECT_RATIO && yMode == MAINTAIN_ASPECT_RATIO)
        {
            // Keep original dimensions
            width = rectWidth;
            height = rectHeight;
        }
        else if (xMode == MAINTAIN_ASPECT_RATIO)
        {
            // y dimension is specified, scale x to maintain aspect ratio
            height = computeSize(this.heightParam, this.heightUnits, containerHeight);
            width = height * aspectRatio;
        }
        else if (yMode == MAINTAIN_ASPECT_RATIO)
        {
            // x dimension is specified, scale y to maintain aspect ratio
            width = computeSize(this.widthParam, this.widthUnits, containerWidth);
            if (aspectRatio != 0)
                height = width / aspectRatio;
            else
                height = 0;
        }
        else
        {
            if (xMode == NATIVE_DIMENSION)
                width = rectWidth;
            else
                width = computeSize(this.widthParam, this.widthUnits, containerWidth);

            if (yMode == NATIVE_DIMENSION)
                height = rectHeight;
            else
                height = computeSize(this.heightParam, this.heightUnits, containerHeight);
        }

        return new Dimension((int) width, (int) height);
    }

    /**
     * Compute a dimension taking into account the units of the dimension.
     *
     * @param size               The size parameter.
     * @param units              One of {@link AVKey#PIXELS} or {@link AVKey#FRACTION}. If the {@code units} value is
     *                           not one of the expected options, {@link AVKey#PIXELS} is used as the default.
     * @param containerDimension The viewport dimension.
     *
     * @return Size in pixels
     */
    protected double computeSize(double size, String units, double containerDimension)
    {
        if (AVKey.FRACTION.equals(units))
            return size * containerDimension;
        else  // Default to pixel
            return size;
    }

    /**
     * Saves the size's current state in the specified <code>restorableSupport</code>. If <code>context</code> is not
     * <code>null</code>, the state is appended to it.  Otherwise the state is added to the
     * <code>RestorableSupport</code> root. This state can be restored later by calling {@link
     * #restoreState(gov.nasa.worldwind.util.RestorableSupport, gov.nasa.worldwind.util.RestorableSupport.StateObject)}.
     *
     * @param restorableSupport the <code>RestorableSupport</code> that receives the size's state.
     * @param context           the <code>StateObject</code> the state is appended to, if not <code>null</code>.
     *
     * @throws IllegalArgumentException if <code>restorableSupport</code> is <code>null</code>.
     */
    public void getRestorableState(RestorableSupport restorableSupport, RestorableSupport.StateObject context)
    {
        if (restorableSupport == null)
        {
            String message = Logging.getMessage("nullValue.RestorableSupportIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        RestorableSupport.StateObject so = restorableSupport.addStateObject(context, "width");
        if (so != null)
        {
            restorableSupport.addStateValueAsString(so, "mode", stringFromMode(this.getWidthMode()));
            restorableSupport.addStateValueAsDouble(so, "param", this.getWidth());

            if (this.getWidthUnits() != null)
                restorableSupport.addStateValueAsString(so, "units", this.getWidthUnits());
        }

        so = restorableSupport.addStateObject(context, "height");
        if (so != null)
        {
            restorableSupport.addStateValueAsString(so, "mode", stringFromMode(this.getHeightMode()));
            restorableSupport.addStateValueAsDouble(so, "param", this.getHeight());

            if (this.getHeightUnits() != null)
                restorableSupport.addStateValueAsString(so, "units", this.getHeightUnits());
        }
    }

    /**
     * Restores the state of any size parameters contained in the specified <code>RestorableSupport</code>. If the
     * <code>StateObject</code> is not <code>null</code> it's searched for state values, otherwise the
     * <code>RestorableSupport</code> root is searched.
     *
     * @param restorableSupport the <code>RestorableSupport</code> that contains the size's state.
     * @param context           the <code>StateObject</code> to search for state values, if not <code>null</code>.
     *
     * @throws IllegalArgumentException if <code>restorableSupport</code> is <code>null</code>.
     */
    public void restoreState(RestorableSupport restorableSupport, RestorableSupport.StateObject context)
    {
        if (restorableSupport == null)
        {
            String message = Logging.getMessage("nullValue.RestorableSupportIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        RestorableSupport.StateObject so = restorableSupport.getStateObject(context, "width");
        if (so != null)
        {
            Integer mode = null;
            String s = restorableSupport.getStateValueAsString(so, "mode");
            if (s != null)
                mode = modeFromString(s);

            Double param = restorableSupport.getStateValueAsDouble(so, "param");
            String units = restorableSupport.getStateValueAsString(so, "units");

            // Restore the width only when the mode and param are specified. null is an acceptable value for units.
            if (mode != null && param != null)
                this.setWidth(mode, param, units);
        }

        so = restorableSupport.getStateObject(context, "height");
        if (so != null)
        {
            Integer mode = null;
            String s = restorableSupport.getStateValueAsString(so, "mode");
            if (s != null)
                mode = modeFromString(s);

            Double param = restorableSupport.getStateValueAsDouble(so, "param");
            String units = restorableSupport.getStateValueAsString(so, "units");

            // Restore the height only when the mode and param are specified. null is an acceptable value for units.
            if (mode != null && param != null)
                this.setHeight(mode, param, units);
        }
    }

    /**
     * Converts the specified <code>string</code> from a mode in human-readable <code>String</code> form to an integer
     * mode constant. This returns <code>null</code> if <code>string</code> is not recognized.
     *
     * @param string the human-readable <code>String</code> to convert to a integer mode constant.
     *
     * @return an integer mode constant, one of <code>NATIVE_DIMENSION</code>, <code>MAINTAIN_ASPECT_RATIO</code>,
     *         <code>EXPLICIT_DIMENSION</code>, or <code>null</code> if <code>string</code> is not recognized.
     */
    protected static Integer modeFromString(String string)
    {
        if ("NativeDimension".equals(string))
            return NATIVE_DIMENSION;
        else if ("MaintainAspectRatio".equals(string))
            return MAINTAIN_ASPECT_RATIO;
        else if ("ExplicitDimension".equals(string))
            return EXPLICIT_DIMENSION;
        else
            return null;
    }

    /**
     * Converts the specified <code>mode</code> to a human-readable <code>String</code>. This returns <code>null</code>
     * if <code>mode</code> is not one of <code>NATIVE_DIMENSION</code>, <code>MAINTAIN_ASPECT_RATIO</code>, or
     * <code>EXPLICIT_DIMENSION</code>.
     *
     * @param mode the mode to convert to a <code>String</code>.
     *
     * @return the specified <code>mode</code> in <code>String</code> form, or <code>null</code> if <code>mode</code> is
     *         not a recognized constant.
     */
    protected static String stringFromMode(int mode)
    {
        if (mode == NATIVE_DIMENSION)
            return "NativeDimension";
        else if (mode == MAINTAIN_ASPECT_RATIO)
            return "MaintainAspectRatio";
        else if (mode == EXPLICIT_DIMENSION)
            return "ExplicitDimension";
        else
            return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Size that = (Size) o;

        if (Double.compare(this.widthParam, that.widthParam) != 0)
            return false;
        if (Double.compare(this.heightParam, that.heightParam) != 0)
            return false;
        if (this.widthUnits != null ? !this.widthUnits.equals(that.widthUnits) : that.widthUnits != null)
            return false;
        if (this.heightUnits != null ? !this.heightUnits.equals(that.heightUnits) : that.heightUnits != null)
            return false;
        if (this.widthMode != that.widthMode)
            return false;
        //noinspection RedundantIfStatement
        if (this.heightMode != that.heightMode)
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = this.widthParam != +0.0d ? Double.doubleToLongBits(this.widthParam) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = this.heightParam != +0.0d ? Double.doubleToLongBits(this.heightParam) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (this.widthUnits != null ? this.widthUnits.hashCode() : 0);
        result = 31 * result + (this.heightUnits != null ? this.heightUnits.hashCode() : 0);
        result = 31 * result + this.widthMode;
        result = 31 * result + this.heightMode;
        return result;
    }
}
