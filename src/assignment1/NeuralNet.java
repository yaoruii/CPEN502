package assignment1;

import Sarb.NeuralNetInterface;

import java.io.File;
import java.io.IOException;

public class NeuralNet implements NeuralNetInterface {
    public int argNumInputs;//The number of inputs in your input vector
    public int argNumHidden;//The number of hidden neurons in your hidden layer. Only a single hidden layer is supported
    public double argLearningRate;//The learning rate coefficient
    public double argMomentumTerm;//The momentum coefficient
    public double argA;// Integer lower bound of sigmoid used by the output neuron only.
    public double argB;// Integer upper bound of sigmoid used by the output neuron only.
    public boolean argUseBipolarForHiddenNer;


    public NeuralNet (
            int argNumInputs,
            int argNumHidden,
            double argLearningRate,
            double argMomentumTerm,
            double argA,
            double argB,
            boolean argUseBipolarForHiddenNer){
        this.argNumInputs = argNumInputs;
        this.argNumHidden = argNumHidden;
        this.argLearningRate = argLearningRate;
        this.argMomentumTerm = argMomentumTerm;
        this.argA =argA;
        this.argB = argB;
        this.argUseBipolarForHiddenNer = argUseBipolarForHiddenNer;
    };

    /**
     * This method implements a bipolar sigmoid of the input X
     * @param x input
     * @return f(x) = 2 / (1+e(-x)) - 1
     */
    @Override
    public double sigmoid(double x) {
        return 0;
    }
    /**
     * This method implements a general sigmoid with asymptotes bounded by (a,b)
     * @param x input
     * @return f(x) = b_minus_a / (1 + e(-x)) - minus_a
     */
    @Override
    public double customSigmoid(double x) {
        return 0;
    }


    /**
     *  Initialize the weights to random values from  -0.5 to +0.5
     *  For say 2 inputs, the input vector is [0] & [1]. We add [2] for the bias.
     *  Like wise for hidden units. For say 2 hidden units which are stored in an array
     *  [0] & [1] are the hidden & [2] the bias.
     *  We also initialize the last weight change arrays. This is to implement the alpha term.
     */
    @Override
    public void initializeWeights() {

    }

    /**
     * Initialize the weights to 0.
     */
    @Override
    public void zeroWeights() {

    }

    /**
     *
     * @param X The input vector. An array of doubles.
     * @return The value returned by th LUT or NN for this input vector
     */
    @Override
    public double outputFor(double[] X) {
        return 0;
    }

    /**
     * This method will tell the NN or the LUT the output value that should be mapped to the given input vector. I.e.
     * the desired correct output value for an input
     * @param X The input vector
     * @param argValue  The new value to learn
     * @return The error in the output for that input vector
     */
    @Override
    public double train(double[] X, double argValue) {
        return 0;
    }


    /**
     * A method to write either a LUT or weights of an neural net to a file.
     * @param argFile argFile of type File
     */
    @Override
    public void save(File argFile) {

    }

    /**
     * Loads the LUT or neural net weights from file. The load must of course have knowledge of how the data was written out by the save method.
     * You should raise an error in the case that an attempt is being made to load data into an LUT or neural net whose structure does not match
     *  the data in the file. (e.g. wrong number of hidden neurons).
     * @param argFileName
     * @throws IOException IOException
     */
    @Override
    public void load(String argFileName) throws IOException {

    }
}
