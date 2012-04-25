// DataPanel.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * The data panel is designed specifically to capture data in the form of
 * {@link DataPoint DataPoints} from the Java standard MouseListener and MouseMotionListener interfa-
 * ces
 * @author Edward Steere
 * @see InteractivePanel
 */
public class DataPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener
{

    // ===Private Data Members===
    /** Stores the dimensions of the panel*/
    private static Dimension windowSize;
    /** Stores the state of the mouse clicker*/
    private static boolean clickerState;
    /** Stores whether the mouse is inside or outside of the area in question*/
    private static boolean containedState;
    /** Stores the current density of the system. NOTE default value is 100*/
    private static int currentDensity = Integer.MAX_VALUE / 2;
    /** Stores the time when the capturing was started*/
    private static long pausedStart;
    /** Stores the accumulated time spent paused*/
    private static long timePaused;
    /** Stores the time that the simulation began*/
    private static long timeStart;
    /** Stores whether or not the system is currently paused*/
    private static boolean capturePaused;
    /** Stores whether the window should still be open or not*/
    private static boolean captureData;
    /** Stores the 'Paused' label*/
    private static final JLabel pausedLabel = new JLabel( "Paused" );
    /** Stores the maximum length of the progress bar*/
    private static final int MAXIMUM_BAR_LENGTH = Integer.MAX_VALUE;
    /** Stores the minimum length of the progress bar*/
    private static final int MINIMUM_BAR_LENGTH = 0;
    /** Stores the change in density each time a plus or minus is captured*/
    private static final int DENSITY_DELTA = 10000000;
    /** Stores the progress bar which indicates the density of the simulation*/
    private static final JProgressBar densityLevel = new JProgressBar( MINIMUM_BAR_LENGTH, MAXIMUM_BAR_LENGTH );
    /** Stores the data processor which handles samples and can return the required 3D arrays*/
    private static DataProcessor sampleProcessor;

    // ===Private Methods===

    /**
     * Checks the initialisation data for the object against the maximum values defined by the supe-
     * rframe
     * @return True if the values currently set to the objects private data members are valid
     * @param superFrame
     *            The frame which this panel is bound to
     */
    private boolean checkValues( Dimension superFrameDimension )
    {

        return true;

    }

    // ===Public Methods===
    /**
     * Creates a new DataPanel by accepting its dimensions and placement within the super frame alon-
     * g with the color that the panel should be.
     * @param panelDimension
     *            The dimensions of the DataPanel (based on the dimensions of the super frame)
     * @param backgroundColor
     *            The color which the display area should be
     * @param programDataProcessor
     *            The DataProcessor which is used by the super thread to store the latest results fr-
     *            om capture
     */
    public DataPanel( Dimension panelDimension, Color backgroundColor, DataProcessor programDataProcessor )
    {

        windowSize = panelDimension;

        sampleProcessor = programDataProcessor;
        setBackground( backgroundColor );
        setOpaque( true );
        setBorder( BorderFactory.createLineBorder( Color.black ) );
        add( densityLevel );
        add( pausedLabel );
        addMouseListener( this );
        addMouseMotionListener( this );
        addKeyListener( this );
        setFocusable( true );
        pausedLabel.setFont( new Font( "Default", Font.ITALIC, 32 ) );
        pausedLabel.setForeground( Color.white );
        densityLevel.setValue( currentDensity );
        sampleProcessor.reInitialise();

        // Set the mouse to the state where it's outside the window and the clicker isn't down
        clickerState = false;
        containedState = false;
        capturePaused = true;
        captureData = true;
        timeStart = System.currentTimeMillis();
        pausedStart = timeStart;
        timePaused = 0;

    }

    /**
     * Resets the state of the DataPanel so that the main program can call a new data capture sessio-
     * n
     */
    public void reset()
    {

        // Create alert dialog here to make sure the user wants to do this
        boolean proceed = true;

        if ( proceed )
        {
            sampleProcessor.reInitialise();
            // Set the mouse to the state where it's outside the window and the clicker isn't down
            clickerState = false;
            containedState = false;
            capturePaused = true;
            captureData = true;
            timeStart = System.currentTimeMillis();
            pausedStart = timeStart;
            timePaused = 0;
        }

    }

    /**
     * Returns whether the simulation is still running or not
     * @return True if the simulation is still capturing data, else false
     */
    public boolean getExecutionState()
    {

        return captureData;

    }

    /** 
     * Returns the minimum size for the dimension of the window. In this case it's the same as the-
     * preferred size. i.e. It is preferrable that the window be no size other than the specified
     * one
     * @return The minimum size of the window
     */
    public Dimension getMinimumSize()
    {

        return windowSize;
    
    }
    
