import java.io.*;

public class FluidData implements java.io.Serializable
{	
    int sizeY, sizeX;
	int innerSizeX, innerSizeY;
    int offset;
    
    /** The type of segment: 1-left, 2-middle, 3-right */
	int numGaussSeidelIterations;
	
	float viscosity[];
	float densityOld[][];
	float densityNew[][];
	float tmp[][];
	float xVelocityOld[][];
	float xVelocityNew[][];
	float yVelocityOld[][];
	float yVelocityNew[][];
	float curl[][];
	float source[][];
	float time;
    float dt = 0.2f;
    
    /**
    * Constructor for the FluidData class. This initialises all
    * the fields and the simulation paramters. Each FluidSimulation
    * instance needs a FluidData object to store its state.
    *
    * Note that the size of each field (density, xVelocity..) is 
    * equal to the size of the entire simulation. The parameters offset
    * and stripSize are used to specify the actual segment to be simulated.
    * Example: if a 512 x 512 simulation is being carried out and it is to 
    * be split among 32 instances, then the parameters would be set as follows:
    *
    * Machine | width | height | stripSize | offset
    * 1         512     512      16          0   
    * 2         512     512      16          16
    * 3         512     512      16          32
    * ...
    *
    * @param width the horizontal resolution of the whole simulation in pixels
    * @param height the vertical resolution of the whole simulation in pixels
    * @param stripSize the size of the strip that this simulation instance is to simulate
    * @param dt the step time per frame (in seconds)
    * @param offset the x position at which the simulation segment starts
    */
    FluidData(int width, int height, int stripSize, float dt, int offset)
    {          
        this.sizeX = width;
        this.sizeY = height;
        this.offset = offset;
        this.innerSizeX = stripSize;
        this.innerSizeY = sizeY-1;
        
        this.dt = dt;
        
        viscosity = new float[]{0,0};
        densityOld = new float[sizeX+1][sizeY+1];
        densityNew = new float[sizeX+1][sizeY+1];
        tmp = new float[sizeX+1][sizeY+1];
        xVelocityOld = new float[sizeX+1][sizeY+1];
        xVelocityNew = new float[sizeX+1][sizeY+1];
        yVelocityOld = new float[sizeX+1][sizeY+1];
        yVelocityNew = new float[sizeX+1][sizeY+1];
        curl = new float[sizeX+1][sizeY+1];
        source = new float[sizeX+1][sizeY+1];
        
        for( int j = 1; j<=sizeX; j++ )
		{
			for( int i=1; i<=sizeY; i++ )
			{
                densityOld[j][i] = 0.0f;
                densityNew[j][i] = 0.0f;
                tmp[j][i] = 0.0f;
                xVelocityOld[j][i] = 0.0f;
                xVelocityNew[j][i] = 0.0f;
                yVelocityOld[j][i] = 0.0f;
                yVelocityNew[j][i] = 0.0f;
                curl[j][i] = 0.0f;
                source[j][i] = 0.0f;
            }
        }
    }
    
    FluidData( FluidData newFluidData )
    {
        this(newFluidData.sizeX, newFluidData.sizeY, newFluidData.innerSizeX, newFluidData.dt, newFluidData.offset);
        copy( newFluidData );
    }   
    public float getDensity(int x, int y)
    {
        return densityNew[x][y];
    }
    public float getXVelocity(int x, int y)
    {
        return xVelocityNew[x][y];
    }
    public float getYVelocity(int x, int y)
    {
        return yVelocityNew[x][y];
    }
    public void setDensity(int x, int y, float value)
    {
        densityOld[x][y] = densityNew[x][y]=value;
    }
    /**
    * Serializes and returns the simulation data.
    *
    * @return a byte array containing all the simulation data
    */
    public byte[] getByteData()
    {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
            ObjectOutput out = new ObjectOutputStream(bos);    
            out.writeObject(this); 
            bytes = bos.toByteArray(); 
             
            out.close(); 
            bos.close(); 
        } catch(IOException e) {}
        
        return bytes;
    }
    
    /**
    * Updates the simulation data using a byte stream. The bytes are
    * required to be a serialized FluidData object.
    *
    * @param bytes a byte array containing the simulation data
    * @return a FluidData object
    */
    public void setByteData(byte[] bytes)
    { 
        Object data = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes); 
            ObjectInput in = new ObjectInputStream(bis); 
            data = in.readObject();  
            
            bis.close(); 
            in.close(); 
        } catch(Exception e) {}
        
        copy((FluidData)data);
       
    }
    
    public void copy(FluidData newData)
    {
        for( int j = 0; j<=sizeX; j++ )
		{
			for( int i=0; i<=sizeY; i++ )
			{
                densityOld[j][i] = newData.densityOld[j][i];
                densityNew[j][i] = newData.densityNew[j][i];
                tmp[j][i] = newData.tmp[j][i];
                xVelocityOld[j][i] = newData.xVelocityOld[j][i];
                xVelocityNew[j][i] = newData.xVelocityNew[j][i]; 
                yVelocityOld[j][i] = newData.yVelocityOld[j][i];
                yVelocityNew[j][i] = newData.yVelocityNew[j][i]; 
                curl[j][i] = newData.curl[j][i];
                source[j][i] = newData.source[j][i];
            }
        } 
    }
    
    public void join(byte[][] bytes)
    {
        FluidData fluidData[] = new FluidData[ bytes.length ];
        
        for( int i=0; i<bytes.length; i++)
        {
            Object data = null;
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes[i]); 
                ObjectInput in = new ObjectInputStream(bis); 
                data = in.readObject();  
                
                bis.close(); 
                in.close(); 
            } catch(Exception e) {System.err.println("Byte read error");}
            
            fluidData[i] = new FluidData((FluidData) data);
            
        }
 
        int segment = 0;
        for( int j = 1; j<sizeX; j++ )
		{
            segment = (j*fluidData.length)/innerSizeX;
            
			for( int i=1; i<=sizeY; i++ )
			{
                densityOld[j][i] = fluidData[segment].densityOld[j][i];
                densityNew[j][i] = fluidData[segment].densityNew[j][i];
                tmp[j][i] = fluidData[segment].tmp[j][i];
                xVelocityOld[j][i] = fluidData[segment].xVelocityOld[j][i];
                xVelocityNew[j][i] = fluidData[segment].xVelocityNew[j][i]; 
                yVelocityOld[j][i] = fluidData[segment].yVelocityOld[j][i];
                yVelocityNew[j][i] = fluidData[segment].yVelocityNew[j][i]; 
                curl[j][i] = fluidData[segment].curl[j][i];
                source[j][i] = fluidData[segment].source[j][i];
            }
        }        
    }
}