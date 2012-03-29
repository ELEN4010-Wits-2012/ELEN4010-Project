package za.ac.wits.elen4010.fluidsim.sim;

import javax.swing.JFrame;
import javax.swing.ImageIcon;

import za.ac.wits.elen4010.fluidsim.sim.Fluid.Side;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Toolkit;

public class GUI extends JFrame
{
    BufferedImage image;
    Fluid topSim = new Fluid( 256, 128, Side.BOTTOM );
    Fluid bottomSim = new Fluid( 256, 128, Side.TOP );
    int size = 256;

    public GUI()
    {

        super( "Fluid" );

        image = new BufferedImage( size * 4, size * 4 + 30, BufferedImage.TYPE_INT_RGB );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setSize( size * 4, size * 4 + 30 );

        setResizable( false );

        for ( int j = 120; j < 150; j++ )
            for ( int i = 10; i < 53; i++ )
            {
                bottomSim.rhoOld[j][i] = 20f;
            }

        setVisible( true );
    }

    public void paint( Graphics g )
    {
        topSim.step();
        bottomSim.step();

        topSim.setOverlap( bottomSim.getEdgeRho(), bottomSim.getEdgeU(), bottomSim.getEdgeV() );
        bottomSim.setOverlap( topSim.getEdgeRho(), topSim.getEdgeU(), topSim.getEdgeV() );

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

    public static void main( String[] args )
    {
        GUI gui = new GUI();

        while ( true )
            gui.repaint();
    }
}
