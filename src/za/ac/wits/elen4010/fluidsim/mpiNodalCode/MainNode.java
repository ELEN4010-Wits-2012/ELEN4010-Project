package za.ac.wits.elen4010.fluidsim.mpiNodalCode;
//ONLY RUN THIS PROGRAM WITH: mpiexec -n 3 java Main
import mpi.*;

import java.net.*;
import java.util.*;


public class MainNode
{
	
    /*Variables*/
    //Environment Variables
    static int mainNodeCount = 0;
    int threadCount ;
    
    List<String> SlaveNodeList = new ArrayList<String>();
    
    //Local factories,container and storage classes
    //MessagingTags tags ;
    NeighboursData neighbourData ;
    
    
    
    
    Boolean hostInitialised = false;
    
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
    	
    }
    
    public void sendInitialConditions(int[][] initData, int Dest) throws MPIException
    {
    	System.out.println("***1***");
    	
    	Data tempData[] = new Data[1];
    	System.out.println("***2***");
    	// - Data requires data to create data. :) call me: 
    	tempData[0] = new Data(initData);
    	System.out.println("***3***");
    	MPI.COMM_WORLD.Send(tempData, 0, 1, MPI.OBJECT, Dest, MessagingTags.Initialcondition_FromServer);
    	
    }
    


}