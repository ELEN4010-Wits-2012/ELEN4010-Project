package za.ac.wits.elen4010.fluidsim.mpiNodalCode;
import mpi.*;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;

import za.ac.wits.elen4010.fluidsim.sim.*;
import za.ac.wits.elen4010.fluidsim.sim.Fluid.Side;

/**
 * Class representing the slave node
 * 
 * @author Graham Peyton
 * @author Rudolf Hoehler
 * 
 */
public class SlaveNode 
{
    /*Variables*/
    //Environment Variables
    int MyRank ;
    final  int HostRank = 0 ;
    int msgAddrsNeighbourAbove, msgAddrsNeighbourBelow;
    
    private Fluid fluid ;
    Fluid.Side myside  ;
    
    
    //Local factories,container and storage classes
    MessagingTags tags ;
    NeighboursData neighbourData[] = new NeighboursData[1] ;
    
    
    String myHost = new String();
    
    Boolean hostInitialised = false;
    
    int[][] LocalDatacopy ;

    // 
    public SlaveNode( int slaveNodeRank,String slaveNodeHost )
    {
	   if (slaveNodeRank != 0)
	   {
		   MyRank = slaveNodeRank ;
		   myHost = slaveNodeHost;
		   neighbourData[0] = new NeighboursData();
		   try {
		       initialiseNode();
		   } 
		   catch (MPIException e) {
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
       if (hostInitialised == false)
       {   
            char[] message = null ;
            //The "?" will be used later to help with string manipulation
            message = ("?" + myHost + "?" + Integer.toString(MyRank) + "?" ).toCharArray();
            
            // Contact MainNode with Thread Rank and Host.   
            MPI.COMM_WORLD.Send(message, 0, message.length, MPI.CHAR,HostRank, MessagingTags.HostAndRank_ToServer);
            
            //Block here and wait to receive Neighbour's Ranks
            System.out.println(new String(message) + " is waiting to recieve!");
            MPI.COMM_WORLD.Recv(neighbourData, 0, 1, MPI.OBJECT, HostRank, MessagingTags.Neighbours_FromServer);
            
            msgAddrsNeighbourAbove = neighbourData[0].NeighbourAbove ;
            msgAddrsNeighbourBelow = neighbourData[0].NeighbourBelow ;
            System.out.println("Above: "+ msgAddrsNeighbourAbove + " Below: " + msgAddrsNeighbourBelow);
            //Block here and wait to receive initial conditions
              
               	//Recieve Fluid Paramteres
            //int[] paramArray = new paramArray[] ;
               	
               //	MPI.COMM_WORLD.Recv()
            //Create Fluid
            if (msgAddrsNeighbourAbove == 0)
            	fluid = new Fluid(1920,1080,myside.TOP);
            else
            	fluid = new Fluid(1920,1080,myside.BOTTOM);
            
            //Recieve Inital Conditions
            //float[][] stupidfloat1 = new float[256][128] ;
            Data datainit = new Data();		   	
            Data[] tempData = new Data[1];
            tempData[0] = datainit ;
            
            //tempData[0].setData(stupidfloat1, stupidfloat1, stupidfloat1, 0);
            //stupidfloat1[50][50] = 42 ;
            System.out.println("--==About to recieve==--");
            MPI.COMM_WORLD.Recv(tempData, 0, 1, MPI.OBJECT, HostRank, MessagingTags.Initialcondition_FromServer);
            System.out.println("--==Recieved==--");
            System.out.println(tempData[0].getuVelocity()[50][50]);
            //LocalDatacopy = new int[tempData[0].getDataArray().length][tempData[0].getDataArray()[0].length];			
               
            hostInitialised = true;
		}
	   
   	
   }
   
   public void run () throws IOException, MPIException
   {
	   Data stepData = new Data();
	   Data[] tempData = new Data[1];
	   	
	   int[] tempRow;
	   tempRow = new int[5] ;
	   int counter ;
	   counter = 0;
	   //Three cases of IF. 1) if I am the first row - above = 0, 2) if I am the last row below = 0, 3) if I am row inbetween.
	  System.out.println("SlavenodeUp!");
	   
	   if (msgAddrsNeighbourAbove == 0) //I am on top
	   {
			while (counter < 10)
			{
		   		//calculate, then send, then receive and hold
			   fluid.step();
			   stepData.setData(fluid.getEdgeRho(), fluid.getEdgeU(), fluid.getEdgeV(), 99)	;
			   tempData[0] = stepData ;
			   MPI.COMM_WORLD.Send(tempData, 0, 1, MPI.OBJECT, msgAddrsNeighbourBelow, MessagingTags.BoundryINfo_ToNeighbourBelow);
			   MPI.COMM_WORLD.Recv(tempData, 0, 1, MPI.OBJECT,msgAddrsNeighbourBelow, MessagingTags.BoundryInfo_FromNeighbourBelow);
			   fluid.setOverlap(tempData[0].getDensity() , tempData[0].getuVelocity(), tempData[0].getvVelocity());
			counter ++ ;
			System.out.println("Stepping Even:" + counter);
			}  
	   }
	   else if (msgAddrsNeighbourBelow == 0)//I am at the bottom
	   {   //receive, then calculate and send
		   while (counter < 10)
			{
		   
		   MPI.COMM_WORLD.Recv(tempData, 0, 1, MPI.OBJECT,msgAddrsNeighbourAbove, MessagingTags.BoundryInfo_FromNeighbourAbove);
		   fluid.setOverlap(tempData[0].getDensity() , tempData[0].getuVelocity(), tempData[0].getvVelocity());
		   System.out.println("Simulation Stepping");
		   fluid.step();
		   
		   System.out.println("Preparing Send");
		   stepData.setData(fluid.getEdgeRho(), fluid.getEdgeU(), fluid.getEdgeV(), 99)	;
		   tempData[0] = stepData ;
		   MPI.COMM_WORLD.Send(tempData, 0, 1, MPI.OBJECT, msgAddrsNeighbourAbove, MessagingTags.BoundryInfo_ToNeighbourAbove);
		   System.out.println("SENT");
		   
			}
	   }
	   else
	   {
		   //the code for above and below goes here

	   }
   }

}
