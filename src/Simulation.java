import java.io.*;
import java.math.*;

public class Simulation
{
	static float x0 = 0, y0 = 0, x1 = 1, y1 = 1;
	public static int imax = 256, jmax = 256;
	static float dt = 0.5f;	
	static float dx = (x1 - x0)/imax;
	static float dy = (y1 - y0)/jmax;
	static float dtdx = dt/dx;
	static float dtdy = dt/dy;
	static float dtdxdx = dtdx/dx;
	static float dtdydy = dtdy/dy;
	static int numGaussSeidelIter = 100;
	
	static float []viscosity = new float[]{0.01f,0.01f};
	static float []lambdaX = new float[]{ viscosity[0]*dtdxdx, viscosity[1]*dtdxdx }; // Is there an n??
	static float []lambdaY = new float[]{ viscosity[0]*dtdydy, viscosity[1]*dtdydy };
	static float []gamma0 = new float[]{ 1/(1+2*(lambdaX[0]+lambdaY[0])),1/(1+2*(lambdaX[1]+lambdaY[1])) };
	static float []gamma1 = new float[]{ lambdaX[0]*gamma0[0], lambdaX[1]*gamma0[1]};
	static float []gamma2 = new float[]{ lambdaY[0]*gamma0[0], lambdaY[1]*gamma0[1]};
	static float []x = new float[imax+1];
	static float []y = new float[jmax+1];
	static float [][]rhoOld = new float[jmax+1][imax+1];
	public static float [][]rhoNew = new float[jmax+1][imax+1];
	static float [][]uOld = new float[jmax+1][imax+1];
	static float [][]uNew = new float[jmax+1][imax+1];
	static float [][]vOld = new float[jmax+1][imax+1];
	static float [][]vNew = new float[jmax+1][imax+1];
	static float source[][] = new float[jmax+1][imax+1];
	static float t = 0.0f;
	
	
	static float halfdx = 0.5f/dx;
	static float halfdy = 0.5f/dy;
	static float dx2 = dx*dx;
	static float dy2 = dy*dy;
	static float invDen = 0.5f/(dx2+dy2);
	static float epsilon0 = dx2*dy2*invDen;
	static float epsilon1 = dy2*invDen;
	static float epsilon2 = dx2*invDen;
	
	static void setBoundaries()
	{
		for( int i=1; i<imax; i++ )
		{
			rhoOld[0][i] = rhoOld[1][i]; 
			rhoOld[jmax][i] = rhoOld[jmax-1][i]; 
			rhoNew[0][i] = rhoNew[1][i]; 
			rhoNew[jmax][i] = rhoNew[jmax-1][i]; 
			
			uOld[0][i] = uOld[1][i]; 
			uOld[jmax][i] = uOld[jmax-1][i]; 
			uNew[0][i] = uNew[1][i]; 
			uNew[jmax][i] = uNew[jmax-1][i]; 
			
			vOld[0][i] = 0;
			vOld[jmax][i] = 0; 	
			vNew[0][i] = 0;
			vNew[jmax][i] = 0;
		}
		for( int j=1; j<jmax; j++ )
		{		
			rhoOld[j][0] = rhoOld[j][1]; 
			rhoOld[j][imax] = rhoOld[j][imax-1]; 
			rhoNew[j][0] = rhoNew[j][1]; 
			rhoNew[j][imax] = rhoNew[j][imax-1]; 

			uOld[j][0] = 0; 
			uOld[j][jmax] = 0; 
			uNew[j][0] = 0; 
			uNew[j][jmax] = 0;
			
			vOld[j][0] = vOld[j][1];
			vOld[j][imax] = vOld[j][imax-1];;
			vNew[j][0] = vNew[j][1];
			vNew[j][imax] = vNew[j][imax-1];;
				
		}
		rhoOld[0][0] = rhoOld[1][1];
		rhoOld[0][imax] = rhoOld[1][imax-1];
		rhoOld[jmax][0] = rhoOld[jmax-1][1];
		rhoOld[jmax][imax] = rhoOld[jmax-1][imax-1];
		
		rhoNew[0][0] = rhoNew[1][1];
		rhoNew[0][imax] = rhoNew[1][imax-1];
		rhoNew[jmax][0] = rhoNew[jmax-1][1];
		rhoNew[jmax][imax] = rhoNew[jmax-1][imax-1];
		
		uOld[0][0] = uOld[0][imax] = uOld[jmax][0] = uOld[jmax][imax] = 0;
		vOld[0][0] = vOld[0][imax] = vOld[jmax][0] = vOld[jmax][imax] = 0;
		
		uNew[0][0] = uNew[0][imax] = uNew[jmax][0] = uNew[jmax][imax] = 0;
		vNew[0][0] = vNew[0][imax] = vNew[jmax][0] = vNew[jmax][imax] = 0;
		
		float max= -100.0f;
		for( int j=0; j<jmax; j++ )
		{
			for( int i=0;i<imax; i++)
			{
				if( (uOld[j][i]*uOld[j][i]+vOld[j][i]*vOld[j][i]) >max ) max = uOld[j][i]*uOld[j][i]+vOld[j][i]*vOld[j][i];
			}
		}
		for( int j=1; j<jmax; j++ )
		{
			for( int i=1;i<imax; i++)
			{
				uOld[j][i] /= Math.sqrt(max)*100 ;
				vOld[j][i] /= Math.sqrt(max)*100 ;
			}
		}
		
	}
	private static float[][] diffuse(float t, float gamma0, float gamma1, float gamma2, float [][]sOld, float [][]sNew)
	{
		System.arraycopy(sOld, 0, sNew,0, sOld.length);
		
		for( int k=0; k<numGaussSeidelIter; k++ )
		{
			for( int j=1; j<jmax; j++ )
			{
				for( int i=1;i<imax; i++)
				{
					sNew[j][i] = gamma0*sOld[j][i] + gamma1*(sNew[j][i+1] + sNew[j][i-1]) + gamma2*(sNew[j+1][i] + sNew[j-1][i]);
				}
			}
		}
		//updateBoundary(t, x, y, sNew);
		
		return sNew;
	}	
	
