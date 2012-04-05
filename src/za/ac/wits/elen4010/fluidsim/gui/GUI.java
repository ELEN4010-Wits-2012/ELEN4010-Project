// GUI.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Arrays;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.RejectedExecutionException;

/**
 * The GUI is the main class of the program and is designed to be able to spawn all of the different
 * components of the application. Namely: a {@link Menu Menu} to select playback material and enter
 * capture mode, a {@link DataPanel DataPanel} to capture material for processing and a
 * {@link DisplayPanel} to playback the results of computation
 * @author Edward Steere
 */
class GUI
{

    // ===Private Data Members===

    /** Stores the window variable which will have items dynamically added and removed from it*/
    private static JFrame displayFrame;
    /** Stores the panel which will be used to capture data*/
    private static DataPanel mouseCapturePanel;
    /** Name of the main window*/
    private final static String APPLICATION_NAME = "Fluidation";
    /** Dimensions of main screen*/
    private final static Dimension APPLICATION_DIMENSIONS = new Dimension( 100, 100 );
    /** Default colour for display and capture panels*/
    private final static Color APPLICATION_PANEL_COLOUR = Color.black;
    /** Stores the amount of time (in milliseconds) that the application should wait before polling the state of a subframe*/
    private final static int MAIN_THREAD_WAIT_TIME = 100;
    /** Stores the program programs data processor to be used in every data capture session*/
    private static DataProcessor programDataProcessor;
    // /** Stores the maximum number of threads that the GUI can handle at any given time*/
    // private final static int MAXIMUM_THREADS = 8;
    // /** Stores the java Executor which handles the threads to be called*/
    // private static final ExecutorService taskRunner = Executors.newFixedThreadPool( MAXIMUM_THREADS );

    // ===Private Methods===

    /** Sets up the datapanel to be added to the window*/
    private static void setupCapturePanel()
    {

        mouseCapturePanel = new DataPanel( APPLICATION_DIMENSIONS, APPLICATION_PANEL_COLOUR, programDataProcessor );

    }

    /** Sets up the window to make the GUI visible*/
    private static void setupWindow()
    {

        displayFrame = new JFrame( APPLICATION_NAME );
        displayFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setupCapturePanel();
        displayFrame.getContentPane().add( mouseCapturePanel );
        displayFrame.getContentPane().setLayout( new FlowLayout() );
        displayFrame.pack();
        displayFrame.setVisible( true );

    }

    // ===Public Methods===

    /**
     * Starts the GUI by creating a window and placing a {@link DataPanel DataPanel} for test purpo-
     * ses
     */
    public static void main( String[] Arguments )
    {

        programDataProcessor = new DataProcessor( APPLICATION_DIMENSIONS );
        setupWindow();

        while ( mouseCapturePanel.getExecutionState() )
        {}

        programDataProcessor.printArrays();
        float[][][] temp = programDataProcessor.getXVelocityInput();
        System.out.println( "======\n" + Arrays.deepToString( temp ) );

        System.exit( 0 );

    }

}