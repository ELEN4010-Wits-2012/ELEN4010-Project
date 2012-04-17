// GUI.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import javax.swing.JFrame;
import javax.swing.JPanel;
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
    /** Stores the panel which will be used to make menu options available*/
    private static MenuPanel menuPanel;
    /** Name of the main window*/
    private final static String APPLICATION_NAME = "Fluidation";
    /** Dimensions of main screen*/
    private final static Dimension APPLICATION_DIMENSIONS = new Dimension( 640, 480 );
    /** Default colour for display and capture panels*/
    private final static Color APPLICATION_PANEL_COLOUR = Color.black;
    /** Stores the amount of time (in milliseconds) that the application should wait before polling the state of a subframe*/
    private final static int MAIN_THREAD_WAIT_TIME = 100;
    /** Stores the program programs data processor to be used in every data capture session*/
    private static DataProcessor programDataProcessor = new DataProcessor( APPLICATION_DIMENSIONS );;
    // /** Stores the maximum number of threads that the GUI can handle at any given time*/
    // private final static int MAXIMUM_THREADS = 8;
    // /** Stores the java Executor which handles the threads to be called*/
    // private static final ExecutorService taskRunner = Executors.newFixedThreadPool( MAXIMUM_THREADS );

    // ===Private Methods===

    /** Sets up the {@link DataPanel DataPanel} to be added to the window*/
    private static void setupCapturePanel()
    {

        mouseCapturePanel = new DataPanel( APPLICATION_DIMENSIONS, APPLICATION_PANEL_COLOUR, programDataProcessor );

    }

    /** Sets up the {@link MenuPanel MenuPanel} to be addedd to the window*/
    private static void setupMenuPanel()
    {

        menuPanel = new MenuPanel( APPLICATION_DIMENSIONS, APPLICATION_PANEL_COLOUR );

    }

    /** Sets up the window to make the GUI visible*/
    private static void setupWindow()
    {

        displayFrame = new JFrame( APPLICATION_NAME );
        displayFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setupMenuPanel();
        setupCapturePanel();

    }

    /** Activates the data panel*/
    private static void activateDataPanel()
    {

        displayFrame.setVisible( false );
        displayFrame.getContentPane().remove( menuPanel );
        displayFrame.getContentPane().add( mouseCapturePanel );
        displayFrame.pack();
        displayFrame.setVisible( true );        

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
        displayFrame.pack();
        displayFrame.setVisible( true );

    }


    /** Listening loop for Menu*/
    private static void menuLoop()
    {

        MenuActions selectedOption = MenuActions.LISTENING;
        boolean menuListening = true;

        while ( menuListening )
        {
            selectedOption = menuPanel.getProgramState();
            if ( selectedOption != MenuActions.LISTENING )
            {
                menuListening = false;
                continue;
            }
        }

        if ( selectedOption == MenuActions.CAPTURE )
        {
            activateDataPanel();
            dataLoop();
            return;
        }

        if ( selectedOption == MenuActions.SAVE )
        {
            programDataProcessor.writeSimulationInput();
            return;
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
        {}

        activateMenuPanel( mouseCapturePanel );
        menuLoop();

        return;

    }

    // ===Public Methods===

    /**
     * Starts the GUI by creating a window and placing a {@link DataPanel DataPanel} for test purpo-
     * ses
     */
    public static void main( String[] Arguments )
    {

        setupWindow();
        activateMenuPanel( null );
        while( true )
        {
            menuLoop();
        }

    }

}