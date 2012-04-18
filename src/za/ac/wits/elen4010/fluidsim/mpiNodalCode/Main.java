package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

/**
 * @Author Rudolf Hoehler
 * @Author Graham Peyton
 *
 * mpiexec -n 3 java Main

*/

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import java.text.*;
import mpi.*;

class Main
{

    

    static public void main(String[] args) throws MPIException 
    {
      
    /* Initialize MPI */
    MPI.Init(args) ;

    /*-=Variables=-*/
    
    //

    int myrank = MPI.COMM_WORLD.Rank() ;
    int p = MPI.COMM_WORLD.Size() ;

    if(myrank != 0) 
    {// Then I am I slave
    	try 
    	{
			SlaveNode LocalSlave = new SlaveNode(myrank, InetAddress.getLocalHost().getHostName().toString());
			LocalSlave.run();
    	
    	} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
    else 
    {  // my_rank == 0 and I am the master node

    	MainNode mainNode = new MainNode();
        
    	
    	int [][] TempDataSet;
    	TempDataSet = new int[5][5];
    	
    	for (int i=0; i != 5; i++) {
            for (int j=0; j != 5; j++)
            {
            	TempDataSet[i][j] = 0;
            }
         }
    	
    	mainNode.sendInitialConditions(TempDataSet, 1);
    	mainNode.sendInitialConditions(TempDataSet, 2);
	
    }
 
    MPI.Finalize();
    
  }
}

