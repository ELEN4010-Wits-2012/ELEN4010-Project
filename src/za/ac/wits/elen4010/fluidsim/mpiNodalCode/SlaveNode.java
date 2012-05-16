package za.ac.wits.elen4010.fluidsim.mpiNodalCode;
import mpi.*;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;

import za.ac.wits.elen4010.fluidsim.sim.*;
import za.ac.wits.elen4010.fluidsim.sim.Fluid.Side;
import za.ac.wits.elen4010.fluidsim.gui.*;

/**
 * Class representing the slave node
 * 
 * @author Graham Peyton
 * @author Rudolf Hoehler
 * 
 */
public class SlaveNode 
{
    /* ---- Variables ----*/
    /** Rank of the current process */
    int MyRank ;
    /** Rank of the master node */
    final  int HostRank = 0 ;
    /** Size of the entire communicator */
    int commSize = 0;
    /** Local fluid object */
    private Fluid fluid ;
    /** The communications module used by the SlaveNodes to communicate to oether nodes*/
    private MpiIO commModule;
    /** Number of frames to calculate */
    int frames = 2;
    
    /** Stores messaging tags */
    MessagingTags tags ;
    /** Bollean stored initialisation state of host */
    Boolean hostInitialised = false;
    /** What does this do? */
    int[][] LocalDatacopy ;

    /** 
     * Slave node constructor
     * @param slaveNodeRank The rank of the current slave process
     * @param p The size of the entire communicator
     */
    public SlaveNode( int slaveNodeRank, int p, MpiIO ioModule ) throws MPIException
    {

        commModule = ioModule;
        commSize = commModule.commWorldSize();

    	if (slaveNodeRank != 0)
    	{
    	    this.commSize = p;
    	    MyRank = slaveNodeRank ;
    		   
    	    try 
    	    {
    	        initialiseNode();
    		} 
    		catch (MPIException e) 
    		{
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    		}
    	}
    	else
    	{
    	    //throw exception log error   
    	}
   }
    
   /** 
    * Function to initialise fluid object handled by slave node
    * @throws MPIException
    */
   public void initialiseNode() throws MPIException
   {
       if (hostInitialised == false)
       {   
            // Array to store user input objects        
            SimulationInput[] initialConditions = new SimulationInput[1];
            // Receive initial conditions
            commModule.mpiReceive(initialConditions, 0, 1, MPI.OBJECT, HostRank, MessagingTags.Initialcondition_FromServer);
            System.out.println("Recieved IC to process rank " + MyRank);
           
            // Set the number of frames
            frames = initialConditions[0].getFrameCount();
            
            // Set the offset value
            final int xDim = 1440; final int yDim = 1080;                   // HARD CODED
            //int stripHeightMin = yDim/(commSize-1);                         // Rounds down
            //System.out.println("stripHeightMin =========== " + stripHeightMin);
            //int stripHeightMax = (int)Math.ceil(yDim/(float)(commSize-1));       // Rounds up
            int stripHeight = yDim/(commSize-1);
            //System.out.println("stripHeightMax ===== " + stripHeightMax + "stripHeightMin ===== " + stripHeightMin);
                    
            if (MyRank == 1)                          // Top Strip
            {
            	fluid = new Fluid( 0, stripHeight, 20, xDim, true, false, initialConditions[0] ); 
            	System.out.println("Top strip: " + xDim + " by " + stripHeight);
            }
            else if (MyRank == commSize-1)            // Bottom Strip
            {
            	stripHeight = yDim - (stripHeight*(commSize-2)); //calculates the height of the other stips combined, and makes the last one the size needed to make the total yDim
            	fluid = new Fluid( (MyRank-1)*stripHeight, stripHeight, 20, xDim, false, true, initialConditions[0] );
            	System.out.println("Bottom Strip " + commModule.myRank()+ ": " + xDim + " by " + stripHeight);
            }
            else                                      // Middle strip
            {
                fluid = new Fluid( (MyRank-1)*stripHeight, stripHeight, 20, xDim, false, false, initialConditions[0] );
                System.out.println("Middle Strip: " + xDim + " by " + stripHeight);
            }
            System.out.println("Initialising fluid for process rank " + MyRank);
            		
            
            /* Initialise the Fluid object
            // fluid.setUserInput(initialConditions);
            // For now, just set the density as follows:
            for ( int j = 50; j < 90; j++ )
                for ( int i = 10; i < 53; i++ )
                    fluid.densityOld[j][i] = 20f;*/
            
            hostInitialised = true;
		}
   }
   
