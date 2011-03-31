package gov.nasa.worldwind.geom;

import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.terrain.Terrain;

/**
 * The Orientation class provides a mechanism for describing and persisting the orientation of a
 * 3-dimensional shape or object in Cartesian space, based on the direction the object is facing (forward),
 * which way is up for the object (up), and which direction is to the object's right-hand side (right).
 * Under the hood, Orientation uses a set of perpendicular unit vectors (an orthonormal basis)
 * to define this orientation. The class also provides general utilities for handling orientations,
 * such as transforming between them.
 *
 * @author ccrick
 * @version $Id:
 */
public class Orientation
{
    // up vector points away from earth's center
    public static final int GLOBE_ORIENTATION = 0;
    // up vector points directly away from the local ground region. Imagine a car placed on a mountainside.
    public static final int TERRAIN_ORIENTATION = 1;

    protected Vec4 right;               // East
    protected Vec4 forward;             // North
    protected Vec4 up;                  // Vertical

    protected Matrix transform;    // matrix to transform a model in local coordinates to this orientation

    public Orientation()
    {
        setRightVector(new Vec4(1,0,0));
        setForwardVector(new Vec4(0,1,0));
        setUpVector(new Vec4(0,0,1));
    }

    public Orientation(Globe globe, Position position)
    {
        setOrientation(globe, position, this.GLOBE_ORIENTATION);
    }

    public Orientation(Globe globe, Position position, int mode)
    {
        setOrientation(globe, position, mode);
    }

    private void setOrientation(Globe globe, Position position, int mode)
    {
        if(mode == this.TERRAIN_ORIENTATION)
        {
            Vec4 point = globe.computePointFromLocation(position);

            // up will be the normal of the local terrain at the given position
            // TODO: is this tangent to the globe or to the local terrain?
            // TODO: should adjust with Altitude Mode/terrain conformance?
            Vec4 upVec = globe.computeSurfaceNormalAtLocation(position.latitude,
                                                        position.longitude).normalize3();
            // forward will point North
            Vec4 forwardVec = globe.computeNorthPointingTangentAtLocation(position.latitude,
                                                        position.longitude).normalize3();
            // take the cross product to get the third orthogonal vector
            Vec4 rightVec = forwardVec.cross3(upVec).normalize3();

            this.setForwardVector(forwardVec);
            this.setUpVector(upVec);
            this.setRightVector(rightVec);
        }
        else        // mode = GLOBE_ORIENTATION, the default
        {
            Matrix transform = this.computeGlobeOrientationTransform(globe, position);

            this.setForwardVector(transform.transformBy3(transform, 1, 0, 0));
            this.setUpVector(transform.transformBy3(transform, 0, 1, 0));
            this.setRightVector(transform.transformBy3(transform, 0, 0, 1));
        }
    }

    private Orientation(Vec4 right, Vec4 forward, Vec4 up)
    {
        this.right = right;
        this.forward = forward;
        this.up = up;
    }

    public Vec4 getForwardVector()
    {
        return this.forward;
    }

    private void setForwardVector(Vec4 forward)
    {
        this.forward = forward;
    }

    public Vec4 getUpVector()
    {
        return this.up;
    }

    private void setUpVector(Vec4 up)
    {
        this.up = up;
    }

    public Vec4 getRightVector()
    {
        return this.right;
    }

    private void setRightVector(Vec4 right)
    {
        this.right = right;
    }

    public Matrix getTransform()
    {
        if (this.transform == null)
            computeTransform();

        return this.transform;
    }

    /**
     * Computes the matrix to transform a model in local coordinates to this orientation
     *
     * @return the computed matrix.
     */
    private Matrix computeTransform()
    {
        // Create an axis-aligned orthonormal basis (the standard basis). Ordering is Forward, Up, Right.
        Orientation orthonormal = new Orientation(new Vec4(1, 0, 0, 1), new Vec4(0, 1, 0, 1), new Vec4(0, 0, 1, 1));

        Matrix A = computeBasisChange(orthonormal, this);

        this.transform = A;

        return A;
    }

