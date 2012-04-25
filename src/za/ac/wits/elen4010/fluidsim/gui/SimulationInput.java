// SimulationInput.java

package za.ac.wits.elen4010.fluidsim.gui;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.ListIterator;
import java.awt.Dimension;

/**
 * Container class for the input of the simulation. Each input is represented as a Velocity which co-
 * rresponds to each consecutive frame to be computed in the simulation. Note that a collection of f-
 * rames should be computed such that they can be played back at 30Hz
 * @author Edward Steere
 * @see FileWriter
 * @see Velocity
 */
public class SimulationInput implements Serializable
{

    // ===Private Data Members===

    /** GUID for the serialised data*/
    private static final long serialVersionUID = 3382827583926218294L;

    /** List of vectors, represented in memory as a 'vector' data type*/
    private List<Velocity> frameByFrameInput;

    /**
     * Stores the dimension of the screen as defined at contruction and depending on the size of inp-
     * ut screen in the GUI
     */
    private Dimension renderFrameDimensions;

    /** Stores an iterator to the next velocity to be taken from the vector of velocities*/
    private ListIterator<Velocity> nextVelocity = null;

    // ===Public Methods===

    /**
     * Constructs a new SimulationInput by accepting a list of {@link Velocity velocities} which sho-
     * uld be used to populate the internal list of velocities
     * @param velocities
     *             The velocities which should be set to the internal list of Velocities
     * @param inputScreenSize
     *             The dimensions of the rendered screen
     */
    public SimulationInput( List<Velocity> velocities, Dimension inputScreenSize )
    {

        // DO ERROR CHECKING HERE!
        frameByFrameInput = new Vector<Velocity>( velocities );
        renderFrameDimensions = inputScreenSize;

    }

    /**
     * Returns the next {@link Velocity Velocity} stored in the list of {@link Velocity Velocities}
     * NOTE: null is returned if the list of velocities is exhausted
     * @return the list of {@link Velocity Velocities} assigned to the SimulationInput
     */
    public Velocity nextVelocity()
    {

        if ( nextVelocity == null )
        {
            nextVelocity = nextVelocity = frameByFrameInput.listIterator();
            return nextVelocity.next();
        }

        if ( nextVelocity.hasNext() )
        {
            return nextVelocity.next();
        }

        return null;

    }

    /**
     * Returns the length of number of input samples (i.e. the number of frames to be rendered
     * @return The number of frames to be rendered
     */
    public int getFrameCount()
    {

        if ( frameByFrameInput == null )
        {
            return 0;
        }

        return frameByFrameInput.size();

    }

    /**
     * Returns the dimensions of the screen so that other components receiving a simulation input ca-
     * n determine  how large the rendering area shpuld be
     * @return A Java 'Dimension' which has methods for getting the width and height of the screen
     */
    public Dimension getRenderingSize()
    {

        return renderFrameDimensions;

    }

    /** Simple toString override method*/
    @Override
    public String toString()
    {

        return "Simulation Input: " + frameByFrameInput;

    }

}