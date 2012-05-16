// RawFrame.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.io.Serializable;

/**
 * Container class for the output of the simulation. Each output of the simulation will generate on
 * RawFrame
 * @author Edward Steere
 * @see FileReader
 */
public class RawFrame implements Serializable
{

    // ===Private Data Members===

    /** GUID for the serialised data*/
    private static final long serialVersionUID = 5349823498324983423L;

    /** Array of floats to be stored as a frame of output data*/
    private float[][] frameData;

    // ===Public Methods===

    /**
     * Sets the frame data 2D array
     * @param data
     *             Data for this frame
     */
    public void setFrame( float[][] data )
    {

        frameData = data;

    }

    /**
     * Creates a new Frame by accepting the data for this frame and setting the frameData variable
     * @param data
     *             Data for this frame
     */
    public RawFrame( float[][] data )
    {

        setFrame( data );

    }

    /**
     * Returns the two dimensional float array
     * @return The float array which represents the current densities on the screen
     */
    public float[][] getFrame()
    {

        return frameData;

    }

}