    /**
     * Computes the matrix to transform a model in local coordinates to its default orientation
     * when placed on the globe.
     *
     * @return the computed matrix.
     */
    public static Matrix computeGlobeOrientationTransform(Globe globe, Position pos)
    {
        return globe.computeSurfaceOrientationAtPosition(pos);
    }

    /**
     * Computes the matrix to transform a model in local coordinates to its default orientation
     * when placed on the terrain.
     *
     * @return the computed matrix.
     */
    public static Matrix computeTerrainOrientationTransform(Terrain terrain, Position pos, Angle radius)
    {
        Vec4 point1 = terrain.getSurfacePoint(pos.latitude.add(radius), pos.longitude.subtract(radius), pos.elevation);
        Vec4 point2 = terrain.getSurfacePoint(pos.latitude.add(radius), pos.longitude.add(radius), pos.elevation);
        Vec4 point3 = terrain.getSurfacePoint(pos.latitude.subtract(radius), pos.longitude.subtract(radius), pos.elevation);
        Vec4 point4 = terrain.getSurfacePoint(pos.latitude.subtract(radius), pos.longitude.add(radius), pos.elevation);

        Vec4 tangentNortheast = point2.subtract3(point3);
        Vec4 tangentNorthwest = point1.subtract3(point4);

        // compute the cross product of the two vectors to get the terrain normal
        Vec4 terrainNormal = tangentNortheast.cross3(tangentNorthwest);

        Vec4 terrainTangent = terrainNormal.cross3(tangentNortheast);
        Orientation terrainOrientation = new Orientation(tangentNortheast, terrainNormal, terrainTangent);

        return terrainOrientation.getTransform();
    }

    /*
     * Returns the rotation matrix to transform start Orientation to end Orientation
     *
     * TODO: handle "impossible" rotations that involve negating one or more axes
     *
     * @param start     base Orientation that will be transformed
     * @param end       destination Orientation, that will result if the computed matrix
     *                  is applied to the start Orientation
     */
    public static Matrix computeBasisChange(Orientation start, Orientation end)
    {
        // compute cross product of start z-axis (up) vector with end up vector to
        // get vector perpendicular to both
        Vec4 rotationVector = start.up.cross3(end.up);

        // compute angle theta between z-axis (up) normal vector and up vector
        Angle theta = end.up.angleBetween3(start.up);

        // compute Matrix to rotate by theta around perpendicular vector, in order to align up vectors
        Matrix A = Matrix.fromAxisAngle(theta, rotationVector);

        // apply this to the orthonormal basis to get intermediate basis
        Orientation result = new Orientation(start.right.transformBy3(A),
                                    start.forward.transformBy3(A),
                                    start.up.transformBy3(A));

        // compute cross product of intermediate basis' forward vector with this
        // forward vector to get vector perpendicular to both
        rotationVector = result.forward.cross3(end.forward);

        // compute angle theta between intermediate basis' forward vector and
        // this forward vector
        theta = end.forward.angleBetween3(result.forward);

        // compute Matrix to rotate by theta around the perpendicular vector
        //Matrix B = Matrix.fromAxisAngle(theta, rotationVector);
        Matrix B = Matrix.fromAxisAngle(theta, rotationVector);

        // multiply the two Matrices together to get Matrix to do the entire rotation
        // in one step
        Matrix C = B.multiply(A);

        // test to make sure you are getting the same (and the right) answer
        result.setRightVector(result.right.transformBy3(B));
        result.setForwardVector(result.forward.transformBy3(B));
        result.setUpVector(result.up.transformBy3(B));


        Orientation direct = new Orientation(start.right.transformBy3(C),
                                    start.forward.transformBy3(C),
                                    start.up.transformBy3(C));
        return C;
    }
}
