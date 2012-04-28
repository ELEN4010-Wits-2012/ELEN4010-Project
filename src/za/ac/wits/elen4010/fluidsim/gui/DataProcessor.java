// DataProcessor.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.awt.Dimension;

/**
 * Buffers data during an execution of the data capture phase of the program. Once the capturing is
 * complete an output file is generated containing two 3 dimensional matrices each with 30hz sampl-
 * ing snapshot of the screen for the entire duration of capture
 * @author Edward Steere
 * @see SamplePoint
 * @see Velocity
 */
public class DataProcessor
{

    // ===Private Data Members===

    /** Stores the buffer of sample points*/
    private List<SamplePoint> samplePoints;
    /** Stores the buffer of velocities*/
    private List<Velocity> velocities;
    /** Stores the path to the output file that the values should be written to*/
    private String outputFileDirectory;
    /** Stores the dimensions of the area used to capture data*/
    private Dimension captureDimensions;
    /** Stores the time in milli seconds between samples (set for 30hz sampling i.e. t = 1/30)*/
    private static final float SAMPLE_TIME = 1f / 30;
    /** Stores the file writer used to generate input data for the server*/
    private static FileWriter<SimulationInput> outputWriter;

    // ===Private Methods===

    /**
     * Gets the next velocity which satisfies the sampleing rate
     * @param startSearch
     *             Iterator to the starting position to search from in the vector
     * @return The next velocity which satisfies the sampling criterion
     */
    private ListIterator<Velocity> getNextVelocity( ListIterator<Velocity> searchStartingPoint, long startTime )
    {

        long accumulatedTime = 0;
        Velocity nextVelocity = null;

        while( ( searchStartingPoint.hasNext() ) && ( accumulatedTime < SAMPLE_TIME ) )
        {
            nextVelocity = searchStartingPoint.next();
            accumulatedTime += nextVelocity.getSampleTime() - startTime;
        }
        nextVelocity = searchStartingPoint.previous();

        return searchStartingPoint;

    }

    /**
     * Gets the next sample point which satisfies the sampleing rate
     * @param startSearch
     *             Iterator to the starting position to search from in the vector
     * @return The next point which satisfies the sampling criterion
     */
    private ListIterator<SamplePoint> getNextSamplePoint( ListIterator<SamplePoint> searchStartingPoint, long startTime )
    {

        long accumulatedTime = 0;
        SamplePoint nextPoint = null;

        while( ( searchStartingPoint.hasNext() ) && ( accumulatedTime < SAMPLE_TIME ) )
        {
            nextPoint = searchStartingPoint.next();
            accumulatedTime += nextPoint.getTimeStamp() - startTime;
        }

        return searchStartingPoint;

    }

    /**
     * Returns a two dimensional array with a value placed at the specified position
     * @param value
     *             The value which will be set at the specified location
     * @param xCoord
     *             The x coordinate of the value (in pixels)
     * @param yCoord
     *             The y coordinate of the value (in pixels)
     * @return A two dimensional array of floats with the relevant value set
     */
    private float[][] generateInputFrame( float value, int xCoord, int yCoord )
    {

        float[][] returnArray = new float[(int)captureDimensions.getWidth()][(int)captureDimensions.getHeight()];

        for ( int l = 0; l != captureDimensions.getWidth(); l++ )
        {
            for ( int L = 0; L != captureDimensions.getHeight(); L++ )
            {
                if ( ( l == xCoord ) && ( L == yCoord ) )
                {
                    returnArray[l][L] = value;
                    continue;
                }
                returnArray[l][L] = 0;
            }
        }

        return returnArray;

    }

    /**
     * Unpacks a list of 2D arrays of floats and then repacks it into a 3D array of floats
     * @param floatList
     *             List of 2D floats to be unpacked
     * @return A 3 dimensional list of floats unpacked from the list of 2 dimensional arrays of floats
     */
    private float[][][] repackFloatMatrixList( List<float[][]> packedFloatMatrixList )
    {

        ListIterator<float[][]> matrixViewer = packedFloatMatrixList.listIterator();
        float[][][] unpackedFloatMatrix = new float[packedFloatMatrixList.size()][(int)captureDimensions.getWidth()][(int)captureDimensions.getHeight()];
        int matrixIndex = 0;

        while( matrixViewer.hasNext() )
        {
            unpackedFloatMatrix[matrixIndex] = matrixViewer.next();
            matrixIndex++;
        }

        return unpackedFloatMatrix;

    }

    // ===Public Methods===

    /**
     * Creates a new DataProcessor by initialising the 
     */
    public DataProcessor( Dimension screenDimensions )
    {

        captureDimensions = screenDimensions;
        samplePoints = new Vector<SamplePoint>();
        velocities = new Vector<Velocity>();

    }

    /**
     * Re initialises the data processor. Intended to be called whenever a new data capture session
     * is started
     */
    public void reInitialise()
    {

        samplePoints = new Vector<SamplePoint>();
        velocities = new Vector<Velocity>();

    }

    /**
     * Special method for re initialising the data processor data by accepting data directly from a
     * Simulation input for the velocities.
     * @param loadedSimulationData
     *             The data passed to the dataprocessor for re initialisation
     */
    public void reInitialise( SimulationInput loadedSimulationData )
    {

        reInitialise();
        Velocity nextVelocity = loadedSimulationData.nextInputVelocity();

        while ( nextVelocity != null )
        {
            velocities.add( nextVelocity );
            nextVelocity = loadedSimulationData.nextInputVelocity();
        }

    }

    /**
     * Processes a new sample by appending the new sample to the sample vector and then (if the samp
     * le vector is greater in length than 1 a velocity is calculated and added to the vector of vel
     * ocities.
     * @param newSample
     *             The next sample to be processed by the program
     */
    public void processNewSample( SamplePoint newSample )
    {

        if ( samplePoints.size() >= 1 )
        {
            // Sometimes a machine can sample faster than the system can record the sample time. In order to prevent division by zero these 'fast' samples must be discarded.
            if ( samplePoints.get( samplePoints.size() - 1 ).getTimeStamp() - newSample.getTimeStamp() == 0 )
            {
                return;
            }
            velocities.add( samplePoints.get( samplePoints.size() - 1 ).getVelocity( newSample ) );
        }
        samplePoints.add( newSample );

    }

    /**
     * Resamples the input data and writes it to a file using the given file writer
     * @param outputWriter
     *             The file writer that this method should use to generate the output data
     * @return True if the file was correctly written else False
     */
    public boolean writeSimulationInput( FileWriter<SimulationInput> outputWriter )
    {

        if ( velocities.size() == 0 )
        {
            return false;
        }

        ListIterator<Velocity> currentVelocity = velocities.listIterator();
        Velocity nextVelocity = currentVelocity.next();
        long startTime = nextVelocity.getSampleTime();
        List<Velocity> sampling = new Vector<Velocity>();
        while ( currentVelocity.hasNext() )
        {
            nextVelocity = currentVelocity.previous();
            currentVelocity = getNextVelocity( currentVelocity, startTime );
            nextVelocity = currentVelocity.next();
            sampling.add( nextVelocity );
            startTime = nextVelocity.getSampleTime();
        }

        outputWriter.writeSimulationData( new SimulationInput( sampling, captureDimensions ) );
        return true;

    }

    // TEMP FOR DEBUGGING PURPOSES!!
    public void printArrays()
    {

        System.out.println( samplePoints );
        System.out.println( velocities );

    }

}