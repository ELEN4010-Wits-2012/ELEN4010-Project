import java.io.*;
import java.math.*;

public class Simulation
{
	static float x0 = 0, y0 = 0, x1 = 1, y1 = 1;
	static int imax = 20, jmax = 20;
	static float dt = 1;	
	static float dx = (x1 - x0)/imax;
	static float dy = (y1 - y0)/jmax;
	static float dtdx = dt/dx;
	static float dtdy = dt/dy;
	static float dtdxdx = dtdx/dx;
	static float dtdydy = dtdy/dy;
	int numGaussSeidelIter = 4;
	
	float []viscosity = new float[2];
	float []lambdaX = new float[]{ viscosity[0]*dtdxdx, viscosity[1]*dtdxdx }; // Is there an n??
	float []lambdaY = new float[]{ viscosity[0]*dtdydy, viscosity[1]*dtdydy };
	float []gamma0 = new float[]{ 1/(1+2*(lambdaX[0]+lambdaY[0])),1/(1+2*(lambdaX[1]+lambdaY[1])) };
	float []x = new float[imax];
	float []y = new float[jmax];
	float [][]rhoOld = new float[jmax][imax];
	float [][]rhoNew = new float[jmax][imax];
	float [][]uOld = new float[jmax][imax];
	float [][]uNew = new float[jmax][imax];
	float [][]vOld = new float[jmax][imax];
	float [][]vNew = new float[jmax][imax];
	
	float t = 0.0f;
	
	
	float halfdx = 0.5f/dx;
	float halfdy = 0.5f/dy;
	float dx2 = dx*dx;
	float dy2 = dy*dy;
	float invDen = 0.5f/(dx2+dy2);
	float epsilon0 = dx2*dy2*invDen;
	float epsilon1 = dy2*invDen;
	float epsilon2 = dx2*invDen;
	
	private void diffuse(float t, float[] x, float[] y, float gamma0, float gamma1, float gamma2, float [][]sOld, float [][]sNew)
	{
		System.arraycopy(sOld, 0, sNew,0, sOld.length);
		
		for( int k=0; k<4; k++ )
		{
			for( int j=1; j<jmax; j++ )
			{
				for( int i=0;i<imax; i++)
				{
					sNew[j][i] = gamma0*sOld[j][i] + gamma1*(sNew[j][i+1] + sNew[j][i-1]) + gamma2*(sNew[j+1][i] + sNew[j-1][i]);
				}
			}
		}
		//updateBoundary(t, x, y, sNew);
	}	
	
	private void initializeImplicit(float[] x, float[] y, float [][]sOld, float [][]sNew)
	{
		for( int j=0; j<=jmax; j++ )
		{
			y[j] = y0 + dy*j;
			for( int i=0; i<=imax; i++)
			{
				x[i] = x0 + dx*i;
				float value = 1; //IinitialTo(x[i], y[i])
				sOld[j][i] = value;
				sNew[j][i] = value;
			}
		}
	}
	
	private float clamp(float i, float min, float max)
	{
		float result =i;
		if( i < min ) 
			result = min;
		else if( i> max )
			result = max;
			
			return result;
	}
		
	private void advect(float t, float[] x, float[] y, float [][]uOld, float [][]vOld, float [][] sOld, float [][] sNew)
	{
		for(int j=1; j<jmax; j++)
		{
			for( int i=1; i<imax; i++)
			{
				float iPrevious = i - dtdx*uOld[j][i];
				iPrevious = clamp(iPrevious, 0.5f, imax-0.5f);
				int i0 = (int)Math.floor(iPrevious); int i1 = i0 +1;
				float a1 = iPrevious - i0;
				float a0 = 1-a1;
				
				float jPrevious = i - dtdy*vOld[j][i];
				jPrevious = clamp(jPrevious, 0.5f, imax-0.5f);
				int j0 = (int)Math.floor(jPrevious); int j1 = j0 +1;
				float b1 = iPrevious - i0;
				float b0 = 1-b1;
				
				sNew[j][i] = b0*(a0*sOld[j0][i0] + a1*sOld[j0][i1]) +b1*(a0*sOld[j1][i0]+a1*sOld[j1][i1]);
			}
		}
	}
	
void updateSource(float t, boolean clampToZero, float [][]sOld, float [][]sNew)
{
float source[][] = new float[imax][jmax];

	int i,j;
	if(clampToZero)
	{
		for(j=0; j<=jmax; j++)
		{
			for(i=0; i<=imax; i++)
			{
				sNew[j][i] = sOld[j][i] + dt*source[j][i];
				if( sNew[j][i] < 0)
					sNew[j][i] = 0;
					
			}
		}
	}
	else
	{
		for(j=0; j<=jmax; j++)
		{
			for(i=0; i<=imax; i++)
			{
				sNew[j][i] = sOld[j][i] + dt*source[j][i];
			}
		}
	}
}

void adjustVelocity(float t, float [][]u, float [][]v)
{
	int i,j;
	float [][]div = new float[jmax][imax];
	for( j = 1; j<jmax; j++)
	{
		for( i=1; i<imax; i++)
		{
			div[j][i] = halfdx*(u[j][i+1] - u[j][i-1]) + halfdy*(u[j+1][i] - u[j-1][i]);
		
		}
	}
	
	for( i=0; i<=imax; i++)
	{
		div[0][i] = div[1][i];
		div[jmax][i] = div[jmax-1][i];
	}
	for(  j=0; j<=jmax; j++)
	{
		div[j][0] = div[j][1];
		div[j][imax] = div[j][imax-1];
	}

	float [][]phi = new float[jmax][imax];
	for( j=0; j<=jmax; j++)
	{
		for( i=0; i<=imax; i++)
		{
			phi[j][i] = 0.0f;
		}
	}
	
	//Poisson solution
	for( int k = 0; k< numGaussSeidelIter; k++)
	{
		for(j=1; j<jmax; j++)
		{
			for(i=1; i<imax; i++)
			{
				phi[j][i] = epsilon0*div[j][i] + epsilon1*(phi[j][i+1] + phi[j][i-1]) + epsilon2*(phi[j+1][i]+phi[j-1][i]);
			}
		}
	}
	
	for(j=1; j<jmax; j++)
	{
		for( i=1; i<imax; i++)
		{
			u[j][i] = u[j][i] + halfdx*(phi[j][i+1] - phi[j][i-1]);
			v[j][i] = v[j][i] + halfdy*(phi[j+1][i] - phi[j-1][i]);
		}
		//updateBoundary(
		//updateBoundary(
	}
}
	
	public static void main(String [] args)
	{
		while(true)
		{
			
		System.out.println("Hello");
	}
}