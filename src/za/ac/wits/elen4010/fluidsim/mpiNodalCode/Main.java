package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

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

import za.ac.wits.elen4010.fluidsim.sim.Fluid;
import mpi.*;

/** 
 * Main MPI program
 * 
 * @author Graham Peyton
 * @author Rudolf Hoehler
 * 
 */
class Main
{
    /**
     * Main MPI method
     */
    static public void main(String[] args) throws MPIException 
    {
      
    /** Initialize MPI */
    MPI.Init(args) ;

    /** Rank of the current process */
    int myrank = MPI.COMM_WORLD.Rank() ;
    /** Size of the communicator */
    int p = MPI.COMM_WORLD.Size() ;

    if(myrank != 0)  // my_rank != 0 => slave node
    {
        try 
        {   //create a slave node
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
    else  // my_rank == 0 => master node
    {  
        MainNode mainNode = new MainNode();
        mainNode.run();
    }
 
    MPI.Finalize();
    
  }
}

