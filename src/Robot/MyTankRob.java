package Robot;
import LUT.StateActionLookUpTable;
import robocode.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.*;
import java.awt.*;
import java.util.Random;

public class MyTankRob extends AdvancedRobot {

    static private StateActionLookUpTable myLUT = new StateActionLookUpTable(
            Energy.values().length,
            Energy.values().length,
            Distance.values().length,
            Distance.values().length,
            Action.values().length
    );

    //game round
    static private int totalNumRounds = 0;
    static private int numRoundsTo100 = 0;
    static private int numWins = 0;
    static double winningRate = 0.0;

    private static double currReward = 0.0;

    private Energy currMyEnergy = Energy.HIGH;
    private Energy currEnemyEnergy = Energy.HIGH;
    private Distance currDistance2Enemy = Distance.NEAR;
    private Distance currDistance2Centre = Distance.NEAR;
    private Action currAction = Action.FORWARD;

    private Energy preMyEnergy = Energy.LOW;
    private Energy preEnemyEnergy = Energy.LOW;
    private Distance preDistance2Enemy = Distance.VERY_CLOSE;
    private Distance preDistance2Centre = Distance.VERY_CLOSE;
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
    private double alpha = 0.70;//learning rate
    private double epsilonInit = 0.65;
    private double epsilon = epsilonInit;


    private boolean isInstance = true;// true: Take intermediate Bonus  into account, false: Only consider the terminal Bonus
    private boolean isOffPolicy = true;//true: off-policy, false: on-policy

    //defined the rewards:
    private double instanceBadReward = -0.25;
    private double terminalBadReward = -0.5;
    private double instanceGoodReward = 1.0;
    private double terminalGoodReward = 2.0;

    static String logFilename = "myTankRobot-logfile.log";
    static PrintStream logger = null;

    //get the centre of the board:
    int xMid = 0;
    int yMid = 0;



    public void run(){
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
                logger = new PrintStream(getDataFile(logFilename));
            } catch (FileNotFoundException e) {
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
                    //core code: get current action
                    if (Math.random() <= epsilon) {
                        currAction = selectRandomAction();
                    } else {
                        currAction = selectGreedyAction(myEnergy, enemyEnergy, enemyDistance, distance2Centre(myX, myY, xMid, yMid));
                    }
                    switch (currAction) {
                        case FORWARD: {
                            setTurnRight(enemyBearing);
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
                            setTurnRight(enemyBearing + 180);
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
                    System.out.println(System.currentTimeMillis() + " action over");
                    //core code
                    //update previous q:
                    double[] previousIndex = new double[]{
                            preMyEnergy.ordinal(),
                            preEnemyEnergy.ordinal(),
                            preDistance2Enemy.ordinal(),
                            preDistance2Centre.ordinal(),
                            preAction.ordinal()
                    };
                    myLUT.train(previousIndex, computeQ(currReward));
                    currOperationMode = OperationMode.SCAN;
            }
        }
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
        currMyEnergy = getEneryEnum(getEnergy());
        currEnemyEnergy = getEneryEnum(e.getEnergy());
        currDistance2Enemy = getDistanceEnum(e.getDistance());
        currDistance2Centre = getDistanceEnum(distance2Centre(myX, myY, xMid, yMid));

        //become perform mode:
        currOperationMode = OperationMode.PERFORM_ACTION;

    }

    private double computeQ( double currReward){
        double previousQ = myLUT.getValueQ(preMyEnergy.ordinal(),
                preEnemyEnergy.ordinal(),
                preDistance2Enemy.ordinal(),
                preDistance2Centre.ordinal(),
                preAction.ordinal());
        double bestQ = myLUT.getBestValueQ(currMyEnergy.ordinal(),
                currEnemyEnergy.ordinal(),
                currDistance2Enemy.ordinal(),
                currDistance2Centre.ordinal());
        double currentQ = myLUT.getValueQ(currMyEnergy.ordinal(),
                currEnemyEnergy.ordinal(),
                currDistance2Enemy.ordinal(),
                currDistance2Centre.ordinal(),
                currAction.ordinal()
        );

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
            myLUT.save(getDataFile("myLUT.dat"));//make sure to use the same filename?
        } catch (Exception e) {
            System.out.println("Save Error!" + e);
        }
        currReward = terminalGoodReward;
        double[] index = new double[]{
                preMyEnergy.ordinal(),
                preEnemyEnergy.ordinal(),
                preDistance2Enemy.ordinal(),
                preDistance2Centre.ordinal(),
                preAction.ordinal()
        };
        myLUT.train(index,computeQ(currReward,isOffPolicy));

        if(numRoundsTo100 < 100){
            numRoundsTo100 += 1;
            totalNumRounds += 1;
            numWins += 1;
        }
        else{
            winningRate = ((double) numWins / numRoundsTo100) * 100;
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
            myLUT.save(getDataFile("myLUT.dat"));
        } catch (Exception e) {
            System.out.println("Save Error!" + e);
        }

        currReward = terminalBadReward;
        double[] index = new double[]{
                preMyEnergy.ordinal(),
                preEnemyEnergy.ordinal(),
                preDistance2Enemy.ordinal(),
                preDistance2Centre.ordinal(),
                preAction.ordinal()
        };
        myLUT.train(index,computeQ(currReward,isOffPolicy));

        if(numRoundsTo100 < 100){
            numRoundsTo100 += 1;
            totalNumRounds += 1;
        }

        else{
            winningRate = ((double) numWins / numRoundsTo100) * 100;
            System.out.println("Winning rate: " + winningRate);
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

    private Action selectGreedyAction(double myEnergy,double enemyEnergy, double enemyDistance, double centreDistance){
        //to do
        return Action.values()[0];


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
