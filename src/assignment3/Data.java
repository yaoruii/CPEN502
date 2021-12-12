package assignment3;

public class Data {
    StateAction preStateAction;
    StateAction currStateAction;
    double currReward;

    public Data(StateAction preStateAction, StateAction currStateAction, double currReward) {
        this.preStateAction = preStateAction;
        this.currStateAction = currStateAction;
        this.currReward = currReward;
    }
}
