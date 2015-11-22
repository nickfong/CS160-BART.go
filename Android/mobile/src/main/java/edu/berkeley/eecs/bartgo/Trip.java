package edu.berkeley.eecs.bartgo;

import java.util.ArrayList;

public class Trip {
    /**
     *  A trip is a journey from one station to another.  It has a set fare.
     *
     *  Initially, the trip doesn't have any legs.  Legs are populated
     *  with the setLegs method.
     */
    private Station startingStation;
    private Station destinationStation;
    private float fare;
    private ArrayList<Legs> legs;

    public Trip(Station start, Station end, float fare) {
        this.startingStation = start;
        this.destinationStation = end;
        this.fare = fare;
        this.legs = null;
    }

    protected void setLegs(ArrayList<Legs> legs) {
        this.legs = legs;
    }

    public ArrayList<Legs> getLegs() {
        return this.legs;
    }

    public float getFare() {
        return this.fare;
    }

    public Station getStartingStation() {
        return this.startingStation;
    }

    public Station getDestinationStation() {
        return this.destinationStation;
    }
}
