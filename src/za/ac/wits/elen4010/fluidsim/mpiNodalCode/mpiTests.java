package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import java.io.IOException;
import java.util.Random;
import za.ac.wits.elen4010.fluidsim.gui.*;
import za.ac.wits.elen4010.fluidsim.mpiNodalCode.*;
import org.junit.*;
import static org.junit.Assert.* ;
import mpi.*;

/**
 * Test suite for mpi classes
 * @author Graham Peyton
 */
class mpiTests {

    /** Random number generator */
    Random randomGenerator = new Random();
    
    /** 
     * Main function for the compiler to recognise
     */
    public static void main(String args[])
    {
        mpiTests tests = new mpiTests();
        tests.testMainNodeRunMethodProducesCorrectSimulationOutput();
        tests.testAggregateDataWorksCorrectly();
    }
    
    /**
     * 
     * Tests the main run function of the main node. 
     * A fake mpiIO module is inserted into the main node
     * 
     */
    @Test
    public void testMainNodeRunMethodProducesCorrectSimulationOutput()
    {
     // ------------ Creating test data ---------------
        /* Read initial conditions from file.
         * The file "testInputRun.in" contains two arrays of an EVEN number of ints from 1-24, concatenated together. 
         * First we split this in half to generate input data, which is fed into the sim. The output produced by the 
         * sim ("test.out") should be the SAME as "testInputRun.in" for the test to pass. 
         */
        RawFrame dataInput = null;

        FileReader<RawFrame> fileReader1 = new FileReader<RawFrame>( "testInputRun.in" );
        dataInput = fileReader1.readNextFrame();
        
        // dataTemp is split exactly half of the dataInput array.
        float[][] data = dataInput.getFrame();
        printArray(data, "data");
        final int rowTemp = data[0].length/2;
        final int colTemp = data.length;
        float[][] dataTemp = new float[ colTemp ][ rowTemp ];
        
        for (int y = 0; y != dataTemp[0].length; ++y) 
            for (int x = 0; x != dataTemp.length; ++x) 
                dataTemp[x][y] = data[x][y];
        
        printArray(dataTemp, "dataTemp");
        
        RenderData[] strip = new RenderData[ 1 ];
        strip[0] = new RenderData(dataTemp);
     
        // ------------ Simulation code ---------------
        // Initialise the fake mpiIO module
        final int commSize = 3;
        MpiIO fakeIO = new FakedIO( strip );
        fakeIO.initProcess(commSize);
        
        // Create a new MainNode. Run the node to generate test.out
        try{
            MainNode mainNode = MainNode.getInstance( fakeIO );
            mainNode.run();                                         // NB: the default number of frames is TWO
        } catch(MPIException e) {
            System.out.println( e.getMessage( ) );
        }
        
        // -------------- Read from file to confirm the contents ----------------
        RawFrame testFrame = null;
        FileReader<RawFrame> fileReader2 = new FileReader<RawFrame>( "test.out" );
        testFrame = fileReader2.readNextFrame();
        
        float[][] tempRawFrame = testFrame.getFrame();
        printArray(tempRawFrame, "tempRawFrame");
        
        assertEquals(data,testFrame.getFrame());
    }
    
    public void printArray(float[][] array, String message)
    {
        System.out.println("======== Printing " + message + " array =======");
        for (int y = 0; y != array[0].length; ++y) {
            for (int x = 0; x != array.length; ++x) {       // Could also use System.arraycopy()
                System.out.println(array[x][y]);
            }
            System.out.println("New row");
        }
    }
    
    @Test 
    public void testAggregateDataWorksCorrectly()
    {
        // ---------------- Generate test input -----------------
        /* "testInputAggregate.in" contains a 6x4 array with values from 1 to 24 (see dataTemp in the above test)
         * This data is fed into the the aggregateData function to test whether it correctly aggregates two strips.
         * The output is compared with "testInputRun.in" (see test above) which contains the expected aggregated result
         */
        FileReader<RenderData> reader = new FileReader<RenderData>( "testInputAggregate.in" );
        RenderData[] renderArray = new RenderData[2];
        renderArray[0] = reader.readNextFrame(); renderArray[0].setSourceRank(1);
        renderArray[1] = reader.readNextFrame(); renderArray[1].setSourceRank(2);
        
        // -------------- Run the simulation code ----------------
        // Initialise the fake mpiIO module - this is actually superfluous here.
        MpiIO fakeIO = new FakedIO( renderArray );
        // Create a new MainNode. Run aggregateData to produce a rawFrame
        RawFrame aggregatedtFrame = null;
        try{
            MainNode mainNode = MainNode.getInstance( fakeIO );
            aggregatedtFrame = mainNode.aggregateData(renderArray);                             // NB: the default number of frames is TWO
        } catch(MPIException e) {
            System.out.println( e.getMessage() );
        }
        
        // -------------- Read from file to confirm the contents ----------------
        // The file will contain two concatenated testInputAggregate.in arrays 
        RawFrame testFrame = null;
        FileReader<RawFrame> fileReader3 = new FileReader<RawFrame>( "testInputRun.in" );       // could also test.out here
        testFrame = fileReader3.readNextFrame();
        
        assertEquals(aggregatedtFrame, testFrame);
    
    }
    
    /**
     * Tests the aggregateData function of MainNode. Confirms that data strips are aggregates correctly.
     */
    //@Test
    //public void 
    
}

/*RenderData hello = new RenderData(dataTemp);
// Write received frames to file
FileWriter<RenderData> fileWriter3 = new FileWriter<RenderData>( "testInputAggregate.in" );
fileWriter3.writeSimulationData( hello );*/


/*Create some dummy data
final int rows = 8; final int cols = 6; int counter = 1;
float[][] data = new float[cols][rows];
for (int y = 0; y != rows; ++y) {
    for (int x = 0; x != cols; ++x) {
        data[x][y] = counter++;
    }
}

// Write the output
System.out.println("Printing data array ___________________");
for (int y = 0; y != data[0].length; ++y) {
    for (int x = 0; x != data.length; ++x) {       // Could also use System.arraycopy()
        System.out.println(data[x][y]);
    }
    System.out.println("New row");
}

RawFrame new_frame = new RawFrame( data ); 
FileWriter<RawFrame> fileWriter = new FileWriter<RawFrame>( "testSimulationData.in" );
fileWriter.writeSimulationData( new_frame );*/

/*// Create dummy data
        final int rows = 8; final int cols = 4; int counter = 1;
        float[][] data = new float[cols][rows];
        for (int y = 0; y != rows; ++y) {
            for (int x = 0; x != cols; ++x) {
                data[x][y] = counter++;
            }
        }
        
        // Write the output
        System.out.println("Printing array ___________________");
        for (int y = 0; y != data[0].length; ++y) {
            for (int x = 0; x != data.length; ++x) {       // Could also use System.arraycopy()
                System.out.println(data[x][y]);
            }
            System.out.println("New row");
        }*/


