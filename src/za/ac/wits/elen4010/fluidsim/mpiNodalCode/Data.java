package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import java.io.*;

class Data implements Serializable
{  

	private static final long serialVersionUID = -7811642495385252127L;
	private int data[][];
    private int yLength;
    private int xLength;


    public Data(int array[][])
    {
        // Initialise dimensions
        yLength = array.length;
        xLength = array[0].length;
    	
        // Allocate memory for the array copy
        data = new int[yLength][xLength];
    	   	
        // Create and store a COPY of the array locally
        for (int y = 0; y != yLength; y++)
            for (int x = 0; x != xLength; x++)
                data[y][x] = array[y][x];
    	
 
    }     
    
    public int getXLength()
    {
        return xLength;
    }
    
    public int getYLength()
    {
        return yLength;
    }
    
    public int[][] getDataArray()
    {
        return data;
    }
    
    public int getRank()
    {
    	return -1;
    }
    public void setRank(int rank)
    {
    	
    }
}
