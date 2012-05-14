package za.ac.wits.elen4010.fluidsim.sim;

import java.io.*;
import java.io.*;
import java.math.*;

import za.ac.wits.elen4010.fluidsim.gui.SimulationInput;
import za.ac.wits.elen4010.fluidsim.gui.Velocity;
import za.ac.wits.elen4010.fluidsim.mpiNodalCode.EdgeData;
import za.ac.wits.elen4010.fluidsim.mpiNodalCode.RenderData;

/**
 * Class representing a fluid. 
 * 
 * The simulation algorithm is based on the popular Navier-Stokes solver
 * created by Jos Stam. The original paper is available here: 
 * www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf
 * <p>
 * The fluid has a density field and two velocity fields u and v (one for each dimension).
 * The fluid can be split vertically into two regions: the rendering and the overlapping regions. The overlapping region
 * is not rendered, and belongs to an adjacent fluid. The overlapping region should be updated to match the appropriate
 * edge of the adjacent fluid after every step.
 * 
 * @author Ronald Clark
 */
public class Fluid
{
    private int jmax, imax;
    private int sizeX, sizeY;
    private float dt = 0.2f;
    private int numGaussSeidelIter = 25;
    
    private SimulationInput userInput;
    private float[] viscosity;
    private float[][] densityOld;            // Remember to remove the public after testing
    private float[][] densityNew;
    private float[][] tmp;
    private float[][] uVelocityOld;
    private float[][] uVelocityNew;
    private float[][] vVelocityOld;
    private float[][] vVelocityNew;
    private float[][] curl;
    private float[][] source;
    private float t;
    private int topRow = 0;
    private int FluidHeight = 100;      // Default value

    private int overlapHeight = 20; // Size of the overlapping region.

    
    /**
     * Enum type used to specify where the overlapping region should be placed.
     * 
     * @author Justin Wernick
     */
    public enum Side
    {
        TOP, BOTTOM
    };

    // Indices of regions. The overlapping region and edge are both OVERLAP_WIDTH wide.
    private int renderTop;
    private int renderBottom;
    private int overlapTop;
    private int overlapBottom;
    private int edgeTop;
    private int edgeBottom;
   
    /**
     * Sets the overlapping region to the given values.
     * 
     * @param density The new density for the overlapping region.
     * @param u The new u velocity field for the overlapping region.
     * @param v The new v velocity field for the overlapping region.
     * 
     * @author Justin Wernick, Ronald Clark
     */
    public void setOverlap( EdgeData overlapData, Side side )
    {
    	int overlapStart;
    	if( side == Side.TOP )
    		overlapStart = overlapTop;
    	else 
    		overlapStart = overlapBottom;
    
    	
        for ( int j = 0; j <= jmax; ++j )
        {
            for ( int i = 0; i < overlapHeight; ++i )
            {
                densityNew[j][i + overlapStart] = overlapData.getDensity()[j][i];
                uVelocityNew[j][i + overlapStart] = overlapData.getHorizontalVelocity()[j][i];
                vVelocityNew[j][i + overlapStart] = overlapData.getVerticalVelocity()[j][i];
            }
        }
    }
    
    /**
     * Gets the edge data (density, u and v velocity) encapsulated
     * as an <code>EdgeData</code> object.
     * 
     * @see EdgeData
     * 
     * @return The edge data values
     * 
     * @author Ronald Clark
     * 
     */
    public EdgeData getEdge( Side side )
    {
    	return new EdgeData(getEdgeDensity(side), getEdgeU(side), getEdgeV(side));
    }
    
    /**
     * Gets the density from the edge next to the overlapping region.
     * 
     * @return A jmax by OVERLAP_WIDTH array of density values from the edge.
     * 
     * @author Justin Wernick, Ronald Clark
     */
    private float[][] getEdgeDensity(Side side)
    {
    	int edgeStart;
    	if( side == Side.TOP )
    		edgeStart = edgeTop;
    	else 
    		edgeStart = edgeBottom;
    	
        float[][] result = new float[jmax + 1][overlapHeight];

        for ( int j = 0; j <= jmax; ++j )
        {
            for ( int i = 0; i < overlapHeight; ++i )
            {
                result[j][i] = densityNew[j][i + edgeStart];
            }
        }

        return result;
    }
    
