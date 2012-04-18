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
    
    /**
     * Main node constructor.
     */
    public MainNode( ) throws MPIException
    {
    	mainNodeCount = mainNodeCount + 1;
    	
    	//Ensures that only one main node can be created through static variable counter
    	if (mainNodeCount == 1)
    	{
    		SlaveNodeList.clear();
    		
    		threadCount = MPI.COMM_WORLD.Size() ;
    		initiliseSlaveNodes();
    		
    	}
    	else
    	{
    		//Throw Exception
    		//Call Destructor
    	}

    
    }

    /**
     * Initialises slave nodes 
     * throws MPIException Throws exception if slave nodes cannot be initialised.
     */
    private void initiliseSlaveNodes() throws MPIException
    {
    	//cycle through the CommWorld and recieve the ID's from all the Threads
    	char[] temp = new char[MessagingTags.DefaultHostandRankMsgLength];
    	
    	for ( int source =1;source != threadCount;source++)
    	{
    		MPI.COMM_WORLD.Recv(temp, 0, MessagingTags.DefaultHostandRankMsgLength, MPI.CHAR, source, MessagingTags.HostAndRank_ToServer);
    	
    		SlaveNodeList.add(new String(temp));
    		System.out.println("***Host Data recieved***");
    	}
    	
    	Iterator<String> iterator = SlaveNodeList.iterator();
    	//while (iterator.hasNext()) 
    	//{
    	//	System.out.println(iterator.next());
    	//}
    	System.out.println("***Preparing Neighbour Data***");
    	//This is hardcoded and will need to be replaced once we move to n rows instead of just two.
    	//Top block will go to 1 and bottom to 2
    	NeighboursData tempNData[] = new NeighboursData[1];
    	tempNData[0] = new NeighboursData();
    	tempNData[0].NeighbourAbove = 0 ;
    	tempNData[0].NeighbourBelow = 2 ;
    	
    	
    	System.out.println("***Sending***");
    	MPI.COMM_WORLD.Send(tempNData, 0, 1, MPI.OBJECT, 1, MessagingTags.Neighbours_FromServer);
    	
    	tempNData[0].NeighbourAbove = 1 ;
    	tempNData[0].NeighbourBelow = 0 ;
    	MPI.COMM_WORLD.Send(tempNData, 0, 1, MPI.OBJECT, 2, MessagingTags.Neighbours_FromServer);
    	
    	/*-------------------------------------------------------------
    	   Send intial conditions
    	 --------------------------------------------------------------*/
    	// Read initial conditions from file
    	// --- Add code here ---
    	
    	// Set total number of frames using size of array of IC objects. 
    	// For now, just use an arb number
    	this.frames = 2;
        
        // Arbitrary initial conditions - JUST TO TEST  
    	// This code will need to be changed to fit the new methodology
        Data initialConditions = new Data();
        float[][] stupidfloat1 = new float[256][256] ;
        float[][] stupidfloat2 = new float[256][256] ;
        
        for ( int j = 120; j < 150; j++ )
            for ( int i = 10; i < 53; i++ )
                stupidfloat2[j][i] = 20f;
        
        stupidfloat1[50][50] = 132 ;
        initialConditions.setData(stupidfloat2, stupidfloat1, stupidfloat1, 1);
        
        // Send initial conditions
        sendInitialConditions(initialConditions, 1);
        sendInitialConditions(initialConditions, 2);
    	
    	
    }
    
    /** 
     * Main program loop on the Master Node. This method waits for slave nodes to return completed frames. 
     * The method also calls a function to aggregate the data and write it to file
     */
    public void run() throws MPIException
    {       
        // Loop through total number of specified frames
        for ( int i = 0; i != frames; i++ )
        {
            RenderData[] stripArray = new RenderData[ threadCount ];    // Stores RenderData objects in an array
            
            // Receive frame data from each process
            for ( int source=1; source != threadCount; source++ )
            {
                RenderData[] strip = new RenderData[1];
                RenderData data = new RenderData();
                strip[0] = data;
                MPI.COMM_WORLD.Recv(strip, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MessagingTags.RenderDataFromSlave);
                
                // Add strip to array
                stripArray[source-1] = strip[0];
            }
            
            RawFrame new_frame = aggregateData(stripArray);     // Aggregate the strips
             
            // Write received frames to file
            // --- Add code here ---
            // FileWriter writer( "my/path" );
            // writer.writeSimulationData(new_frame);
        }
        
    }
    
    /**
     * Sends initial conditions to slave nodes. 
     * @param 
     */
    private void sendInitialConditions(Data initData, int Dest) throws MPIException
    {
    	System.out.println("***1***");
    	Data tempData[] = new Data[1];
    	System.out.println("***2***");
    	// - Data requires data to create data. :) call me: 
    	tempData[0] = new Data();
    	tempData[0] = initData;
    	System.out.println("***3***");
    	MPI.COMM_WORLD.Send(tempData, 0, 1, MPI.OBJECT, Dest, MessagingTags.Initialcondition_FromServer);
    	
    }
    
    /**
     * Function to aggregate RenderData objects into one large 
     * @param stripArray An array of RenderData objects that will be aggregated
     * @return A RawFrame object containg the aggregates array
     */
    // Function to aggregate subarrays into combined array
    private RawFrame aggregateData( RenderData[] stripArray )
    {
        int segmentHeight = stripArray[0].getYLength();
        int segmentWidth = stripArray[0].getXLength();
        int aggregatedHeight = segmentHeight*threadCount;              // NB: change this - they may not be all the same
        float tempArray[][] = new float[aggregatedHeight][segmentWidth];  // Aggregated array
        
        // Copy data objects' content to combined array.
        // Assume the objects are NOT sorted according to rank
        // The rank of each object corresponds to the index: i+1
        for( int i = 0; i != threadCount; i++ )
        {
            int currentRank = stripArray[i].getSourceRank();
            float objectArray[][] = stripArray[i].getDensity();
            int firstSegmentRow = (currentRank - 1)*segmentHeight;       // First row of each new segment
            
            // Copy the rows of each object array to the aggregated array
            for( int j = 0; j != segmentWidth; j++ )
                System.arraycopy( stripArray[j], 0, tempArray[j + firstSegmentRow], 0, aggregatedHeight );
        }
        
        // Create an aggregated array object
        RawFrame new_frame = new RawFrame(tempArray);
        return new_frame;
    }


}