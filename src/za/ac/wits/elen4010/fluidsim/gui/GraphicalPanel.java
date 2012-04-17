// GraphicalPanel.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

/**
 * The GraphicalPanel handles the rendering of the simulation by reading the input file via the
 * {@link FileReader FileReader} which streams {@link RawFrame RawFrames} to the main program which
 * contain the float matrices necessary to display the density output graphically on the screen
 * @author Edawrd Steere
 * @see FileReader
 * @see DataPanel
 * @see MenuPanel
 */
public class GraphicalPanel extends JPanel
{

    // ===Private Data Members===

    /** Stores the dimensions of the panel*/
    private static Dimension windowSize;
    /** Maximum depth that a colour can have in the buffered image*/
    private static final short MAX_DEPTH = 255;
    /** Image which gets rendered*/
    private BufferedImage renderedImage;

    // ===Private Methods===

    // ===Public Methods===

    /**
     * Creates a new GraphicalPanel by accepting is demensions and background colour
     * @param panelDimension
     *             The dimensions of the menu panel
     * @param backgroundColor
     *             The background color of the menu panel
     */
    public GraphicalPanel( Dimension panelDimension, Color backgroundColor )
    {

        windowSize = panelDimension;
        setBackground( backgroundColor );
        setOpaque( true );
        setBorder( BorderFactory.createLineBorder( Color.black ) );
        renderedImage = new BufferedImage( (int)panelDimension.getWidth(), (int)panelDimension.getHeight(), BufferedImage.TYPE_INT_RGB );

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
     * @return The prefered size of the window
     */
    public Dimension getPreferredSize()
    {

        return windowSize;
        
    }

    /**
     * Function which allows the GraphicalPanel to be 'told' what the image data for the next frame
     * should be
     * @param nextFrame
     *             The data for the next frame. In the form of a RawFrame type
     */
    public void setImage( RawFrame nextFrame )
    {

        float[][] frameData = nextFrame.getFrame();

        // ASSERT THAT ITS THE CORRECT SIZE HERE!!

        for ( int l = 0; l != windowSize.getWidth(); ++l )
        {
            for ( int L = 0; L != windowSize.getHeight(); ++L )
            {
                int intensity = (int)( frameData[l][L] ) % MAX_DEPTH;
                Color intensityColor = new Color( intensity, intensity, intensity );
                renderedImage.setRGB( l, L, intensityColor.getRGB() );
            }
        }

        

    }

}