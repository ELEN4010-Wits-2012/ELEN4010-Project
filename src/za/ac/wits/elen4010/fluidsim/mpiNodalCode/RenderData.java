package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import java.io.Serializable;

/**
 * Class for serializing rendering data to be sent by MPI back to the master node.
 * 
 * @author Justin Wernick
 */
public class RenderData implements Serializable
{
    private static final long serialVersionUID = -3289105536498865383L;
    
    /** Density data for rendering region of strip */
    private float[][] density;
    /** Rank of slave node that owns this strip */
    private int sourceRank;
    /** Stores the x-length of the array */
    private int xLength;
    /** Stores the y-Length of the array */
    private int yLength;
    
    /** 
     * Constructor, makes a copy of arrays to transport.
     */
    public RenderData( float[][] density )
    {        
        this.density = new float[density.length][];
            
        for (int x = 0; x < density.length; ++x)
        {
            this.density[x] = new float[density[x].length];
            for (int y = 0; y != density[x].length; ++y)
            {
                this.density[x][y] = density[x][y];
            }
        }
        yLength = density.length;
        xLength = density[0].length;
    }
    
    /**
     * Default constructor
     */
    public RenderData()
    {
        this.density = new float[0][];
        this.sourceRank = 0;
        this.xLength = 0;
        this.yLength = 0;
    }
    
    public float[][] getDensity()
    {
        return density;
    }
    
    public int getSourceRank()
    {
        return sourceRank;
    }
    
    public void setSourceRank( int rank )
    {
        this.sourceRank = rank;
    }
    
    public int getXLength()
    {
        return xLength;
    }
    
    public int getYLength()
    {
        return yLength;
    }
}
