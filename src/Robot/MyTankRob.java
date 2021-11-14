package Robot;
import LUT.StateActionLookUpTable;
import robocode.*;

import java.awt.*;
import java.sql.SQLOutput;
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
    private Distance currDistance2Centre = Distance.NEAR;//？
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


        //begin the battle until one party is dead:
        while(true){
            //to do?
            if(totalNumRounds > 40000) epsilon = 0.0;

            switch (currOperationMode) {
                //?
                case SCAN:
                    currReward = 0.0;
                    turnRadarLeft(360);
                    break;
                case PERFORM_ACTION:
                    //core code: get current action
                    if (isOffPolicy) {
                        if (Math.random() <= epsilon) {
                            currAction = selectRandomAction();
                        } else {
                            currAction = selectGreedyAction(myEnergy, enemyEnergy, enemyDistance, distance2Centre(myX, myY, xMid, yMid));
                        }
                    } else {
                        //do nothing

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
                            //current energy ?
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
                    myLUT.train(previousIndex, computeQ(currReward, isOffPolicy));
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

        //to do
        /**
         * current state become previous state:
         */

        /**
         * get current state:
         */

        //become perform mode:
        currOperationMode = OperationMode.PERFORM_ACTION;
        System.out.println(System.currentTimeMillis()+ " scan over");




    }

    private double computeQ( double currReward, boolean isOffPolicy){
        System.out.println(System.currentTimeMillis()+" action over, need to finish scan");
        double previousQ = myLUT.getValueQ(preMyEnergy.ordinal(),
                preEnemyEnergy.ordinal(),
                preDistance2Enemy.ordinal(),
                preDistance2Centre.ordinal(),
                preAction.ordinal());
        //st+1, at+1
        ;

        double bestQ = myLUT.getBestValueQ(currMyEnergy.ordinal(),
                currEnemyEnergy.ordinal(),
                currDistance2Enemy.ordinal(),
                currDistance2Centre.ordinal());

        double updatedPreviousQ = 0;
        if(isOffPolicy){
//            bestQ;
        }else{
            //st+1, at+1 => currentQ
            //st+1, at   => currentQ ?
            if(Math.random() <= epsilon){
                currAction = selectRandomAction();
            }else{
                currAction = selectGreedyAction(myEnergy, enemyEnergy, enemyDistance, distance2Centre(myX, myY, xMid,yMid));
            }
            double currentQ = myLUT.getValueQ(currMyEnergy.ordinal(),
                    currEnemyEnergy.ordinal(),
                    currDistance2Enemy.ordinal(),
                    currDistance2Centre.ordinal(),
                    currAction.ordinal()
            );
//            currentQ



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
        //to do


    }

    @Override
    public void onDeath(DeathEvent e){
        //to do


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









}
