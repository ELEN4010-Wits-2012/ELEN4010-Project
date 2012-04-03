package za.ac.wits.elen4010.fluidsim.mpiNodalCode;
<<<<<<< HEAD
=======

>>>>>>> 5aa0af3559b96534d77f710b04755008fa4cb79c
class Test {
    
    public static void main(String[] args) {
	    
	    final int rows = 8, cols = 8;
		
	    // "Read in" Data. The Data array can be rectangular
	    int dataArray[][] = new int[rows][cols];
    	
	    for (int i=0; i != rows; i++)
	        for (int j=0; j != cols; j++)
	            dataArray[i][j] = 3;
    	
	    Data data = new Data(dataArray);	
    	
	    // Display data
	    Test.displayArray("Initial 2D array :", data);
    	
	    // Split up data between 4 processes
	    int processes = 4;
	    Data dataObjectArray[] = new Data[processes];
	    dataObjectArray = arrayManipulator.splitData(data, processes);
    	
	    // Display Data sets
	    for(int i = 0; i != processes; i++)
	    {
	        System.out.println("Initial dataset values for rank " + dataObjectArray[i].getRank());
	        Test.displayArray("Data set value: ", dataObjectArray[i]);
	    }
    	
	    // Aggregate Data
	    Data aggregatedData = arrayManipulator.aggregateData(dataObjectArray);
    	
	    // Display aggregated data
	    Test.displayArray("Aggregated 2D array :", aggregatedData);
    	
	}
    
    public static void displayArray(String message, Data data)
    {
        for (int i=0; i != data.getYLength(); i++)
            for (int j=0; j != data.getXLength(); j++)
                System.out.println(message + data.getDataArray()[i][j] );
    }
}