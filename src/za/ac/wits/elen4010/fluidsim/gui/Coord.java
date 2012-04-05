// Coord.java

package za.ac.wits.elen4010.fluidsim.gui;

/**
 * Implements the {@link DataPoint DataPoint} interface for a coordinate type. This stores the x
 * and y position of a pixel in a panel in pixels
 * @author Edward Steere
 * @see DataPoint
 */
public class Coord implements DataPoint
{

    // ===Private Data Members===
    /** Stores the X position of the Coord in pixels*/
    private int xCoord;
    /** Stores the Y position of the Coord in pixels*/
    private int yCoord;

    // ===Private Methods===

    // ===Public Methods===
    /**
     * Creates a new Coord from an x and y coordinate
     * @param incomingXCoord X position of the Coord in pixels
     * @param incomingYCoord Y position of the Coord in pixels
     */
    public Coord( int incomingXCoord, int incomingYCoord )
    {

        xCoord = incomingXCoord;
        yCoord = incomingYCoord;

    }

}
