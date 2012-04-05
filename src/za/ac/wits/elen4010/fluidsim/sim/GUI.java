package za.ac.wits.elen4010.fluidsim.sim;

import javax.swing.JFrame;

import za.ac.wits.elen4010.fluidsim.sim.Fluid.Side;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Prototyping GUI for running Fluid simulations in one thread.
 * 
 * @author Justin Worthe and Ronald Clark
 */
public class GUI extends JFrame
{
    BufferedImage image;
    
    //XXX Note that the fluids are created with a width, height, and overlapping side. The widths have to be the same.
    Fluid topSim = new Fluid( 256, 128, Side.BOTTOM );
    Fluid bottomSim = new Fluid( 256, 128, Side.TOP );
    
    int size = 256;
    
    //XXX The object outputs must be initialised once and then used for all outputs, otherwise the existing objects are overwritten.
    ObjectOutput outputTop;
    ObjectOutput outputBottom;
    
    public GUI()
    {
        //sets up JFrame for output
        super( "Fluid" );
        image = new BufferedImage( size * 4, size * 4 + 30, BufferedImage.TYPE_INT_RGB );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( size * 4, size * 4 + 30 );
        setResizable( false );
        setVisible( true );
        
        
        //this bit creates some density to start with, so that it does not look as boring.
        for ( int j = 120; j < 150; j++ )
            for ( int i = 10; i < 53; i++ )
            {
                bottomSim.rhoOld[j][i] = 20f;
            }

        //XXX This is the creation of the output stream. Important to note, the filename is set here.
        try
        {
            OutputStream fileTop = new FileOutputStream( "topSim.out" );
            OutputStream bufferTop = new BufferedOutputStream( fileTop );
            outputTop = new ObjectOutputStream( bufferTop );
            OutputStream fileBottom = new FileOutputStream( "bottomSim.out" );
            OutputStream bufferBottom = new BufferedOutputStream( fileBottom );
            outputBottom = new ObjectOutputStream( bufferBottom );
        }
        catch(IOException ex)
        {
            System.err.println("Error in file creation");
        }
        
        
    }

    public void paint( Graphics g )
    {
        //XXX These 4 lines are the simulation and synchronisation between the two matrices. In the integrated system, getEdgeRho, U and V will be called on one node, MPI will send the results to the adjacent node, and then the adjacent node can call setOverlap.
        topSim.step();
        bottomSim.step();
        topSim.setOverlap( bottomSim.getEdgeRho(), bottomSim.getEdgeU(), bottomSim.getEdgeV() );
        bottomSim.setOverlap( topSim.getEdgeRho(), topSim.getEdgeU(), topSim.getEdgeV() );
        
        //XXX Important to note that a new RenderingFluid is created for each frame. The object output thinks it's the same object being updated otherwise.
        //Initialisation is still done with width and height because it would be better for the garbage collector if we can figure out how to not keep making new ones.
        RenderingFluid outputObjectTop = new RenderingFluid(256,128);
        RenderingFluid outputObjectBottom = new RenderingFluid(256,128);
        
        //XXX Data to be saved is stuck in the RenderingFluid objects here
        outputObjectTop.setDensity( topSim.getRenderRho() );
        outputObjectTop.setT( topSim.t );
        outputObjectBottom.setDensity( bottomSim.getRenderRho() );
        outputObjectBottom.setT( bottomSim.t );
        
        //XXX Data is written to file in this try/catch statement. Don't forget to flush.
        try
        {
            outputTop.writeObject(outputObjectTop);
            outputBottom.writeObject(outputObjectBottom);
            outputTop.flush();
            outputBottom.flush();
        }
        catch(IOException ex)
        {
            System.err.println("Error in file output");
        }

        
        //Prototype rendering, ignore for final system.
        for ( int x = 0; x < topSim.jmax * 4; x++ )
        {
            for ( int y = topSim.renderTop * 4; y < topSim.renderBottom * 4; y++ )
            {
                int r = (int) (255.0 * (topSim.rhoNew[x / 4][y / 4]));
                if ( r > 255 )
                    r = 255;

                Color densColor = new Color( r, r, r );
                image.setRGB( x, y - topSim.renderTop * 4 + 30, densColor.getRGB() );
            }
        }
        for ( int x = 0; x < bottomSim.jmax * 4; x++ )
        {
            for ( int y = bottomSim.renderTop * 4; y < bottomSim.renderBottom * 4; y++ )
            {
                int r = (int) (255.0 * (bottomSim.rhoNew[x / 4][y / 4]));
                if ( r > 255 )
                    r = 255;

                Color densColor = new Color( r, r, r );
                image.setRGB( x, y + (topSim.renderBottom - topSim.renderTop - bottomSim.renderTop) * 4 + 30, densColor.getRGB() );
            }
        }
        
//        for ( int x = 1; x < 4 * bottomSim.jmax; x += 8 )
//        {
//            for ( int y = 1; y < 4 * bottomSim.imax; y += 8 )
//            {
//                Graphics gd = image.createGraphics();
//                int dx = (int) (80 * (bottomSim.uNew[(int) x / 4][(int) y / 4]));
//                int dy = (int) (80 * (bottomSim.vNew[(int) x / 4][(int) y / 4]));
//                gd.drawLine( x, y, x + dx, y + dy );
//                gd.dispose();
//            }
//        }

        g.drawImage( image, 0, 0, this );
    }
    
    protected void finalize()
    {
        //XXX When the simulation ends, call outputTop.close() and outputBottom.close(). It's better than nothing in a finalize method, but it should rather be called manually when simulating is done.
        try
        {
            outputTop.close();
            outputBottom.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public static void main( String[] args )
    {
        GUI gui = new GUI();

        while ( true )
            gui.repaint();
    }
}
