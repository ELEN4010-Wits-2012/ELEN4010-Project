// FakedIO.java

/**
 * A class which implements {@link MpiIO MpiIO} to inject "Fake" test data to the system for testing
 * purposes
 * @author Edward Steere
 * @see MpiIO
 * @see TrueIO
 */
public class FakedIO implements MpiIO
{

    /** Data to be "received" when the mpiReceive command is called*/
    private Object[] receivedData;
    /** Status object to be "returned" when the mpiReveive command is called*/
    private Status reveicedStatus;

    // ===Public Methods===

    /**
     * Creates a new FakedIO class by accepting the data which will be "received" every time the mpi-
     * Receive command is called along with the Status object
     * @param data
     *             The fake data to be returned whenever the mpiReceive command is called
     * @param status
     *             The fake status to be returned whenever the mpiReceive command is called
     */
    public FakedIO( Oject[] data, Status status )
    {

        receivedData = data;
        receivedStatus = status;

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
    public Status mpiReceive( object data, int offset, int count, Datatype dataType, int source, int tag )
    {

        for ( int l = offset, l != count; ++l )
        {
            data[l] = receivedData[l];
        }

        return receivedStatus;

    }

}