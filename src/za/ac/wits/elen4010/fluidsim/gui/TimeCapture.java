// TimeCapture.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard Dependancies
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;

/**
 * A logging class which ensures synchronised logging and logging for different identifiers of two
 * heirarchical levels. i.e. log data is identified based on a broader identifier and a lower level
 * identifier -> LogExample-LogExampleSubID.txt. The data for each unique identifier will be stored
 * in seperate text files as comma seperated data so that it can be extracted at a later stage.
 * @author Edward Steere
 * @see LabelNode
 * @see DataNode
 */
public class TimeCapture
{

    // ===Private Data Members===

    /**
     * A note on the architecture of the hierarchical storage of data in memory:
     * The higher level is a linked list in memory, where each node on the list is the start of a l-
     * inked list containing all the sub identifiers for that list. The higher level nodes are
     * {@link LabelNodes LableNodes} and the lower level nodes are {@link DataNode DataNodes}
     */
    private List<LabelNode> timedProcesses;
    /** The pointer for this instance of this singleton*/
    private static TimeCapture instance = null;
    
    private boolean active = false;

    // ===Private Methods===

    /**
     * Creates a new TimeCapture singleton by initialising the linked List of
     * {@link LabelNode LabelNodes} which are the highest level of the hierarchy of CSV data log st-
     * orage
     */
    private TimeCapture()
    {

        timedProcesses = new LinkedList<LabelNode>();

    }

    // ===Public Methods===

    /**
     * Gets an instance of the TimeCapture singleton, if one doesnt exist a new one is created
     * @return An instance of the TimeCapture singleton
     */
    public static synchronized TimeCapture getInstance()
    {

        if ( instance == null )
        {
            instance = new TimeCapture();
        }

        return instance;

    }

    /**
     * Adds a new event to the structure fo storage as csv data at the lowest level
     * @param processName
     *             The name of the process attempting to write the data to the structure
     * @param functionName
     *             The name of the function which is logging the event
     * @param timeDelta
     *             The time delta for the execution of the function in nanoseconds.
     */
    public synchronized void addTimedEvent( String processName, String functionName, long timeDelta )
    {
        if ( active )
        {
            ListIterator<LabelNode> nodeFinder = timedProcesses.listIterator();
            LabelNode nextNode = null;
    
            while ( nodeFinder.hasNext() )
            {
                nextNode = nodeFinder.next();
                if ( nextNode.getLabel().equals( processName ) )
                {
                    nextNode.addSubEvent( functionName, timeDelta );
                    return;
                }
            }
    
            // If the node was in the list the function shouldn't reach this point
            timedProcesses.add( new LabelNode( processName ) );
            timedProcesses.get( timedProcesses.size() - 1 ).addSubEvent( functionName, timeDelta );
        }

    }

    /**
     * Writes the csv data contained in each subprocess to the relevant file
     */
    public synchronized void writeCSVData()
    {
        if (active)
        {
            ListIterator<LabelNode> nodeFinder = timedProcesses.listIterator();
            LabelNode nextNode = null;
    
            while ( nodeFinder.hasNext() )
            {
                nextNode = nodeFinder.next();
                nextNode.writeData();
            }
        }

    }
    
    public synchronized void setActive( boolean active )
    {
        this.active = active;
    }
    
    public synchronized boolean getActive()
    {
        return active;
    }

}