package assignment1;

import Sarb.NeuralNetInterface;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import java.lang.Math;

public class NeuralNet implements NeuralNetInterface {
    private int argNumInputs;//The number of inputs in your input vector
    private int argNumHidden;//The number of hidden neurons in your hidden layer. Only a single hidden layer is supported
    private int argNumOutput;
    private double argLearningRate;//The learning rate coefficient
    private double argMomentumTerm;//The momentum coefficient
    private double argA;// Integer lower bound of sigmoid used by the output neuron only.
    private double argB;// Integer upper bound of sigmoid used by the output neuron only.
    private boolean argUseBipolar;
    private double randomWeightLower;
    private double randomWeightUpper;

    //each layer's output:
    private double[] input2Hidden;
    private double[] hidden2Output;
    private double[] netOutput;

    //size of each layer's output:
    private int input2HiddenSize;
    private int hidden2OutputSize;
    private int netOutputSize;

    private final double nodeForBias = 1;
    private final double errorRate = 0.5;

    //each layer's weights:
    private double[][] input2HiddenWeight;
    private double[][] hidden2OutputWeight;

    //each layer's weights' change:
    private double[][] input2HiddenWeightChange;
    private double[][] hidden2OutputWeightChange;

    //each layer's delta:δi
    private double[] hiddenErrorSignal;
    private double[] outputErrorSignal;

    //random generator:
    private Random random;


    public NeuralNet(
            int argNumInputs,
            int argNumHidden,
            int argNumOutput,
            double argLearningRate,
            double argMomentumTerm,
            double argA,
            double argB,
            boolean argUseBipolar,
            double randomWeightLower,
            double randomWeightUpper){

        this.argNumInputs = argNumInputs;
        this.argNumHidden = argNumHidden;
        this.argNumOutput = argNumOutput;
        this.argLearningRate = argLearningRate;
        this.argMomentumTerm = argMomentumTerm;
        this.argA = argA;
        this.argB = argB;
        this.argUseBipolar = argUseBipolar;
        this.randomWeightLower = randomWeightLower;
        this.randomWeightUpper = randomWeightUpper;

        //initialize:
        this.random = new Random();

        this.input2HiddenSize = this.argNumInputs + 1;
        this.hidden2OutputSize = this.argNumHidden + 1;
        this.netOutputSize = this.argNumOutput;

        this.input2Hidden = new double[input2HiddenSize];
        this.hidden2Output = new double[hidden2OutputSize];
        this.netOutput = new double[netOutputSize];

        this.input2HiddenWeight = new double[input2HiddenSize][hidden2OutputSize];
        this.hidden2OutputWeight = new double[hidden2OutputSize][netOutputSize];

        this.input2HiddenWeightChange = new double[input2HiddenSize][hidden2OutputSize];
        this.hidden2OutputWeightChange = new double[hidden2OutputSize][netOutputSize];

        this.hiddenErrorSignal = new double[hidden2OutputSize];
        this.outputErrorSignal = new double[netOutputSize];


    }



    /**
     * This method implements a bipolar sigmoid of the input X
     *
     * @param x input
     * @return f(x) = 2 / (1+e(-x)) - 1
     */
    @Override
    public double sigmoid(double x) {
        return 0;
    }

    /**
     * This method implements a general sigmoid with asymptotes bounded by (a,b)
     *
     * @param x input
     * @return f(x) = b-a / (1 + e(-x)) + a
     */
    @Override
    public double customSigmoid(double x) {
        //1-(-1) = 2
        return (argB-argA)/(1+Math.exp(-x))+argA;
    }


    /**
     * Initialize the weights to random values from  -0.5 to +0.5
     * For say 2 inputs, the input vector is [0] & [1]. We add [2] for the bias.
     * Like wise for hidden units. For say 2 hidden units which are stored in an array
     * [0] & [1] are the hidden & [2] the bias.
     * We also initialize the last weight change arrays. This is to implement the alpha term.
     */
    @Override
    public void initializeWeights() {
        for(int i = 0; i<input2HiddenSize; i++){
            for(int j = 0; j<hidden2OutputSize-1; j++){
                input2HiddenWeight[i][j] = random.nextDouble()*(randomWeightUpper - randomWeightLower) + randomWeightLower;
            }
        }
        for(int i = 0; i< hidden2OutputSize; i++){
            for(int j = 0; j<netOutputSize; j++){
                hidden2OutputWeight[i][j] = random.nextDouble()*(randomWeightUpper - randomWeightLower) + randomWeightLower;
            }
        }


    }

    /**
     * Initialize the weights to 0.
     */
    @Override
    public void zeroWeights() {
        for(int i = 0; i<input2HiddenSize; i++){
            for(int j = 0; j<hidden2OutputSize-1; j++){
                input2HiddenWeightChange[i][j] = 0;
            }
        }
        for(int i = 0; i< hidden2OutputSize; i++){
            for(int j = 0; j<netOutputSize; j++){
                hidden2OutputWeightChange[i][j] = 0;
            }
        }
    }

