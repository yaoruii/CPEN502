package assignment3;
import Robot.Distance;
import Robot.Energy;
import Robot.PositionX;
import Robot.PositionY;
import assignment1.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class NeuralNetRunnerV2 {
    public static void main(String[] args) throws IOException {
        int argA = -1;
        int argB = 1;


        int energyNum = Energy.values().length; // Number of types of HP
        int distanceNum = Distance.values().length ; // Number of types of distance
        int positionXNum = PositionX.values().length;
        int positionYNum = PositionY.values().length;
        int actionNum = 5; // 5 kinds of actions in this project
        int numExpectedRows = energyNum * energyNum * distanceNum* positionXNum*positionYNum * actionNum;
        int numExpectedCols = 6;

//        int numExpectedRows = 8 * 6 * 4 * 4 * 5;
//        int numExpectedCols = 5;  // 5 states + 1 action = 6

        int inputNum = numExpectedCols;
        int outputNum = 1;
        int hiddenNum = 42;

        double learningRate = 0.001;
        double momentum = 0.7;
        double acceptError = 0.1;//5000 -> 5:
        double minOutput, maxOutput;

        boolean useBipolar = true;

        /* Define input array and output array respectively */
        double [][] inputArray = new double[numExpectedRows][numExpectedCols];
        double [] expectOutput = new double[numExpectedRows];

        /* The array which contains the maximum value for each input */
        int [] inputMaxArray = {energyNum- 1, energyNum - 1,  distanceNum - 1,
                PositionX.values().length - 1, PositionY.values().length - 1, actionNum-1};

        /* Initialize a Look-up Table and load the value got in last assignment */
        LUTV2 LUT = new LUTV2(energyNum, energyNum, distanceNum, positionXNum, positionYNum,actionNum);
        //LUTV2.load("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/src/assignment3/luttest.txt", inputArray, expectOutput);
        LUT.load2NN("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/src/assignment3/myLUT_1210.dat", inputArray, expectOutput);

        //normalizeInput(inputArray, inputMaxArray, argA, argB, numExpectedRows, numExpectedCols);
        maxOutput = getMaxOutput(expectOutput, numExpectedRows);
        minOutput = getMinOutput(expectOutput, numExpectedRows);
        normalizeOutput(expectOutput, maxOutput, minOutput, argA, argB, numExpectedRows);



//        for(double learningRateite = learningRate;learningRateite<0.5; learningRateite += 0.05 ){
          for(int hiddenNumite = hiddenNum; hiddenNumite<43; hiddenNumite +=6){
            NeuralNet neuralNet = new NeuralNet(inputNum, hiddenNum, outputNum,learningRate, momentum,
                    argA, argB,useBipolar,-0.5, 0.5);


            neuralNet.initializeWeights();
            neuralNet.zeroWeights();

            int epoch = 0;
            double error = 0.0;
            FileWriter fw = new FileWriter("./src/assignment3/LR1211"+ "_maybe_best" + ".txt");
            while (epoch == 0 || error > acceptError) {
//        while (epoch < maxEpoch) { /* Used when testing the hyper parameters */
                error = 0.0;
                /* The for loop indicates the algorithm for one epoch */
                for (int index = 0; index < inputArray.length; index++) {
                    double[] input = inputArray[index]; // switch between binary test and bipolar test here
                    double output = expectOutput[index]; //switch between binary test and bipolar test here

                    error += neuralNet.train(input, output);
                }

                //System.out.println(error);
                double rms = Math.pow((2*error/inputArray.length), 0.5);
                //System.out.println("Error at epoch " + epoch + " is " + rms);
                //write the result to a txt file:
                try{
//                if (epoch%1 == 0) {
                    System.out.println("Error at epoch " + epoch + " is " + rms);
                    fw.write(epoch + " " + rms + "\n");
//                }
                }catch(IOException e){
                    e.printStackTrace();
                }
                epoch += 1;
            }
            fw.close();
        }

    }

    /* Normalize input array to the range of [-1, 1] */
    public static void normalizeInput(double [][] inputArray, int [] inputMaxArray,
                                      int argA, int argB, int numExpectedRows, int numExpectedCols){
        for(int col = 0; col < numExpectedCols; col += 1){
            for (int row = 0; row < numExpectedRows; row += 1){
                //0: 0, 1: 0.5, 2:1 *(2) -1 = 0: -1, 1: 0, 2: 1
                inputArray[row][col] = (argB - argA)*(inputArray[row][col] - 0) / (inputMaxArray[col]- 0) + argA;
            }
        }
    }

    /* Get the maximum Q value in the whole LUT */
    public static double getMaxOutput(double [] expectOutput, int numExpectedRows){
        double result = 0.0;
        for(int index = 0; index < numExpectedRows; index += 1){
            if(expectOutput[index] > result){
                result = expectOutput[index];
            }
        }
        return result;
    }


    /* Get the minimum Q value in the whole LUT */
    public static double getMinOutput(double [] expectOutput, int numExpectedRows){
        double result = 1.0;
        for(int index = 0; index < numExpectedRows; index += 1){
            if(expectOutput[index] < result){
                result = expectOutput[index];
            }
        }
        return result;
    }

    /* Normalize output array to the range of [-1, 1] */
    public static void normalizeOutput(double [] expectOutput, double maxOutput, double minOutput,
                                       int argA, int argB, int numExpectedRows){
        for(int index = 0; index < numExpectedRows; index += 1){
            expectOutput[index] = (argB - argA) * (expectOutput[index] - minOutput) / (maxOutput - minOutput) + argA;
            //System.out.println(expectOutput[index]);
        }
    }





}