    /**
     * Gets the render data encapsulated as a <code>RenderData</code>
     * object.
     * 
     * @return the render data
     */
    public RenderData getRenderData()
    {
    	return new RenderData( getRenderDensity() );
    }
    
    /**
     * Gets the density from the rendering region.
     * 
     * @return A jmax by (renderBottom - renderTop + 1) array of density values.
     * 
     * @author Justin Wernick
     */
    private float[][] getRenderDensity()
    {
        float[][] result = new float[jmax + 1][renderBottom - renderTop + 1];

        for ( int j = 0; j <= jmax; ++j )
        {
            for ( int i = 0; i <= renderBottom - renderTop; ++i )
            {
                result[j][i] = densityNew[j][i + renderTop];
            }
        }

        return result;
    }
    
    /** 
     * Gets the current elapsed time in the simulation.
     * 
     * @return The amount time in seconds that has been simulated so far.
     */
    public float getTime()
    {
        return t;
    }

    /**
     * Gets the u velocity field from the edge next to the overlapping region.
     * 
     * @return A jmax by OVERLAP_WIDTH array of u velocity field values from the edge.
     * 
     * @author Justin Wernick, Ronald Clark
     */
    private float[][] getEdgeU(Side side)
    {
    	int edgeStart;
    	if( side == Side.TOP )
    		edgeStart = edgeTop;
    	else 
    		edgeStart = edgeBottom;
    	
        float[][] result = new float[jmax + 1][overlapHeight];

        for ( int j = 0; j <= jmax; ++j )
        {
            for ( int i = 0; i < overlapHeight; ++i )
            {
                result[j][i] = uVelocityNew[j][i + edgeStart];
            }
        }

        return result;
    }

    /**
     * Gets the v velocity field from the edge next to the overlapping region.
     * 
     * @return A jmax by OVERLAP_WIDTH array of v velocity field values from the edge.
     * 
     * @author Justin Wernick
     */
    private float[][] getEdgeV(Side side)
    {
    	int edgeStart;
    	if( side == Side.TOP )
    		edgeStart = edgeTop;
    	else 
    		edgeStart = edgeBottom;
    	
        float[][] result = new float[jmax + 1][overlapHeight];

        for ( int j = 0; j <= jmax; ++j )
        {
            for ( int i = 0; i < overlapHeight; ++i )
            {
                result[j][i] = vVelocityNew[j][i + edgeStart];
            }
        }

        return result;
    }

    /**
     * Creates a new Fluid simulation with the given size, overlapping characteristics and user input.
     * 
     * @param width The size of the renderable area in the x direction.
     * @param height The size of the renderable area in the y direction.
     * @param overlapSide The side of the simulation on which overlapping occurs.
     * 
     * @author Ronald Clark and Justin Wernick
     */
    public Fluid( int topRowNum, int renderingHeight, int overlappingHeight, int width, boolean isTop, boolean isBottom, SimulationInput userInput )
    {
    	this.userInput = userInput;
    	this.topRow = isTop ? topRowNum : topRowNum-overlappingHeight;
    	this.FluidHeight = renderingHeight;
    	
    	overlapHeight= overlappingHeight;
    	
        jmax = width - 1;
        imax = (isTop || isBottom) ? renderingHeight - 1 + overlapHeight : renderingHeight - 1 + 2*overlapHeight;

        sizeX = jmax - 2;
        sizeY = imax - 2;

        overlapTop =  0;
        overlapBottom = (isTop || isBottom) ? renderingHeight : overlapHeight + renderingHeight;
        edgeTop = overlapHeight;
        edgeBottom = isTop ? renderingHeight - overlapHeight : renderingHeight - overlapHeight + overlapHeight;
        
        renderTop = isTop ? 0 : overlapHeight;
        renderBottom = isTop ? renderingHeight - 1 : imax;

        viscosity = new float[] { 0, 0 };
        densityOld = new float[jmax + 1][imax + 1];
        densityNew = new float[jmax + 1][imax + 1];
        tmp = new float[jmax + 1][imax + 1];
        uVelocityOld = new float[jmax + 1][imax + 1];
        uVelocityNew = new float[jmax + 1][imax + 1];
        vVelocityOld = new float[jmax + 1][imax + 1];
        vVelocityNew = new float[jmax + 1][imax + 1];
        curl = new float[jmax + 1][imax + 1];
        source = new float[jmax + 1][imax + 1];
        t = 0.0f;
    }