   /**
    * Main program loop on the slave node
    * @throws IOException
    * @throws MPIException
    */
   public void run () throws IOException, MPIException
   {
       System.out.println("Running slave process rank " + MyRank);
       
       if (MyRank == 1) //I am on top
	   {
			for ( int i = 0; i != frames; i++ )
			{
                int step = i+1;
                System.out.println("Stepping top strip: " + step);
                stepTopProcess();
			}
			    
	   }
	   else if (MyRank == commSize-1) // I am at the bottom
	   {   
           for ( int i = 0; i != frames; i++ )
           {
               int step = i+1;
               System.out.println("Stepping bottom strip: " + step);
               stepBottomProcess();
           }
	            
	   }
	   else // I am a middle strip
	   {
	       for ( int i = 0; i != frames; i++ )
	       {
	           if ( MyRank % 2 == 0 ) // My Rank is even
	           {
	               int step = i+1;
	               System.out.println("Stepping centre even strip: " + step);
	               stepEvenProcess();
	           }
	               
	           else                   // My Rank is odd
	           {
	               int step = i+1;
	               System.out.println("Stepping centre odd strip: " + step);
	               stepOddProcess();
	           }   
	       }
	   }
   }
   
   /**
    * Sends RenderData objects back to the master
    * 
    */
   private void sendRenderData() throws MPIException
   {
       long startTime = System.nanoTime();
       
        // This step could also be done after calculating all frames
        RenderData[] tempOut = new RenderData[1];
        tempOut[0] = fluid.getRenderData();
        tempOut[0].setSourceRank( MyRank );
        System.out.println("Dimensions of rank " + MyRank + " RenderData: " + tempOut[0].getXLength() + " by " + tempOut[0].getYLength());
        commModule.mpiSend( tempOut, 0, 1, MPI.OBJECT, HostRank, MessagingTags.RenderDataFromSlave );
        
       // tempOut[0].PrintDensity();
        
        long elapsedTime = System.nanoTime() - startTime;
        TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "sendRenderData", elapsedTime );
   }
   
   /**
    * Steps the top process
    * @throws MPIException
    */
   private void stepTopProcess() throws MPIException
   {
       EdgeData[] edge = new EdgeData[1];
       
       long stepStartTime = System.nanoTime();
       // calculate, send to master node
       fluid.step();
       long stepElapsedTime = System.nanoTime() - stepStartTime;
       TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "stepTopProcess", stepElapsedTime );
       
       sendRenderData();
       
       long boundarycommStartTime = System.nanoTime();
       // Send local boundary conditions
       edge[0] = fluid.getEdge( Side.BOTTOM );
       commModule.mpiSend(edge, 0, 1, MPI.OBJECT, MyRank+1, MessagingTags.BoundryInfo_ToNeighbourBelow);
       
       // Receive local boundary conditions and set overlap
       commModule.mpiReceive(edge, 0, 1, MPI.OBJECT, MyRank+1, MessagingTags.BoundryInfo_FromNeighbourBelow);
       fluid.setOverlap( edge[0] , Side.BOTTOM);  
       System.out.println("Top sent/received edges");
       
     //fake function name has been used to distinguish from other timer
       long boundarycommElapsedTime = System.nanoTime() - boundarycommStartTime;
       TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "sendBoundaryTopProcess", boundarycommElapsedTime );
   }
   
   /**
    * Steps the bottom process by one iteration
    * @throws MPIException
    */
   private void stepBottomProcess() throws MPIException
   {
       EdgeData[] edge = new EdgeData[1];
       
       // Calculate, send to master node
       long stepStartTime = System.nanoTime();
       fluid.step();
       long stepElapsedTime = System.nanoTime() - stepStartTime;
       TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "stepBottomProcess", stepElapsedTime );
       
       sendRenderData();
       
       long boundarycommStartTime = System.nanoTime();
       // Receive boundary conditions and set fluid overlap
       commModule.mpiReceive(edge, 0, 1, MPI.OBJECT,MyRank-1, MessagingTags.BoundryInfo_FromNeighbourAbove);
       fluid.setOverlap( edge[0] , Side.TOP);
       
       // Send Boundary Conditions 
       edge[0] = fluid.getEdge( Side.TOP );
       commModule.mpiSend(edge, 0, 1, MPI.OBJECT, MyRank-1, MessagingTags.BoundryInfo_ToNeighbourAbove);
       System.out.println("Bottom sent/received edges");
       
     //fake function name has been used to distinguish from other timer
       long boundarycommElapsedTime = System.nanoTime() - boundarycommStartTime;
       TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "sendBoundaryBottomProcess", boundarycommElapsedTime );
   }
   
   /**
    * Steps a centre even process
    * @throws MPIException 
    */
   private void stepEvenProcess() throws MPIException
   {
       EdgeData[] edge = new EdgeData[1];

       // Calculate, send to master node
       long stepStartTime = System.nanoTime();
       fluid.step();
       long stepElapsedTime = System.nanoTime() - stepStartTime;
       TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "stepEvenProcess", stepElapsedTime );
       
       sendRenderData();
       
       long boundarycommStartTime = System.nanoTime();
       // ======= Send below, receive above, send above, receive below =======
       edge[0] = fluid.getEdge( Side.BOTTOM );
       commModule.mpiSend(edge, 0, 1, MPI.OBJECT, MyRank+1, MessagingTags.BoundryInfo_ToNeighbourBelow);
       commModule.mpiReceive(edge, 0, 1, MPI.OBJECT, MyRank-1, MessagingTags.BoundryInfo_FromNeighbourAbove);
       fluid.setOverlap( edge[0] , Side.TOP);
       edge[0] = fluid.getEdge( Side.TOP );
       commModule.mpiSend(edge, 0, 1, MPI.OBJECT, MyRank-1, MessagingTags.BoundryInfo_ToNeighbourAbove);
       commModule.mpiReceive(edge, 0, 1, MPI.OBJECT, MyRank+1, MessagingTags.BoundryInfo_FromNeighbourBelow);
       fluid.setOverlap( edge[0] , Side.BOTTOM);
       System.out.println("Centre even sent/received edges");
       
       //fake function name has been used to distinguish from other timer
       long boundarycommElapsedTime = System.nanoTime() - boundarycommStartTime;
       TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "sendBoundaryEvenProcess", boundarycommElapsedTime );
   }
   
   /**
    * Steps a centre odd process
    * @throws MPIException
    */
   private void stepOddProcess() throws MPIException
   {
       EdgeData[] edge = new EdgeData[1];

       // Calculate, send to master node
       long stepStartTime = System.nanoTime();
       fluid.step();
       long stepElapsedTime = System.nanoTime() - stepStartTime;
       TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "stepOddProcess", stepElapsedTime );

       sendRenderData();
       
       long boundarycommStartTime = System.nanoTime();
       // ======= Receive above, send below, receive below, send above =======
       commModule.mpiReceive(edge, 0, 1, MPI.OBJECT, MyRank-1, MessagingTags.BoundryInfo_FromNeighbourAbove);
       fluid.setOverlap( edge[0] , Side.TOP);
       edge[0] = fluid.getEdge( Side.BOTTOM );
       commModule.mpiSend(edge, 0, 1, MPI.OBJECT, MyRank+1, MessagingTags.BoundryInfo_ToNeighbourBelow);
       commModule.mpiReceive(edge, 0, 1, MPI.OBJECT, MyRank+1, MessagingTags.BoundryInfo_FromNeighbourBelow);
       fluid.setOverlap( edge[0] , Side.BOTTOM);
       edge[0] = fluid.getEdge( Side.TOP );
       commModule.mpiSend(edge, 0, 1, MPI.OBJECT, MyRank-1, MessagingTags.BoundryInfo_ToNeighbourAbove);
       System.out.println("Centre odd sent/received edges");
       
       //fake function name has been used to distinguish from other timer
       long boundarycommElapsedTime = System.nanoTime() - boundarycommStartTime;
       TimeCapture.getInstance().addTimedEvent( Integer.toString( MyRank ), "sendBoundaryOddProcess", boundarycommElapsedTime );
   }
}
