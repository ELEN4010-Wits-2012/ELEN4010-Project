package za.ac.wits.elen4010.fluidsim.tests;

import za.ac.wits.elen4010.fluidsim.mpiNodalCode.EdgeData;

import static org.junit.Assert.*;

import org.junit.Test;

/** 
 * Unit Tests for EdgeData class
 * 
 * @author Justin Wernick
 */
public class EdgeDataTest
{

    @Test
    public void testEdgeData()
    {
        float[][] density = {{1,2,3,4,5},{6,7,8}};
        float[][] horVelocity = {{9,10,11,12,13},{14,15,16}};
        float[][] verVelocity = {{17,18,19,20,21},{22,23,24}};
        EdgeData testData = new EdgeData( density, horVelocity, verVelocity );
        
        for (int x=0; x<density.length; ++x)
        {
            assertArrayEquals( density[x], testData.getDensity()[x], 0 );
            assertArrayEquals( horVelocity[x], testData.getHorizontalVelocity()[x], 0 );
            assertArrayEquals( verVelocity[x], testData.getVerticalVelocity()[x], 0 );
        }
    }
    
    @Test
    public void testEdgeOuterCrash()
    {
        float[][] density = {{1,2,3,4,5},{6,7,8}};
        float[][] horVelocity = {{9,10,11,12,13},{14,15,16}, {1}};
        float[][] verVelocity = {{17,18,19,20,21},{22,23,24}};
        try
        {
            EdgeData testData = new EdgeData( density, horVelocity, verVelocity );
            fail( "Exception should have been thrown " );
        }
        catch (RuntimeException e)
        {
        }
        
    }
    
    @Test
    public void testEdgeInnerCrash()
    {
        float[][] density = {{1,2,3,4,5},{6,7,8}};
        float[][] horVelocity = {{9,10,11,12,13},{14,15,16}};
        float[][] verVelocity = {{17,19,20,21},{22,23,24}};
        
        try
        {
            EdgeData testData = new EdgeData( density, horVelocity, verVelocity );
            fail( "Exception should have been thrown " );
        }
        catch (RuntimeException e)
        {
        }
    }

}
