// TrueIO.java
package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

// Standard dependancies
import mpi.*;
import java.lang.Object;

/**
 * A class which implements {@link MpiIO MpiIO} to achieve the "true" vesion of the MPI IO operations
 * @author Edward Steere
 * @see MpiIO
 * @see FakedIO
 */
public class TrueIO implements MpiIO
{

    // ===Public Methods===

    /**
     * Creates a new TrueIO object (since it has no state there are no initialisation conditions
     */
    public TrueIO()
    {}

    /**
     * Implementation of the {@link MpiIO MpiIO} {@link mpiSend mpiSend} function which uses the MPI-
     * Java commands
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
    public void mpiSend( Object data, int offset, int count, Datatype dataType, int destination, int tag ) throws MPIException
    {

        MPI.COMM_WORLD.Send( data, offset, count, dataType, destination, tag );

    }

    /**
     * Implementation of the {@link MpiIO MpiIO} {@link mpiReceive mpiReceive} function which uses the
     * MPI-Java commands
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
    public Status mpiReceive( Object data, int offset, int count, Datatype dataType, int source, int tag ) throws MPIException
    {

        MPI.COMM_WORLD.Recv( data, offset, count, dataType, source, tag );
        return new Status();
    }

}