    /**
     * Sets the value of boundary cells. X velocity on the left and right boundary is opposite to that of the inner cell
     * adjacent to it. X velocity on the top and bottom is equal to that of the inner cell adjacent to it.
     * 
     * @param boundaryType the type of boundary to set. 1 = x velocity 2 = y velocity
     * 
     * @param field the field on which to set the boundary
     * 
     * @author Ronald Clark
     */
    private void setBoundary( int boundaryType, float[][] field )
    {
        for ( int k = 1; k <= sizeX; k++ )
        {
            field[k][0] = boundaryType == 2 ? -field[k][1] : field[k][1];
            field[k][sizeY + 1] = boundaryType == 2 ? -field[k][sizeY] : field[k][sizeY];
        }
        for ( int k = 1; k <= sizeY; k++ )
        {
            field[0][k] = boundaryType == 1 ? -field[1][k] : field[1][k];
            field[sizeX + 1][k] = boundaryType == 1 ? -field[sizeX][k] : field[sizeX][k];
        }
        field[0][0] = 0.5f * (field[1][0] + field[0][1]);
        field[0][sizeY + 1] = 0.5f * (field[1][sizeY + 1] + field[0][sizeY]);
        field[sizeX + 1][0] = 0.5f * (field[sizeX][0] + field[sizeX + 1][1]);
        field[sizeX + 1][sizeY + 1] = 0.5f * (field[sizeX][sizeY + 1] + field[sizeX + 1][sizeY]);
    }

    /**
     * Calculates the z component of the curl of the 2 dimensional velocity field. Stores the result in curl.
     * 
     * @author Ronald Clark
     */
    private void calculateCurl()
    {
        for ( int j = 1; j <= sizeX; j++ )
        {
            for ( int i = 1; i <= sizeY; i++ )
            {
                float dudy = (uVelocityNew[j][i + 1] - uVelocityNew[j][i - 1]) * 0.5f;
                float dvdx = (vVelocityNew[j + 1][i] - vVelocityNew[j - 1][i]) * 0.5f;
                curl[j][i] = dudy - dvdx;
            }
        }
    }

    /**
     * Enhances the strength of vortices. A rotational velocity is added to the velocity field wherever there is a large
     * amount of rotation (curl). This enhances the swirls in the liquid and prevents them from dissipating.
     * 
     * @author Ronald Clark
     */
    private void addVorticity()
    {
        calculateCurl();
        for ( int j = 2; j < sizeX; j++ )
        {
            for ( int i = 2; i < sizeY; i++ )
            {
                float dwdx = (curl[j + 1][i] - curl[j - 1][i]) * 0.5f;
                float dwdy = (curl[j][i + 1] - curl[j][i - 1]) * 0.5f;

                float magnitude = (float) Math.sqrt( dwdx * dwdx + dwdy * dwdy ) + 0.00001f;

                dwdx /= magnitude;
                dwdy /= magnitude;

                float v = curl[j][i];

                uVelocityOld[j][i] = dwdy * -v;
                vVelocityOld[j][i] = dwdx * v;
            }
        }
        updateSource( 0, false, uVelocityOld, uVelocityNew );
        updateSource( 0, false, vVelocityOld, vVelocityNew );
    }

    /**
     * Simulate the floating movement of the fluid due to hot areas. The density of the fluid is used as an indication
     * of the temperature.
     * 
     * @author Ronald Clark
     */
    private void addBouyancy( float[][] velocityField )
    {
        float ambientTemperature = 0.0f;
        float a = 0.000625f;
        float b = 0.025f;

        for ( int j = 1; j <= sizeX; j++ )
        {
            for ( int i = 1; i <= sizeY; i++ )
            {
                ambientTemperature += densityNew[j][i];
            }
        }
        ambientTemperature /= sizeX * sizeY;

        for ( int j = 1; j <= sizeX; j++ )
        {
            for ( int i = 1; i <= sizeY; i++ )
            {
                velocityField[j][i] = a * densityNew[j][i] - b * (densityNew[j][i] - ambientTemperature);
            }
        }
    }

