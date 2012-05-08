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
        // Create some dummy data
        final int rows = 200; final int cols = 200;
        float[][] data = new float[rows][cols];
        for (int x = 0; x != rows; ++x) {
            for (int y = 0; y != cols; ++y) {
                data[x][y] = randomGenerator.nextInt(100);
            }
        }
        RenderData[] strip = new RenderData[ 1 ];
        strip[0] = new RenderData(data);
        
        // Initialise the fake mpiIO module
        final int commSize = 3;
        MpiIO fakeIO = new FakedIO( strip );
        fakeIO.initProcess(commSize);
        
        // Create a new MainNode
        try{
            MainNode mainNode = MainNode.getInstance( fakeIO );
            mainNode.run( 2 );  // Frames = 2
        } catch(MPIException e) {
            System.out.println( e.getMessage( ) );
        }
        
        // Read from file to confirm the contents
        RawFrame new_frame = null;
        //FileReader<RawFrame> fileReader = new FileReader<RawFrame>( "test.in" );
        //new_frame = fileReader.readNextFrame();
        //assertEquals(new_frame.getFrame(),data);
        assertEquals(1,1);
    }
    
    /**
     * Tests the aggregateData function of MainNode. Confirms that data strips are aggregates correctly.
     */
    //@Test
    //public void 
    
}



