package za.ac.wits.elen4010.fluidsim.mpiNodalCode;
//ONLY RUN THIS PROGRAM WITH: mpiexec -n 3 java Main
import mpi.*;
import za.ac.wits.elen4010.fluidsim.gui.*;

import java.net.*;
import java.util.*;

/**
 * Main class representing the main node
 * @author Graham Peyton
 * @author Rudolf Hoehler
 */
public class MainNode
{
    /** Count that ensures only one Main Node is created */
    static int mainNodeCount = 0;
    /** Count of the number of available processes */
    int threadCount ;
    /** List of slave nodes */
    List<String> SlaveNodeList = new ArrayList<String>();
    /** Local factories,container and storage classes */
    NeighboursData neighbourData ;
    /** Hold host state - intialised or not*/
    Boolean hostInitialised = false;
    /** Stores the total number of frames */
    int frames;
    /** The communications module used by the MainNode to communicate to other nodes*/
    private MpiIO commModule;
    /** Main node singleton instance */
    private static MainNode mainNodeInstance = null;
    
    /**
     * Main node constructor.
     * @param ioModule to handle mpi IO requests
     * @throws mpiException if slave nodes cannot be initialised
     */
    private MainNode( MpiIO ioModule ) throws MPIException
    {
        commModule = ioModule;
		SlaveNodeList.clear();
		threadCount = commModule.commWorldSize() ;
		initiliseSlaveNodes();
    }
    
    /**
     * Return an instance of the main node singleton
     * @return instance of the main node singleton
     * @throws mpiException if slave nodes cannot be initialised
     */
    public static MainNode getInstance( MpiIO ioModule ) throws MPIException
    {
        if( mainNodeInstance == null )
        {
            mainNodeInstance = new MainNode( ioModule );
        }
        return mainNodeInstance;
    }

    /**
     * Initialises slave nodes by sending initial conditions
     * @throws MPIException Throws exception if slave nodes cannot be initialised.
     */
    private void initiliseSlaveNodes() throws MPIException
    {
        long startTime = System.nanoTime();
        
        /* XXXXXXXXX THIS CODE WILL BE IMPLEMENTED ONCE WE HAVE A TEST FILE XXXXXXXXXXX
        SimulationInput[] tempInput = new SimulationInput[1];
        // Read initial conditions from file
        FileReader<SimulationInput> fileReader = new FileReader<SimulationInput>( "test.in" );
        tempInput[0] = fileReader.readNextFrame();
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX */
        
        // For now I'm testing it with null
        // XXXXXXXXXX REPLACE THIS ONCE WE HAVE A TEST FILE XXXXXXXXXXXXXX
        SimulationInput[] tempInput = new SimulationInput[1];
        tempInput[0] = null;
        
        // Send initial conditions to all slaves
        for ( int source = 1; source != threadCount; source++ )
        {
            commModule.mpiSend(tempInput, 0, 1, MPI.OBJECT, source, MessagingTags.Initialcondition_FromServer);
            System.out.println("sent IC to process rank " + source);
        }
        
        long elapsedTime = System.nanoTime() - startTime;
        
        TimeCapture.getInstance().addTimedEvent( "0", "initiliseSlaveNodes", elapsedTime );
    }
    
    /** 
     * Main program loop on the Master Node. This method waits for slave nodes to return completed frames. 
     * The method also calls a function to aggregate the data and write it to file
     * 
     * @param frames Number of simulation frames
     * @throws MPIException
     * 
     */
    public void run( int frames ) throws MPIException
    {
        long startTime = System.nanoTime();
        
        // Loop through total number of specified frames
        for ( int i = 0; i != frames; i++ )
        {
            RenderData[] stripArray = new RenderData[ threadCount ];    // Stores RenderData objects in an array
            
         // Receive frame data from each process
            for ( int source=1; source != threadCount; source++ )
            {
                RenderData[] strip = new RenderData[1];
                //Status mpiStatus = commModule.mpiReceive(strip, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MessagingTags.RenderDataFromSlave);
                commModule.mpiReceive(strip, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MessagingTags.RenderDataFromSlave);
                //System.out.println("Source of received strip: " + mpiStatus.source);
                //strip[0].setSourceRank( mpiStatus.source );
                
                // Dirty bit of code to correctly set the source of the data for testing purposes:
                //For some reason, mpiStatus.source doesn't want to work....
                if( commModule instanceof FakedIO ) {
                    strip[0].setSourceRank(source);
                    System.out.println("Source ======= " + source );
                }
                else if( commModule instanceof TrueIO ) {
                    // Do nothing
                }
                    
                System.out.println("Master node received data strip from slave");
                // Add strip to array
                stripArray[source-1] = strip[0]; int temp = source-1;
                System.out.println("stripArray [" + temp +"] >>>>>>>> " + stripArray[source -1].getSourceRank());
            }
            
            System.out.println("stripArray [0] >>>>>>>> " + stripArray[0].getSourceRank());
            System.out.println("stripArray [1] >>>>>>>> " + stripArray[1].getSourceRank());
            
            System.out.println(Arrays.toString(stripArray));             
            System.out.println("Aggregating a frame");
            RawFrame new_frame = aggregateData(stripArray);                      // Aggregate the strips
            System.out.println("Frame aggregated, sending to file!");

            // Write received frames to file
            FileWriter<RawFrame> fileWriter = new FileWriter<RawFrame>( "test.out" );
            fileWriter.writeSimulationData( new_frame );
        }
        
        long elapsedTime = System.nanoTime() - startTime;
        TimeCapture.getInstance().addTimedEvent( "0", "run", elapsedTime );
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }
    
    /**
     * Function to aggregate RenderData objects into one large array
     * @param stripArray An array of RenderData objects that will be aggregated
     * @return A RawFrame object containing the aggregated array
     */
    // Function to aggregate subarrays into combined array
    private RawFrame aggregateData( RenderData[] stripArray )
    {
        int segmentHeight = stripArray[0].getYLength();                    
        System.out.println("-----> Segment height = " + segmentHeight);
        int segmentWidth = stripArray[0].getXLength();
        System.out.println("-----> Segment Width = " + segmentWidth);
        int aggregatedHeight = segmentHeight*(threadCount-1);               // There are threadCount-1 slave nodes
        System.out.println("-----> Aggregated Height = " + aggregatedHeight);     
        float tempArray[][] = new float[aggregatedHeight][segmentWidth];    // Aggregated array
        
        // Copy data objects' content to combined array.
        // Assume the objects are NOT sorted according to rank
        // The rank of each object corresponds to the index: i+1
        for( int i = 0; i != (threadCount-1); i++ )
        {
            int currentRank = stripArray[i].getSourceRank();
            System.out.println("-----> Aggregating from rank = " + currentRank);
            float density[][] = stripArray[i].getDensity();
            int firstSegmentRow = (currentRank - 1)*segmentHeight;       // First row of each new segment
            
            // Copy the rows of each object array to the aggregated array
            for( int j = 0; j != segmentHeight; j++ )
            {
                System.arraycopy( density[j], 0, tempArray[j + firstSegmentRow], 0, segmentHeight );
            }
        }
        System.out.println("-----> Finished aggregation = ");
        
        // Return the aggregated array object
        return new RawFrame(tempArray);
    }


}