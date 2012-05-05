// FakedIO.java
package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import mpi.*;
import java.lang.Object;
/**
 * A class which implements {@link MpiIO MpiIO} to inject "Fake" test data to the system for testing
 * purposes
 * @author Edward Steere
 * @author Graham Peyton
 * @see MpiIO
 * @see TrueIO
 */
public class FakedIO implements MpiIO
{

    /** Data to be "received" when the mpiReceive command is called*/
    private Object[] receivedData = new Object[1];;
    /** Status object to be "returned" when the mpiReveive command is called*/
    //private Status receivedStatus;
    /** Fake rank of the current process */
    private int myRank;
    /** Fake communicator size */
    private int commWorldSize;

    // ===Public Methods===

    /**
     * Creates a new FakedIO class by accepting the data which will be "received" every time the mpi-
     * Receive command is called along with the Status object
     * @param data
     *             The fake data to be returned whenever the mpiReceive command is called
     * @param status
     *             The fake status to be returned whenever the mpiReceive command is called
     */
    public FakedIO( Object data )
    {
        receivedData = data;
        //receivedStatus = status;

    }
    
    /**
     * Sets the fake size of the communicator and the current rank of the processor
     * @param commSize
     *              The size of the communicator
     * @param currentRank
     *              The current rank of the fake process             
     */
    public void initProcess(int commSize, int currentRank)
    {
        commWorldSize = commSize;
        myRank = currentRank;
    }

    /**
     * Implementation of the {@link MpiIO MpiIO} {@link mpiSend mpiSend} function which does nothing
     * with the data being sent (NOTE: you could make it log the data to a file or something...?)
     * @param data
     *             The data which will be sent over the network in the format of a buffer of the ite-
     *             ms to be sent
     * @param offset
     *             The off set from the start of the buffer to begin sending from
     * @param count
     *             The number of items (starting at offset) to send
     * @param dataType
     *             An indicator for the datatype which was sent (i.e. the type found in the buffer)
     * @param destination
     *             The rank which should receive the command
     * @param tag
     *             The tag which defines the user specified message type
     */
    public void mpiSend( Object data, int offset, int count, Datatype dataType, int destination, int tag )
    {

        return;

    }

    /**
     * Implementation of the {@link MpiIO MpiIO} {@link mpiReceive mpiReceive} function which return-
     * s the "fake data" used to initialise the FakedIO object
     * @param data
     *             The data which will be sent over the network in the format of a buffer of the ite-
     *             ms to be sent
     * @param offset
     *             The off set from the start of the buffer to begin sending from
     * @param count
     *             The number of items (starting at offset) to send
     * @param dataType
     *             An indicator for the datatype which was sent (i.e. the type found in the buffer)
     * @param source
     *             The rank which sent the message
     * @param tag
     *             The tag which defines the user specified message type
     * @return The status of the operation which contains important information such as the source a-
     * nd tag fields
     */
    public Status mpiReceive( Object data, int offset, int count, Datatype dataType, int source, int tag )
    {

        //for ( int l = offset; l != count; ++l )
        //{
            data = receivedData;
        //}
        //data = receivedData;
        return new Status();

    }
    
    /**
     * Returns the fake rank of current process
     * @return      The fake rank of the current process
     */
    public int myRank() 
    {
        return myRank;
    }
    
    /**
     * Returns the fake size of the communicator
     * @return      The fake size of the comm_world communicator
     */
    public int commWorldSize() 
    {
        return commWorldSize;
    }


}
