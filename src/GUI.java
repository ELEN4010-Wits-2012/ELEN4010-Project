import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Toolkit;

public class GUI extends JFrame
{
	BufferedImage image;
    int size = 64;
    
	FluidSimulation segment1, segment2;
	FluidData fluidData;
	
	public GUI()
	{
		super("Fluid");
        
        fluidData = new FluidData(64, 64, 64, 0.2f, 0);
        segment1 = new FluidSimulation( new FluidData(64, 64, 32, 0.2f, 0) );
        segment2 = new FluidSimulation( new FluidData(64, 64, 33, 0.2f, 31) );
        
		image = new BufferedImage(size*4, size*4+30, BufferedImage.TYPE_INT_RGB);
         
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(size*4,size*4+30);
		setResizable(false);
		setVisible(true);
	}

	public void paint(Graphics g)
	{
        segment1.data.copy(fluidData);
		segment2.data.copy(fluidData);
        
		segment1.step();
        segment2.step();
        byte []data1 = segment1.data.getByteData();
        byte []data2 = segment2.data.getByteData();
        byte data[][] = new byte[2][data1.length];
        data[0] = data1;
        data[1] = data2;
        fluidData.join(data);

        segment1.data.copy(fluidData);
		segment2.data.copy(fluidData);
        
		for( int x = 0; x<size*4; x++)
		{
			for( int y = 0; y<size*4; y++)
			{
                int dx = (int)(255.0*(fluidData.getDensity(x/4,y/4)));
			
				int r = dx;// (int)(Simulation.rhoOld[x][y]*255.0f);
				if (r>255) r = 255;
				
				Color densColor = new Color(r, r, r);
				image.setRGB(x,y+30,densColor.getRGB());
			}
		}
		for( int x = 1; x<4*size; x+=8)
		{
			for( int y = 1; y<4*size; y+=8)
			{
				Graphics gd = image.createGraphics();
				int dx = (int)(80*(fluidData.getXVelocity((int)x/4,(int)y/4)));
				int dy = (int)(80*(fluidData.getYVelocity((int)x/4,(int)y/4)));
				//gd.drawLine(x,y,x+dx, y+dy);
				gd.dispose();
			}	
		}
		g.drawImage(image,0,0,this);
	}

	public static void main(String[]args)
	{
		GUI gui = new GUI();
		
			for( int i=22; i<42; i++)
				for( int j=22; j<42; j++)
				{
					gui.fluidData.setDensity(j,i,1f);
                    
				}
				
		while( true )
			gui.repaint();
	}
}
