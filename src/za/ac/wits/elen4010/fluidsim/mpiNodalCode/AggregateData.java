  
 package za.ac.wits.elen4010.fluidsim.mpiNodalCode;
 import za.ac.wits.elen4010.fluidsim.gui.*;
 
 class AggregateData
 {
  private  float tempArray[][] = new float[640][480];
  private RawFrame  localRawFrame = new RawFrame(tempArray); 
    AggregateData()
     {
 
    }
    
 public RawFrame aggregateData( RenderData[] stripArray, int threadCount )
    {
        int segmentHeight = stripArray[0].getYLength();                    
        System.out.println("-----> Segment height = " + segmentHeight);
        int segmentWidth = stripArray[0].getXLength();
        System.out.println("-----> Segment Width = " + segmentWidth);
        int aggregatedHeight = segmentHeight*(threadCount-1);               // There are threadCount-1 slave nodes
        System.out.println("-----> Aggregated Height = " + aggregatedHeight);     
        //float tempArray[][] = new float[aggregatedHeight][segmentWidth];    // Aggregated array
  
        
        // Copy data objects' content to combined array.
        // Assume the objects are NOT sorted according to rank
        // The rank of each object corresponds to the index: i+1
        for( int i = 0; i != (threadCount-1); i++ )
        {
            int currentRank = stripArray[i].getSourceRank();
            System.out.println("-----> Aggregating from rank = " + currentRank);
            float density[][] = stripArray[i].getDensity();
            int firstSegmentRow = (currentRank - 1)*segmentHeight;       // First row of each new segment
            
            // Copy the rows of each object array to the aggregated array
            for( int y = 0; y != segmentHeight; y++ )
            {
                for ( int x = 0; x != segmentWidth; x++ )
                    tempArray[x][y+firstSegmentRow] = density[x][y];
            }
        }
        //tempArray = transpose(tempArray);
        System.out.println("-----> Finished aggregation = ");
        
        // Return the aggregated array object
        localRawFrame.setFrame(tempArray);
        
        if(localRawFrame.getFrame() != tempArray)
        {
            System.out.println("THIS IS A MAJOR PROBLEM - ARRAYS NOT BEING SET");
        }
        else
        {
        System.out.println("FRAMES ARE EQUAL");
        }
        
        return localRawFrame;
        
    }
 }
