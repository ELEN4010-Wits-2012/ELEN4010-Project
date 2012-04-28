// LabelNode.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard Dependancies
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * The label node acts as the highest level of the hierarchy for the {@link TimeCapture TimeCapture}
 * class. It has a name and a linked list to data nodes, which store the actual data for the simulat-
 * ion.
 * @author Edward Steere
 * @see TimeCapture
 * @see DataNode
 */
public class LabelNode
{

    // ===Private Data Members===
    /** The writer which this node uses to write it's data*/
    private PrintWriter outStream;
    /** The label for this node*/
    private String nodeLabel;
    /** The start of the linked list of {@link DataNode DataNodes}*/
    private List<DataNode> subNodes;

    // ===Public Methods===

    /**
     * Creates a new LabelNode by accpeting the label for this node and initialising the subNodes l-
     * ist
     * @param label
     *             The identifier/label for this node
     */
    public LabelNode( String label )
    {

        subNodes = new LinkedList<DataNode>();
        nodeLabel = label;

    }

    /**
     * Adds a sample to this node by accepting its sub name and the time data
     * @param subName
     *             The name of the {@link DataNode DataNode} which the sample should be added to
     * @param data
     *             The data which should be appended to the destination {@link DataNode DataNode}
     */
    public void addSubEvent( String subName, long data )
    {

        ListIterator<DataNode> dataFinder = subNodes.listIterator();
        DataNode nextNode = null;

        while ( dataFinder.hasNext() )
        {
            nextNode = dataFinder.next();
            if ( nextNode.getSubName().equals( subName ) )
            {
                nextNode.addSample( data );
                return;
            }
        }

        // If the node was in the list the function shouldn't reach this point
        subNodes.add( new DataNode( subName, data ) );

    }

    /**
     * Simple getter function for the label of this node so that the structure can be identified by 
     * the object which created it.
     * @return The label for this node
     */
    public String getLabel()
    {

        return nodeLabel;

    }

    /**
     * Writes the data for each of this nodes sub nodes to the relevant file
     */
    public void writeData()
    {

        ListIterator<DataNode> dataFinder = subNodes.listIterator();
        DataNode nextNode = null;
        String fileName = null;

        while ( dataFinder.hasNext() )
        {
            nextNode = dataFinder.next();
            fileName = nodeLabel + "-" + nextNode.getSubName() + ".txt";
            try
            {
                outStream = new PrintWriter( new FileWriter( fileName ) );
            }
            catch ( IOException couldntOpenLogFile )
            {
                System.err.println( "Couldn't open the log file for: " + fileName );
                couldntOpenLogFile.printStackTrace( System.err );
            }
            try
            {
                outStream.println( nextNode.getCSVData() );
                outStream.flush();
                outStream.close();
            }
            catch ( Exception couldntWriteToOutputFile )
            {
                System.err.println( "Couldn't write the data to: " + fileName );
                couldntWriteToOutputFile.printStackTrace( System.err );
            }
        }
    }

}