// Velocity.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.lang.Math;
import java.io.Serializable;

/**
 * Simple storage object designed to carry velocity information
 * @author Edward Steere
 * @see SamplePoint
 */
public class Velocity implements Serializable
{

    // ===Private Data Members===

    /** Stores the serialisable GUID for this object*/
    private static final long serialversionUID = 4869473834957839230L;
    /** Stores the direction of the velocity as a ratio: delta y/delta x*/
    private float xVelocity;
    /** Stores the magnitude of the velocity in pixels per second*/
    private float yVelocity;
    /** Stores the sample time for this velocity*/
    private long sampleTime;
    /** Stores the x coordinate of the velocity*/
    private int xCoordinate;
    /** Stores the y coordinate of the velocity*/
    private int yCoordinate;
    /** Stores the density of the sample*/
    private float density;

    // ===Private Methods===

    // ===Public Methods===

    /**
     * Creates a new velocity object from the change in x, y and time
     * @param deltaX
     *             Change in x between two sample points
     * @param deltaY
     *             Change in y between two sample points
     * @param deltaTime
     *             Time between the two samplePoints
     * @param sampleTime
     *             The time at which the second sample was taken
     * @param xPlacment
     *             The x coordinate of the starting point of the velocity vecotr
     * @param yPlacemnt
     *             The y coordinate of the starting point of the velocity vector
     * @param sampleDensity
     *             Density of the system at the time of the sample
     */
    public Velocity( int deltaX, int deltaY, long deltaTime, long velocitySampleTime, int xPlacement, int yPlacement, float sampleDensity )
    {

        // DEBUG!! Check that this operation is upcasting correctly
        sampleTime = velocitySampleTime;
        xVelocity = (float)deltaX / deltaTime;
        yVelocity = (float)deltaY / deltaTime;
        xCoordinate = xPlacement;
        yCoordinate = yPlacement;
        density = sampleDensity;

    }

    /**
     * Simple function to get the sample time of a velocity (NOTE: this is the time of the second s-
     * ample which was used in generating the velocity)
     * @return sample time of the Velocity
     */
    public long getSampleTime()
    {

        return sampleTime;

    }

    /**
     * Simple function to get the density of a velocity
     * @return density of a velocity
     */
    public float getDensity()
    {

        return density;

    }

    /**
     * Simple function to get the x component of a velocity
     * @return x component of a velocity
     */
    public float getXComponent()
    {

        return xVelocity;

    }

    /**
     * Simple function to get the y component of a velocity
     * @return y component of a velocity
     */
    public float getYComponent()
    {

        return yVelocity;

    }

    /**
     * Simple function to get the x coordinate of a velocity (NOTE: this is the coordinate of the st-
     * artpoint of the velocity vecotr)
     * @return x coordinate of a velocity vector
     */
    public long getXCoordinate()
    {

        return xCoordinate;

    }

    /**
     * Simple function to get the y coordinate of a velocity (NOTE: this is the coordinate of the st-
     * artpoint of the velocity vecotr)
     * @return y coordinate of a velocity vector
     */
    public long getYCoordinate()
    {

        return yCoordinate;

    }

    /**
     * Overides the standard toString method for printing a velocity
     * @return A string which represents this velocity
     */
    @Override
    public String toString()
    {

        return "Velocity: Xcomp = " + getXComponent() + ", Ycomp =" + getYComponent() + ", Density = " + getDensity() + ", Coordinates = [" + getXCoordinate() + ", " + getYCoordinate() + "] , Sample time = " + getSampleTime();

    }

}