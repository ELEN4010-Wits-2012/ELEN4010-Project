package za.ac.wits.elen4010.fluidsim.sim;

import java.io.Serializable;

/**
 * Object for writing the rendering part of the fluid's density to a file.
 * 
 * @author Justin Wernick
 */
public class RenderingFluid implements Serializable
{
    private static final long serialVersionUID = 6211211966938359294L;
    
    private float[][] density;
    private float t;

    public void setDensity( float[][] density )
    {
        this.density = density;
    }

    public void setT( float t )
    {
        this.t = t;
    }
    
    public float[][] getDensity()
    {
        return density;
    }

    public float getT()
    {
        return t;
    }

    public RenderingFluid(int width, int height)
    {
        density = new float[width][height];
        t = 0;
    }
}
