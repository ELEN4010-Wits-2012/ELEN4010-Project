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
 * @author Justin Wernick and Ronald Clark
 */
public class GUI extends JFrame
{
    BufferedImage image;
    
    //XXX Note that the fluids are created with a width, height, and overlapping side. The widths have to be the same.
    Fluid topSim = new Fluid( 0, 100, 20, 300, true, false );
    Fluid centerSim = new Fluid( 0, 100, 20, 300, false, false );
    Fluid bottomSim = new Fluid( 0, 100, 20, 300, false, true );
    
    int size = 300;
    
    //XXX The object outputs must be initialised once and then used for all outputs, otherwise the existing objects are overwritten.
    ObjectOutput outputTop;
    ObjectOutput outputCenter;
    ObjectOutput outputBottom;
    
    public GUI()
    {
        //sets up JFrame for output
        super( "Fluid" );
        image = new BufferedImage( size , size  + 30, BufferedImage.TYPE_INT_RGB );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( size , size + 30 );
        setResizable( false );
        setVisible( true );
        
        
        //this bit creates some density to start with, so that it does not look as boring.
        for ( int j = 120; j < 150; j++ )
            for ( int i = 10; i < 53; i++ )
            {
                bottomSim.densityOld[j][i] = 20f;
            }

        //XXX This is the creation of the output stream. Important to note, the filename is set here.
        try
        {
            OutputStream fileTop = new FileOutputStream( "topSim.out" );
            OutputStream bufferTop = new BufferedOutputStream( fileTop );
            outputTop = new ObjectOutputStream( bufferTop );
            OutputStream fileCenter = new FileOutputStream( "centerSim.out" );
            OutputStream bufferCenter = new BufferedOutputStream( fileCenter );
            outputCenter = new ObjectOutputStream( bufferCenter );
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
        centerSim.step();
        bottomSim.step();
        
        topSim.setOverlap( centerSim.getEdge( Side.TOP ) , Side.BOTTOM);
        centerSim.setOverlap( topSim.getEdge( Side.BOTTOM ) , Side.TOP);
        centerSim.setOverlap( bottomSim.getEdge( Side.TOP ) , Side.BOTTOM);
        bottomSim.setOverlap( centerSim.getEdge( Side.BOTTOM ), Side.TOP );
        
        //XXX Important to note that a new RenderingFluid is created for each frame. The object output thinks it's the same object being updated otherwise.
        //Initialisation is still done with width and height because it would be better for the garbage collector if we can figure out how to not keep making new ones.
        RenderingFluid outputObjectTop = new RenderingFluid(300,100);
        RenderingFluid outputObjectCenter = new RenderingFluid(300,100);
        RenderingFluid outputObjectBottom = new RenderingFluid(300,100);
        
        //XXX Data to be saved is stuck in the RenderingFluid objects here
        outputObjectTop.setDensity( topSim.getRenderDensity() );
        outputObjectTop.setT( topSim.t );
        outputObjectCenter.setDensity( centerSim.getRenderDensity() );
        outputObjectCenter.setT( centerSim.t );
        outputObjectBottom.setDensity( bottomSim.getRenderDensity() );
        outputObjectBottom.setT( bottomSim.t );
        
        //XXX Data is written to file in this try/catch statement. Don't forget to flush.
        try
        {
            outputTop.writeObject(outputObjectTop);
            outputCenter.writeObject(outputObjectCenter);
            outputBottom.writeObject(outputObjectBottom);
            outputTop.flush();
            outputBottom.flush();
        }
        catch(IOException ex)
        {
            System.err.println("Error in file output");
        }

        
        //Prototype rendering, ignore for final system.
        for ( int x = 0; x < topSim.jmax ; x++ )
        {
            for ( int y = topSim.renderTop ; y < topSim.renderBottom; y++ )
            {
                int r = (int) (255.0 * (topSim.densityNew[x][y]));
                if ( r > 255 )
                    r = 255;

                Color densColor = new Color( r, 0, 0 );
                image.setRGB( x, y - topSim.renderTop + 30, densColor.getRGB() );
            }
        }
        for ( int x = 0; x < centerSim.jmax ; x++ )
        {
            for ( int y = centerSim.renderTop ; y < centerSim.renderBottom; y++ )
            {
                int r = (int) (255.0 * (centerSim.densityNew[x][y]));
                if ( r > 255 )
                    r = 255;

                Color densColor = new Color( 0, 0, r );
                image.setRGB( x, y - centerSim.renderTop +100 + 30, densColor.getRGB() );
            }
        }
        for ( int x = 0; x < bottomSim.jmax; x++ )
        {
            for ( int y = bottomSim.renderTop ; y < bottomSim.renderBottom; y++ )
            {
                int r = (int) (255.0 * (bottomSim.densityNew[x][y]));
                if ( r > 255 )
                    r = 255;

                Color densColor = new Color( 0, r, 0 );
                image.setRGB( x, y - bottomSim.renderTop + 200 + 30, densColor.getRGB() );
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
            outputCenter.close();
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
