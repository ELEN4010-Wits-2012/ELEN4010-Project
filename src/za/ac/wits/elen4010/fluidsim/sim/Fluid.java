package za.ac.wits.elen4010.fluidsim.sim;

import java.io.*;
import java.io.*;
import java.math.*;

/**
 * Class representing a fluid. The fluid has a density field and
 * two velocity fields u and v (one for each dimension).
 * 
 * @author Ronald Clark
 */
public class Fluid
{
	public int imax = 256, jmax = 256;
	public int sizeX = 254, sizeY = 254;
	float dt = 0.2f;	
	int numGaussSeidelIter = 2;
	
	float []viscosity = new float[]{0,0};
	float [][]rhoOld = new float[jmax+1][imax+1];
	float [][]rhoNew = new float[jmax+1][imax+1];
	float [][]tmp = new float[jmax+1][imax+1];
	float [][]uOld = new float[jmax+1][imax+1];
	float [][]uNew = new float[jmax+1][imax+1];
	float [][]vOld = new float[jmax+1][imax+1];
	float [][]vNew = new float[jmax+1][imax+1];
	float [][]curl = new float[jmax+1][imax+1];
	float source[][] = new float[jmax+1][imax+1];
	float t = 0.0f;
	
    /**
     * Sets the value of boundary cells. X velocity on the left and right boundary
     * is opposite to that of the inner cell adjacent to it. X velocity on the top 
     * and bottom is equal to that of the inner cell adjacent to it.
     * 
     * @param boundaryType the type of boundary to set. 1 = x velocity 2 = y velocity
     * @param field the field on which to set the boundary
     */
	void setBoundary(int boundaryType, float [][] field)
	{
		for( int k = 1; k<=sizeX; k++)
		{
			field[0][k] = boundaryType == 1 ? -field[1][k] : field[1][k];
			field[sizeX+1][k] = boundaryType == 1 ? -field[sizeX][k] : field[sizeX][k];
			field[k][0] = boundaryType == 2 ? -field[k][1] : field[k][1];
			field[k][sizeY+1] = boundaryType == 2 ? -field[k][sizeY] : field[k][sizeY];
		}
		field[0][0] = 0.5f * (field[1][0] + field[0][1]);
		field[0][sizeY+1] = 0.5f * (field[1][sizeY+1] + field[0][sizeY]);
		field[sizeX+1][0] = 0.5f * (field[sizeX][0] + field[sizeX+1][1]);
		field[sizeX+1][sizeY+1] = 0.5f * (field[sizeX][sizeY+1] + field[sizeX+1][sizeY]);
	}
	
	/**
	 * Calculates the z component of the curl of the 2 dimensional
	 * velocity field. Stores the result in curl.
	 * 
	 */
	void calculateCurl()
	{
		for( int j = 1; j<=sizeX; j++ )
		{
			for( int i=1; i<=sizeY; i++ )
			{
				float dudy = (uNew[j][i+1] - uNew[j][i-1])*0.5f;
				float dvdx = (vNew[j+1][i] - vNew[j-1][i])*0.5f;
				curl[j][i] = dudy - dvdx;
			}
		}
	}
	
	/**
	 * Enhances the strength of vortices. A rotational velocity
	 * is added to the velocity field wherever there is a large amount
	 * of rotation (curl). This enhances the swirls in the liquid and
	 * prevents them from dissipating.
	 * 
	 */
	void addVorticity()
	{
		calculateCurl();
		for( int j = 2; j<sizeX; j++ )
		{
			for( int i = 2; i<sizeY; i++ )
			{
				float dwdx = (curl[j+1][i] - curl[j-1][i])*0.5f;
				float dwdy = (curl[j][i+1] - curl[j][i-1])*0.5f;
				
				float magnitude = (float)Math.sqrt(dwdx*dwdx + dwdy*dwdy) + 0.00001f;
				
				dwdx /= magnitude;
				dwdy /= magnitude;
				
				float v = curl[j][i];
				
				uOld[j][i] = dwdy * -v;
				vOld[j][i] = dwdx * v;
			}
		}
		updateSource(0,false, uOld, uNew);
		updateSource(0,false, vOld, vNew);
	}
	
