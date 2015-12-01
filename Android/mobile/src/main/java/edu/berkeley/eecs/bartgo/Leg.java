package edu.berkeley.eecs.bartgo;

import java.util.ArrayList;

public class Leg {
    protected Station startStation;
    protected Station endStation;
    protected String trainDestination;
    protected ArrayList<Train> trains;
    protected Route route;
    protected int startingTrainId;

    public Leg(Station startStation, Station endStation, String trainDestination, Route route, int startingTrainId) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.trainDestination = trainDestination;
        this.route = route;
        this.trains = null;
        this.startingTrainId = startingTrainId;
    }

    public void setTrains(ArrayList<Train> trains) {
        this.trains = trains;
    }
}
