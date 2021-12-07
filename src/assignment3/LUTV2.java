package assignment3;
import LUT.StateActionLookUpTable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LUTV2 extends StateActionLookUpTable{
    private double[][][][][] lut;
    private int[][][][][] numOfVisits;
    private int numEnergyDim1;
    private int numEnergyDim2;
    private int numDistanceDim1;
    private int numDistanceDim2;
    private int numActionDim;

    public LUTV2(int numEnergyDim1, int numEnergyDim2, int numDistanceDim1, int numDistanceDim2, int numActionDim) {
        super(numEnergyDim1, numEnergyDim2, numDistanceDim1, numDistanceDim2, numActionDim);
        this.numEnergyDim1 = numEnergyDim1;
        this.numEnergyDim2 = numEnergyDim2;
        this.numDistanceDim1 = numDistanceDim1;
        this.numDistanceDim2 = numDistanceDim2;
        this.numActionDim = numActionDim;

        lut = new double[numEnergyDim1][numEnergyDim2][numDistanceDim1][numDistanceDim2][numActionDim];
        numOfVisits = new int[numEnergyDim1][numEnergyDim2][numDistanceDim1][numDistanceDim2][numActionDim];
        this.initialiseLUT();

    }

    /* Load the LUT in a format useful for training an NN */
    public void load2NN(String fileName, double [][] inputArray, double [] expectOutput) throws IOException {

        FileInputStream inputFile = new FileInputStream(fileName);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader( inputFile ));

//        int numExpectedRows = numEnergyDim1 * numEnergyDim2 * numDistanceDim1* numDistanceDim2 * numActionDim;
        int numExpectedRows = 405;

        // Check the number of rows is compatible
        int numRows = Integer.valueOf( inputReader.readLine() );
        // Check the number of dimensions is compatible
        int numDimensions = Integer.valueOf( inputReader.readLine() );

        if ( numRows != numExpectedRows || numDimensions != 5) {
            System.out.printf (
                    "*** rows/dimensions expected is %s/%s but %s/%s encountered\n",
                    numExpectedRows, 5, numRows, numDimensions
            );
            inputReader.close();
            throw new IOException();
        }

        int row = 0;
        for (int a = 0; a < numEnergyDim1; a++) {
            for (int b = 0; b < numEnergyDim2; b++) {
                for (int c = 0; c < numDistanceDim1; c++) {
                    for (int d = 0; d < numDistanceDim2; d++) {
                        for (int e = 0; e < numActionDim; e++) {
                            int col = 0;
                            //if(col<6)continue;
                            // Read line formatted like this: <e,d,e2,d2,a,q,visits\n>
                            String line = inputReader.readLine();
                            String tokens[] = line.split(", ");
                            int dim1 = Integer.parseInt(tokens[0]);
                            int dim2 = Integer.parseInt(tokens[1]);
                            int dim3 = Integer.parseInt(tokens[2]);
                            int dim4 = Integer.parseInt(tokens[3]);
                            int dim5 = Integer.parseInt(tokens[4]); // actions
                            inputArray[row][col] = dim1;//2, 0:0, 1:0.5,2:1
                            col += 1;
                            inputArray[row][col] = dim2;
                            col += 1;
                            inputArray[row][col] = dim3;
                            col += 1;
                            inputArray[row][col] = dim4;
                            col += 1;
                            inputArray[row][col] = dim5;

                            double q = Double.parseDouble(tokens[5]);
                            expectOutput[row] = q;
                            row += 1;

                            int v = Integer.parseInt(tokens[6]);
                            lut[a][b][c][d][e] = q;

                            numOfVisits[a][b][c][d][e] = v;
                        }
                    }
                }
            }
        }
        inputReader.close();
    }


}
