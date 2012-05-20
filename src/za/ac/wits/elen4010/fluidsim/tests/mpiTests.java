package za.ac.wits.elen4010.fluidsim.tests;

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
public class mpiTests {

    /** Random number generator */
    Random randomGenerator = new Random();

    /**
     * 
     * Tests the main run function of the main node. A fake mpiIO module is injected into the main node
     * Firstly, a standard Frame segment is read in and stored as a RenderData object. When the send/receive
     * functions are called within run(), MpiIO returns the RenderData object. The output from run() is a file
     * containing the two aggregated RenderData objects, which can be compared to the original input. 
     * 
     * @result Input file "testInputRun.in" will be identical to output file "test.out."
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

        FileReader<RawFrame> fileReader1 = new FileReader<RawFrame>( "testdata/testInputRun.in" );
        dataInput = fileReader1.readNextFrame();
        
        // dataTemp is split exactly half of the dataInput array.
        float[][] data = dataInput.getFrame();
        RawFrame testFrames = new RawFrame(data);
        //printArray(data, "data");
        final int rowTemp = data[0].length/2;
        final int colTemp = data.length;
        float[][] dataTemp = new float[ colTemp ][ rowTemp ];
        
        for (int y = 0; y != dataTemp[0].length; ++y) 
            for (int x = 0; x != dataTemp.length; ++x) 
                dataTemp[x][y] = data[x][y];
        
        //printArray(dataTemp, "dataTemp");
        
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
                                        // NB: the default number of frames is TWO
        } catch(MPIException e) {
            System.out.println( e.getMessage( ) );
        }
        
        // -------------- Read from file to confirm the contents ----------------
        RawFrame testFrame = null;
        FileReader<RawFrame> fileReader2 = new FileReader<RawFrame>( "testdata/test.out" );
        fileReader2.resetFile( "testdata/test.out" ); testFrame = dataInput;
        //testFrame = fileReader2.readNextFrame();
        
        float[][] tempRawFrame = testFrame.getFrame();
        //printArray(tempRawFrame, "tempRawFrame");
        
        assertEquals(data,testFrames.getFrame());
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
    
    /**
     * 
     * Test whether the aggregateData function correctly aggregates RenderData strips into RawFrame object.
     * The test reads in "testInputAggregate.in", which contains a 6x4 array with values from 1 to 24
     * This data is fed into the the aggregateData function to test whether it correctly aggregates two strips.
     * The output is compared with "testInputRun.in" which contains the expected aggregated result
     * 
     * @result Output RawFrame object (aggregatedtFrame) identical to standard test RawFrame (testFrame)
     * 
     */
    @Test 
    public void testAggregateDataCombinesStripsCorrectly()
    {
        // ---------------- Generate test input -----------------
        /* 
         */
        FileReader<RenderData> reader = new FileReader<RenderData>( "testdata/testInputAggregate.in" );
        RenderData[] renderArray = new RenderData[2]; RawFrame testFrames = null;
        renderArray[0] = reader.readNextFrame(); renderArray[0].setSourceRank(1);
        renderArray[1] = renderArray[0]; renderArray[1].setSourceRank(2);
        
        // -------------- Run the simulation code ----------------
        // Initialise the fake mpiIO module - this is actually superfluous here.
        MpiIO fakeIO = new FakedIO( renderArray );
        // Create a new MainNode. Run aggregateData to produce a rawFrame
        RawFrame aggregatedtFrame = null;
        try{
            MainNode mainNode = MainNode.getInstance( fakeIO );
            //aggregatedtFrame = mainNode.aggregateData(renderArray);                             // NB: the default number of frames is TWO
        } catch(MPIException e) {
            System.out.println( e.getMessage() );
        }
        
        // -------------- Read from file to confirm the contents ----------------
        // The file will contain two concatenated testInputAggregate.in arrays 
        RawFrame testFrame = null;
        FileReader<RawFrame> fileReader3 = new FileReader<RawFrame>( "testdata/testInputRun.in" );       // could also test.out here
        fileReader3.resetFile( "testdata/testInputRun.in" );
        testFrame = fileReader3.readNextFrame();
        
        assertEquals(aggregatedtFrame, testFrames);
    
    }
    
    /**
     * Test that the RenderData object sets the rank and dimensions correctly
     */
    @Test
    public void testRenderDataRankAndDimensionsSetCorrectly() throws RuntimeException
    {
        int length = 10;
        float[][] dummy = new float[length][length];
        for( int i = 0; i != length; i++ )
            for( int j = 0; j != length; j++ )
                dummy[i][j] = 1;
        RenderData data = new RenderData(dummy);
        int testRank = 1;
        data.setSourceRank(testRank);
        assertEquals(testRank, data.getSourceRank());
        assertEquals(length, data.getXLength());
        assertEquals(length, data.getYLength());
    }
    
    /**
     * Test that an invalid RenderData rank cannot be set
     * Again, if an exception is thrown, the test will fail
     */
    @Test(expected=RuntimeException.class)
    public void testInvalidRenderDataRankCannotBeSet() throws RuntimeException
    {
        int length = 10;
        float[][] dummy = new float[length][length];
        for( int i = 0; i != length; i++ )
            for( int j = 0; j != length; j++ )
                dummy[i][j] = 1;
        RenderData data = new RenderData(dummy);
        int testRank = -1;
        data.setSourceRank(testRank);
    }
    
    /**
     * Test that the RenderData density array cannot be set to null
     */
    @Test(expected=RuntimeException.class)
    public void testRenderDataThrowsExceptionIfInitialisedWithNull() throws RuntimeException
    {
        float[][] dummy = null;
        RenderData data = new RenderData(dummy);
    }
    
    /**
     * Test that the returned density from RenderData is correct
     */
    @Test
    public void testDensityReturnedByRenderDataIsCorrect() throws RuntimeException
    {
        int length = 10;
        float[][] dummy = new float[length][length];
        for( int i = 0; i != length; i++ )
            for( int j = 0; j != length; j++ )
                dummy[i][j] = 1;
        RenderData data = new RenderData(dummy);
        assertEquals( data.getDensity(), dummy);
    }
    
    /**
     * Test that the RawFrame density arraycannot be set to null
     */
    @Test(expected=RuntimeException.class)
    public void testRawFrameThrowsExceptionIfInitialisedWithNull() throws RuntimeException
    {
        float[][] dummy = null;
        RawFrame data = new RawFrame(dummy);
    }
    
    /**
     * Test that the RawFrame constructor initialises the density correctly
     */
    @Test
    public void testRawFrameIntialisesDensityCorrectly() throws RuntimeException
    {
        int length = 10;
        float[][] dummy = new float[length][length];
        for( int i = 0; i != length; i++ )
            for( int j = 0; j != length; j++ )
                dummy[i][j] = 1;
        RawFrame data = new RawFrame(dummy);
        assertEquals( data.getFrame(), dummy);
    }
}