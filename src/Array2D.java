import java.io.*;

class Array2D implements Serializable
{  
    int row = 10;
    int col = 10;
    int myArray[][] = new int [row][col];

    public Array2D()
    {
        for( int i = 0; i != row; i++ )
            for ( int j = 0; j != col; j++ )
                myArray[i][j] = 42;
    }           
}
