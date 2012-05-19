// SamplePoint.java

package za.ac.wits.elen4010.fluidsim.gui;

/**
 * SamplePoint acts as a simple data storage device to allow for enhanced communication between the
 * {@link DataPanel DataPanel} and {@link DataProcessor DataProcessor} modules. Theoretically it cou-
 * ld be extended to enhance calculations on both sides and determine variables such as rate through
 * functional operators as apposed to functions
 * @author Edward Steere
 * @see DataPanel
 * @see DataProcessor
 */
public class SamplePoint
{

    // ===Private Data Members===

    /** Stores the x coord of the sample in pixels*/
    private int xCoord;
    /** Stores the y coord of the sample in pixels*/
    private int yCoord;
    /** Stores the density value when the sample was taken*/
    private float density;
    /** 
     * Stores the timestamp for the sample in miliseconds (NOTE: the precision of this variable
     * varies with operating systems
     */
    private long timeStamp;

    // ===Private Methods===

    // ===Public Methods===

    /**
     * Creates a new SamplePoint given a time stamp and the coords of the input
     * @param proposedXCoord Proposed value for the x coord of the sample in pixels
     * @param proposedYCoord Proposed value for the y coord of the sample in pixels
     * @param proposedTimeStamp Proposed time stamp value for the sample
     */
    public SamplePoint( int proposedXCoord, int proposedYCoord, long proposedTimeStamp, float proposedDensity )
    {

        xCoord = proposedXCoord;
        yCoord = proposedYCoord;
        timeStamp = proposedTimeStamp;
        density = proposedDensity;

    }

    /** Simple function to get the density of a SamplePoint
     * @return density of the SamplePoint
     */
    public float getDensity()
    {

        return density;

    }

    /**
     * Simple function to get the x coord of a SamplePoint
     * @return x coord of the SamplePoint
     */
    public int getXCoord()
    {

        return xCoord;

    }

    /**
     * Simple function to get the y coord of a SamplePoint
     * @return y coord of the SamplePoint
     */
    public int getYCoord()
    {

        return yCoord;

    }

    /**
     * Simple function to get the time stamp of a SamplePoint
     * @return time stamp of the SamplePoint
     */
    public long getTimeStamp()
    {

        return timeStamp;

    }

    /**
     * Simple function to generate a plain coord from a SamplePoint (i.e. discard the time info-
     * rmation
     * @return coord A coord generated from the information stored inside the SamplePoint
     *//*
    public Coord getCoord()
    {

        return new Coord( xCoord, yCoord );

    }*/

    /**
     * Function to return the velocity of movement between two SamplePoints
     * @return Velocity of movement between the two samplepoints
     * @param endPoint the point at which the velocity calculation ends
     */
    public Velocity getVelocity( SamplePoint endPoint )
    {

        return new Velocity( endPoint.getXCoord() - getXCoord(), endPoint.getYCoord() - getYCoord(), endPoint.getTimeStamp() - getTimeStamp(), endPoint.getTimeStamp(), getXCoord(), getYCoord(), getDensity() );

    }

    /**
     * Overrides the standard toString method for printing a samplePoint
     * @return A string which represents this velocity
     */
    @Override
    public String toString()
    {

        return "SamplePoint: xCoord = " + getXCoord() + ", yCoord = " + getYCoord() + ", timeStamp = " + getTimeStamp();

    }

}

