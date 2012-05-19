// GUITests.java

package za.ac.wits.elen4010.fluidsim.tests;

// Standard dependancies
import java.io.Serializable;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;
import java.util.logging.StreamHandler;
import java.util.logging.SimpleFormatter;
import java.awt.Dimension;

// Non standard dependancies
import org.junit.*;
import static org.junit.Assert.*;
import za.ac.wits.elen4010.fluidsim.gui.*;

/**
 * A class which performs unit tests on the interfaces of the various classes which comprise the GUI
 * @author Edward Steere
 */
public class GUITests
{

    /**
     * A test serialisable class for the pruposes of the {@link GUITests GUITests}
     * {@link FoleWriter FileWriter} and {@link FileReader FileReader} classes
     * @author Edward Steere
     */
    private static class TestSerialisable implements Serializable
    {

        // ===Private Data Members===

        /** GUID for the serialised data*/
        private static final long serialVersionUID = 2849683754896958374L;

        /** A primitive array type for comparison purposes*/
        int[] someArray;

        // ===Public Methods===

        /**
         * Creates a new {@link TestSerialisable TestSerialisable} by accepting an array of integers and
         * setting the internal value
         * @param inputArray
         *             An array of integers to be set as the data for the class
         */
        public TestSerialisable( int[] inputArray )
        {

            someArray = inputArray;

        }

        /**
         * Simple get method for the array of integers {@link someArray someArray}
         * @return
         *             The array of integers {@link someArray someArray}
         */
        public int[] getData()
        {

            return someArray;

        }

        /**
         * Compares one Testserialisable to another by comparing the values in the array
         * {@link someArray someArray} (not the pointer the the start of the array)
         * @param otherTestSerialisable
         *             Another {@link TestSerialisable TestSerialisable} which should be compared to thi-
         *             s one
         * @return True if the two are equal otherwise false
         */
        public boolean compare( TestSerialisable otherTestSerialisable )
        {

            if ( Arrays.equals( someArray, otherTestSerialisable.getData() ) )
            {
                return true;
            }

            return false;

        }

    }

    /**
     * Another test serializable type with a different serialUID. This should cause the program to d-
     * etect that the data isn't the correct type when reading an object from a file made by these
     * @author Edward Steere
     */
    private class DifferentSerialisable implements Serializable
    {

        // ===Private Data Members===

        /** GUID for the serialised data*/
        private static final long serialVersionUID = 6849680754896058374L;

        /** A primitive array type for comparison purposes*/
        int[] someArray;

        // ===Public Methods===

        /**
         * Creates a new {@link DifferentSerialisable DifferentSerialisable} by accepting an array of
         * integers and setting the internal value
         * @param inputArray
         *             An array of integers to be set as the data for the class
         */
        public DifferentSerialisable( int[] inputArray )
        {

            someArray = inputArray;

        }

        /**
         * Simple get method for the array of integers {@link someArray someArray}
         * @return
         *             The array of integers {@link someArray someArray}
         */
        public int[] getData()
        {

            return someArray;

        }

        /**
         * Compares one Testserialisable to another by comparing the values in the array
         * {@link someArray someArray} (not the pointer the the start of the array)
         * @param otherTestSerialisable
         *             Another {@link TestSerialisable TestSerialisable} which should be compared to thi-
         *             s one
         * @return True if the two are equal otherwise false
         */
        public boolean compare( TestSerialisable otherTestSerialisable )
        {

            if ( Arrays.equals( someArray, otherTestSerialisable.getData() ) )
            {
                return true;
            }

            return false;

        }

    }

    // ===Private Data Members===

    /** Test data for serialisation*/
    private static final int[] TEST_DATA = {2, 678, 231, 0, 0, 12, 123};
    /** {@link TestSerialisable TestSerialisable} for testing file writer and reader*/
    private static final TestSerialisable TEST_WRITE_READ_OBJECT = new TestSerialisable( TEST_DATA );
    /** A file name to write the test data to*/
    private static final String FILE_PATH_1 = "Test1.in";
    /** A file name to write the test data to*/
    private static final String FILE_PATH_2 = "Test2.in";
    /** A file name to write the test data to*/
    private static final String FILE_PATH_3 = "Test3.in";
    /** A file name to write the test data to*/
    private static final String FILE_PATH_4 = "Test4.in";
    /** The number of objects to write to file in the multiple object tests*/
    private static final int NUMBER_OF_OBJECTS = 10;
    /** The dimensions of the virtual test screen*/
    private static final Dimension TEST_SCREEN_DIMENSIONS = new Dimension( 1080, 1440 );
    /** Precision to compare floats up to (6 decimal places)*/
    private static final float PRECISION = 0.0000001f;
    /** A test sample point to be used as test input for DataProcessor tests and testing the SamplePoint*/
    private static final SamplePoint TEST_SAMPLE_POINT = new SamplePoint( 5, 5, 120L, 12.3f );
    /** Another test sample point for comparison in the SamplePoint tests*/
    private static final SamplePoint TEST_SAMPLE_POINT_LATER = new SamplePoint( 10, 10, 130L, 12.3f );
    /** The velocity which *should* be made from the two above SamplePoints*/
    private static final Velocity TEST_DERIVED_VELOCITY = new Velocity( 5, 5, 10L, 130L, 5, 5, 12.3f );
    /** A test RawFrame to be used for the SimulationInput tests*/
    //private static final RawFrame TEST_RAW_FRAME = new RawFrame( {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}} );
    /** The screen dimensions of the SimulationInput virtual screen*/
    private static final Dimension INPUT_SCREEN_DIMENSION = new Dimension( 3, 3 );
    /** The number of objects to be put into a simulation input*/
    private static final int NUMBER_OF_FRAMES = 0;

