package assignment3;

import LUT.StateActionLookUpTable;
import Robot.Action;
import Robot.Distance;
import Robot.Energy;
import Robot.OperationMode;
import assignment1.NeuralNet;
import robocode.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class MyRobotV2 extends AdvancedRobot{

    static private StateActionLookUpTable myLUT = new StateActionLookUpTable(
            Energy.values().length,
            Energy.values().length,
            Distance.values().length,
            Distance.values().length,
            Action.values().length
    );
    private static double learningRate = 0.1;
    public static double momentum = 0.9;
    public static int argA = -1;
    public static int argB = 1;
    public static int inputNum = 5; // MyHp, EnemyHp, Distance2Enemy, Distance2Centre, Action
    public static int outputNum = 1;
    public static int hiddenNum = 9;
    public static int actionType = Action.values().length;

    public static double error = 0.0;
    public static double errorRate = 0.0;

    static private NeuralNet neuralNet = new NeuralNet(inputNum, hiddenNum, outputNum,
                                                       learningRate,momentum,argA, argB, true,
                                                        -0.5, 0.5);

    //game round
    static private int totalNumRounds = 0;
    static private int numRoundsTo100 = 0;
    static private int numWins = 0;
    static double winningRate = 0.0;

    static int gap = 100;

    private static double currReward = 0.0;

    private double currMyEnergy = 100.0;
    private double currEnemyEnergy = 100.0;
    private double currDistance2Enemy = 300;
    private double currDistance2Centre = 200;
    private Action currAction = Action.FORWARD;

    private double preMyEnergy = 120;
    private double preEnemyEnergy = 120;
    private double preDistance2Enemy = 50;
    private double preDistance2Centre = 40;
    private Action preAction = Action.FORWARD;

    private Random random = new Random();

    private double myX = 0.0;
    private double myY = 0.0;
    private double myEnergy = 0.0;
    private double enemyEnergy = 0.0;
    private double enemyBearing = 0.0;
    private double enemyDistance = 0.0;
    private double centreDistance = 0.0;


    //there are two modes that the tank can be in
    private OperationMode currOperationMode = OperationMode.SCAN;

    //discount factor and learning rate used by RL:
    private double gamma = 0.90;//discount factor
    private double alpha = 0.1;//learning rate
    private double epsilonInit = 0.75;
    private double epsilon = epsilonInit;


    private boolean isInstance = true;// true: Take intermediate Bonus  into account, false: Only consider the terminal Bonus
    private boolean isOffPolicy = true;//true: off-policy, false: on-policy

    //defined the rewards:
    private double instanceBadReward = -0.25;
    private double terminalBadReward = -0.5;
    private double instanceGoodReward = 1.0;
    private double terminalGoodReward = 2.0;

    static String logFilename = "myTankRobot-logfile_Winning_rate_NN.txt";
    static PrintStream logger = null;

    //get the centre of the board:
    int xMid = 0;
    int yMid = 0;

    private boolean isOnlineLearning = true;



    public void run(){
        if(isOnlineLearning){
            neuralNet.initializeWeights();
            neuralNet.zeroWeights();
        }
        //customize the robot:
        setBodyColor(Color.cyan);
        setGunColor(Color.black);
        setBulletColor(Color.orange);
        setRadarColor(Color.white);

        //get centre board
        xMid = (int) getBattleFieldWidth()/2;
        yMid = (int) getBattleFieldHeight()/2;


        //creat the log:
        //to do
        if(logger == null){
            try {
                logger = new PrintStream(new RobocodeFileOutputStream(getDataFile(logFilename)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.printf("gamma,   %2.2f\n", gamma);
            logger.printf("alpha,   %2.2f\n", alpha);
            logger.printf("epsilon, %2.2f\n", epsilon);
            logger.printf("instanceBadReward, %2.2f\n", instanceBadReward);
            logger.printf("terminalBadReward, %2.2f\n", terminalBadReward);
            logger.printf("instanceGoodReward, %2.2f\n", instanceGoodReward);
            logger.printf("terminalGoodReward, %2.2f\n\n", terminalGoodReward);
        }


        //begin the battle until one party is dead:
        while(true){
            //to do?
            //
            if(totalNumRounds > 40000) epsilon = 0.0;

            switch (currOperationMode) {
                case SCAN:
                    currReward = 0.0;
                    turnRadarLeft(360);
                    break;
                case PERFORM_ACTION:

                    if(isOnlineLearning){
                        if (Math.random() <= epsilon) {
                            currAction = selectRandomAction();
                        } else {
                            currAction = Action.values()[selectGreedyActionFromNN(currMyEnergy, currEnemyEnergy, currDistance2Enemy, currDistance2Centre)];
                        }
                    }

                    switch (currAction) {
                        case FORWARD: {
//                            setTurnRight(enemyBearing);
                            setAhead(100);
                            execute();
                            break;
                        }
                        case FIRE: {
                            turnGunRight(getHeading() - getGunHeading() + enemyBearing);
                            fire(3);
                            execute();
                            break;
                        }
                        case RETREAT: {
                            setTurnRight(180);
                            setAhead(100);
                            execute();
                            break;
                        }
                        case LEFT: {
                            setTurnLeft(90);
                            setAhead(100);
                            execute();
                            break;
                        }
                        case RIGHT: {
                            setTurnRight(90);
                            setAhead(100);
                            execute();
                            break;
                        }
                    }
                    //core code
                    //update previous q:
                    double[] previousIndex = new double[]{
                            preMyEnergy,
                            preEnemyEnergy,
                            preDistance2Enemy,
                            preDistance2Centre,
                            preAction.ordinal()
                    };
                    //myLUT.train(previousIndex, computeQ(currReward, isOffPolicy));
                    if(isOnlineLearning){
                        neuralNet.train(previousIndex, computeQFromNN(currReward, isOffPolicy));

                    }
                    currOperationMode = OperationMode.SCAN;
            }
        }
    }

    private int selectGreedyActionFromNN(double currMyEnergy, double currEnemyEnergy, double currDistance2Enemy, double currDistance2Centre ){
        double maxQ = Double.MIN_VALUE;
        int bestActionIndex =0;
        for(int index = 0; index < actionType; index += 1){
            double actionIndex = index;
            double [] statesAction = {currMyEnergy, currEnemyEnergy, currDistance2Enemy, currDistance2Centre, actionIndex};
            double currQ = neuralNet.outputFor(statesAction);
            if( currQ> maxQ){
                maxQ = currQ;
                bestActionIndex = index;
            }
        }
        return bestActionIndex;

    }
    @Override
    public void onScannedRobot(ScannedRobotEvent e){
        //update real state value:
        enemyBearing = e.getBearing();//
        enemyDistance = e.getDistance();//my enemy
        enemyEnergy = e.getEnergy();

        myX = getX();
        myY = getY();
        myEnergy = getEnergy();

        /**
         * current state become previous state:
         */
        preAction = currAction;

        preMyEnergy = currMyEnergy;
        preEnemyEnergy = currEnemyEnergy;
        preDistance2Enemy = currDistance2Enemy;
        preDistance2Centre = currDistance2Centre;

        /**
         * get current state:
         */
//        currMyEnergy = getEneryEnum(getEnergy());
//        currEnemyEnergy = getEneryEnum(e.getEnergy());
//        currDistance2Enemy = getDistanceEnum(e.getDistance());
//        currDistance2Centre = getDistanceEnum(distance2Centre(myX, myY, xMid, yMid));


        currMyEnergy = getEnergy();
        currEnemyEnergy = e.getEnergy();
        currDistance2Enemy = e.getDistance();
        currDistance2Centre = distance2Centre(myX, myY, xMid, yMid);

        //become perform mode:
        currOperationMode = OperationMode.PERFORM_ACTION;

    }
    public double computeQFromNN(double currReward,boolean isOffPolicy){
        double [] previousInput = {
                preMyEnergy,
                preEnemyEnergy,
                preDistance2Enemy,
                preDistance2Centre,
                preAction.ordinal()};

        double previousQ = neuralNet.outputFor(previousInput);

        double [] currentInput = {
                currMyEnergy,
                currEnemyEnergy,
                currDistance2Enemy,
                currDistance2Centre,
                currAction.ordinal()
        };

        double currentQ = neuralNet.outputFor(currentInput);

        int bestActionIndex = selectGreedyActionFromNN(
                currMyEnergy,
                currEnemyEnergy,
                currDistance2Enemy,
                currDistance2Centre);

        double [] bestQInput = {
                currMyEnergy,
                currEnemyEnergy,
                currDistance2Enemy,
                currDistance2Centre,
                bestActionIndex
        };
        double bestQ = neuralNet.outputFor(bestQInput);
        double updatedPreviousQ = 0;
        if(isOffPolicy){
//            bestQ;
            updatedPreviousQ = previousQ + alpha*(currReward + gamma*bestQ-previousQ);
        }else{
            updatedPreviousQ = previousQ + alpha*(currReward + gamma*currentQ - previousQ);
        }
        return updatedPreviousQ;

    }

    @Override
    public void onHitByBullet(HitByBulletEvent e){
        if(isInstance) currReward = instanceBadReward;

    }

    @Override
    public void onBulletHit(BulletHitEvent e){
        if(isInstance) currReward = instanceGoodReward;

    }

    @Override
    public void onBulletMissed(BulletMissedEvent e){
        if(isInstance) currReward = instanceBadReward;


    }

    @Override
    public void onHitWall(HitWallEvent e){
        if(isInstance) currReward = instanceBadReward;
    }

    @Override
    public void onWin(WinEvent e){

        //save LUT
        try {
            myLUT.save(getDataFile("myLUT_NN.dat"));//make sure to use the same filename?
        } catch (Exception exception) {
            System.out.println("Save Error!" + exception);
        }
        currReward = terminalGoodReward;
        double[] index = new double[]{
                preMyEnergy,
                preEnemyEnergy,
                preDistance2Enemy,
                preDistance2Centre,
                preAction.ordinal()
        };
        //myLUT.train(index,computeQ(currReward,isOffPolicy));
        if(isOnlineLearning){
            neuralNet.train(index, computeQFromNN(currReward, isOffPolicy));
        }

        if(numRoundsTo100 < gap){
            numRoundsTo100 += 1;
            totalNumRounds += 1;
            numWins += 1;
        }
        else{
            winningRate = ((double) numWins / gap) * 100;
            logger.printf("Winning rate: %2.1f\n", winningRate);//??
            logger.flush();//??
            numRoundsTo100 = 0;
            numWins = 0;
        }





    }

    @Override
    public void onDeath(DeathEvent e){

        //save LUT
        try {
            myLUT.save(getDataFile("myLUT_NN.dat"));
        } catch (Exception exception) {
            System.out.println("Save Error!" + exception);
        }

        currReward = terminalBadReward;
        double[] index = new double[]{
                preMyEnergy,
                preEnemyEnergy,
                preDistance2Enemy,
                preDistance2Centre,
                preAction.ordinal()
        };
        //myLUT.train(index,computeQ(currReward,isOffPolicy));
        if(isOnlineLearning){
            neuralNet.train(index, computeQFromNN(currReward, isOffPolicy));
        }
        if(numRoundsTo100 < gap){
            numRoundsTo100 += 1;
            totalNumRounds += 1;
        }
        else{
            winningRate = ((double) numWins / gap) * 100;
            logger.printf("Winning rate: %2.1f\n", winningRate);
            logger.flush();
            numRoundsTo100 = 0;
            numWins = 0;
        }


    }



    private Action selectRandomAction(){
        int num = Action.values().length;
        int selectedIndex = random.nextInt(num);
        return Action.values()[selectedIndex];
    }

    private Action selectGreedyAction( int myEnergy,int enemyEnergy, int enemyDistance, int centreDistance){
        double bestQ = -1;
        Action bestAction = null;
        for(Action action: Action.values()){
            double currQ = myLUT.getValueQ(myEnergy,enemyEnergy,enemyDistance,centreDistance,action.ordinal());
            if(bestQ <currQ){
                bestQ = currQ;
                bestAction = action;
            }
        }
        return bestAction;

    }

    private double distance2Centre(double x1, double y1, double centreX, double centreY){
        this.centreDistance = Math.sqrt(Math.pow(x1 - centreX, 2) + Math.pow(y1 - centreY, 2));
        return this.centreDistance;
    }

    private Energy getEneryEnum(double energy){
        Energy energyEnum;
        if(energy <= 33){
            energyEnum = Energy.LOW;
        }else if(energy <=66){
            energyEnum = Energy.MEDIUM;
        }else{
            energyEnum = Energy.HIGH;
        }
        return energyEnum;
    }

    private Distance getDistanceEnum(double distance){
        Distance distanceEnum;
        if(distance <= 100){
            distanceEnum = Distance.VERY_CLOSE;
        }else if(distance <= 400){
            distanceEnum = Distance.NEAR;
        }else{
            distanceEnum = Distance.FAR;
        }
        return distanceEnum;
    }
}
