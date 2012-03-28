import java.io.*;

class Data implements Serializable
{  
    private int data[][];
    private int yLength;
    private int xLength;
    private int rank;

    public Data(int array[][])
    {
        // Intitialise dimensions
        yLength = array.length;
        xLength = array[0].length;
    	
        // Allocate memory for the array copy
        data = new int[yLength][xLength];
    	   	
        // Create and store a COPY of the array
        for (int y = 0; y != yLength; y++)
            for (int x = 0; x != xLength; x++)
                data[y][x] = array[y][x];
    	
        rank = 0;		// Default rank 
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
    
    public void setRank(int Rank)
    {
        rank = Rank;
    }
    
    public int getRank()
    {
        return rank;
    }
}
