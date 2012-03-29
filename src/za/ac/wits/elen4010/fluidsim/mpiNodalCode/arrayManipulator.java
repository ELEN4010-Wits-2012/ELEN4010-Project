package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import java.math.*;

class arrayManipulator
{	
    static private int processes = 1;			// Default number of processes
    static private int segmentWidth;
    static private int MPIprocesses;
	
    static public void readData( String filename, Data data )
    {
        // Read in file into 2D array. For now, I've just created a 2D array manually.
        int dataArray[][] = new int[8][8];
    	
        for (int i=0; i != 8; i++)
            for (int j=0; j != 8; j++)
                dataArray[i][j] = 3;
		
        data = new Data(dataArray);						
        }
	
    static public Data[] splitData( Data data, int processes )
    {
        //final int blocksPerSide = sqrt(processes);		// Blocks/side (assuming square)
        MPIprocesses = processes;
        segmentWidth = ( data.getXLength() )/MPIprocesses;	// If ODD, round down (ignore last element in calculations)
        Data dataObjects[] = new Data[ MPIprocesses ];		// Allocate memory for sub-arrays
        int sourceArray[][] = data.getDataArray();			// Pointer to actual data array
		
        // Package subarrays in objects using:
        // public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
        for( int i = 0; i != MPIprocesses; i++ )
        {
            int tempArray[][] = new int[segmentWidth][data.getXLength()];
            int firstSegmentRow = i*segmentWidth;			// First row of each new segment
			
            // Copy the rows of each segment, starting from the first row of each segment
            for(int j = 0; j != segmentWidth; j++)
                System.arraycopy( sourceArray[j + firstSegmentRow], 0, tempArray[j], 0, data.getXLength() );
			
            dataObjects[i] = new Data(tempArray);			// Dynamically create new data object
            dataObjects[i].setRank(i+1);
        }
			
        // Return array of objects
        return dataObjects;
    }
	
    // Function to aggregate subarrays into combined array
    static public Data aggregateData( Data[] dataObjects )
    {
        int aggregatedWidth = segmentWidth*MPIprocesses;
        int tempArray[][] = new int[aggregatedWidth][aggregatedWidth];	// Aggregated array
		
        // Copy data objects' content to combined array.
        // Assume the objects are NOT sorted according to rank
        // The rank of each object corresponds to the index: i+1
        for( int i = 0; i != MPIprocesses; i++ )
        {
            int currentRank = dataObjects[i].getRank();
            int objectArray[][] = dataObjects[i].getDataArray();
            int firstSegmentRow = (currentRank - 1)*segmentWidth;		// First row of each new segment
			
            // Copy the rows of each object array to the aggregated array
            for( int j = 0; j != segmentWidth; j++ )
                System.arraycopy( objectArray[j], 0, tempArray[j + firstSegmentRow], 0, aggregatedWidth );
        }
		
        // Create an aggregated array object
        Data newData = new Data(tempArray);
        return newData;
    }
	
}