	private static float clamp(float i, float min, float max)
	{
		float result =i;
		if( i < min ) 
			result = min;
		else if( i> max )
			result = max;
			
			return result;
	}
		
	private static float[][] advect(float t, float [][]uOld, float [][]vOld, float [][] sOld, float [][] sNew)
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
				
				float jPrevious = j - dtdy*vOld[j][i];
				jPrevious = clamp(jPrevious, 0.5f, jmax-0.5f);
				int j0 = (int)Math.floor(jPrevious); int j1 = j0 +1;
				float b1 = jPrevious - j0;
				float b0 = 1-b1;
				
				sNew[j][i] = b0*(a0*sOld[j0][i0] + a1*sOld[j0][i1]) +b1*(a0*sOld[j1][i0]+a1*sOld[j1][i1]);
			}
		}
		return sNew;
	}
	
	static float[][] updateSource(float t, boolean clampToZero, float [][]sOld, float [][]sNew)
	{
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
		
		return sNew;
	}

	static float[][][] adjustVelocity(float t, float [][]u, float [][]v)
	{
		int i,j;
		float [][]div = new float[jmax+1][imax+1];
		for( j = 1; j<jmax; j++)
		{
			for( i=1; i<imax; i++)
			{
				div[j][i] = halfdx*(u[j][i+1] - u[j][i-1]) + halfdy*(v[j+1][i] - v[j-1][i]);
			
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

		float [][]phi = new float[jmax+1][imax+1];
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
		
		float [][][]result = new float[2][jmax+1][imax+1];
		result[0] = u;
		result[1] = v;
		
		return result;
	}
	
	static void swapRho()
	{
		float [][] tmp = new float[rhoOld.length][rhoOld.length];
		System.arraycopy(rhoOld,0,tmp,0,rhoOld.length);
		System.arraycopy(rhoNew,0,rhoOld,0,rhoNew.length);
		System.arraycopy(tmp,0,rhoNew,0,tmp.length);
	}
	static void swapUV()
	{
		float [][] tmp = new float[rhoOld.length][rhoOld.length];
		System.arraycopy(uOld,0,tmp,0,uOld.length);
		System.arraycopy(uNew,0,uOld,0,uNew.length);
		System.arraycopy(tmp,0,uNew,0,tmp.length);
		
		System.arraycopy(vOld,0,tmp,0,vOld.length);
		System.arraycopy(vNew,0,vOld,0,vNew.length);
		System.arraycopy(tmp,0,vNew,0,tmp.length);
	}
	
	public static void step()
	{
			// Density
			rhoNew = updateSource(t, true, rhoOld, rhoNew);
			setBoundaries();
			swapRho();
			//rhoNew = diffuse(t, gamma0[0], gamma1[0], gamma2[0],rhoOld, rhoNew);
			//setBoundaries();
			//swapRho();
			rhoNew = advect(t,uOld, vOld, rhoOld, rhoNew);
			setBoundaries();
			swapRho();
			
			//Velocity
			//uNew = updateSource(t, true, uOld, uNew);
			//setBoundaries();
			//vNew = updateSource(t, true, vOld, vNew);	
			//setBoundaries();
			//swapUV();
			
			uNew = diffuse(t, gamma0[1], gamma1[1], gamma2[1],uOld, uNew);
			setBoundaries();
			vNew = diffuse(t, gamma0[1], gamma1[1], gamma2[1],vOld, vNew);
			setBoundaries();
			swapUV();
			
			float[][][] newValues = adjustVelocity(t, uOld, vOld);
			uOld = newValues[0];
			vOld = newValues[1];
			setBoundaries();
			uNew = advect(t,uOld, vOld, uOld, uNew);
			vNew = advect(t,uOld, vOld, vOld, vNew);
			newValues = adjustVelocity(t, uNew, vNew);
			uNew = newValues[0];
			vNew = newValues[1];
			setBoundaries();
			swapUV();
			
			t = t+dt;
		
	}
}