// Velocity.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.lang.Math;

/**
 * Simple storage object designed to carry velocity information
 * @author Edward Steere
 * @see SamplePoint
 */
public class Velocity
{

    // ===Private Data Members===

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
     */
    public Velocity( int deltaX, int deltaY, long deltaTime, long velocitySampleTime, int xPlacement, int yPlacement )
    {

        // DEBUG!! Check that this operation is upcasting correctly
        sampleTime = velocitySampleTime;
        xVelocity = (float)deltaX / deltaTime;
        yVelocity = (float)deltaY / deltaTime;

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

        return "Velocity: Xcomp = " + getXComponent() + ", Ycomp =" + getYComponent() + ", Sample time = " + getSampleTime();

    }

}