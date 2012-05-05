// MpiIO.java
package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import mpi.*;
import java.lang.Object;
/**
 * Interface for an MPI send and receive object which should be implemented by any object which perf-
 * orms send/receive operations or fakes them
 * @author Edward Steere
 * @see TrueIO
 * @see FakedIO
 */
public interface MpiIO
{

    /**
     * Acts as the sending command for the class which implements the interface
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
    public void mpiSend( Object data, int offset, int count, Datatype dataType, int destination, int tag ) throws MPIException;


    /**
     * Acts as the receiving command for the class which implements the interface
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
    public Status mpiReceive( Object data, int offset, int count, Datatype dataType, int source, int tag ) throws MPIException;

}
