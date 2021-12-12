package assignment3;
import LUT.StateActionLookUpTable;

import java.io.*;

public class LUTV2 extends StateActionLookUpTable{
//    private double[][][][][][] lut;
//    private int[][][][][][] numOfVisits;
//
//    private int numEnergyDim1;
//    private int numEnergyDim2;
//    private int numDistanceDim1;
//    private int numPosition1;
//    private int numPosition2;
//    private int numActionDim;

    public LUTV2(int numEnergyDim1,
                                  int numEnergyDim2,
                                  int numDistanceDim1,
                                  int numPosition1,
                                  int numPosition2,
                                  int numActionDim){
        super(numEnergyDim1, numEnergyDim2, numDistanceDim1, numPosition1, numPosition2,numActionDim);
//        this.numEnergyDim1 = numEnergyDim1;
//        this.numEnergyDim2 = numEnergyDim2;
//        this.numDistanceDim1 = numDistanceDim1;
//        this.numPosition1 = numPosition1;
//        this.numPosition2 = numPosition2;
//        this.numActionDim = numActionDim;
//
//        lut = new double[numEnergyDim1][numEnergyDim2][numDistanceDim1][numPosition1][numPosition2][numActionDim];
//        numOfVisits = new int[numEnergyDim1][numEnergyDim2][numDistanceDim1][numPosition1][numPosition2][numActionDim];
//        this.initialiseLUT();

    }

    /* Load the LUT in a format useful for training an NN */
    public void load2NN(String fileName, double [][] inputArray, double [] expectOutput) throws IOException {

        FileInputStream inputFile = new FileInputStream(fileName);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader( inputFile ));

        //int numExpectedRows = numEnergyDim1 * numEnergyDim2 * numDistanceDim1* numPosition1*numPosition2 * numActionDim;
//        int numExpectedRows = 405;

        // Check the number of rows is compatible
        int numRows = Integer.valueOf( inputReader.readLine() );
        // Check the number of dimensions is compatible
        int numDimensions = Integer.valueOf( inputReader.readLine() );
//
////        if ( numRows != numExpectedRows || numDimensions != 5) {
//            System.out.printf (
//                    "*** rows/dimensions expected is %s/%s but %s/%s encountered\n",
//                    numExpectedRows, 7, numRows, numDimensions
//            );
////            inputReader.close();
//            throw new IOException();
//        }

        int row = 0;
        for (int a = 0; a < this.numEnergyDim1; a++) {
            for (int b = 0; b < numEnergyDim2; b++) {
                for (int c = 0; c < numDistanceDim1; c++) {
                    for (int d = 0; d < numPosition1; d++) {
                        for (int e = 0; e < numPosition2; e++) {
                            for (int action = 0; action < numActionDim; action++) {
                                int col = 0;
                                //if(col<6)continue;
                                // Read line formatted like this: <e,d,e2,d2,a,q,visits\n>
                                String line = inputReader.readLine();
//                                System.out.println(line);
                                String tokens[] = line.split(",");
                               /* System.out.println(tokens.length);*/

                                int dim1 = Integer.parseInt(tokens[0]);
                                int dim2 = Integer.parseInt(tokens[1]);
                                int dim3 = Integer.parseInt(tokens[2]);
                                int dim4 = Integer.parseInt(tokens[3]);
                                int dim5 = Integer.parseInt(tokens[4]);
                                int dim6 = Integer.parseInt(tokens[5]);// actions
                                inputArray[row][col] = dim1;//2, 0:0, 1:0.5,2:1
                                col += 1;
                                inputArray[row][col] = dim2;
                                col += 1;
                                inputArray[row][col] = dim3;
                                col += 1;
                                inputArray[row][col] = dim4;
                                col += 1;
                                inputArray[row][col] = dim5;
                                col += 1;
                                inputArray[row][col] = dim6;

                                double q = Double.parseDouble(tokens[6]);
                                expectOutput[row] = q;
                                row += 1;

                                int v = Integer.parseInt(tokens[7]);
                                lut[a][b][c][d][e][action] = q;

                                numOfVisits[a][b][c][d][e][action] = v;
                            }
                        }
                    }
                }
            }
        }
        inputReader.close();
    }


    public static int load(String filename, double [][] trainInput, double [] trainOutput) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        int z = 0;
        double minQ = Double.MAX_VALUE;
        double maxQ = Double.MIN_VALUE;
        try {
            for (int i = 0; i < trainInput.length; i++) {
                String splitLine[] = line.split("\t");
                int accessCnt = Integer.parseInt(splitLine[2]);
                if (accessCnt > 0) {
                    trainInput[z][0] = Double.parseDouble(splitLine[0].substring(0,1)) + 1;
                    trainInput[z][1] = Double.parseDouble(splitLine[0].substring(1,2)) + 1;
                    trainInput[z][2] = Double.parseDouble(splitLine[0].substring(2,3)) + 1;
                    trainInput[z][3] = Double.parseDouble(splitLine[0].substring(3,4)) + 1;
                    trainInput[z][4] = Double.parseDouble(splitLine[0].substring(4,5)) + 1;
                    trainOutput[z] = Double.parseDouble(splitLine[1]);
                    if (trainOutput[z] < minQ) {
                        minQ = trainOutput[z];
                    }
                    if (trainOutput[z] > maxQ) {
                        maxQ = trainOutput[z];
                    }
                    z++;
                }
                line = reader.readLine();
            }

            // Normalize the Q-values to {-1, 1}
            for (int i = 0; i < z; i++) {
                trainOutput[i] = (trainOutput[i] - minQ) * 2 / (maxQ - minQ) - 1;
                //System.out.println("expect: "+trainOutput[i] );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }

        return z;
    }


}
