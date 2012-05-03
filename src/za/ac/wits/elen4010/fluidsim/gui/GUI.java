// GUI.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
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

    /** Stores the file writer which is used throughout the program for the saving of files*/
    private static FileWriter<SimulationInput> outputWriter;
    /** Stores the file chooser used through the program for saving and loading*/
    private static final JFileChooser fileChooser = new JFileChooser();
    /** Stores the window variable which will have items dynamically added and removed from it*/
    private static JFrame displayFrame;
    /** Stores the panel which will be used to capture data*/
    private static DataPanel mouseCapturePanel;
    /** Stores the panel which will be used to make menu options available*/
    private static MenuPanel menuPanel;
    /** Stores the panel which will be used to display the results of the computation*/
    private static GraphicalPanel visualisationPanel;
    /** Name of the main window*/
    private final static String APPLICATION_NAME = "Fluidation";
    /** Dimensions of the capture and rendering screens*/
    private final static Dimension APPLICATION_DIMENSIONS = new Dimension( 640, 480 );
    /** Dimensions of the menu panel*/
    private final static Dimension MENU_DIMENSIONS = new Dimension( 300, 570 );
    /** Default colour for display and capture panels*/
    private final static Color APPLICATION_PANEL_COLOUR = Color.black;
    /** Stores the amount of time (in milliseconds) that the application should wait before polling the state of a subframe*/
    private final static int MAIN_THREAD_WAIT_TIME = 100;
    /** Stores the program programs data processor to be used in every data capture session*/
    private static DataProcessor programDataProcessor = new DataProcessor( APPLICATION_DIMENSIONS );
    /** Stores the name of the file that should be used to read data to the program*/
    private static final String FILE_NAME = "out.dat";
    /** Stores the file reader that should be used to render each frame*/
    private static FileReader<RawFrame> simulationOutputReader;
    /** Stores the file reader that should be user to read capture data to the program*/
    private static FileReader<SimulationInput> simulationInputReader;
    /** Insets for the display frame*/
    private static Insets frameInsets;
    /** The error message to be displayed if a file couldn't be written due to there being no data to write*/
    private static final String NO_DATA_TO_WRITE = "There is no data in the DataProcessor. Please begin a capture session or load a new data set";
    /** The error message to be displayed if a file doesn't end in the correct extension to be read*/
    private static final String UNSUPORTED_FILE_TYPE = "File extension is not supported (nothing loaded)";
    /** The error message to be displayed if no simulation data has been loaded but the user tries to visualise*/
    private static final String NO_DATA_TO_VISUALISE = "No visualisation data loaded. Not visualising";
    /** The error message to be displayed if no simulation data was actually contained in the file which was loaded*/
    private static final String NO_DATA_IN_FILE = "The file didn't contain any valid simulation input.\nPlease select another file.";
    /** The error message to be displayed if no {@link RawFrame RawFrames} are in the file*/
    private static final String NO_RAW_FRAMES_IN_FILE = "The file didn't contain any valid simulation output data.\nPlease load another file.";
    /** Prompt the user that the program is being executed on the server*/
    private static final String EXECUTING_SIMULATION = "The simulation is being executed on the HPC.\nBe advised that this will take time.";
    /** Prompt the user that the program is being executed on the server*/
    private static final String COULDNT_EXECUTE_SIMULATION = "Failed to execute simulation on the server please try again. If the problem persists, check that this program has access to your machines command terminal.";
    /** The bash command used to copy the files to the server and run the simulation*/
    private static final String BASH_COPY_AND_RUN = "emacs";
    /**
     * Supported simulation input data file extensions NOTE: these should be gicen in upper case to
     * work with the determineFileType() method
     */
    private static final String[] SIMULATION_INPUT_NAMES = new String[]{ ".IN" };
    /**
     * Supported simulation output data file extensions NOTE: these should be given in upper case to
     * work with the determineFileType() method
     */
    private static final String[] SIMULATION_OUTPUT_NAMES = new String[]{ ".OUT" };
    /** The file type which was determined using the determineFileType() function*/
    private static enum loadedFileType
    {
        INPUT, OUTPUT,  UNSUPORTED;
    }
    // /** Stores the maximum number of threads that the GUI can handle at any given time*/
    // private final static int MAXIMUM_THREADS = 8;
    // /** Stores the java Executor which handles the threads to be called*/
    // private static final ExecutorService taskRunner = Executors.newFixedThreadPool( MAXIMUM_THREADS );

    // ===Private Methods===

    /**
     * Determines the file type by the full path passed to the function
     * @param filePath
     *             The absolute path to the file
     * @return The type of file NOTE: if the file isn't supported then the UNSUPORTED option is returned
     */
    private static loadedFileType determineFileType( String filePath )
    {

        filePath = filePath.toUpperCase();

        // Determine whether the input file is a simulation input file
        for ( String inputName : SIMULATION_INPUT_NAMES )
        {
            if ( filePath.endsWith( inputName ) )
            {
                return loadedFileType.INPUT;
            }
        }

        // Determine whether the input file is a simulation output file
        for ( String outputName : SIMULATION_OUTPUT_NAMES )
        {
            if ( filePath.endsWith( outputName ) )
            {
                return loadedFileType.OUTPUT;
            }
        }

        // File type unrecognised
        return loadedFileType.UNSUPORTED;

    }

    /** Sets up the {@link DataPanel DataPanel} to be added to the window*/
    private static void setupCapturePanel()
    {

        mouseCapturePanel = new DataPanel( APPLICATION_DIMENSIONS, APPLICATION_PANEL_COLOUR, programDataProcessor );

    }

    /** Sets up the {@link MenuPanel MenuPanel} to be addedd to the window*/
    private static void setupMenuPanel()
    {

        menuPanel = new MenuPanel( MENU_DIMENSIONS, APPLICATION_PANEL_COLOUR );

    }

    /** Sets up the {@link GraphicalPanel GraphicalPanel} to be addedd to the window*/
    private static void setupVisualisationPanel()
    {

        visualisationPanel = new GraphicalPanel( APPLICATION_DIMENSIONS, APPLICATION_PANEL_COLOUR );

    }

    /** Sets up the window to make the GUI visible*/
    private static void setupWindow()
    {

        displayFrame = new JFrame( APPLICATION_NAME );
        displayFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        displayFrame.setResizable( false );
        frameInsets = displayFrame.getInsets();
        setupMenuPanel();
        setupCapturePanel();
        setupVisualisationPanel();

    }

    /** Activates the visualisation panel*/
    private static void activateVisualisationPanel()
    {

        displayFrame.setVisible( false );
        displayFrame.getContentPane().remove( menuPanel );
        displayFrame.getContentPane().add( visualisationPanel );
        displayFrame.pack();
        try
        {
            Thread.sleep( MAIN_THREAD_WAIT_TIME );
        }
        catch ( InterruptedException ExecutionPrematurelyInterrupted )
        {
            System.err.println( "Execution of the program was prematurely interrupted" );
            ExecutionPrematurelyInterrupted.printStackTrace( System.err );
        }
        displayFrame.setVisible( true );

    }

    /**
     * Activates the data panel
     * @return Whether or not the user decided to create start a new capture session
     */
    private static boolean activateDataPanel()
    {

        Object[] options = { "Capture", "Cancel" };
        int selectedOption = JOptionPane.showOptionDialog( null, "Are you sure you want to start a new capture session?\nAny unsaved data will be lost.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1] );
        if ( selectedOption == 1 )
        {
            return false;
        }
        displayFrame.setVisible( false );
        mouseCapturePanel.reset();
        displayFrame.getContentPane().remove( menuPanel );
        displayFrame.getContentPane().add( mouseCapturePanel );
        displayFrame.pack();
        try
        {
            Thread.sleep( MAIN_THREAD_WAIT_TIME );
        }
        catch ( InterruptedException ExecutionPrematurelyInterrupted )
        {
            System.err.println( "Execution of the program was prematurely interrupted" );
            ExecutionPrematurelyInterrupted.printStackTrace( System.err );
        }
        displayFrame.setVisible( true );

        return true;

    }

    /**
     * Activates the menu panel
     * @param activePanel
     *             The panel which was active prior to the menu being activated again
     */
    private static void activateMenuPanel( JPanel activePanel )
    {

        displayFrame.setVisible( false );
        if ( activePanel != null )
        {
            displayFrame.getContentPane().remove( activePanel );
        }
        displayFrame.getContentPane().add( menuPanel );
        displayFrame.setSize( MENU_DIMENSIONS.width + frameInsets.left + frameInsets.right, MENU_DIMENSIONS.height + frameInsets.top + frameInsets.bottom );
        try
        {
            Thread.sleep( MAIN_THREAD_WAIT_TIME );
        }
        catch ( InterruptedException ExecutionPrematurelyInterrupted )
        {
            System.err.println( "Execution of the program was prematurely interrupted" );
            ExecutionPrematurelyInterrupted.printStackTrace( System.err );
        }
        displayFrame.setVisible( true );

    }

    /** Listening loop for Menu*/
    private static void menuLoop()
    {

        MenuActions selectedOption = MenuActions.LISTENING;
        boolean menuListening = true;

        while ( menuListening )
        {
            try
            {
                Thread.sleep( MAIN_THREAD_WAIT_TIME );
            }
            catch ( InterruptedException ExecutionPrematurelyInterrupted )
            {
                System.err.println( "Execution of the program was prematurely interrupted" );
                ExecutionPrematurelyInterrupted.printStackTrace( System.err );
            }
            selectedOption = menuPanel.getProgramState();
            if ( selectedOption != MenuActions.LISTENING )
            {
                menuListening = false;
                continue;
            }
        }

        if ( selectedOption == MenuActions.CAPTURE )
        {
            if ( !( activateDataPanel() ) )
            {
                activateMenuPanel( null );
                return;
            }
            dataLoop();
            return;
        }

        if ( selectedOption == MenuActions.EXECUTE )
        {
            if ( outputWriter == null )
            {
                JOptionPane.showMessageDialog( displayFrame, NO_DATA_TO_WRITE );
                return;
            }
            if ( determineFileType( outputWriter.getFilePath() ) != loadedFileType.INPUT )
            {
                JOptionPane.showMessageDialog( displayFrame, NO_DATA_TO_WRITE ); 
                return;
            }
            try
            {
                JOptionPane.showMessageDialog( displayFrame, EXECUTING_SIMULATION );
                Runtime.getRuntime().exec( BASH_COPY_AND_RUN );
            }
            catch ( IOException couldntExecute )
            {
                JOptionPane.showMessageDialog( displayFrame, EXECUTING_SIMULATION );
            }
        }

        if ( selectedOption == MenuActions.VISUALISE )
        {
            activateVisualisationPanel();
            visualisationLoop();
            return;
        }

        if ( selectedOption == MenuActions.SAVE )
        {
            int option = fileChooser.showSaveDialog( displayFrame );
            if ( option == JFileChooser.APPROVE_OPTION )
            {
                File saveFile = fileChooser.getSelectedFile();
                if ( saveFile.exists() )
                {
                    Object[] options = { "Save", "Cancel" };
                    int save = JOptionPane.showOptionDialog( null, "Are you sure you want to over write that file.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1] );
                    if ( save == 1 )
                    {
                        return;
                    }
                }
                if ( outputWriter == null )
                {
                    outputWriter = new FileWriter<SimulationInput>( saveFile.getAbsolutePath() );
                }
                else
                {
                    outputWriter.resetFile( saveFile.getAbsolutePath() );
                }
                if ( !( programDataProcessor.writeSimulationInput( outputWriter ) ) )
                {
                    JOptionPane.showMessageDialog( displayFrame, NO_DATA_TO_WRITE );
                    return;
                }
            }
        }

        if ( selectedOption == MenuActions.LOAD )
        {
            int option = fileChooser.showOpenDialog( displayFrame );
            if ( option == JFileChooser.APPROVE_OPTION )
            {
                File loadFile = fileChooser.getSelectedFile();
                String filePath = loadFile.getAbsolutePath();
                loadedFileType fileType = determineFileType( loadFile.getName() );
                switch ( fileType )
                {
                    case INPUT:
                        if ( simulationInputReader == null )
                        {
                            simulationInputReader = new FileReader<SimulationInput>( filePath );
                        }
                        else
                        {
                            simulationInputReader.resetFile( filePath );
                        }
                        SimulationInput storedInputData = simulationInputReader.readNextFrame();
                        if ( storedInputData == null )
                        {
                            JOptionPane.showMessageDialog( displayFrame, NO_DATA_IN_FILE );
                            return;
                        }
                        programDataProcessor.reInitialise( storedInputData );
                        return;
                    case OUTPUT:
                        if ( simulationOutputReader == null )
                        {
                            simulationOutputReader = new FileReader<RawFrame>( filePath );
                        }
                        else
                        {
                            simulationOutputReader.resetFile( filePath );
                        }
                        return;
                    case UNSUPORTED:
                        JOptionPane.showMessageDialog( displayFrame, UNSUPORTED_FILE_TYPE );
                        return;
                    default:
                        System.err.println( "Something horribly wrong happened (a default statement on an enum switch was reached). Terminating program." );
                        System.exit( -1 );
                        return;
                }
            }
        }

        if ( selectedOption == MenuActions.EXIT )
        {
            System.exit( 0 );
            return;
        }

    }

    /** Listening loop for Data*/
    private static void dataLoop()
    {

        while ( mouseCapturePanel.getExecutionState() )
        {
            try
            {
                Thread.sleep( MAIN_THREAD_WAIT_TIME );
            }
            catch ( InterruptedException ExecutionPrematurelyInterrupted )
            {
                System.err.println( "Execution of the program was prematurely interrupted" );
                ExecutionPrematurelyInterrupted.printStackTrace( System.err );
            }
        }

        activateMenuPanel( mouseCapturePanel );

        return;

    }

    /** Rendering loop for visualisation*/
    private static void visualisationLoop()
    {
        long benchmarkStartTime = System.nanoTime();

        if ( simulationOutputReader == null )
        {
            JOptionPane.showMessageDialog( displayFrame, NO_DATA_TO_VISUALISE );
            activateMenuPanel( visualisationPanel );
            
            long benchmarkElapsedTime = System.nanoTime() - benchmarkStartTime;
            TimeCapture.getInstance().addTimedEvent( "gui", "visualisationLoop", benchmarkElapsedTime );
            return;
        }

        long startTime = System.currentTimeMillis();
        RawFrame nextFrame = simulationOutputReader.readNextFrame();

        if ( nextFrame == null )
        {
            JOptionPane.showMessageDialog( displayFrame, NO_RAW_FRAMES_IN_FILE );
        }

        while( nextFrame != null )
        {
            while ( System.currentTimeMillis() - startTime < 33 )
            {
                try
                {
                    Thread.sleep( MAIN_THREAD_WAIT_TIME );
                }
                catch ( InterruptedException ExecutionPrematurelyInterrupted )
                {
                    System.err.println( "Execution of the program was prematurely interrupted" );
                    ExecutionPrematurelyInterrupted.printStackTrace( System.err );
                }      
            }
            startTime = System.currentTimeMillis();
            visualisationPanel.setImage( nextFrame );
            nextFrame = simulationOutputReader.readNextFrame();
        }

        activateMenuPanel( visualisationPanel );

        long benchmarkElapsedTime = System.nanoTime() - benchmarkStartTime;
        TimeCapture.getInstance().addTimedEvent( "gui", "visualisationLoop", benchmarkElapsedTime );
        return;

    }

    // ===Public Methods===

    /**
     * Starts the GUI by creating a window and placing a {@link DataPanel DataPanel} for test purpo-
     * ses
     */
    public static void main( String[] Arguments )
    {

        outputWriter = null;
        simulationInputReader = null;
        simulationOutputReader = null;
        setupWindow();
        activateMenuPanel( null );
        while( true )
        {
            menuLoop();
            try
            {
                Thread.sleep( MAIN_THREAD_WAIT_TIME );
            }
            catch ( InterruptedException ExecutionPrematurelyInterrupted )
            {
                System.err.println( "Execution of the program was prematurely interrupted" );
                ExecutionPrematurelyInterrupted.printStackTrace( System.err );
            }
        }

    }

}