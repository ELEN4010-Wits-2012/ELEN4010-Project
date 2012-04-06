import java.io.*;
import java.math.*;

/**
* Main class used to perform the fluid simulation. 
*
*/
public class FluidSimulation
{
    FluidData data;
	
     /**
     * Constructor for the FluidSimulation class. This method
     * initialises the simulation data.
     *
     * @param width Horizontal resolution of the simulation in pixels
     * @param height Vertical resolution of the simulation in pixels
     * @param dt Time step for the simulation (eg. 0.2f)
     **/
    FluidSimulation(FluidData data)
    {        
        this.data = data;
    }
    
    
    /**
    * Advances the fluid simulation by one frame.
    *
    */
	public void step()
	{
			// Density
			
			updateSource( true, data.densityOld, data.densityNew);	       
			swapDensity();
			           
			diffuse(0, data.densityOld, data.densityNew);         
			swapDensity();
			
			advect(0, data.xVelocityNew, data.yVelocityNew, data.densityOld, data.densityNew);
				
			for( int i = 0; i<=data.sizeY; i++)
				for( int j=0; j<=data.sizeX; j++ )
					data.densityOld[i][j] = 0;
			
			//Velocity
            
			updateSource(false, data.xVelocityOld, data.xVelocityNew);
			updateSource(false, data.yVelocityOld, data.yVelocityNew);				
           
			addVorticity();
			
			addBouyancy(data.yVelocityOld);
			updateSource(false, data.yVelocityOld, data.yVelocityNew);
			
			swapU();
			diffuse(0, data.xVelocityOld, data.xVelocityNew);
			
			swapV();
			diffuse(0, data.yVelocityOld, data.yVelocityNew);
			
			adjustVelocity(data.xVelocityNew, data.yVelocityNew);
			swapU(); swapV();
			
			advect(1,data.xVelocityOld, data.yVelocityOld, data.xVelocityOld, data.xVelocityNew);
			advect(2,data.xVelocityOld, data.yVelocityOld, data.yVelocityOld, data.yVelocityNew);
			
			adjustVelocity(data.xVelocityNew, data.yVelocityNew);
	
			for( int i = 0; i<=data.sizeY; i++)
				for( int j=0; j<=data.sizeX; j++ )
					data.xVelocityOld[i][j] = data.yVelocityOld[i][j] = 0;
	
	}   

    // **************************** Internal fluid simulation methods *****************************************
    
	private void setBoundary(int boundaryType, float [][] field)
	{
		for( int k = 1; k<=data.sizeX; k++)
		{
			field[0][k] = boundaryType == 1 ? -field[1][k] : field[1][k];
			field[data.sizeX][k] = boundaryType == 1 ? -field[data.sizeX-1][k] : field[data.sizeX-1][k];
			field[k][0] = boundaryType == 2 ? -field[k][1] : field[k][1];
			field[k][data.sizeY] = boundaryType == 2 ? -field[k][data.sizeY-1] : field[k][data.sizeY-1];
		}
		field[0][0] = 0.5f * (field[1][0] + field[0][1]);
		field[0][data.sizeY] = 0.5f * (field[1][data.sizeY] + field[0][data.sizeY-1]);
		field[data.sizeX][0] = 0.5f * (field[data.sizeX-1][0] + field[data.sizeX][1]);
		field[data.sizeX][data.sizeY] = 0.5f * (field[data.sizeX-1][data.sizeY] + field[data.sizeX][data.sizeY-1]);
	}
	
	private void calculateCurl()
	{
		for( int j = 1+data.offset; j<data.innerSizeX+data.offset; j++ )
		{
			for( int i=1; i<=data.innerSizeY; i++ )
			{
				float dudy = (data.xVelocityNew[j][i+1] - data.xVelocityNew[j][i-1])*0.5f;
				float dvdx = (data.yVelocityNew[j+1][i] - data.yVelocityNew[j-1][i])*0.5f;
				data.curl[j][i] = dudy - dvdx;
			}
		}
	}
	
	private void addVorticity()
	{
		calculateCurl();
		for( int j = 2+data.offset; j<data.innerSizeX+data.offset; j++ )
		{
			for( int i = 2; i<data.innerSizeY; i++ )
			{
				float dwdx = (data.curl[j+1][i] - data.curl[j-1][i])*0.5f;
				float dwdy = (data.curl[j][i+1] - data.curl[j][i-1])*0.5f;
				
				float magnitude = (float)Math.sqrt(dwdx*dwdx + dwdy*dwdy) + 0.00001f;
				
				dwdx /= magnitude;
				dwdy /= magnitude;
				
				float v = data.curl[j][i];
				
				data.xVelocityOld[j][i] = dwdy * -v;
				data.yVelocityOld[j][i] = dwdx * v;
			}
		}
		updateSource(false, data.xVelocityOld, data.xVelocityNew);
		updateSource(false, data.yVelocityOld, data.yVelocityNew);
	}
	
