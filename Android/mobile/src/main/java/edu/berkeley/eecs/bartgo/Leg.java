package edu.berkeley.eecs.bartgo;

import java.util.ArrayList;

public class Leg {
    Station startStation;
    Station endStation;
    String trainDestination;
    ArrayList<Train> trains;
    public Route route;

    public Leg(Station startStation, Station endStation, String trainDestination, Route route) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.trainDestination = trainDestination;
        this.route = route;
        this.trains = null;
    }

    public void setTrains(ArrayList<Train> trains) {
        this.trains = trains;
    }
}
