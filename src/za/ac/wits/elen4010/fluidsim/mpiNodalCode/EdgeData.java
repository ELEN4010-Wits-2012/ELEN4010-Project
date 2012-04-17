package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import java.io.Serializable;

/**
 * Class for serializing rendering data to be sent by MPI back to the master node.
 * 
 * @author Justin Wernick
 */
public class EdgeData implements Serializable
{
    private static final long serialVersionUID = 5789888438571080030L;

    /** Density data for edge */
    private float[][] density;
    /** Horizontal component of velocity data for edge */
    private float[][] horizontalVelocity;
    /** Vertical component of velocity data for edge */
    private float[][] verticalVelocity;
    
    /** 
     * Constructor, makes a copy of arrays to transport.
     */
    public EdgeData( float[][] density, float[][] horizontalVelocity, float[][] verticalVelocity )
    {
        if (density.length != horizontalVelocity.length) throw new RuntimeException("Arrays must be same size");
        if (density.length != verticalVelocity.length) throw new RuntimeException("Arrays must be same size");
        
        
        this.density = new float[density.length][];
        this.horizontalVelocity = new float[density.length][];
        this.verticalVelocity = new float[density.length][];
            
        for (int x = 0; x < density.length; ++x)
        {
            if (density[x].length != horizontalVelocity[x].length) throw new RuntimeException("Arrays must be same size");
            if (density[x].length != verticalVelocity[x].length) throw new RuntimeException("Arrays must be same size");
            
            this.density[x] = new float[density[x].length];
            this.horizontalVelocity[x] = new float[density[x].length];
            this.verticalVelocity[x] = new float[density[x].length];
            
            for (int y = 0; y != density[x].length; ++y)
            {
                this.density[x][y] = density[x][y];
                this.horizontalVelocity[x][y] = horizontalVelocity[x][y];
                this.verticalVelocity[x][y] = verticalVelocity[x][y];
            }
        }
 
    }
    
    
    public float[][] getDensity()
    {
        return density;
    }
    
    public float[][] getHorizontalVelocity()
    {
        return horizontalVelocity;
    }

    public float[][] getVerticalVelocity()
    {
        return verticalVelocity;
    }

}
