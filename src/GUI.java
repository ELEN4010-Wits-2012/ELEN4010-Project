import javax.swing.JFrame;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Toolkit;

public class GUI extends JFrame
{
	BufferedImage image;

	public GUI()
	{
		 
		 super("Fluid");

		 image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		 setSize(256,256);

		 setResizable(false);

		 setVisible(true);
	}

	public void paint(Graphics g)
	{	
		Simulation.step();
		
		for( int x = 0; x<Simulation.jmax; x++)
		{
			for( int y = 0; y<Simulation.imax; y++)
			{
				int r = (int)(Simulation.rhoOld[x][y]*255.0f);
				if (r>255) r = 255;
				else if(r<0) r = 0;
				
				Color densColor = new Color(r, r, r);
				image.setRGB(x,y,densColor.getRGB());
			}
		}
		for( int x = 0; x<Simulation.jmax; x+=16)
		{
			for( int y = 0; y<Simulation.imax; y+=16)
			{
				Graphics gd = image.createGraphics();
				int dx = (int)(16*(Simulation.uOld[x][y])/Math.sqrt(Simulation.uOld[x][y]*Simulation.uOld[x][y]+Simulation.vOld[x][y]*Simulation.vOld[x][y]));
				int dy = (int)(16*(Simulation.vOld[x][y])/Math.sqrt(Simulation.uOld[x][y]*Simulation.uOld[x][y]+Simulation.vOld[x][y]*Simulation.vOld[x][y]));
				gd.drawLine(x,y,x+dy, y+dx);
				gd.dispose();
			}	
		}
		g.drawImage(image,0,0,this);
	}

	public static void main(String[]args)
	{
		GUI gui = new GUI();
		
			for( int i=118; i<138; i++)
				for( int j=220; j<240; j++)
				{
					Simulation.rhoOld[i][j] =Simulation.rhoNew[i][j] = 10f;
				}
			for( int i=118; i<138; i++)
				for( int j=200; j<220; j++)
				{
					Simulation.vOld[i][j] = Simulation.vNew[i][j] = 0.01f;
				}
				
		while( true )
			gui.repaint();
	}
}