	/**
	 * Simulate the floating movement of the fluid due to hot areas.
	 * The density of the fluid is used as an indication of the temperature.
	 * 
	 */
	void addBouyancy(float [][] velocityField)
	{
		float ambientTemperature = 0.0f;
		float a = 0.000625f;
		float b = 0.025f;
		
		for( int j = 1; j<=sizeX; j++)
		{
			for( int i=1; i<=sizeY; i++ )
			{
				ambientTemperature += rhoNew[j][i];
			}
		}
		ambientTemperature /= sizeX*sizeY;
		
		for( int j = 1; j<=sizeX; j++)
		{
			for( int i=1; i<=sizeY; i++ )
			{
				velocityField[j][i] = a*rhoNew[j][i] - b*(rhoNew[j][i] - ambientTemperature);
			}
		}
	}
	
	/**
	 * Diffuse the fluid by moving density out of the current cell to 
	 * the four adjacent cells and into the current cell from the adjacent cells.
	 * This is done in reverse. See **** 
	 * 
	 * @param boundaryType specifies the type of field being diffused
	 * @param sOld the field values during the previous timestep
	 * @param sNew the field values during the current timestep
	 */
	private void diffuse(int boundaryType, float [][]sOld, float [][]sNew)
	{	
		float a = dt * sizeX * sizeY * viscosity[0];
		solveLinearSystem(boundaryType, sNew, sOld, a, 1+4*a);
		
	}	
	
	/**
	 * Move a field along with the velocity field. Linear interpolation 
	 * of the four nearest points is used to find the new value of the cell.
	 * 
	 * @param boundaryType specifies the type of field being advected
	 * @param uOld the x-velocity field to move along
	 * @param vOld the y-velocity field to move along
	 * @param sOld the field values during the previous timestep
	 * @param sNew the field values during the current timestep
	 */
	private void advect(int boundaryType, float [][]uOld, float [][]vOld, float [][] sOld, float [][] sNew)
	{
		float dt0 = dt*sizeX;
		for(int j=1; j<=sizeX; j++)
		{
			for( int i=1; i<=sizeY; i++)
			{
				float iPrevious = i - dt0*vOld[j][i];
				
				if(iPrevious > sizeY+0.5 ) iPrevious = sizeY+0.5f;
				if(iPrevious < 0.5) iPrevious = 0.5f;
				
				int i0 = (int)Math.floor(iPrevious); 
				int i1 = i0 +1;	
				float a1 = iPrevious - i0;
				float a0 = 1-a1;
				
				float jPrevious = j - dt0*uOld[j][i];
				
				if(jPrevious > sizeX+0.5 ) jPrevious = sizeX + 0.5f;
				if(jPrevious < 0.5) jPrevious = 0.5f;
				
				int j0 = (int)Math.floor(jPrevious); 
				int j1 = j0 +1;
				
				float b1 = jPrevious - j0;
				float b0 = 1-b1;
				
				sNew[j][i] = b0*(a0*sOld[j0][i0] + a1*sOld[j0][i1]) +b1*(a0*sOld[j1][i0]+a1*sOld[j1][i1]);
			}
		}
		setBoundary(boundaryType, sNew);
	}
	
	/**
	 * Add sources to a field. The sources are specified in the 'old'
	 * field vector.
	 * 
	 * @param xOld vector containing the sources
	 * @param xNew the field to add the sources to
	 */
	void updateSource(float t, boolean clampToZero, float [][]xOld, float [][]xNew)
	{	
		for(int j=0; j<=jmax; j++)
		{
			for(int i=0; i<=imax; i++)
			{
				xNew[j][i] += dt*xOld[j][i];
						
			}
		}	
	}

