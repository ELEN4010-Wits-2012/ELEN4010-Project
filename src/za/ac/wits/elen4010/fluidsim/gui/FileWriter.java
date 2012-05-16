// FileWriter.java

package za.ac.wits.elen4010.fluidsim.gui;

// Standard dependancies
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Responsible for accepting objects and printing them to the output file specified. In the
 * {@link GUI GUI} it handles writing of type {@link SimulationInput SimulationInput} and writing th-
 * em to the specified file
 * @author Edward Steere
 * @see SimulationInput
 */
public class FileWriter<T>
{

    // ===Private Data Members===

    /** Stores the name of the file to be written to*/
    private String outFilePath;
    /** Stores the object stream used for output*/
    private ObjectOutputStream outFile;
    

    // ===Public Methods===

    /**
     * Creates a new FileWriter, bound to the given file path
     * @param filePath
     *             The name of the file that the input data for the simulation should be writtern to
     */
    public FileWriter( String filePath )
    {

        outFilePath = filePath;

        try
        {
            OutputStream outputStream = new FileOutputStream( outFilePath );
            outFile = new ObjectOutputStream( outputStream );
        }
        catch ( IOException couldntOpenOutputStream )
        {
            System.err.println( "Couldn't open simulation input file (the output file for this program)" );
            couldntOpenOutputStream.printStackTrace( System.err );
        }

    }

    /**
     * Allows for the file to be closed and then opened again under a different name/path
     * @param filePath
     *             The name of the file that the input data for the simulation should be written to
     */
    public void resetFile( String filePath )
    {

        try
        {
            outFile.close();
            OutputStream outputStream = new FileOutputStream( filePath );
            outFile = new ObjectOutputStream( outputStream );
            outFilePath = filePath;
        }
        catch ( IOException couldntOpenOutputStream )
        {
            System.err.println( "Couldn't reset to a new simulation input file (the output file for this program)" );
            couldntOpenOutputStream.printStackTrace( System.err );
        }

    }

    /**
     * Accepts an object and writes it to the end of the {@link outFile outFile} stream
     * @param inputData
     *             A SimulationInput object to be written to the file
     */
    public void writeSimulationData( T inputData )
    {

        try
        {
           // outFile.writeObject( inputData );
           outFile.reset();
           outFile.writeUnshared( inputData );
           System.out.println("WRITE INITIAL PRINT: " + inputData);
            outFile.flush();
        }
        catch ( IOException couldntWriteNewObject )
        {
            System.err.println( "Couldn't write a new object to the simulation input file (the output file for this program)" );
            couldntWriteNewObject.printStackTrace( System.err );
        }

    }

    /**
     * Returns the path to the file currently loaded by the file reader
     * @return The path to the currently loaded file
     */
    public String getFilePath()
    {

        return outFilePath;

    }

}
