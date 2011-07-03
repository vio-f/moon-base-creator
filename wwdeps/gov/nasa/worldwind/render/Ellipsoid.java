/*
Copyright (C) 2001, 2011 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.render;

import com.sun.opengl.util.BufferUtil;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.airspaces.Geometry;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import javax.xml.stream.*;
import java.io.IOException;
import java.net.URL;
import java.nio.*;
import java.util.Vector;

/**
 * A general ellipsoid volume defined by a center position and the three ellipsoid axis radii. If A is the radius in the
 * north-south direction, and b is the radius in the east-west direction, and c is the radius in the vertical direction
 * (increasing altitude), then A == B == C defines a sphere, A == B > C defines a vertically flattened spheroid
 * (disk-shaped), A == B < C defines a vertically stretched spheroid.
 *
 * @author tag
 * @version $Id: Ellipsoid.java 14763 2011-02-19 00:28:56Z ccrick $
 */
public class Ellipsoid extends AbstractShape
{
    /**
     * Maintains globe-dependent computed data such as Cartesian vertices and extents. One entry exists for each
     * distinct globe that this shape encounters in calls to {@link AbstractShape#render(DrawContext)}. See {@link
     * AbstractShape}.
     */
    protected static class ShapeData extends AbstractShapeData
    {
        /** Holds the computed tessellation of the ellipsoid in model coordinates. */
        protected Geometry mesh;

        public ShapeData(DrawContext dc)
        {
            super(dc, 0, 0); // specify 0 as expiry time since only size/position transform changes with time
        }

        public Geometry getMesh()
        {
            return mesh;
        }

        public void setMesh(Geometry mesh)
        {
            this.mesh = mesh;
        }
    }

    @Override
    protected AbstractShapeData createCacheEntry(DrawContext dc)
    {
        return new ShapeData(dc);
    }

    /**
     * Returns the current shape data cache entry.
     *
     * @return the current data cache entry.
     */
    protected ShapeData getCurrentShapeData()
    {
        return (ShapeData) this.getCurrentData();
    }

    protected static final int DEFAULT_SUBDIVISIONS = 2;

    protected Position centerPosition = Position.ZERO;
    protected double northSouthRadius = 1; // radius in the north-south (latitudinal) direction
    protected double verticalRadius = 1; // radius in the vertical direction
    protected double eastWestRadius = 1; // radius in the east-west (longitudinal) direction

    protected Angle heading; // rotation about vertical axis, positive counter-clockwise
    protected Angle tilt; // rotation about east-west axis, positive counter-clockwise
    protected Angle roll; // rotation about north-south axis, positive counter-clockwise

    protected double northSouthScalingFactor = 1;  // scaling factor along the north-south axis
    protected double verticalScalingFactor = 1;   // scaling factor along the vertical axis
    protected double eastWestScalingFactor = 1;   // scaling factor along the east-west axis

    protected boolean renderExtent = false;

    // Textures
    protected Object imageSource; // image source for the optional texture
    protected WWTexture texture; // an optional texture for the base polygon

    // Geometry.
    protected double detailHint = 0;
    protected int subdivisions = DEFAULT_SUBDIVISIONS;
    protected GeometryBuilder geometryBuilder = new GeometryBuilder();

    // Key values for Level of Detail-related key-value pairs
    protected static final String GEOMETRY_CACHE_KEY = Geometry.class.getName();
    protected static final long DEFAULT_GEOMETRY_CACHE_SIZE = 16777216L; // 16 megabytes
    protected static final String GEOMETRY_CACHE_NAME = "Airspace Geometry"; // use same cache as Airspaces

    /** Construct a default ellipsoid with centerPosition ZERO and radii all equal to one. */
    public Ellipsoid()
    {
        this.setUpGeometryCache();
    }

