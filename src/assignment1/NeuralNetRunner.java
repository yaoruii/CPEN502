package assignment1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class NeuralNetRunner {

    public NeuralNet neuralNet;
    public double[][] input;
    public double[] targetOutput;



    public double acceptError = 0.05;


    private int train(int trail, int epoch, List<List<Double>> trailEpochError) {
        System.out.print(String.format("---- Begin %d trail now ----\n", trail+1));
        trailEpochError.add(new LinkedList<>());
        neuralNet.initializeWeights();
        for (int i = 1; i <= epoch || epoch == -1; i++) {
            double sumError = 0;
            for (int j = 0; j < input.length; j++) {
                sumError += neuralNet.train(input[j], targetOutput[j]);
            }
            System.out.print(String.format("------- error in %d epoch is %f ----\n", i, sumError));
            trailEpochError.get(trail).add(sumError);
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
        System.out.print("if choose binary representation, please input 1, if use bipolar representation, please input 0:\n");
        int binaryRep = sc.nextInt();
        sc.close();
        int totalSuccEpoch = 0;
        int totalSuccTrail = 0;
        int aveSuccEpoch = 0;
        NeuralNetRunner runner = new NeuralNetRunner();
        int lower;
        int upper;
        boolean argUseBipolar;
        int minStopEpoch = Integer.MAX_VALUE;
        int minStopEpochIdx = numTrials-1;
        if (binaryRep == 1) {
            runner.input = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
            runner.targetOutput = new double[]{0, 1, 1, 0};
            argUseBipolar = false;
            lower = 0;
            upper = 1;
        } else {
            runner.input = new double[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
            runner.targetOutput = new double[]{-1, 1, 1, -1};
            argUseBipolar = true;
            lower = -1;
            upper = 1;
        }
        runner.neuralNet = new NeuralNet(
                2,
                4,
                1,
                0.2,
                0.9,
                lower,
                upper,
                argUseBipolar,
                -0.5,
                0.5

        );
        List<List<Double>> trailEpochError = new LinkedList<>();
        for (int i = 0; i < numTrials; i++) {
            int stopepoch = runner.train(i, epoch, trailEpochError);
            if (stopepoch != -1) {
                //reach the target error:
                System.out.print(String.format("---- %d trail training stops in epoch %d\n", i+1, stopepoch));
                totalSuccEpoch += stopepoch;
                if(minStopEpoch>stopepoch){
                    minStopEpoch = stopepoch;
                    minStopEpochIdx = i;
                }
                totalSuccTrail++;
            } else {
                //can not reach the target error:
                System.out.print(String.format("---- %d trail training can not reach target error\n", i+1));
            }
        }
        System.out.print("---------------- SUMMARY---------------\n");
        if (totalSuccTrail > 0) aveSuccEpoch = totalSuccEpoch / totalSuccTrail;
        System.out.print(String.format("The average convergence epoch is %d\n", aveSuccEpoch));
        System.out.print(String.format("The Best trail is %d,with the minimum epoch %d\n", minStopEpochIdx+1, minStopEpoch));

        //write the result to a txt file:
        try{
            FileWriter fw = new FileWriter("./src/assignment1/momen_0.9_bestResult_bipolarRep_"+ argUseBipolar+ ".txt");
            for (int i = 0; i < minStopEpoch; i++) {
                fw.write(trailEpochError.get(minStopEpochIdx).get(i) + "\n");
            }
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


}
