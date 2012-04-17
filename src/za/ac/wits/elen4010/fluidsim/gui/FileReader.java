// FileReader.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Respondible for streaming frames from the output of the simulation and passing them onto the main
 * program as RawFrames
 * @author Edward Steere
 * @see RawFrame
 */
public class FileReader
{

    // ===Private Data Members===
    
    /** Stores the name of the file to be read from*/
    private String inFilePath;
    /** Stores the object stream used as input for the program*/
    private ObjectInput inFile;

    // ===Public Methods===

    /**
     * Creates a new FileReader, bound to the given file path
     * @param filePath
     *             The name of the file that the input data for the simulation should be writtern to
     */
    public FileReader( String filePath )
    {

        inFilePath = filePath;

        try
        {
            InputStream inputStream = new FileInputStream( inFilePath );
            inFile = new ObjectInputStream( inputStream );
        }
        catch ( IOException couldntOpenInputStream )
        {
            System.err.println( "Couldn't open the simulation output file (the input file for this program)" );
            couldntOpenInputStream.printStackTrace( System.err );
        }
    }

    /**
     * Allows for the file to be cloased and then opened again under a different name/path
     * @param filePath
     *             The name of the file that the input data for the simulation should be written to
     */
    public void resetFile( String filePath )
    {

        try
        {
            inFile.close();
            InputStream inputStream = new FileInputStream( filePath );
            inFile = new ObjectInputStream( inputStream );
            inFilePath = filePath;
        }
        catch ( IOException couldntOpenInputStream )
        {
            System.err.println( "Couldn't reset to a new simulation output file (the input file for this program)" );
            couldntOpenInputStream.printStackTrace( System.err );
        }

    }

    /**
     * Gets the next frame of data as a {@link RawFrame RawFrame} and returns it to the calling pro-
     * cedure
     * @return The next frame of data stored in the file
     */
    public RawFrame readNextFrame()
    {

        try
        {
            return ( RawFrame )inFile.readObject();
        }
        catch( IOException couldntReadNextFrameFromFile )
        {
            System.err.println( "Couldn't read the next object from the simulation output file (input file for this program)" );
            couldntReadNextFrameFromFile.printStackTrace( System.err );
        }
        catch( ClassNotFoundException couldntBindInputToClass )
        {
            System.err.println( "Couldn't bind the input to the correct class: (class not found exception)" );
            couldntBindInputToClass.printStackTrace( System.err );
        }

        return null;

    }

}