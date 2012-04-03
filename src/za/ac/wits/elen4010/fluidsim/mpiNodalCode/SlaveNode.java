package za.ac.wits.elen4010.fluidsim.mpiNodalCode;
import mpi.*;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;


public class SlaveNode 
{
    /*Variables*/
    //Environment Variables
    int MyRank ;
    final  int HostRank = 0 ;
    int msgAddrsNeighbourAbove, msgAddrsNeighbourBelow;
    
    //Local factories,container and storage classes
    MessagingTags tags ;
    NeighboursData neighbourData[] = new NeighboursData[1] ;
    
    
    String myHost = new String();
    
    Boolean hostInitialised = false;
    
    int[][] LocalDatacopy ;


   public SlaveNode( int slaveNodeRank,String slaveNodeHost )
   {
	   if (slaveNodeRank != 0)
	   {
		   MyRank = slaveNodeRank ;
		   myHost = slaveNodeHost;
		   neighbourData[0] = new NeighboursData();
		   try {
			initialiseNode();
		} catch (MPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	   else
	   {
		//throw exception log error   
	   }
	   

   
   
   }
    
   // Primary goal here is to call home with Rank+Host and receive neighbours list + initial conditions.
   public void initialiseNode() throws MPIException
   {
	
	   // a host may only be initialised once. If the main node is not listening for the message then the porgram will be blocked after the fist send.
	   if (hostInitialised == false)
		{   
			
			char[] message = null ;
			//The "?" will be used later to help with string manipulation
			message = ("?" + myHost + "?" + Integer.toString(MyRank) + "?" ).toCharArray();
			//System.out.println(message);
			
			// Contact MainNode with Thread Rank and Host.   
		   	MPI.COMM_WORLD.Send(message, 0, message.length, MPI.CHAR,HostRank, MessagingTags.HostAndRank_ToServer);
		   	
		    //Block here and wait to receive Neighbour's Ranks
		   	System.out.println(new String(message) + " is waiting to recieve!");
		   	MPI.COMM_WORLD.Recv(neighbourData, 0, 1, MPI.OBJECT, HostRank, MessagingTags.Neighbours_FromServer);
		   	
		   	msgAddrsNeighbourAbove = neighbourData[0].NeighbourAbove ;
		   	msgAddrsNeighbourBelow = neighbourData[0].NeighbourBelow ;
		   	System.out.println("Above: "+ msgAddrsNeighbourAbove + " Below: " + msgAddrsNeighbourBelow);
		   	//Block here and wait to receive initial conditions
		  
		   	Data tempData[] = new Data[1];
		   	int[][] dummy;
		   	dummy = new int[5][5];
	    	//This seems stupid - Graham? Thoughts? If i didn't mention this over the phone message me
	    	tempData[0] = new Data(dummy);
		   	MPI.COMM_WORLD.Recv(tempData, 0, 1, MPI.OBJECT, HostRank, MessagingTags.Initialcondition_FromServer);
		   	
		   	LocalDatacopy = new int[tempData[0].getDataArray().length][tempData[0].getDataArray()[0].length];
		   
		   	hostInitialised = true;
		}
	   
   	
   }
   
   public void run () throws IOException, MPIException
   {
	   int[] tempRow;
	   tempRow = new int[5] ;
	   //Three cases of IF. 1) if I am the first row - above = 0, 2) if I am the last row below = 0, 3) if I am row inbetween.
	   if (msgAddrsNeighbourAbove == 0) //I am on top
	   {
		   //calculate, then send, then receive and hold
		   for(int a = 0; a <9 ; a++)
		   {
			   LocalDatacopy[4] = tempRow ;
			   //Increment First Row
			   for(int j = 0; j != LocalDatacopy[0].length;j++)
			   {
			   	LocalDatacopy[0][j]++;
			   }

			   for(int j = 0; j != 50;j++)
			   {
				   System.out.println("");
			   }

			   
			   System.out.println(Arrays.toString(LocalDatacopy[0]));
			   System.out.println(Arrays.toString(LocalDatacopy[1]));
			   System.out.println(Arrays.toString(LocalDatacopy[2]));
			   System.out.println(Arrays.toString(LocalDatacopy[3]));
			   System.out.println(Arrays.toString(LocalDatacopy[4]));
		
			  
			   try 
			   {
				Thread.sleep(500);
			   } catch (InterruptedException e) 
			   {
				// TODO Auto-generated catch block
				e.printStackTrace();
			   }
			   
			   MPI.COMM_WORLD.Send(LocalDatacopy[4], 0, 5, MPI.INT, msgAddrsNeighbourBelow, MessagingTags.BoundryInfo_FromNeighbourAbove);
			   MPI.COMM_WORLD.Recv(tempRow, 0, 5, MPI.INT,msgAddrsNeighbourBelow, MessagingTags.BoundryInfo_FromNeighbourAbove);
		   }
		   
		   
	   }
	   else if (msgAddrsNeighbourBelow == 0)//I am at the bottom
	   {   //receive, then calculate and send
		   for(int a = 0; a <9 ; a++)
		   {
			   MPI.COMM_WORLD.Recv(tempRow, 0, 5, MPI.INT, msgAddrsNeighbourAbove, MessagingTags.BoundryInfo_FromNeighbourAbove);
			   
			   for(int j = 0; j != tempRow.length;j++)
			   {
				   tempRow[j]++;
			   }
			   
			   MPI.COMM_WORLD.Send(tempRow, 0, 5, MPI.INT, msgAddrsNeighbourAbove, MessagingTags.BoundryInfo_FromNeighbourAbove);
	  
		   }
	   }
	   else
	   {
		   //the code for above and below goes here
	   }
   }

}
