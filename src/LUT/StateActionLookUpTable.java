package LUT;

import Sarb.LUTInterface;
import robocode.RobocodeFileOutputStream;

import java.io.*;
import java.util.Arrays;

public class StateActionLookUpTable implements LUTInterface {

    private double[][][][][] lut;
    private int[][][][][] numOfVisits;
    private int numEnergyDim1;
    private int numEnergyDim2;
    private int numDistanceDim1;
    private int numDistanceDim2;
    private int numActionDim;

    public StateActionLookUpTable(int numEnergyDim1,
                                  int numEnergyDim2,
                                  int numDistanceDim1,
                                  int numDistanceDim2,
                                  int numActionDim){
        this.numEnergyDim1 = numEnergyDim1;
        this.numEnergyDim2 = numEnergyDim2;
        this.numDistanceDim1 = numDistanceDim1;
        this.numDistanceDim2 = numDistanceDim2;
        this.numActionDim = numActionDim;

        lut = new double[numEnergyDim1][numEnergyDim2][numDistanceDim1][numDistanceDim2][numActionDim];
        numOfVisits = new int[numEnergyDim1][numEnergyDim2][numDistanceDim1][numDistanceDim2][numActionDim];
        this.initialiseLUT();

    }

    @Override
    public double outputFor(double[] X) {
        return 0;
    }

    @Override
    public double train(double[] X, double argValue) {
        return argValue;
    }

    @Override
    public void save(File argFile) {
        PrintStream file = null;

        try {
            file = new PrintStream(new RobocodeFileOutputStream(argFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        file.println(numEnergyDim1 * numEnergyDim2 * numDistanceDim1 * numDistanceDim2 * numActionDim);
        file.println(5);

        for (int A = 0; A < numEnergyDim1; A++) {
            for (int B = 0; B < numEnergyDim2; B++) {
                for (int C = 0; C < numDistanceDim1; C++) {
                    for (int D = 0; D < numDistanceDim2; D++) {
                        for (int E = 0; E < numActionDim; E++) {
                            String row = String.format("%d, %d, %d, %d, %d, %2.5f, %d",
                                    A, B, C, D, E,
                                    lut[A][B][C][D][E],
                                    numOfVisits[A][B][C][D][E]
                            );
                            file.println(row);
                        }
                    }
                }
            }
        }
        file.close();
    }

    @Override
    public void load(String argFileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(argFileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        int expectedNumOfRows = numEnergyDim1 * numEnergyDim2 * numDistanceDim1 * numDistanceDim2 * numActionDim;
        int actualNumOfRows = Integer.valueOf(bufferedReader.readLine());
        int actualNumOfDimensions = Integer.valueOf(bufferedReader.readLine());

        if (actualNumOfRows != expectedNumOfRows || actualNumOfDimensions != 5) {
            System.out.printf("Actual numbers of rows and dimensions are %s and %s, while %s and 5 are expected",
                    actualNumOfRows, actualNumOfDimensions, expectedNumOfRows);
            bufferedReader.close();
            throw new IOException();
        }

        for (int A = 0; A < numEnergyDim1; A++) {
            for (int B = 0; B < numEnergyDim2; B++) {
                for (int C = 0; C < numDistanceDim1; C++) {
                    for (int D = 0; D < numDistanceDim2; D++) {
                        for (int E = 0; E < numActionDim; E++) {

                            String title = bufferedReader.readLine();
                            String[] arr = title.split(",");

//                            int energyDim1 = Integer.parseInt(arr[0]);
//                            int energyDim2 = Integer.parseInt(arr[1]);
//                            int distanceDim1 = Integer.parseInt(arr[2]);
//                            int distanceDim2 = Integer.parseInt(arr[3]);
//                            int actionDim = Integer.parseInt(arr[4]);

                            int Q = Integer.parseInt(arr[5]);
                            int visit = Integer.parseInt(arr[6]);

                            lut[A][B][C][D][E] = Q;
                            numOfVisits[A][B][C][D][E] = visit;
                        }
                    }
                }
            }
        }
        bufferedReader.close();
    }

    /**
     * Initialise the look up table to all zeros.
     */
    @Override
    public void initialiseLUT() {
    }


    /**
     *  A helper method that translates a vector being used to index the look up table
     *  into an ordinal that can then be used to access the associated look up table element
     * @param X  The state action vector used to index the LUT
     * @return  The index where this vector maps to
     */
    @Override
    public int indexFor(double[] X) {
        return 0;
    }

    public double getValueQ(int stateAction1, int stateAction2, int stateAction3, int stateAction4, int stateAction5){
        return lut[stateAction1][stateAction2][stateAction3][stateAction4][stateAction5];
    }

    public double getBestValueQ(int state1, int state2, int state3, int state4){
        double[] allAvailable = lut[state1][state2][state3][state4];
        double bestQ = allAvailable[0];
        for(int i = 1; i<allAvailable.length; i++){
            bestQ = Math.max(bestQ, allAvailable[i]);
        }
        return bestQ;

    }


}