    // ===Public Methods===

    // ===Reader/Writer Tests===

    /**
     * Tests that an object written with the file writer and then read immediately is still the same
     * object
     */
    @Test
    public void objectsWrittenToFileAreIdenticalAfterReading()
    {

        FileWriter<TestSerialisable> testWriter = new FileWriter( FILE_PATH_1 );

        testWriter.writeSimulationData( TEST_WRITE_READ_OBJECT );

        FileReader<TestSerialisable> testReader = new FileReader( FILE_PATH_1 );
        

        assertTrue( "The object isn't the same after reading it from the file!", testReader.readNextFrame().compare( TEST_WRITE_READ_OBJECT ) );

    }

    /**
     * Tests that multiple instances of the same object can be written to a file and then read corre-
     * ctly
     */
    @Test
    public void multipleIdenticalObjectsCanBeWrittenToFileAndReadCorrectly()
    {

        FileWriter<TestSerialisable> testWriter = new FileWriter( FILE_PATH_1 );

        for ( int l = 0; l != NUMBER_OF_OBJECTS; ++l )
        {
            testWriter.writeSimulationData( TEST_WRITE_READ_OBJECT );
        }

        FileReader<TestSerialisable> testReader = new FileReader( FILE_PATH_1 );
        boolean overAllResult = true;
        for ( int l = 0; l != NUMBER_OF_OBJECTS; ++l )
        {
            overAllResult &= testReader.readNextFrame().compare( TEST_WRITE_READ_OBJECT );
        }

        assertTrue( "The objects aren't the same after reading it from the file!", overAllResult );

    }

    /**
     * Tests that {@link FileReader FileReaders} throw exceptions when reading the wrong type
     */
    @Test
    public void readingTheWrongTypeThrowsException()
    {

        FileWriter<TestSerialisable> testWriter = new FileWriter( FILE_PATH_1 );

        testWriter.writeSimulationData( TEST_WRITE_READ_OBJECT );

        FileReader<DifferentSerialisable> testReader = new FileReader( FILE_PATH_1 );

        boolean exceptionCaught = false;

        try
        {
            DifferentSerialisable otherSerialisable = testReader.readNextFrame();
        }
        catch ( ClassCastException couldntCast )
        {
            exceptionCaught = true;
        }

        assertTrue( "The file reader still read the data even though it had a different UID", exceptionCaught );

    }

    // ===SamplePoint/Velocity Tests===

    /**
     * Tests that the velocity generated from two sample points is correct according to the data in
     * the sample points
     */
    @Test
    public void correctVelecityGeneratedFromSamplePoints()
    {

        Velocity testVelocity = TEST_SAMPLE_POINT.getVelocity( TEST_SAMPLE_POINT_LATER );

        assertEquals( "The sample time generated isn't correct", testVelocity.getSampleTime(), TEST_DERIVED_VELOCITY.getSampleTime() );
        assertEquals( "The Density generated isn't correct", testVelocity.getDensity(), TEST_DERIVED_VELOCITY.getDensity(), PRECISION );
        assertEquals( "The X velocity generated isn't correct", testVelocity.getXComponent(), TEST_DERIVED_VELOCITY.getXComponent(), PRECISION );
        assertEquals( "The Y velocity generated isn't correct", testVelocity.getYComponent(), TEST_DERIVED_VELOCITY.getYComponent(), PRECISION );
        assertEquals( "The X coordinate generated isn't correct", testVelocity.getXCoordinate(), TEST_DERIVED_VELOCITY.getXCoordinate() );
        assertEquals( "The Y coordinate generated isn't correct", testVelocity.getYCoordinate(), TEST_DERIVED_VELOCITY.getYCoordinate() );

    }

    // ===SimulationInput Tests===

    /**
     * A test to determine whether the SimulationInput counts frames correctly
     */
    @Test
    public void displaysCorrecNumberOfFrames()
    {

        List<Velocity> testList = new Vector<Velocity>();

        for ( int l = 0; l != NUMBER_OF_FRAMES; ++l )
        {
            testList.add( TEST_DERIVED_VELOCITY );
        }

        SimulationInput testInput = new SimulationInput( testList, INPUT_SCREEN_DIMENSION );

        int frames = testInput.getFrameCount();
        assertEquals( "The list isn't the correct size", frames, NUMBER_OF_FRAMES );

        Velocity testVelocity = null;
        for ( int l = 0; l != frames; l ++ )
        {
            testVelocity = testInput.nextInputVelocity();
            assertNotNull( "The list is shorter than expected", testVelocity );
        }

        assertNull( "The list is longer than expected", testInput.nextInputVelocity() );

    }

    // ===DataProcessor Tests===

    /**
     * Tests that an empty dataprocessor doesn't write any data to the output file
     */
    @Test
    public void emptyDataProcessorDoesntWriteToFile()
    {

        FileWriter<SimulationInput> testWriter = new FileWriter( FILE_PATH_2 );

        DataProcessor testProcessor = new DataProcessor( TEST_SCREEN_DIMENSIONS );

        boolean wroteData = false;

        wroteData = testProcessor.writeSimulationInput( testWriter );

        assertFalse( "Data was written to the file", wroteData );

        testProcessor.processNewSample( TEST_SAMPLE_POINT );

        testProcessor.reInitialise();

        testProcessor.writeSimulationInput( testWriter );

        assertFalse( "Data was writtent to the file", wroteData );

    }

}