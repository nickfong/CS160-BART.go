package edu.berkeley.eecs.bartgo;

import java.util.ArrayList;

public class Leg {
    Station startStation;
    Station endStation;
    String trainDestination;
    ArrayList<Train> trains;

    public Leg(Station startStation, Station endStation, String trainDestination) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.trainDestination = trainDestination;
        this.trains = null;
    }

    public void setTrains(ArrayList<Train> trains) {
        this.trains = trains;
    }
}
