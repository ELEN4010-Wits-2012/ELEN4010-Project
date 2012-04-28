// MenuPanel.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;

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
    /** Stores the dimensions for all buttons on the window*/
    private static final Dimension BUTTON_DIMENSION = new Dimension( 200, 40 );
    /** Stores the left off set for buttons*/
    private static final int LEFT_OFFSET = 50;
    /** Stores the "Capture" button*/
    private static JButton captureButton;
    /** Stores the vertical off set for the capture button*/
    private static final int CAPTURE_VERTICAL_OFFSET = 70;
    /** Stores Capture button text*/
    private static final String CAPTURE_TEXT = "Capture";
    /** Stores the "Execute" button*/
    private static JButton executeButton;
    /** Stores the vertical off set for the visualise button*/
    private static final int EXECUTE_VERTICAL_OFFSET = 140;
    /** Stores the Visualisation button text*/
    private static final String EXECUTE_TEXT = "Execute";
    /** Stores the "Visualise" button*/
    private static JButton visualiseButton;
    /** Stores the vertical off set for the visualise button*/
    private static final int VISUALISE_VERTICAL_OFFSET = 210;
    /** Stores the Visualisation button text*/
    private static final String VISUALISE_TEXT = "Visualise";
    /** Stores the "Save" button*/
    private static JButton saveButton;
    /** Stores the vertical off set for the save button*/
    private static final int SAVE_VERTICAL_OFFSET = 280;
    /** Stores the Save button text*/
    private static final String SAVE_TEXT = "Save";
    /** Stores the "Load" button*/
    private static JButton loadButton;
    /** Stores the vertical off set for the load button*/
    private static final int LOAD_VERTICAL_OFFSET = 350;
    /** Stores the Load button text*/
    private static final String LOAD_TEXT = "Load";
    /** Stores the "Exit" button*/
    private static JButton exitButton;
    /** Stores the vertical off set for the exit button*/
    private static final int EXIT_VERTICAL_OFFSET = 420;
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
        windowSize = panelDimension;
        Insets menuInsets = getInsets();
        // Use abolute positioning to place buttons with more ease
        setLayout( null );

        // Change the button fore and back grounds
        UIManager.put( "Button.background", Color.black );
        UIManager.put( "Button.foreground", Color.white );

        // Set up each of the buttons
        captureButton = new JButton( CAPTURE_TEXT );
        add( captureButton );
        captureButton.setBounds( LEFT_OFFSET + menuInsets.left, CAPTURE_VERTICAL_OFFSET + menuInsets.top, BUTTON_DIMENSION.width, BUTTON_DIMENSION.height );
        captureButton.setMnemonic( KeyEvent.VK_C );
        captureButton.setActionCommand( CAPTURE_TEXT );
        captureButton.addActionListener( this );

        executeButton = new JButton( EXECUTE_TEXT );
        add( executeButton );
        executeButton.setBounds( LEFT_OFFSET + menuInsets.left, EXECUTE_VERTICAL_OFFSET + menuInsets.top, BUTTON_DIMENSION.width, BUTTON_DIMENSION.height );
        executeButton.setMnemonic( KeyEvent.VK_C );
        executeButton.setActionCommand( EXECUTE_TEXT );
        executeButton.addActionListener( this );

        visualiseButton = new JButton( VISUALISE_TEXT );
        add( visualiseButton );
        visualiseButton.setBounds( LEFT_OFFSET + menuInsets.left, VISUALISE_VERTICAL_OFFSET + menuInsets.top, BUTTON_DIMENSION.width, BUTTON_DIMENSION.height );
        visualiseButton.setMnemonic( KeyEvent.VK_V );
        visualiseButton.setActionCommand( VISUALISE_TEXT );
        visualiseButton.addActionListener( this );
        
        saveButton = new JButton( SAVE_TEXT );
        add( saveButton );
        saveButton.setBounds( LEFT_OFFSET + menuInsets.left, SAVE_VERTICAL_OFFSET + menuInsets.top, BUTTON_DIMENSION.width, BUTTON_DIMENSION.height );
        saveButton.setMnemonic( KeyEvent.VK_S );
        saveButton.setActionCommand( SAVE_TEXT );
        saveButton.addActionListener( this );

        loadButton = new JButton( LOAD_TEXT );
        add( loadButton );
        loadButton.setBounds( LEFT_OFFSET + menuInsets.left, LOAD_VERTICAL_OFFSET + menuInsets.top, BUTTON_DIMENSION.width, BUTTON_DIMENSION.height );
        loadButton.setMnemonic( KeyEvent.VK_L );
        loadButton.setActionCommand( LOAD_TEXT );
        loadButton.addActionListener( this );

        exitButton = new JButton( EXIT_TEXT );
        add( exitButton );
        exitButton.setBounds( LEFT_OFFSET + menuInsets.left, EXIT_VERTICAL_OFFSET + menuInsets.top, BUTTON_DIMENSION.width, BUTTON_DIMENSION.height );
        exitButton.setMnemonic( KeyEvent.VK_X );
        exitButton.setActionCommand( EXIT_TEXT );
        exitButton.addActionListener( this );

    }

    /**
     * Returns the minimum size for the dimension of the window. In this case it's the same as the
     * preffered size. i.e. It is preferrable that the window is not resisable
     * @return The mimimum size of the window
     */
    public Dimension getMinimumSize()
    {

        return windowSize;

    }

    /**
     * Returns the preferred size of the window. In this case it's the same as the mimumum size. i.e
     * it is preferrable that the window is not resisable
     * @return The preferred size of the window
     */
    public Dimension getPreferredSize()
    {

        return windowSize;

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

        if ( EXECUTE_TEXT.equals( eventText ) )
        {
            programState = MenuActions.EXECUTE;
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