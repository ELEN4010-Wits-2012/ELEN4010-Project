// MenuPanel.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * The Menu panel is designed to allow the user to change the state of the program between capture a-
 * nd render through the {@link DataPanel DataPanel} and {@link GraphicalPanel GraphicalPanel} respe-
 * ctively. It also allows the user to save or load previous data capture and rendering sessions
 * @author Edward Steere
 * @see GraphicalPanel
 * @see DataPanel
 */
public class MenuPanel extends JPanel implements ActionListener
{

    // ===Private Data Members===

    /** Stores the dimensions of the panel*/
    private static Dimension windowSize;
    /** Stores the "Capture" button*/
    private static JButton captureButton;
    /** Stores Capture button text*/
    private static final String CAPTURE_TEXT = "Capture";
    /** Stores the "Visualise" button*/
    private static JButton visualiseButton;
    /** Stores the Visualisation button text*/
    private static final String VISUALISE_TEXT = "Visualise";
    /** Stores the "Save" button*/
    private static JButton saveButton;
    /** Stores the Save button text*/
    private static final String SAVE_TEXT = "Save";
    /** Stores the "Load" button*/
    private static JButton loadButton;
    /** Stores the Load button text*/
    private static final String LOAD_TEXT = "Load";
    /** Stores the "Exit" button*/
    private static JButton exitButton;
    /** Stores the Exit button text*/
    private static final String EXIT_TEXT = "Exit";
    /** Stores the state of the program. If a selection hasn't been made the state is: LISTENING*/
    private static MenuActions programState = MenuActions.LISTENING;

    // ===Private Methods===

    // ===Public Methods===

    /**
     * Creates a new MenuPanel by accepting its dimensions and background colour
     * @param panelDimension
     *             The dimensions of the menu panel
     * @param backgroundColor
     *             The background color of the menu panel
     */
    public MenuPanel( Dimension panelDimension, Color backgroundColor )
    {

        setBackground( backgroundColor );
        setOpaque( true );
        setBorder( BorderFactory.createLineBorder( Color.black ) );

        captureButton = new JButton( CAPTURE_TEXT );
        captureButton.setMnemonic( KeyEvent.VK_C );
        captureButton.setActionCommand( CAPTURE_TEXT );
        captureButton.addActionListener( this );

        visualiseButton = new JButton( VISUALISE_TEXT );
        visualiseButton.setMnemonic( KeyEvent.VK_V );
        visualiseButton.setActionCommand( VISUALISE_TEXT );
        visualiseButton.addActionListener( this );
        
        saveButton = new JButton( SAVE_TEXT );
        saveButton.setMnemonic( KeyEvent.VK_S );
        saveButton.setActionCommand( SAVE_TEXT );
        saveButton.addActionListener( this );

        loadButton = new JButton( LOAD_TEXT );
        loadButton.setMnemonic( KeyEvent.VK_L );
        loadButton.setActionCommand( LOAD_TEXT );
        loadButton.addActionListener( this );

        exitButton = new JButton( EXIT_TEXT );
        exitButton.setMnemonic( KeyEvent.VK_X );
        exitButton.setActionCommand( EXIT_TEXT );
        exitButton.addActionListener( this );

        add( captureButton );
        add( visualiseButton );
        add( saveButton );
        add( loadButton );
        add( exitButton );

    }

    /**
     * Handles a button being pressed by checking which action string was passed to the function and
     * then responding accordingly
     * @param ActionEvent
     *             The string corresponding to the action which was performed
     */
    public void actionPerformed( ActionEvent handledEvent )
    {

        String eventText = handledEvent.getActionCommand();

        if ( CAPTURE_TEXT.equals( eventText ) )
        {
            programState = MenuActions.CAPTURE;
            return;
        }

        if ( VISUALISE_TEXT.equals( eventText ) )
        {
            programState = MenuActions.VISUALISE;
            return;
        }

        if ( SAVE_TEXT.equals( eventText ) )
        {
            programState = MenuActions.SAVE;
            return;
        }

        if ( LOAD_TEXT.equals( eventText ) )
        {
            programState = MenuActions.LOAD;
            return;
        }

        if ( EXIT_TEXT.equals( eventText ) )
        {
            programState = MenuActions.EXIT;
            return;
        }


    }

    /**
     * Simple function to return the state of the program
     * @return the state of the program (as defined in the enumerator MenuActions*/
    public MenuActions getProgramState()
    {

        if ( programState != MenuActions.LISTENING )
        {
            MenuActions returnValue = programState;
            programState = MenuActions.LISTENING;
            return returnValue;
        }

        return programState;

    }

}