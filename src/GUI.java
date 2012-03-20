import javax.swing.JFrame;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Toolkit;

public class GUI extends JFrame
{
	BufferedImage image;
	Fluid smoke = new Fluid();
	int size = 256;
	
	public GUI()
	{
		 
		 super("Fluid");

		 image = new BufferedImage(size*4, size*4+30, BufferedImage.TYPE_INT_RGB);

		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		 setSize(size*4,size*4+30);

		 setResizable(false);

		 setVisible(true);
	}

	public void paint(Graphics g)
	{	
		smoke.step();
		
		for( int x = 0; x<smoke.jmax*4; x++)
		{
			for( int y = 0; y<smoke.imax*4; y++)
			{
			int dx = (int)(255.0*(smoke.rhoNew[x/4][y/4]));
			
				int r = dx;// (int)(Simulation.rhoOld[x][y]*255.0f);
				if (r>255) r = 255;
				
				Color densColor = new Color(r, r, r);
				image.setRGB(x,y+30,densColor.getRGB());
			}
		}
		for( int x = 1; x<4*smoke.jmax; x+=8)
		{
			for( int y = 1; y<4*smoke.imax; y+=8)
			{
				Graphics gd = image.createGraphics();
				int dx = (int)(80*(smoke.uNew[(int)x/4][(int)y/4]));
				int dy = (int)(80*(smoke.vNew[(int)x/4][(int)y/4]));
				//gd.drawLine(x,y,x+dx, y+dy);
				gd.dispose();
			}	
		}
		g.drawImage(image,0,0,this);
	}

	public static void main(String[]args)
	{
		GUI gui = new GUI();
		
			for( int i=120; i<150; i++)
				for( int j=100; j<150; j++)
				{
					gui.smoke.rhoOld[j][i] = 100f;
				}
			for( int i=10; i<55; i++)
				for( int j=40; j<41; j++)
				{
					gui.smoke.uOld[j][i] = 0f;
				}
				
		while( true )
			gui.repaint();
	}
}