    /**
     * Returns the preferred size of the window. In this case it's the same as the minimum size. i.e.
     * It is preferrable that the window be no size other than the specified one
     * @return The preferred size of the window
     */
    public Dimension getPreferredSize()
    {

        return windowSize;
        
    }

    // These are stubs atm, implement them when you have the key mapping class up and running!
    /**
     * Captures the mousePressed event and sets the  clicker state to true so that the program knows
     * that the clicker is in a down state
     * @param pressed
     *            Event variable corresponding to the mouse being pressed
     */
    public void mousePressed( MouseEvent pressed )
    {

        clickerState = true;

    }
    
    /**
     * Captures the mouseReleased event and sets the clicker state to false so that the program knows
     * that the clicker is in an up state
     * @param released
     *             Event variable corresponding to the mouse being released
     */
    public void mouseReleased( MouseEvent released )
    {

        clickerState = false;

    }
    
    /**
     * Captures the mouseEntered event and sets the contained state to true so that capturing contin-
     * ues
     * @param entered
     *             Event variable corresponding to the mouse entering {@link #eventPanel eventPanel}
     */
    public void mouseEntered( MouseEvent entered )
    {

        containedState = true;

    }
    
    /**
     * Captures the mouseExited event and sets the contained state to false so that capturing doesn't
     * continue
     * @param exited
     *             Event variable corresponding to the mouse leaving {@link #eventPanel eventPanel}
     */
    public void mouseExited( MouseEvent exited )
    {

        containedState = false;

    }
    
    /**
     * Captures the mouseClicked event and returns (this event isn't used)
     * @param clicked
     *             Event variable corresponding to the mouse being clicked
     */
    public void mouseClicked( MouseEvent clicked )
    {

        // STUB!
        return;

    }
    
    /**
     * Captures the mouseMoved event and returns (this event isn't used)
     * @param moved
     *             Event variable corresponding to the mouse being moved
     */
    public void mouseMoved( MouseEvent moved )
    {

        // STUB!
        return;

    }
    
    /**
     * Captures the mouseDragged event and handles it appropriately. Note that the currentTimeMillis
     * method is system dependant. i.e. per system the precision might vary
     * @param dragged
     *             Event variable corresponding to the mouse being dragged
     */
    public void mouseDragged( MouseEvent dragged )
    {

        if ( !capturePaused && captureData )
        {
            if ( containedState )
            {
                //System.out.println( "Captured movement: " + dragged.getX() + ", " + dragged.getY() + ". Time = " + ( System.currentTimeMillis() - timePaused - timeStart ) );
                SamplePoint eventSamplePoint = new SamplePoint( dragged.getX(), dragged.getY(), System.currentTimeMillis() - timePaused - timeStart, (float)currentDensity );
                sampleProcessor.processNewSample( eventSamplePoint );
            }
        }

    }

    /**
     * Captures the key typed event and returns (this event isn't used)
     * @param typedKey
     *            Event variable corresponding to a key being typed
     */
    public void keyTyped( KeyEvent typedKey )
    {

        // STUB!
        return;
    
    }

    /**
     * Captures the key pressed event and handles the event appropriately. /<SPACE/> = pause capturi-
     * ng, /<ESC/> = stop capturing, /<+/> = increase density, /<-/> = decrese density
     * @param pressedKey
     *            Event variable corresponding to a key being pressed
     */
    public void keyPressed( KeyEvent pressedKey )
    {

        switch( pressedKey.getKeyCode() )
        {
            case KeyEvent.VK_SPACE:
                // Pause the capturing
                capturePaused = !capturePaused;
                if ( !capturePaused )
                {
                    timePaused += System.currentTimeMillis() - pausedStart;
                    pausedLabel.setVisible( false );
                }
                else
                {
                    pausedStart = System.currentTimeMillis();
                    pausedLabel.setVisible( true );
                }
                return;

            case KeyEvent.VK_ESCAPE:
                // Stop the simulation write to the file etc
                // NOTE: when the thread realises this and stops executing the finalize method should be called automatically to store the results in the given text file
                captureData = false;
                return;

            case KeyEvent.VK_EQUALS:
            case KeyEvent.VK_PLUS:
                // Increment the density
                if ( currentDensity != MAXIMUM_BAR_LENGTH )
                {
                    currentDensity += DENSITY_DELTA;
                    densityLevel.setValue( currentDensity );
                }
                return;

            case KeyEvent.VK_MINUS:
                // Decrement the density
                if ( currentDensity != MINIMUM_BAR_LENGTH )
                {
                    currentDensity -= DENSITY_DELTA;
                    densityLevel.setValue( currentDensity );
                }
                return;

            default:
                // STUB!!
                return;
            
        }

    }

    /**
     * Captures the key released event and returns (this event isn't used)
     * @param releasedKey
     *            Event variable corresponding to a key being released
     */
    public void keyReleased( KeyEvent releasedKey )
    {

        // STUB!
        return;

    }

}