    /**
     * @param X The input vector. An array of doubles.
     * @return The value returned by th LUT or NN for this input vector
     */
    @Override
    public double outputFor(double[] X) {
        //load the input vector in and add bias node for the input layer
        for (int i = 0; i < argNumInputs; i++){
            input2Hidden[i] = X[i];
        }
        input2Hidden[argNumInputs] = nodeForBias;

        //calculate the output for hidder layer and add bias node for hidder layer
        for (int i = 0; i < argNumHidden; i++){
            hidden2Output[i] = 0;
            for(int j = 0; j <= argNumInputs; j++){
                hidden2Output[i] += input2Hidden[j] * input2HiddenWeight[j][i];
            }
            hidden2Output[i] = customSigmoid(hidden2Output[i]);
        }
        hidden2Output[argNumHidden] = nodeForBias;

        //calculate nerualnet's output
        for (int i = 0; i< argNumOutput;i++){
            netOutput[i] = 0;
            for (int j = 0; j <= argNumHidden;j++){
                netOutput[i] += hidden2Output[j] * hidden2OutputWeight[j][i];
            }
            netOutput[i] = customSigmoid(netOutput[i]);
        }
        return netOutput[0];//only the first output in our case when numoutput=1
    }

    /**
     * This method will tell the NN or the LUT the output value that should be mapped to the given input vector. I.e.
     * the desired correct output value for an input
     *
     * @param X        The input vector
     * @param argValue The new value to learn
     * @return The error in the output for that input vector
     */
    @Override
    public double train(double[] X, double argValue) {
        double error = 0;
        //System.out.println("X is " + X[0] + X[1] + X[2] + X[3] + X[4] );
        double trainOutput = outputFor(X);
//        System.out.println("trainOutput is " + trainOutput);//-1
//        System.out.println("argValue is "+ argValue);
        error += errorRate*Math.pow((trainOutput-argValue), 2);
        updateWeights(trainOutput, argValue);
        return error;
    }

    private void updateWeights(double trainOutput, double argValue) {
        // Calculate δ from hidden layer to output layer
        for (int i = 0; i < argNumOutput; i++) {
            if (!argUseBipolar) {
                outputErrorSignal[i] = trainOutput * (1 - trainOutput) * (argValue - trainOutput);
            } else {
                outputErrorSignal[i] = 0.5 * (1 - Math.pow(trainOutput, 2)) * (argValue - trainOutput);
            }
        }
        // Update the weight change
        for (int j = 0; j < argNumOutput; j++) {
            for (int i = 0; i <= argNumHidden; i++) {
                hidden2OutputWeight[i][j] += argMomentumTerm * hidden2OutputWeightChange[i][j] + argLearningRate * outputErrorSignal[j] * hidden2Output[i];
                hidden2OutputWeightChange[i][j] = argMomentumTerm * hidden2OutputWeightChange[i][j] + argLearningRate * outputErrorSignal[j] * hidden2Output[i];
            }
        }
        // Calculate δ from input layer to hidden layer
        for (int i = 0; i < argNumHidden; i++) {
            if (!argUseBipolar) {
                hiddenErrorSignal[i] = hidden2Output[i] * (1 - hidden2Output[i]);
            } else {
                hiddenErrorSignal[i] = 0.5 * (1 - Math.pow(hidden2Output[i], 2));
            }
            double totalSum = 0.0;
            for (int j = 0; j < argNumOutput; j++) {
                totalSum += outputErrorSignal[j] * hidden2OutputWeight[i][j];
            }
            hiddenErrorSignal[i] *= totalSum;
        }
        // Update the weight change
        for (int j = 0; j < argNumHidden; j++) {
            for (int i = 0; i <= argNumInputs; i++) {
                input2HiddenWeight[i][j] += argMomentumTerm * input2HiddenWeightChange[i][j] + argLearningRate * hiddenErrorSignal[j] * input2Hidden[i];
                input2HiddenWeightChange[i][j] = argMomentumTerm * input2HiddenWeightChange[i][j] + argLearningRate * hiddenErrorSignal[j] * input2Hidden[i];
            }
        }
    }


    /**
     * A method to write either a LUT or weights of an neural net to a file.
     *
     * @param argFile argFile of type File
     */
    @Override
    public void save(File argFile) {

    }

    /**
     * Loads the LUT or neural net weights from file. The load must of course have knowledge of how the data was written out by the save method.
     * You should raise an error in the case that an attempt is being made to load data into an LUT or neural net whose structure does not match
     * the data in the file. (e.g. wrong number of hidden neurons).
     *
     * @param argFileName
     * @throws IOException IOException
     */
    @Override
    public void load(String argFileName) throws IOException {

    }
}