    /**
     * Construct an ellipsoid from a specified center position and axes lengths.
     *
     * @param centerPosition   the ellipsoid's center position.
     * @param northSouthRadius the ellipsoid's north-south radius, in meters.
     * @param verticalRadius   the ellipsoid's vertical radius, in meters.
     * @param eastWestRadius   the ellipsoid's east-west radius, in meters.
     *
     * @throws IllegalArgumentException if the center position is null or any of the radii are not greater than 0.
     */
    public Ellipsoid(Position centerPosition, double northSouthRadius, double verticalRadius, double eastWestRadius)
    {
        if (centerPosition == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (northSouthRadius <= 0 || eastWestRadius <= 0 || verticalRadius <= 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "radius <= 0");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.centerPosition = centerPosition;
        this.northSouthRadius = northSouthRadius;
        this.verticalRadius = verticalRadius;
        this.eastWestRadius = eastWestRadius;

        this.setUpGeometryCache();
    }

    /**
     * Construct an ellipsoid from a specified center position, axes lengths and rotation angles. All angles are
     * specified in degrees and positive angles are counter-clockwise.
     *
     * @param centerPosition   the ellipsoid's center position.
     * @param northSouthRadius the ellipsoid's north-south radius, in meters.
     * @param verticalRadius   the ellipsoid's vertical radius, in meters.
     * @param eastWestRadius   the ellipsoid's east-west radius, in meters.
     * @param heading          the ellipsoid's azimuth, its rotation about its vertical axis.
     * @param tilt             the ellipsoids pitch, its rotation about its east-west axis.
     * @param roll             the ellipsoid's roll, its rotation about its north-south axis.
     */
    public Ellipsoid(Position centerPosition, double northSouthRadius, double verticalRadius, double eastWestRadius,
        Angle heading, Angle tilt, Angle roll)
    {
        if (centerPosition == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (northSouthRadius <= 0 || eastWestRadius <= 0 || verticalRadius <= 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "radius <= 0");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.centerPosition = centerPosition;
        this.northSouthRadius = northSouthRadius;
        this.verticalRadius = verticalRadius;
        this.eastWestRadius = eastWestRadius;
        this.heading = heading;
        this.tilt = tilt;
        this.roll = roll;

        this.setUpGeometryCache();
    }

    @Override
    protected void initialize()
    {
        // Nothing to override
    }

    /**
     * Indicates this ellipsoid's center position.
     *
     * @return this ellipsoid's center position.
     */
    public Position getCenterPosition()
    {
        return centerPosition;
    }

    /**
     * Specifies this ellipsoid's center position.
     *
     * @param centerPosition this ellipsoid's center position.
     */
    public void setCenterPosition(Position centerPosition)
    {
        if (centerPosition == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        this.centerPosition = centerPosition;

        reset();
    }

    /**
     * Returns the Ellipsoid's referencePosition, which is its centerPosition
     *
     * @return  the centerPosition of the Ellipsoid
     */
    public Position getReferencePosition()
    {
        return this.centerPosition;
    }

    /**
     * Indicates the radius of this ellipsoid's axis in the north-south (latitudinal) direction.
     *
     * @return this ellipsoid's radius in the north-south direction.
     */
    public double getNorthSouthRadius()
    {
        return northSouthRadius;
    }

    /**
     * Specifies this ellipsoid's radius in meters in the north-south (latitudinal) direction.
     * The radius must be greater than 0.
     *
     * @param northSouthRadius the ellipsoid radius in the north-south direction. Must be greater than 0.
     *
     * @throws IllegalArgumentException if the radius is not greater than 0.
     */
    public void setNorthSouthRadius(double northSouthRadius)
    {
        if (northSouthRadius <= 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "northSouthRadius <= 0");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.northSouthRadius = northSouthRadius;

        reset();
    }

    /**
     * Indicates the radius of this ellipsoid's axis in the east-west (longitudinal) direction.
     *
     * @return this ellipsoid's radius in the east-west direction.
     */
    public double getEastWestRadius()
    {
        return eastWestRadius;
    }

    /**
     * Specifies this ellipsoid's radius in meters in the east-west (longitudinal) direction.
     * The radius must be greater than 0.
     *
     * @param eastWestRadius the ellipsoid radius in the east-west direction. Must be greater than 0.
     *
     * @throws IllegalArgumentException if the radius is not greater than 0.
     */
    public void setEastWestRadius(double eastWestRadius)
    {
        if (eastWestRadius <= 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "eastWestRadius <= 0");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.eastWestRadius = eastWestRadius;

        reset();
    }

    /**
     * Indicates the radius of this ellipsoid's axis in the vertical (altitudinal) direction.
     *
     * @return this ellipsoid's radius in the vertical direction.
     */
    public double getVerticalRadius()
    {
        return verticalRadius;
    }

    /**
     * Specifies this ellipsoid's radius in meters in the vertical (altitudinal) direction. The radius must be greater
     * than 0.
     *
     * @param verticalRadius the ellipsoid radius in the vertical direction. Must be greater than 0.
     *
     * @throws IllegalArgumentException if the radius is not greater than 0.
     */
    public void setVerticalRadius(double verticalRadius)
    {
        if (verticalRadius <= 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "verticalRadius <= 0");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.verticalRadius = verticalRadius;

        reset();
    }

    /**
     * Indicates this ellipsoid's azimuth, its rotation about its vertical axis. North corresponds to an azimuth of 0.
     * Angles are in degrees and positive counter-clockwise.
     *
     * @return this ellipsoid's azimuth.
     */
    public Angle getHeading()
    {
        return this.heading;
    }

    /**
     * Specifies this ellipsoid's azimuth, its rotation about its vertical axis. North corresponds to an azimuth of 0.
     * Angles are in degrees and positive counter-clockwise.
     *
     * @param heading the ellipsoid's azimuth, in degrees.
     */
    public void setHeading(Angle heading)
    {
        this.heading = heading;

        reset();
    }

    /**
     * Indicates this ellipsoid's pitch, its rotation about its east-west axis. Angles are positive counter-clockwise.
     *
     * @return this ellipsoid's azimuth.
     */
    public Angle getTilt()
    {
        return this.tilt;
    }

    /**
     * Specifies this ellipsoid's pitch, its rotation about its east-west axis. Angles are positive counter-clockwise.
     *
     * @param tilt the ellipsoid's pitch, in degrees.
     */
    public void setTilt(Angle tilt)
    {
        this.tilt = tilt;

        reset();
    }

    /**
     * Indicates this ellipsoid's roll, its rotation about its north-south axis. Angles are positive counter-clockwise.
     *
     * @return this ellipsoid's azimuth.
     */
    public Angle getRoll()
    {
        return roll;
    }

    /**
     * Specifies this ellipsoid's roll, its rotation about its north-south axis. Angles are in degrees and positive
     * counter-clockwise.
     *
     * @param roll the ellipsoid's azimuth, in degrees.
     */
    public void setRoll(Angle roll)
    {
        this.roll = roll;

        reset();
    }

    /**
     * Retrieves the Ellipsoid's scaling factor along its north-south axis.
     *
     * @return this ellipsoid's north-south scaling factor.
     */
    public double getNorthSouthScalingFactor()
    {
        return this.northSouthScalingFactor;
    }

    /**
     * Specifies this ellipsoid's scaling factor along its north-south axis.
     *
     * @param northSouthScalingFactor the ellipsoid's north-south scaling factor.
     */
    public void setNorthSouthScalingFactor(double northSouthScalingFactor)
    {
        this.northSouthScalingFactor = northSouthScalingFactor;

        reset();
    }

    /**
     * Retrieves the Ellipsoid's scaling factor along its vertical axis.
     *
     * @return this ellipsoid's vertical scaling factor.
     */
    public double getVerticalScalingFactor()
    {
        return this.verticalScalingFactor;
    }

    /**
     * Specifies this ellipsoid's scaling factor along its vertical axis.
     *
     * @param verticalScalingFactor the ellipsoid's vertical scaling factor.
     */
    public void setVerticalScalingFactor(double verticalScalingFactor)
    {
        this.verticalScalingFactor = verticalScalingFactor;

        reset();
    }

    /**
     * Retrieves the Ellipsoid's scaling factor along its east-west axis.
     *
     * @return this ellipsoid's east-west scaling factor.
     */
    public double getEastWestScalingFactor()
    {
        return this.eastWestScalingFactor;
    }

    /**
     * Specifies this ellipsoid's scaling factor along its east-west axis.
     *
     * @param eastWestScalingFactor the ellipsoid's east-west scaling factor.
     */
    public void setEastWestScalingFactor(double eastWestScalingFactor)
    {
        this.eastWestScalingFactor = eastWestScalingFactor;

        reset();
    }

    /**
     * Indicates the Ellipsoid's detail hint, which is described in {@link #setDetailHint(double)}.
     *
     * @return the detail hint
     *
     * @see #setDetailHint(double)
     */
    public double getDetailHint()
    {
        return this.detailHint;
    }

    /**
     * Modifies the relationship of the Ellipsoid's tessellation resolution to its distance from the eye. Values greater
     * than 0 cause higher resolution tessellation, but at an increased performance cost. Values less than 0 decrease
     * the default tessellation resolution at any given distance from the eye. The default value is 0. Values typically
     * range between -0.5 and 0.5.
     *
     * @param detailHint the degree to modify the default tessellation resolution of the Ellipsoid. Values greater than
     *                   1 increase the resolution. Values less than zero decrease the resolution. The default value is
     *                   0.
     */
    public void setDetailHint(double detailHint)
    {
        this.detailHint = detailHint;
    }

    /**
     * Indicates the image source for this ellipsoid's optional texture.
     *
     * @return the image source of the ellipsoid's texture.
     */
    public Object getImageSource()
    {
        return this.imageSource;
    }

    /**
     * Specifies the image source for this ellipsoid's optional texture. Texture coordinates are automatically
     * generated. The horizontal texture coordinate origin is at the top-most point of the ellipsoid, the vertical
     * origin is the southern-most point of the ellipsoid.
     *
     * @param imageSource the texture image source. May be a {@link java.io.File}, file path, a stream, a URL or a
     *                    {@link java.awt.image.BufferedImage}.
     */
    public void setImageSource(Object imageSource)
    {
        this.imageSource = imageSource;
    }

    /**
     * Get the texture applied to this Ellipsoid. The texture is loaded on a background thread. This method will return
     * null until the texture has been loaded.
     *
     * @return the texture, or null if there is no texture or the texture is not yet available.
     */
    protected WWTexture getTexture()
    {
        if (this.texture != null)
            return this.texture;
        else
            return this.initializeTexture();
    }

    @Override
    protected boolean mustApplyTexture(DrawContext dc)
    {
        return !dc.isPickingMode() && this.getTexture() != null
            && this.getCurrentShapeData().getMesh().getBuffer(Geometry.TEXTURE) != null;
    }

    /** Create the geometry cache supporting the Level of Detail system. */
    protected void setUpGeometryCache()
    {
        if (!WorldWind.getMemoryCacheSet().containsCache(GEOMETRY_CACHE_KEY))
        {
            long size = Configuration.getLongValue(AVKey.AIRSPACE_GEOMETRY_CACHE_SIZE, DEFAULT_GEOMETRY_CACHE_SIZE);
            MemoryCache cache = new BasicMemoryCache((long) (0.85 * size), size);
            cache.setName(GEOMETRY_CACHE_NAME);
            WorldWind.getMemoryCacheSet().addCache(GEOMETRY_CACHE_KEY, cache);
        }
    }

    /**
     * Retrieve the geometry cache supporting the Level of Detail system.
     *
     * @return the geometry cache.
     */
    protected MemoryCache getGeometryCache()
    {
        return WorldWind.getMemoryCache(GEOMETRY_CACHE_KEY);
    }

    @Override
    protected boolean doMakeOrderedRenderable(DrawContext dc)
    {
        ShapeData shapeData = this.getCurrentShapeData();

        Vec4 refPt = this.computeReferencePoint(dc);
        if (refPt == null)
            return false;
        shapeData.setReferencePoint(refPt);

        shapeData.setEyeDistance(dc.getView().getEyePoint().distanceTo3(refPt));

        // determine with how many subdivisions the geometry should be tessellated
        computeSubdivisions(dc, shapeData);

        // Recompute tessellated positions because the geometry or view may have changed.
        this.makeGeometry(shapeData);

        if (shapeData.getMesh().getBuffer(Geometry.VERTEX) != null)
            shapeData.setExtent(this.computeExtent(dc));

        // If the shape is less that a pixel in size, don't render it.
        if (shapeData.getExtent() == null || dc.isSmall(shapeData.getExtent(), 1))
            return false;

        if (!this.intersectsFrustum(dc))
            return false;

        return !(shapeData.getMesh() == null || shapeData.getMesh().getBuffer(Geometry.VERTEX) == null
            || shapeData.getMesh().getCount(Geometry.VERTEX) < 2);
    }

    @Override
    protected boolean isOrderedRenderableValid(DrawContext dc)
    {
        return this.getCurrentShapeData().getMesh().getBuffer(Geometry.VERTEX) != null;
    }

    @Override
    protected OGLStackHandler beginDrawing(DrawContext dc, int attrMask)
    {
        OGLStackHandler ogsh = super.beginDrawing(dc, attrMask);

        if (!dc.isPickingMode())
        {
            // Push an identity texture matrix. This prevents drawGeometry() from leaking GL texture matrix state. The
            // texture matrix stack is popped from OGLStackHandler.pop().
            ogsh.pushTextureIdentity(dc.getGL());
        }

        return ogsh;
    }

    @Override
    protected void doDrawOutline(DrawContext dc)
    {
        Geometry mesh = this.getCurrentShapeData().getMesh();

        // set to draw using GL_LINES
        mesh.setMode(Geometry.ELEMENT, GL.GL_LINES);

        // set up MODELVIEW matrix to properly position, orient and scale this shape
        setModelViewMatrix(dc);

        this.drawGeometry(dc, this.getCurrentShapeData());

        // return render mode to GL_TRIANGLE
        mesh.setMode(Geometry.ELEMENT, GL.GL_TRIANGLES);
    }

    @Override
    protected void doDrawInterior(DrawContext dc)
    {
        GL gl = dc.getGL();

        // render extent if specified
        if (this.renderExtent)
        {
            Box extent = (Box) this.getCurrentShapeData().getExtent();
            extent.render(dc);
        }

        // set up MODELVIEW matrix to properly position, orient and scale this shape
        setModelViewMatrix(dc);

        // set up the texture if one exists
        if (mustApplyTexture(dc))
        {
            WWTexture texture = getTexture();
            texture.bind(dc);
            texture.applyInternalTransform(dc);

            Geometry mesh = this.getCurrentShapeData().getMesh();
            gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, mesh.getBuffer(Geometry.TEXTURE).rewind());
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        }
        else
        {
            gl.glDisable(GL.GL_TEXTURE_2D);
            gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        }

        drawGeometry(dc, this.getCurrentShapeData());
    }

    /**
     * Computes the Ellipsoid's extent using a bounding box.
     *
     * @param dc the current drawContext
     *
     * @return the computed extent.
     */
    protected Extent computeExtent(DrawContext dc)
    {
        Matrix matrix = computeRenderMatrix(dc);

        // create a list of vertices representing the extrema of the unit sphere
        Vector<Vec4> extrema = new Vector<Vec4>(4);
        // transform the extrema by the render matrix to get their final positions
        Vec4 point = matrix.transformBy3(matrix, -1, 1, -1);   // far upper left
        extrema.add(point);
        point = matrix.transformBy3(matrix, 1, 1, 1);   // near upper right
        extrema.add(point);
        point = matrix.transformBy3(matrix, 1, -1, -1);   // near lower left
        extrema.add(point);
        point = matrix.transformBy3(matrix, -1, -1, 1);   // far lower right
        extrema.add(point);
        Box boundingBox = Box.computeBoundingBox(extrema);

        Vec4 centerPoint = getCurrentData().getReferencePoint();

        return boundingBox != null ? boundingBox.translate(centerPoint) : null;
    }

    /**
     * Computes the Ellipsoid's extent using a bounding box.
     *
     * @param globe                the current globe
     * @param verticalExaggeration the current vertical exaggeration
     *
     * @return the computed extent.
     *
     * @throws IllegalArgumentException if the globe is null.
     */
    public Extent getExtent(Globe globe, double verticalExaggeration)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // compute a bounding box for vertices transformed to their world coordinates
        Matrix matrix = computeRenderMatrix(globe, verticalExaggeration);

        // create a list of vertices representing the extrema of the unit sphere
        Vector<Vec4> extrema = new Vector<Vec4>(4);
        // transform the extrema by the render matrix to get their final positions
        Vec4 point = matrix.transformBy3(matrix, -1, 1, -1);   // far upper left
        extrema.add(point);
        point = matrix.transformBy3(matrix, 1, 1, 1);   // near upper right
        extrema.add(point);
        point = matrix.transformBy3(matrix, 1, -1, -1);   // near lower left
        extrema.add(point);
        point = matrix.transformBy3(matrix, -1, -1, 1);   // far lower right
        extrema.add(point);
        Box boundingBox = Box.computeBoundingBox(extrema);

        // get - or compute - the center point, in global coordinates
        Position pos = this.getCenterPosition();
        if (pos == null)
            return null;

        Vec4 centerPoint = this.computeReferencePoint(globe, verticalExaggeration);

        // translate the bounding box to its correct location
        return boundingBox != null ? boundingBox.translate(centerPoint) : null;
    }

    /**
     * Computes the Ellipsoid's sector.  Not currently supported.
     *
     * @return the bounding sector for this Ellipsoid
     */
    public Sector getSector()
    {
            String message = Logging.getMessage("unsupportedOperation.getSector");
            Logging.logger().severe(message);
            throw new UnsupportedOperationException(message);
    }

    /**
     * Create and initialize this texture from the image source. If the image is not in memory this method will request
     * that it be loaded and return null.
     *
     * @return the texture, or null if the texture is not yet available.
     */
    protected WWTexture initializeTexture()
    {
        Object imageSource = this.getImageSource();
        if (imageSource instanceof String || imageSource instanceof URL)
        {
            URL imageURL = WorldWind.getDataFileStore().requestFile(imageSource.toString());
            if (imageURL != null)
            {
                this.texture = new BasicWWTexture(imageURL, true);
                return this.texture;
            }
            // Else wait for the retriever to retrieve the image before creating the texture
        }
        else if (imageSource != null)
        {
            this.texture = new BasicWWTexture(imageSource, true);
            return this.texture;
        }

        return null;
    }

    /**
     * Sets the Ellipsoid's referencePoint, which is essentially its centerPosition in Cartesian coordinates.
     *
     * @param dc the current DrawContext
     *
     * @return the computed reference point relative to the globe associated with the draw context.
     */
    protected Vec4 computeReferencePoint(DrawContext dc)
    {
        Position pos = this.getCenterPosition();
        if (pos == null)
            return null;

        return computePoint(dc.getTerrain(), pos);
    }

    /**
     * Sets the Ellipsoid's referencePoint, which is essentially its centerPosition in Cartesian coordinates.
     *
     * @param globe                the current globe
     * @param verticalExaggeration the current vertical exaggeration
     *
     * @return the computed reference point, or null if the point could not be computed.
     */
    protected Vec4 computeReferencePoint(Globe globe, double verticalExaggeration)
    {
        Position pos = this.getCenterPosition();
        if (pos == null)
            return null;

        double elevation = globe.getElevation(pos.latitude, pos.longitude);

        double height;
        if (this.getAltitudeMode() == WorldWind.CLAMP_TO_GROUND)
            height = 0d + elevation * verticalExaggeration;
        else if (this.getAltitudeMode() == WorldWind.RELATIVE_TO_GROUND)
            height = pos.getAltitude() + elevation * verticalExaggeration;
        else    // ABSOLUTE elevation mode
        {
            // Raise the shape to accommodate vertical exaggeration applied to the terrain.
            height = pos.getAltitude() * verticalExaggeration;
        }

        return globe.computePointFromPosition(pos, height);
    }

    /*
    // Old extent calculation using a bounding sphere
    protected Extent computeExtent(DrawContext dc)
    {

        // use the longest radius as the bounding sphere's radius
        double a = this.getNorthSouthRadius();
        double b = this.getVerticalRadius();
        double c = this.getEastWestRadius();
        double longestRadius = (a > b) ? a : b;
        longestRadius = (longestRadius > c) ? longestRadius : c;

        Vec4 centerPoint = getReferencePoint(dc);

        return new gov.nasa.worldwind.geom.Sphere(centerPoint, longestRadius);
    }
     */

    /**
     * Transform all vertices with the provided matrix
     *
     * @param vertices    the buffer of vertices to transform
     * @param numVertices the number of distinct vertices in the buffer (assume 3-space)
     * @param matrix      the matrix for transforming the vertices
     *
     * @return the transformed vertices.
     */
    protected FloatBuffer computeTransformedVertices(FloatBuffer vertices, int numVertices, Matrix matrix)
    {
        int size = vertices.capacity();
        FloatBuffer newVertices = BufferUtil.newFloatBuffer(size);

        // transform all vertices by the render matrix
        for (int i = 0; i < numVertices; i++)
        {
            Vec4 point = matrix.transformBy3(matrix, vertices.get(i), vertices.get(i + 1), vertices.get(i + 2));
            newVertices.put((float) point.getX()).put((float) point.getY()).put((float) point.getZ());
        }

        newVertices.rewind();

        return newVertices;
    }

    /**
     * Computes a threshold value, based on the current detailHint, for use in the sufficientDetail() calculation.
     *
     * @return the detailThreshold
     */
    protected double computeDetailThreshold()
    {
        // these values must be calibrated on a shape-by-shape basis
        double detailThreshold = 0.04;
        double rangeDetailThreshold = 0.039;

        detailThreshold += this.getDetailHint() * rangeDetailThreshold;

        return detailThreshold;
    }

    /**
     * Computes the number of subdivisions necessary to achieve the expected Level of Detail given the shape's
     * relationship to the viewer.
     *
     * @param dc        the current drawContext.
     * @param shapeData the current globe-specific shape data
     */
    protected void computeSubdivisions(DrawContext dc, ShapeData shapeData)
    {
        // test again possible subdivision values
        int minDivisions = 0;
        int maxDivisions = 6;

        if (shapeData.getExtent() != null)
        {
            for (int divisions = minDivisions; divisions <= maxDivisions; divisions++)
            {
                this.subdivisions = divisions;
                if (this.sufficientDetail(dc, divisions, shapeData))
                    break;
            }
        }
    }

    /**
     * Determines whether a given number of model subdivisions will provide enough tessellation detail (LOD) to the
     * shape, given its current size and distance from the viewer.
     *
     * @param dc           the current drawContext
     * @param subdivisions the number of subdivisions to test
     * @param shapeData    the current globe-specific shape data
     *
     * @return whether the provided subdivisions will provide a sufficient level of detail
     */
    protected boolean sufficientDetail(DrawContext dc, int subdivisions, ShapeData shapeData)
    {
        if (dc.getView() == null)
        {
            String message = "nullValue.DrawingContextViewIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (subdivisions < 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "subdivisions < 0");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Extent extent = shapeData.getExtent();
        if (extent == null)
            return true;

        double thresholdDensity = this.computeDetailThreshold();

        double d = dc.getView().getEyePoint().distanceTo3(extent.getCenter());
        double pixelSize = dc.getView().computePixelSizeAtDistance(d);
        double shapeScreenSize = extent.getDiameter() / pixelSize;

        // formula for this object's current vertex density
        double vertexDensity = Math.pow(subdivisions, 3) / shapeScreenSize;

        return vertexDensity > thresholdDensity;
    }

    /**
     * Computes the transform to use during rendering to convert the unit sphere geometry representation of this
     * Ellipsoid to its correct Ellipsoid location, orientation and scale
     *
     * @param globe                the current globe
     * @param verticalExaggeration the current vertical exaggeration
     *
     * @return the modelview transform for this Ellipsoid
     *
     * @throws IllegalArgumentException if globe is null
     */
    protected Matrix computeRenderMatrix(Globe globe, double verticalExaggeration)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Matrix matrix = Matrix.IDENTITY;

        // translate and orient
        Position refPosition = globe.computePositionFromPoint(this.computeReferencePoint(globe, verticalExaggeration));
        matrix = matrix.multiply(globe.computeSurfaceOrientationAtPosition(refPosition));

        // now apply the user-specified heading/tilt/roll:

        // heading
        if(heading != null)
            matrix = matrix.multiply(Matrix.fromRotationZ(Angle.POS360.subtract(this.heading)));
        // tilt
        if(tilt != null)
            matrix = matrix.multiply(Matrix.fromRotationX(this.tilt));
        // roll
        if(roll != null)
            matrix = matrix.multiply(Matrix.fromRotationY(this.roll));

        // finally, scale it along each of the axis
        matrix = matrix.multiply(Matrix.fromScale(this.getEastWestRadius() * this.eastWestScalingFactor,
            this.getNorthSouthRadius() * this.northSouthScalingFactor,
            this.getVerticalRadius() * this.verticalScalingFactor));

        return matrix;
    }

    /**
     * Computes the transform to use during rendering to convert the unit sphere geometry representation of this
     * Ellipsoid to its correct Ellipsoid location, orientation and scale
     *
     * @param dc the current draw context
     *
     * @return the modelview transform for this Ellipsoid
     *
     * @throws IllegalArgumentException if draw context is null or the referencePoint is null
     */
    protected Matrix computeRenderMatrix(DrawContext dc)
    {
        Matrix matrix = Matrix.IDENTITY;

        // translate and orient
        Position refPosition = dc.getGlobe().computePositionFromPoint(this.getCurrentShapeData().getReferencePoint());
        matrix = matrix.multiply(dc.getGlobe().computeSurfaceOrientationAtPosition(refPosition));

        // now apply the user-specified heading/tilt/roll:

        // heading
        if(heading != null)
            matrix = matrix.multiply(Matrix.fromRotationZ(Angle.POS360.subtract(this.heading)));
        // tilt
        if(tilt != null)
            matrix = matrix.multiply(Matrix.fromRotationX(this.tilt));
        // roll
        if(roll != null)
            matrix = matrix.multiply(Matrix.fromRotationY(this.roll));

        // finally, scale it along each of the axis
        matrix = matrix.multiply(Matrix.fromScale(this.getEastWestRadius() * this.eastWestScalingFactor,
            this.getNorthSouthRadius() * this.northSouthScalingFactor,
            this.getVerticalRadius() * this.verticalScalingFactor));

        return matrix;
    }

    /**
     * Called during drawing to set the modelview matrix to apply the correct position, scale
     * and orientation for this Ellipsoid.
     *
     * @param dc    the current DrawContext
     *
     * @throws IllegalArgumentException if draw context is null or the draw context GL is null
     */
    protected void setModelViewMatrix(DrawContext dc)
    {
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        Matrix matrix = dc.getView().getModelviewMatrix();
        matrix = matrix.multiply(computeRenderMatrix(dc));

        GL gl = dc.getGL();

        // Were applying a scale transform on the modelview matrix, so the normal vectors must be re-normalized
        // before lighting is computed.
        gl.glEnable(GL.GL_NORMALIZE);

        gl.glMatrixMode(GL.GL_MODELVIEW);

        double[] matrixArray = new double[16];
        matrix.toArray(matrixArray, 0, false);
        gl.glLoadMatrixd(matrixArray, 0);
    }

    //**************************************************************//
    //********************  Geometry Rendering  ********************//
    //**************************************************************//

    protected GeometryBuilder getGeometryBuilder()
    {
        return this.geometryBuilder;
    }

    /**
     * Sets the Geometry mesh for this Ellipsoid, either by pulling it from the geometryCache, or by creating it anew if
     * the appropriate geometry does not yet exist in the cache.
     *
     * @param shapeData the current shape data.
     */
    protected void makeGeometry(ShapeData shapeData)
    {
        // attempt to retrieve a cached unit ellipsoid with the same number of subdivisions
        Object cacheKey = new Geometry.CacheKey(this.getClass(), "Sphere", this.subdivisions);
        Geometry geom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (geom == null)
        {
            // if none exists, create a new one
            shapeData.setMesh(new Geometry());
            makeUnitSphere(this.subdivisions, shapeData.getMesh());
            //this.restart(dc, geom);
            this.getGeometryCache().add(cacheKey, shapeData.getMesh());
        }
        else
        {
            // otherwise, just use the one from the cache
            shapeData.setMesh(geom);
        }
    }

   /**
     * Generates a unit sphere geometry, including the vertices, indices, normals and texture coordinates,
    *  tessellated with the specified number of divisions.
     *
     * @param subdivisions the number of times to subdivide the unit sphere geometry
     * @param dest         the Geometry container to hold the computed points, etc.
     */
    protected void makeUnitSphere(int subdivisions, Geometry dest)
    {
        float radius = 1.0f;

        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(GeometryBuilder.OUTSIDE);

        // create ellipsoid in model space
        GeometryBuilder.IndexedTriangleBuffer itb =
            gb.tessellateSphereBuffer(radius, subdivisions);

        // add extra vertices so that texture will not have a seam
        int seamVerticesIndex = itb.getVertexCount();
        gb.fixSphereSeam(itb, (float) Math.PI);

        FloatBuffer normalBuffer = BufferUtil.newFloatBuffer(3 * itb.getVertexCount());
        gb.makeIndexedTriangleBufferNormals(itb, normalBuffer);

        FloatBuffer textureCoordBuffer = BufferUtil.newFloatBuffer(2 * itb.getVertexCount());
        gb.makeUnitSphereTextureCoordinates(itb, textureCoordBuffer, seamVerticesIndex);

        dest.setElementData(GL.GL_TRIANGLES, itb.getIndexCount(), itb.getIndices());
        dest.setVertexData(itb.getVertexCount(), itb.getVertices());
        dest.setNormalData(normalBuffer.limit(), normalBuffer);
        dest.setTextureCoordData(textureCoordBuffer.limit(), textureCoordBuffer);
    }

    /**
     * Generates ellipsoidal geometry, including the vertices, indices, normals and texture coordinates, tessellated
     * with the specified number of divisions.
     *
     * @param a            the Ellipsoid radius along the east-west axis
     * @param b            the Ellipsoid radius along the vertical axis
     * @param c            the Ellipsoid radius along the north-south axis
     * @param subdivisions the number of times to subdivide the unit sphere geometry
     * @param dest         the Geometry container to hold the computed points, etc.
     */
    protected void makeEllipsoid(double a, double b, double c, int subdivisions, Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(GeometryBuilder.OUTSIDE);

        // create ellipsoid in model space
        GeometryBuilder.IndexedTriangleBuffer itb =
            gb.tessellateEllipsoidBuffer((float) a, (float) b, (float) c, subdivisions);

        FloatBuffer normalBuffer = BufferUtil.newFloatBuffer(3 * itb.getVertexCount());
        gb.makeIndexedTriangleBufferNormals(itb, normalBuffer);

        dest.setElementData(GL.GL_TRIANGLES, itb.getIndexCount(), itb.getIndices());
        dest.setVertexData(itb.getVertexCount(), itb.getVertices());
        dest.setNormalData(normalBuffer.limit(), normalBuffer);
    }

    /**
     * Renders the Ellipsoid
     *
     * @param dc        the current draw context
     * @param shapeData the current shape data
     *
     * @throws IllegalArgumentException if the draw context is null or the element buffer is null
     */
    protected void drawGeometry(DrawContext dc, ShapeData shapeData)
    {
        Geometry mesh = shapeData.getMesh();

        if (mesh.getBuffer(Geometry.ELEMENT) == null)
        {
            String message = "nullValue.ElementBufferIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int mode, count, type;
        Buffer elementBuffer;

        mode = mesh.getMode(Geometry.ELEMENT);
        count = mesh.getCount(Geometry.ELEMENT);
        type = mesh.getGLType(Geometry.ELEMENT);
        elementBuffer = mesh.getBuffer(Geometry.ELEMENT);

        this.drawGeometry(dc, mode, count, type, elementBuffer, shapeData);
    }

    /**
     * Renders the Ellipsoid, using data from the provided buffer and the given parameters.
     *
     * @param dc            the current draw context
     * @param mode          the render mode
     * @param count         the number of elements to be drawn
     * @param type          the data type of the elements to be drawn
     * @param elementBuffer the buffer containing the list of elements to be drawn
     * @param shapeData     this shape's current globe-specific shape data
     */
    protected void drawGeometry(DrawContext dc, int mode, int count, int type, Buffer elementBuffer,
        ShapeData shapeData)
    {
        if (elementBuffer == null)
        {
            String message = "nullValue.ElementBufferIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Geometry mesh = shapeData.getMesh();

        if (mesh.getBuffer(Geometry.VERTEX) == null)
        {
            String message = "nullValue.VertexBufferIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        GL gl = dc.getGL();

        int size, glType, stride;
        Buffer vertexBuffer, normalBuffer;

        size = mesh.getSize(Geometry.VERTEX);
        glType = mesh.getGLType(Geometry.VERTEX);
        stride = mesh.getStride(Geometry.VERTEX);
        vertexBuffer = mesh.getBuffer(Geometry.VERTEX);

        normalBuffer = null;
        if (!dc.isPickingMode())
        {
            if (mustApplyLighting(dc))
            {
                normalBuffer = mesh.getBuffer(Geometry.NORMAL);
                if (normalBuffer == null)
                {
                    gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
                }
                else
                {
                    glType = mesh.getGLType(Geometry.NORMAL);
                    stride = mesh.getStride(Geometry.NORMAL);
                    gl.glNormalPointer(glType, stride, normalBuffer);
                }
            }
        }

        // cull the back face
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glFrontFace(GL.GL_CCW);

        // testing
        //dc.getGLRuntimeCapabilities().setVertexBufferObjectEnabled(true);

        // decide whether to draw with VBO's or VA's
        if (dc.getGLRuntimeCapabilities().isUseVertexBufferObject() && (this.getVboIds(dc)) != null)
        {
            // render using VBO's
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, getVboIds(dc)[0]);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, this.getVboIds(dc)[1]);

            gl.glVertexPointer(size, glType, stride, 0);

            gl.glDrawElements(mode, count, type, 0);

            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
        else
        {
            // render using vertex arrays
            gl.glVertexPointer(size, glType, stride, vertexBuffer.rewind());
            gl.glDrawElements(mode, count, type, elementBuffer);
        }

        // turn off normals rescaling, which was turned on because shape had to be scaled
        gl.glDisable(GL.GL_RESCALE_NORMAL);

        // disable back face culling
        gl.glDisable(GL.GL_CULL_FACE);

        if (!dc.isPickingMode())
        {
            if (mustApplyLighting(dc))
            {
                // re-enable normals if we temporarily turned them off earlier
                if (normalBuffer == null)
                    gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
            }
            // this.logGeometryStatistics(dc, geom);
        }
    }

    /**
     * Fill this shape's vertex buffer objects. If the vertex buffer object resource IDs don't yet exist, create them.
     *
     * @param dc the current draw context.
     */
    protected void fillVBO(DrawContext dc)
    {
        GL gl = dc.getGL();
        ShapeData shapeData = this.getCurrentShapeData();
        Geometry mesh = shapeData.getMesh();

        int[] vboIds = (int[]) dc.getGpuResourceCache().get(shapeData.getVboCacheKey());
        if (vboIds == null)
        {
            int size = mesh.getBuffer(Geometry.VERTEX).limit() * BufferUtil.SIZEOF_FLOAT;
            size += mesh.getBuffer(Geometry.ELEMENT).limit() * BufferUtil.SIZEOF_FLOAT;

            vboIds = new int[2];
            gl.glGenBuffers(vboIds.length, vboIds, 0);
            dc.getGpuResourceCache().put(shapeData.getVboCacheKey(), vboIds, GpuResourceCache.VBO_BUFFERS, size);
            mesh.setRefillIndexVBO(true);
        }

        if (mesh.getRefillIndexVBO())
        {
            try
            {
                IntBuffer ib = (IntBuffer) mesh.getBuffer(Geometry.ELEMENT);
                gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);
                gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, ib.limit() * BufferUtil.SIZEOF_FLOAT, ib.rewind(),
                    GL.GL_DYNAMIC_DRAW);

                mesh.setRefillIndexVBO(false);
            }
            finally
            {
                gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }

        try
        {
            FloatBuffer vb = (FloatBuffer) mesh.getBuffer(Geometry.VERTEX);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboIds[0]);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, vb.limit() * BufferUtil.SIZEOF_FLOAT, vb.rewind(),
                GL.GL_STATIC_DRAW);
        }
        finally
        {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        }
    }

    /**
     * Move the Ellipsoid to the specified destination.
     *
     * @param position the position to move the Ellipsoid to
     */
    public void moveTo(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position oldPosition = this.getReferencePosition();
        if (oldPosition == null)
            return;

        setCenterPosition(position);

        this.reset();
    }

    @Override
    protected void doExportAsKML(XMLStreamWriter xmlWriter) throws IOException, XMLStreamException
    {
            String message = Logging.getMessage("unsupportedOperation.doExportAsKML");
            Logging.logger().severe(message);
            throw new UnsupportedOperationException(message);
    }
}