    /**
     * Diffuse the fluid by moving density out of the current cell to the four adjacent cells and into the current cell
     * from the adjacent cells. This is done in reverse. See ****
     * 
     * @param boundaryType specifies the type of field being diffused
     * @param sOld the field values during the previous timestep
     * @param sNew the field values during the current timestep
     * 
     * @author Ronald Clark
     */
    private void diffuse( int boundaryType, float[][] sOld, float[][] sNew )
    {
        float a = dt * sizeX * sizeY * viscosity[0];
        solveLinearSystem( boundaryType, sNew, sOld, a, 1 + 4 * a );

    }

    /**
     * Move a field along with the velocity field. Linear interpolation of the four nearest points is used to find the
     * new value of the cell.
     * 
     * @param boundaryType specifies the type of field being advected
     * @param uVelocity the x-velocity field to move along
     * @param vVelocity the y-velocity field to move along
     * @param fieldOld the field values during the previous timestep
     * @param fieldNew the field values during the current timestep
     * 
     * @author Ronald Clark
     */
    private void advect( int boundaryType, float[][] uVelocity, float[][] vVelocity, float[][] fieldOld, float[][] fieldNew )
    {
        float dt0 = dt * sizeX;
        for ( int j = 1; j <= sizeX; j++ )
        {
            for ( int i = 1; i <= sizeY; i++ )
            {
                float iPrevious = i - dt0 * vVelocity[j][i];

                if ( iPrevious > sizeY + 0.5 )
                    iPrevious = sizeY + 0.5f;
                if ( iPrevious < 0.5 )
                    iPrevious = 0.5f;

                int i0 = (int) Math.floor( iPrevious );
                int i1 = i0 + 1;
                float a1 = iPrevious - i0;
                float a0 = 1 - a1;

                float jPrevious = j - dt0 * uVelocity[j][i];

                if ( jPrevious > sizeX + 0.5 )
                    jPrevious = sizeX + 0.5f;
                if ( jPrevious < 0.5 )
                    jPrevious = 0.5f;

                int j0 = (int) Math.floor( jPrevious );
                int j1 = j0 + 1;

                float b1 = jPrevious - j0;
                float b0 = 1 - b1;

                fieldNew[j][i] = b0 * (a0 * fieldOld[j0][i0] + a1 * fieldOld[j0][i1]) + b1 * (a0 * fieldOld[j1][i0] + a1 * fieldOld[j1][i1]);
            }
        }
        setBoundary( boundaryType, fieldNew );
    }

    /**
     * Add sources to a field. The sources are specified in the 'old' field vector.
     * 
     * @param xOld vector containing the sources
     * @param xNew the field to add the sources to
     * 
     * @author Ronald Clark
     */
    private void updateSource( float t, boolean clampToZero, float[][] xOld, float[][] xNew )
    {
        for ( int j = 0; j <= jmax; j++ )
        {
            for ( int i = 0; i <= imax; i++ )
            {
                xNew[j][i] += dt * xOld[j][i];

            }
        }
    }

    /**
     * Make the velocity field divergence-free ie. mass conserving. This is done by computing the 'potential' field phi
     * by solving the poisson equations. The divergence of phi is then subtracted from the original field, leaving only
     * the curl (rotational)-component of the field.
     * 
     * @author Ronald Clark
     */
    private void adjustVelocity( float t, float[][] u, float[][] v )
    {
        int i, j;
        float[][] div = new float[jmax + 1][imax + 1];
        float[][] phi = new float[jmax + 1][imax + 1];

        for ( j = 1; j <= sizeX; j++ )
        {
            for ( i = 1; i <= sizeY; i++ )
            {
                div[j][i] = -0.5f / sizeX * (u[j + 1][i] - u[j - 1][i] + v[j][i + 1] - v[j][i - 1]);
                phi[j][i] = 0;
            }
        }
        setBoundary( 0, div );
        setBoundary( 0, phi );

        solveLinearSystem( 0, phi, div, 1.0f, 4.0f );

        for ( j = 1; j <= sizeX; j++ )
        {
            for ( i = 1; i <= sizeY; i++ )
            {
                u[j][i] -= 0.5f * sizeX * (phi[j + 1][i] - phi[j - 1][i]);
                v[j][i] -= 0.5f * sizeY * (phi[j][i + 1] - phi[j][i - 1]);
            }
        }
        setBoundary( 1, u );
        setBoundary( 2, v );

    }

