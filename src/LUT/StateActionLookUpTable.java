package LUT;

import Sarb.LUTInterface;

import java.io.File;
import java.io.IOException;
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

        return argValue;
    }

    @Override
    public void save(File argFile) {

    }

    @Override
    public void load(String argFileName) throws IOException {

    }

    /**
     * Initialise the look up table to all zeros.
     */
    @Override
    public void initialiseLUT() {
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