	/**
	 * Make the velocity field divergence-free ie. mass conserving.
	 * This is done by computing the 'potential' field phi by solving the 
	 * poisson equations. The divergence of phi is then subtracted from 
	 * the original field, leaving only the curl (rotational)-component  
	 * of the field.
	 * 
	 */
	void adjustVelocity(float t, float [][]u, float [][]v)
	{
		int i,j;
		float [][]div = new float[jmax+1][imax+1];
		float [][]phi = new float[jmax+1][imax+1];
		
		for( j = 1; j<=sizeX; j++)
		{
			for( i=1; i<=sizeY; i++)
			{
				div[j][i] = -0.5f/sizeX*(u[j+1][i] - u[j-1][i] + v[j][i+1] - v[j][i-1]);
				phi[j][i] = 0;
			}
		}
		setBoundary(0, div);
		setBoundary(0, phi);
		
		solveLinearSystem(0, phi, div, 1.0f, 4.0f);

		for(j=1; j<=sizeX; j++)
		{
			for( i=1; i<=sizeY; i++)
			{
				u[j][i] -= 0.5f*sizeX*(phi[j+1][i] - phi[j-1][i]);
				v[j][i] -= 0.5f*sizeY*(phi[j][i+1] - phi[j][i-1]);
			}
		}
		setBoundary(1, u);
		setBoundary(2, v);
		
	}
	
	/**
	 * Solves a linear system of equations using the Gauss-Seidel
	 * method.
	 * 
	 * A = BX
	 */
	void solveLinearSystem(int boundaryType, float[][] X, float [][] A, float a, float c)
	{
	        for (int k = 0; k < 25; k++)
		{
		    for (int i = 1; i <= sizeY; i++)
		    {
			for (int j = 1; j <= sizeX; j++)
			{
			    X[j][i] = (a * ( X[j-1][i] + X[j+1][i]
					    +   X[j][i-1] + X[j][i+1])
					    +  A[j][i]) / c;
			}
		    }
		    setBoundary(boundaryType, X);
		}
	}
	
	/**
	 * Swap the old and new density fields.
	 * 
	 */
	void swapRho(){ tmp = rhoNew; rhoNew = rhoOld; rhoOld = tmp; }
	void swapU(){ tmp = uOld; uOld = uNew; uNew = tmp; }
	void swapV(){ tmp = vOld; vOld = vNew; vNew = tmp; }
	
	/**
	 * Perform one simulation step. This updates the density
	 * and velocity fields.
	 * 
	 * (Add Forces) -> Diffuse -> Advect -> Project
	 */
	public void step()
	{
			// Density
			updateSource(t, true, rhoOld, rhoNew);	
			swapRho();
			
			diffuse(0, rhoOld, rhoNew);
			swapRho();
			
			advect(0, uNew, vNew, rhoOld, rhoNew);
				
			for( int i = 0; i<=imax; i++)
				for( int j=0; j<=jmax; j++ )
					rhoOld[i][j] = 0;
			
			//Velocity
			updateSource(t, false, uOld, uNew);
			updateSource(t, false, vOld, vNew);	
			
			addVorticity();
			
			addBouyancy(vOld);
			updateSource(t, false, vOld, vNew);
			
			swapU();
			diffuse(0, uOld, uNew);
			
			swapV();
			diffuse(0, vOld, vNew);
			
			adjustVelocity(t, uNew, vNew);
			swapU(); swapV();
			
			advect(1,uOld, vOld, uOld, uNew);
			advect(2,uOld, vOld, vOld, vNew);
			
			adjustVelocity(t, uNew, vNew);
	
			for( int i = 0; i<=imax; i++)
				for( int j=0; j<=jmax; j++ )
					uOld[i][j] = vOld[i][j] = 0;
			
			t = t+dt; 
	}
}