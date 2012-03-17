/**
 * @Author Rudolf Hoehler
 * @Author Graham Peyton
 *
 * Silly little mpiJava test program:
 *  -Main node generates two random numbers and Messages them directly to each thread
 *  -Threads send their greetings as well as the receive the two number and send back the result of their product
 *  -The program takes no arguments and log file paths are hard coded - See below foe details
 *
 *
 *
 *
 * sudo mkdir /mpiSandBox/
 * chmod 777 /mpiSandBox  
 * mpiexec -n 12 -hosts RhTestMpi1,RhTestMpi2,RhTestMpi3 java Hello

*/

import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import java.text.*;
import mpi.* ;
 


class Main
{
    
    static public void main(String[] args) throws MPIException
    {
      
    //Initialize the mpi environment
	MPI.Init(args) ;
    int my_rank = 0; // Rank of process
	int source = 0;  // Rank of sender
	int dest = 0;    // Rank of receiver 
	int tag=50;  // General Tag for messages
	int tagAns = 51; // ANSWER Tag for messages
	int tagSums = 52 ; // // SUMS Tag for messages	
    int tag2DArray = 53; // 2D array Tag
	int myrank = MPI.COMM_WORLD.Rank() ; // Rank of the current process
	int p = MPI.COMM_WORLD.Size() ;  // total number of processes in the global communicator
    int rows = 10;
    int columns = 10;
    int MsgSize = 4;
	int array2D[][] = new int[rows][columns]; // 2D array
    int array2DR[][] = new int[rows][columns]; // 2D array
    //int test[][] = new int[2][2];
    Array2D test[] = new Array2D[4] ;

    for( int i = 0; i != 4; i++ )
    {
        test[i] = new Array2D();
    }

	//Initialize Program Variables
	int sums [] = new int [] {0, 0} ;
	int ans  [] = new int [] {0};
	Random randIntGen = new Random() ;


    //setup date format for time stamp
	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	//get current date time with Date()
	Date theDate = new Date();
    try 
    {//try and open a file and write to the log file 
    
	/*setup the log file */		
	String FileName = ("/mpiSandBox/temp" + InetAddress.getLocalHost().getHostName() + ".txt");
	FileWriter outFile = new FileWriter(FileName, true);
	PrintWriter writeOut = new PrintWriter(outFile); //Write out is the object that writes to the log file.
	
	//Time Stamp and Headings
	writeOut.println("");
	writeOut.println("---===### LOG FILE : " + InetAddress.getLocalHost().getHostName() + " : Thread Rank " +Integer.toString(myrank)+ " :###===---");
	writeOut.println(dateFormat.format(theDate));
	writeOut.println("");	


	    if(myrank != 0) // 
	    {
	    /*
	    All code for the Nodes goes here
	    */
	    dest=0;
	    char[] message = null;

	    //Send thread's' greetings
		message = ( "Greetings from computer: " + InetAddress.getLocalHost().getHostName()  ).toCharArray();	   
	    MPI.COMM_WORLD.Send( message, 0, message.length, MPI.CHAR,dest, tag );
	    writeOut.println("Sent message: " + "Greetings from computer: " + InetAddress.getLocalHost().getHostName());
	    
        // Receive 2D array
        MPI.COMM_WORLD.Recv( test, 0,MsgSize, MPI.OBJECT, 0, tag2DArray );
        //log 
	    //writeOut.println("Recieved 2D array:" + array2DR[0][0] + " " + array2DR[1][1]) ;
         for (int i=0; i != rows; i++) {
            for (int j=0; j != columns; j++)
            {
                 test[0].myArray[i][j]++;
                 writeOut.println("Rec Looping: " + test[0].myArray[i][j]);
            }
         }
       
        
        MPI.COMM_WORLD.Send( test, 0, MsgSize, MPI.OBJECT, 0, tag2DArray);


        //Receive two random numbers from main node
	    MPI.COMM_WORLD.Recv( sums, 0, 2, MPI.INT, 0, tagSums );
	    //log
	    writeOut.println("Recieved message: " +sums[0] +" " + sums[1] ) ;
	    //compute
	    ans[0] = sums[0] * sums[1];
	    //send
	    MPI.COMM_WORLD.Send( ans, 0,1, MPI.INT,0, tagAns );
	    //log
	    writeOut.println("Sent message: " + ans[0]);   
	        	

            
	    }// end if

         // Observation:
         // Each send & receive is a blocked process. Must wait for each process to calculate before receiving
         // Only then continue on to the next process. Requires two separate for loops - one for send, one for receive. 
	    else
	    {  // my_rank == 0
	    /*
	    All code for the Main Node goes here
	    */
	    System.out.println("This Is the Main Node");
	    writeOut.println("This Is the Main Node");
	        for ( source =1;source != p;source++)
	        {
		    char[] message = new char [40];
		    //receive greetins
		    MPI.COMM_WORLD.Recv( message, 0, 40, MPI.CHAR, source, tag );
		    System.out.println( new String(message) );
		    
		    //generate and send random number
            sums[0] = randIntGen.nextInt(99);
		    sums[1] = randIntGen.nextInt(99);
            
            // Create and initialise 2D array test
            
            for (int i=0; i != rows; i++)
            {
                for (int j=0; j != columns; j++)
                {
                    test[0].myArray[i][j] = randIntGen.nextInt(99);
                    writeOut.println("Send 2D array :" + test[0].myArray[i][j] );
                }
            }

            MPI.COMM_WORLD.Send( test, 0, MsgSize, MPI.OBJECT, source, tag2DArray );
		    writeOut.println( "Sent 2D array " );
            
            MPI.COMM_WORLD.Recv( test, 0, MsgSize, MPI.OBJECT, source, tag2DArray );

            for (int i=0; i != rows; i++)
            {
                for (int j=0; j != columns; j++)
                {
                     writeOut.println("Receive 2D array :" + test[0].myArray[i][j] );
                }
            }      

           
            writeOut.println("Send Sums to thread " + Integer.toString(source)+ ". " + sums[0] + " x " + sums[1] );
		    MPI.COMM_WORLD.Send( sums, 0, 2, MPI.INT, source, tagSums );
		    writeOut.print("Receive ans from thread " + Integer.toString(source)+ "."  );
		    MPI.COMM_WORLD.Recv( ans, 0, 1, MPI.INT, source, tagAns );
		    writeOut.println(" " + ans[0]);
	        }
	    }//end elseif
    
    
    //close the log file 
    writeOut.println( "______EOL______" );
	writeOut.close();
    } 
    catch (IOException e)
    {//log file trycatch
			e.printStackTrace();
	}
    
			
	MPI.Finalize();
    }
}