	private void addBouyancy(float [][] velocityField)
	{
		float ambientTemperature = 0.0f;
		float a = 0.000625f;
		float b = 0.025f;
		
		for( int j = 1; j<data.sizeX; j++)
		{
			for( int i=1; i<=data.sizeY; i++ )
			{
				ambientTemperature += data.densityNew[j][i];
			}
		}
		ambientTemperature /= data.sizeX*data.sizeY;
		
		for( int j = 1+data.offset; j<data.innerSizeX+data.offset; j++)
		{
			for( int i=1; i<=data.innerSizeY; i++ )
			{
				velocityField[j][i] = a*data.densityNew[j][i] - b*(data.densityNew[j][i] - ambientTemperature);
			}
		}
	}
	
	private void diffuse(int boundaryType, float [][]fieldOld, float [][]fieldNew)
	{	
		float a = data.dt * data.innerSizeX * data.innerSizeY * data.viscosity[0];
		solveLinearSystem(boundaryType, fieldNew, fieldOld, a, 1+4*a);
		
	}	
			
	private void advect(int boundaryType, float [][]xVelocityOld, float [][]yVelocityOld, float [][] fieldOld, float [][] fieldNew)
	{
		float dt0 = data.dt*data.innerSizeX;
		for(int j=1+data.offset; j<data.innerSizeX+data.offset; j++)
		{
			for( int i=1; i<=data.innerSizeY; i++)
			{
				float iPrevious = i - dt0*yVelocityOld[j][i];
				
				if(iPrevious > data.innerSizeY+0.5 ) iPrevious = data.innerSizeY+0.5f;
				if(iPrevious < 0.5) iPrevious = 0.5f;
				
				int i0 = (int)Math.floor(iPrevious); 
				int i1 = i0 +1;	
				float a1 = iPrevious - i0;
				float a0 = 1-a1;
				
				float jPrevious = j - dt0*xVelocityOld[j][i];
				
				if(jPrevious > data.sizeX+0.5 ) jPrevious = data.sizeX + 0.5f;
				if(jPrevious < 0.5) jPrevious = 0.5f;
				
				int j0 = (int)Math.floor(jPrevious); 
				int j1 = j0 +1;
				
				float b1 = jPrevious - j0;
				float b0 = 1-b1;
				
				fieldNew[j][i] = b0*(a0*fieldOld[j0][i0] + a1*fieldOld[j0][i1]) +b1*(a0*fieldOld[j1][i0]+a1*fieldOld[j1][i1]);
			}
		}
		setBoundary(boundaryType, fieldNew);
	}
	
	private void updateSource(boolean clampToZero, float [][]fieldOld, float [][]fieldNew)
	{	
		for(int j=0+data.offset; j<data.innerSizeX+data.offset; j++)
		{
			for(int i=0; i<=data.sizeY; i++)
			{
				fieldNew[j][i] += data.dt*fieldOld[j][i];
						
			}
		}	
	}

	private void adjustVelocity(float [][]xVelocity, float [][]yVelocity)
	{
		int i,j;
		float [][]div = new float[data.sizeX+1][data.sizeY+1];
		float [][]phi = new float[data.sizeX+1][data.sizeY+1];
		
		for( j = 1+data.offset; j<data.innerSizeX+data.offset; j++)
		{
			for( i=1; i<=data.innerSizeY; i++)
			{
				div[j][i] = -0.5f/data.innerSizeX*(xVelocity[j+1][i] - xVelocity[j-1][i] + yVelocity[j][i+1] - yVelocity[j][i-1]);
				phi[j][i] = 0;
			}
		}
		setBoundary(0, div);
		setBoundary(0, phi);
		
		solveLinearSystem(0, phi, div, 1.0f, 4.0f);

		for(j=1+data.offset; j<data.innerSizeX+data.offset; j++)
		{
			for( i=1; i<=data.innerSizeY; i++)
			{
				xVelocity[j][i] -= 0.5f*data.innerSizeX*(phi[j+1][i] - phi[j-1][i]);
				yVelocity[j][i] -= 0.5f*data.innerSizeY*(phi[j][i+1] - phi[j][i-1]);
			}
		}
		setBoundary(1, xVelocity);
		setBoundary(2, yVelocity);
		
	}
	
	private void solveLinearSystem(int boundaryType, float[][] fieldNew, float [][] fieldOld, float a, float c)
	{
	    for (int k = 0; k < 25; k++)
		{
		    for (int i = 1; i < data.sizeY; i++)
		    {
                for (int j = 1+data.offset; j < data.innerSizeX+data.offset; j++)
                {
                    fieldNew[j][i] = (a * ( fieldNew[j-1][i] + fieldNew[j+1][i]
                            +   fieldNew[j][i-1] + fieldNew[j][i+1])
                            +  fieldOld[j][i]) / c;
                }
		    }
		    setBoundary(boundaryType, fieldNew);
		}
	}
	
	void swapDensity(){ data.tmp = data.densityNew; data.densityNew = data.densityOld; data.densityOld = data.tmp; }
	void swapU(){ data.tmp = data.xVelocityOld; data.xVelocityOld = data.xVelocityNew; data.xVelocityNew = data.tmp; }
	void swapV(){ data.tmp = data.yVelocityOld; data.yVelocityOld = data.yVelocityNew; data.yVelocityNew = data.tmp; }
}