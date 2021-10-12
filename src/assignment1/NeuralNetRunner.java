package assignment1;

import java.util.Scanner;

public class NeuralNetRunner {

    public NeuralNet neuralNet;
    public double[][] input;
    public double[] targetOutput;



    public double acceptError = 0.05;


    private int train(int trail, int epoch) {
        System.out.print(String.format("---- Begin %d trail now ----\n", trail));
        neuralNet.initializeWeights();
        for (int i = 1; i <= epoch || epoch == -1; i++) {
            double sumError = 0;
            for (int j = 0; j < input.length; j++) {
                sumError += neuralNet.train(input[j], targetOutput[j]);
            }
            System.out.print(String.format("------- error in %d epoch is %f ----\n", i, sumError));
            if (sumError <= acceptError) {
                System.out.print(String.format("---- Find the target error at %d epochs \n", i));
                return i;
            }
        }
        return -1;
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("how many time you want to train:\n");
        int numTrials = sc.nextInt();
        System.out.print("how many epoch you want to run in each trail, if you want to keep training until error smaller than target error, input -1: \n");
        int epoch = sc.nextInt();
        System.out.print("if choose binary representation, please input 0, if use bipolar representation, please input 1:\n");
        int binaryRep = sc.nextInt();
        sc.close();
        int totalSuccEpoch = 0;
        int totalSuccTrail = 0;
        int aveSuccEpoch = 0;
        NeuralNetRunner runner = new NeuralNetRunner();
        boolean argUseBipolar;
        if (binaryRep == 1) {
            runner.input = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
            runner.targetOutput = new double[]{0, 1, 1, 0};
            argUseBipolar = false;
        } else {
            runner.input = new double[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
            runner.targetOutput = new double[]{-1, 1, 1, -1};
            argUseBipolar = true;
        }
        runner.neuralNet = new NeuralNet(
                2,
                4,
                1,
                0.2,
                0.0,
                -1,
                1,
                argUseBipolar

        );
        for (int i = 0; i < numTrials; i++) {
            int stopepoch = runner.train(i, epoch);
            if (stopepoch != -1) {
                //reach the target error:
                System.out.print(String.format("---- %d trail training stops in epoch %d", i, stopepoch));
                totalSuccEpoch += stopepoch;
                totalSuccTrail++;
            } else {
                //can not reach the target error:
                System.out.print(String.format("---- %d trail training can not reach target error", i));
            }
        }
        System.out.print("-----SUMMARY-------\n");
        if (totalSuccTrail > 0) aveSuccEpoch = totalSuccEpoch / totalSuccTrail;
        System.out.print(String.format("The average convergence rate is %d", aveSuccEpoch));

    }


}
