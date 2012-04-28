// DataNode.java

package za.ac.wits.elen4010.fluidsim.gui;

/**
 * The data node acts as the lowest level of the heirarchy for the {@link TimeCapture TimeCapture}
 * class. It has a name and the CSV data stored as a string
 * @author Edward Steere
 * @see TimeCapture
 * @see LabelNode
 */
public class DataNode
{

    // ===Private Data Members===

    /** The name of the DataNode (i.e. subprocess)*/
    private String subName;
    /** The CSV data stored at this point*/
    private String timeData;

    // ===Public Methods===

    /**
     * Creates a new DataNode by accepting the name of the subprocess which it corresponds to and
     * the first sample to be stored in the csv data string
     * @param name
     *             The name of the data node and hence subprocess which it corresponds to
     * @param data
     *             The first sample to be added to the csv string
     */
    public DataNode( String name, long data )
    {

        subName = name;
        timeData = String.valueOf( data );

    }

    /**
     * Returns the name of the data node, i.e. the subprocess it corresponds to
     * @return The name of the DataNode
     */
    public String getSubName()
    {

        return subName;

    }

    /**
     * Adds new sample data to the node
     * @param data
     *             The new sample to be appended to the csv data
     */
    public void addSample( long data )
    {

        timeData = timeData + "," + String.valueOf( data );

    }

    /**
     * Returns the CSV data string
     * @return The CSV data string
     */
    public String getCSVData()
    {

        return timeData;

    }

}