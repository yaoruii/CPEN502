package assignment3;

import Robot.Action;

public class StateAction {
    double energy;
    double enemyEnergy;
    double distance2Enemy;
    double xPosition;
    double yPosition;
    Action action;

    public StateAction(double energy, double enemyEnergy, double distance2Enemy, double xPosition, double yPosition, Action action) {
        this.energy = energy;
        this.enemyEnergy = enemyEnergy;
        this.distance2Enemy = distance2Enemy;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.action = action;
    }
}
