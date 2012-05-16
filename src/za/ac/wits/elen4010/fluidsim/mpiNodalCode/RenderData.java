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
        xLength = density.length;
        yLength = density[0].length;
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
        sourceRank = rank;
    }
    
    public int getXLength()
    {
        return xLength;
    }
    
    public int getYLength()
    {
        return yLength;
    }
     public void PrintDensity()
    {
    printArray(density,"RenderData:");
    }
     public void printArray(float[][] array, String message)
    {
        System.out.println("======== Printing " + message + " array =======");
        for (int y = 0; y != array[0].length; ++y) {
            for (int x = 0; x != array.length; ++x) {       // Could also use System.arraycopy()
                System.out.print(array[x][y] + "|");
            }
            System.out.println("New row");
        }
    }
}
