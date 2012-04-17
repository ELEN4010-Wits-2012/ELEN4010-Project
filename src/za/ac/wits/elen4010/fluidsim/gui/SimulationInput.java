// SimulationInput.java

package za.ac.wits.elen4010.fluidsim.gui;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.ListIterator;

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

    /** Stores an iterator to the next velocity to be taken from the vector of velocities*/
    private ListIterator<Velocity> nextVelocity;

    // ===Public Methods===

    /**
     * Constructs a new SimulationInput by accepting a list of {@link Velocity velocities} which sho-
     * uld be used to populate the internal list of velocities
     * @param velocities
     *             The velocities which should be set to the internal list of Velocities
     */
    public SimulationInput( List<Velocity> velocities )
    {

        frameByFrameInput = new Vector<Velocity>( velocities );
        nextVelocity = frameByFrameInput.listIterator();

    }

    /**
     * Returns the next {@link Velocity Velocity} stored in the list of {@link Velocity Velocities}
     * NOTE: null is returned if the list of velocities is exhausted
     * @return the list of {@link Velocity Velocities} assigned to the SimulationInput
     */
    public Velocity nextVelocity()
    {

        if ( nextVelocity.hasNext() )
        {
            return nextVelocity.next();
        }

        return null;

    }

}