    /**
     * Solves a linear system of equations using the Gauss-Seidel method.
     * 
     * A = BX
     * 
     * @author Ronald Clark
     */
    private void solveLinearSystem( int boundaryType, float[][] X, float[][] A, float a, float c )
    {
        for ( int k = 0; k < numGaussSeidelIter; k++ )
        {
            for ( int i = 1; i <= sizeY; i++ )
            {
                for ( int j = 1; j <= sizeX; j++ )
                {
                    X[j][i] = (a * (X[j - 1][i] + X[j + 1][i] + X[j][i - 1] + X[j][i + 1]) + A[j][i]) / c;
                }
            }
            setBoundary( boundaryType, X );
        }
    }

    /**
     * Swap the old and new density fields.
     * 
     */
    private void swapRho()
    {
        tmp = densityNew;
        densityNew = densityOld;
        densityOld = tmp;
    }

    private void swapU()
    {
        tmp = uVelocityOld;
        uVelocityOld = uVelocityNew;
        uVelocityNew = tmp;
    }

    private void swapV()
    {
        tmp = vVelocityOld;
        vVelocityOld = vVelocityNew;
        vVelocityNew = tmp;
    }

    /**
     * Update the currernt frame's densities and velocities
     * with the input provided by the user.
     * 
     */
    private void addUserInput()
    {
    	Velocity currentVelocity = userInput.nextInputVelocity();
    	if( currentVelocity != null ) {
	    	
    	    int x_input = (int)currentVelocity.getXCoordinate();
	    	int y_input = (int)currentVelocity.getYCoordinate();
	    	
	    	if( y_input >= topRow && y_input <= (topRow + imax) ) 
	    	{  
	    	    int x = x_input;
	    	    int y = y_input - topRow;                          // Eliminate the offset
	    	    densityOld[x][y] += currentVelocity.getDensity();
	            uVelocityOld[x][y] += currentVelocity.getXComponent();
	            vVelocityOld[x][y] += currentVelocity.getYComponent();
	    	}
    	}
  
    }
    
    /**
     * Perform one simulation step. This updates the density and velocity fields.
     * 
     * (Add Forces) -> Diffuse -> Advect -> Project
     * 
     * @author Ronald Clark
     */
    public void step()
    {
    	addUserInput();
    	
        // Density
        updateSource( t, true, densityOld, densityNew );
        swapRho();

        diffuse( 0, densityOld, densityNew );
        swapRho();

        advect( 0, uVelocityNew, vVelocityNew, densityOld, densityNew );

        for ( int i = 0; i <= imax; i++ )
            for ( int j = 0; j <= jmax; j++ )
                densityOld[j][i] = 0;

        // Velocity
        updateSource( t, false, uVelocityOld, uVelocityNew );
        updateSource( t, false, vVelocityOld, vVelocityNew );

        addVorticity();

        addBouyancy( vVelocityOld );
        updateSource( t, false, vVelocityOld, vVelocityNew );

        swapU();
        diffuse( 0, uVelocityOld, uVelocityNew );

        swapV();
        diffuse( 0, vVelocityOld, vVelocityNew );

        adjustVelocity( t, uVelocityNew, vVelocityNew );
        swapU();
        swapV();

        advect( 1, uVelocityOld, vVelocityOld, uVelocityOld, uVelocityNew );
        advect( 2, uVelocityOld, vVelocityOld, vVelocityOld, vVelocityNew );

        adjustVelocity( t, uVelocityNew, vVelocityNew );

        for ( int i = 0; i <= imax; i++ )
            for ( int j = 0; j <= jmax; j++ )
                uVelocityOld[j][i] = vVelocityOld[j][i] = 0;

        t = t + dt;
        
    }
}