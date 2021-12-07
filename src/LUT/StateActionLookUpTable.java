package LUT;

import Sarb.LUTInterface;
import robocode.RobocodeFileOutputStream;

import java.io.*;
import java.util.Arrays;

public class StateActionLookUpTable implements LUTInterface {

    private double[][][][][] lut;
    private int[][][][][] numOfVisits;

    public double[][][][][] getLut() {
        return lut;
    }

    public void setLut(double[][][][][] lut) {
        this.lut = lut;
    }

    public int[][][][][] getNumOfVisits() {
        return numOfVisits;
    }

    public void setNumOfVisits(int[][][][][] numOfVisits) {
        this.numOfVisits = numOfVisits;
    }

    public int getNumEnergyDim1() {
        return numEnergyDim1;
    }

    public void setNumEnergyDim1(int numEnergyDim1) {
        this.numEnergyDim1 = numEnergyDim1;
    }

    public int getNumEnergyDim2() {
        return numEnergyDim2;
    }

    public void setNumEnergyDim2(int numEnergyDim2) {
        this.numEnergyDim2 = numEnergyDim2;
    }

    public int getNumDistanceDim1() {
        return numDistanceDim1;
    }

    public void setNumDistanceDim1(int numDistanceDim1) {
        this.numDistanceDim1 = numDistanceDim1;
    }

    public int getNumDistanceDim2() {
        return numDistanceDim2;
    }

    public void setNumDistanceDim2(int numDistanceDim2) {
        this.numDistanceDim2 = numDistanceDim2;
    }

    public int getNumActionDim() {
        return numActionDim;
    }

    public void setNumActionDim(int numActionDim) {
        this.numActionDim = numActionDim;
    }

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


    /**
     * @param X The input vector. An array of doubles.
     * @return The value returned by th LUT or NN for this input vector
     */
    @Override
    public double outputFor(double[] X) {
        int idx1 = (int) X[0];
        int idx2 = (int) X[1];
        int idx3 = (int) X[2];
        int idx4 = (int) X[3];
        int idx5 = (int) X[4];
        return lut[idx1][idx2][idx3][idx4][idx5];
    }

    @Override
    public double train(double[] X, double argValue) {
        int idx1 = (int) X[0];
        int idx2 = (int) X[1];
        int idx3 = (int) X[2];
        int idx4 = (int) X[3];
        int idx5 = (int) X[4];

        lut[idx1][idx2][idx3][idx4][idx5] = argValue;
        numOfVisits[idx1][idx2][idx3][idx4][idx5] ++;
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
    public void initialiseLUT
      () {

        for(int A = 0; A < numEnergyDim1; A += 1){
            for(int B = 0; B < numEnergyDim2; B += 1){
                for(int C = 0; C < numDistanceDim1; C += 1){
                    for(int D = 0; D < numDistanceDim2; D += 1){
                        for(int E = 0; E < numActionDim; E += 1){
                            lut[A][B][C][D][E] = Math.random();
                        }
                    }
                }
            }
        }
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
