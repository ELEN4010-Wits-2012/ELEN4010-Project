package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import java.io.*;

class Data implements Serializable
{  
<<<<<<< HEAD
    /**
     * 
     */
    private static final long serialVersionUID = -7811642495385252127L;
    private float density[][];
    private float uVelocity[][];
    private float vVelocity[][];
=======

	private static final long serialVersionUID = -7811642495385252127L;
	private int data[][];
>>>>>>> 191467cd4826d927cda911ff466165ccce60002a
    private int yLength;
    private int xLength;
    private int Rank ;
    


    public void  setData(float d[][],float uVel[][],float vVel[][], int rank )
    {
        
        
        
        // Initialise dimensions
        yLength = d.length;
        xLength = d[0].length;
        Rank = rank ;
        // Allocate memory for the array copy
        density = new float[yLength][xLength];
        uVelocity = new float[yLength][xLength];
        vVelocity = new float[yLength][xLength];
        
       
        // Create and store a COPY of the array locally
        for (int y = 0; y != yLength; y++)
            for (int x = 0; x != xLength; x++)
            {
                density[y][x] = d[y][x];
                uVelocity[y][x] = uVel[y][x];
                vVelocity[y][x] =vVel[y][x];
            }
 
    }     
    
    public int getXLength()
    {
        return xLength;
    }
    
    public int getYLength()
    {
        return yLength;
    }
    
    public float[][] getuVelocity()
    {
        return uVelocity;
    }
<<<<<<< HEAD
    public float[][] getvVelocity()
    {
        return vVelocity;
    }
    public float[][] getDensity()
    {
        return density;
    }
    public int getRank()
    {
        return Rank;
    }



=======
    
    public int getRank()
    {
    	return -1;
    }
    public void setRank(int rank)
    {
    	
    }
>>>>>>> 191467cd4826d927cda911ff466165ccce60002a
}
