package za.ac.wits.elen4010.fluidsim.sim;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

/**
 * Quick and nasty class to test if output files are being written correctly.
 * 
 * Warning: Quick and nasty is NASTY. This class should disappear after the integration build.
 * 
 * @author Justin Worthe
 */
public class GUIFromFile extends JFrame
{
    BufferedImage image;
    int size = 256;
    
    RenderingFluid topSim;
    RenderingFluid bottomSim;

    ObjectInput topStream;
    ObjectInput bottomStream;
    
    public boolean moreToShow = true;
    
    public GUIFromFile()
    {
        super( "Fluid" );

        image = new BufferedImage( size * 4, size * 4 + 30, BufferedImage.TYPE_INT_RGB );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setSize( size * 4, size * 4 + 30 );

        setResizable( false );
        setIgnoreRepaint( true );
        
        try
        {
            InputStream fileTop = new FileInputStream( "topSim.out" );
            InputStream bufferTop = new BufferedInputStream( fileTop );
            topStream = new ObjectInputStream ( bufferTop );
            
            InputStream fileBottom = new FileInputStream( "bottomSim.out" );
            InputStream bufferBottom = new BufferedInputStream( fileBottom );
            bottomStream = new ObjectInputStream ( bufferBottom );
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        
        setVisible( true );
    }

    public void paint( Graphics g )
    {
        try
        {
            topSim = (RenderingFluid)topStream.readObject();
            bottomSim = (RenderingFluid)bottomStream.readObject();
        }
        catch(EOFException ex)
        {
            moreToShow = false;
            try
            {
                topStream.close();
                bottomStream.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        catch(ClassNotFoundException ex)
        {
            System.err.println("Class not found exception");
            ex.printStackTrace();
        }
        catch(IOException ex)
        {
            System.err.println("IO exception");
            ex.printStackTrace();
        }

        drawFluid(topSim.getDensity(), 0);
        drawFluid(bottomSim.getDensity(), 128);

        g.drawImage( image, 0, 0, this );
    }

    private void drawFluid(float[][] density, int verticalOffset)
    {
        for ( int x = 0; x < density.length * 4; x++ )
        {
            for ( int y = 0; y < density[x/4].length * 4; y++ )
            {
                int r = (int) (255.0 * (density[x / 4][y / 4]));
                if ( r > 255 )
                    r = 255;

                Color densColor = new Color( r, r, r );
                image.setRGB( x, y + verticalOffset * 4 + 30, densColor.getRGB() );
            }
        }
    }
    
    public static void main( String[] args )
    {
        GUIFromFile gui = new GUIFromFile();

        while ( gui.moreToShow )
            gui.repaint();
    }
}
