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

import za.ac.wits.elen4010.fluidsim.gui.TimeCapture;
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
      
        TimeCapture.getInstance().setActive( true );     

        /** Correct io module to be used for the actual simulation*/
        MpiIO correctIO = new TrueIO();
        
        /** Initialize MPI */
        MPI.Init(args) ;
    
        /** Rank of the current process */
        int myrank = correctIO.myRank();
        /** Size of the communicator */
        int p = correctIO.commWorldSize();
        /** Total number of simulation frames */
        int frames = 2;

    
        if(myrank != 0)  // my_rank != 0 => slave node
        {
            try 
            {   //create a slave node
                SlaveNode LocalSlave = new SlaveNode( myrank, p, correctIO );
                LocalSlave.run( frames );
            
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
            MainNode mainNode = MainNode.getInstance( correctIO );
            mainNode.run( frames );
        }
     
        TimeCapture.getInstance().writeCSVData();
        
        MPI.Finalize();
    
  }
}

