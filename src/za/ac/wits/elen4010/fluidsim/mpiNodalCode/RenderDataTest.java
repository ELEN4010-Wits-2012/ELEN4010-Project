package za.ac.wits.elen4010.fluidsim.mpiNodalCode;

import static org.junit.Assert.*;

import org.junit.Test;

/** 
 * Unit Tests for RenderData class
 * 
 * @author Justin Wernick
 */
public class RenderDataTest
{

    @Test
    public void testRenderData()
    {
        float[][] test = {{1,2,3,4,5},{6,7,8,9},{10,11}};
        RenderData testData = new RenderData(test);
        
        for (int x=0; x<test.length; ++x)
        {
            assertArrayEquals( test[x], testData.getDensity()[x], 0 );
        }
        
    }

    @Test
    public void testSourceRank()
    {
        float[][] test = {{1,2,3,4,5},{6,7,8,9},{10,11}};
        RenderData testData = new RenderData(test);
        
        testData.setSourceRank( 5 );
        assertEquals( 5, testData.getSourceRank() );
    }

}
