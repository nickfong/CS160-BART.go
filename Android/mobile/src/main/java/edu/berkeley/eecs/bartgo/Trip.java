package edu.berkeley.eecs.bartgo;

import java.util.ArrayList;

public class Trip {
    /**
     *  A trip is a journey from one station to another.  It has a set fare.
     *
     *  Initially, the trip doesn't have any chains.  Chains are populated
     *  with the setChains method.
     */
    private Station startingStation;
    private Station destinationStation;
    private float fare;
    private ArrayList<Chain> chains;

    public Trip(Station start, Station end, float fare) {
        this.startingStation = start;
        this.destinationStation = end;
        this.fare = fare;
        this.chains = null;
    }

    protected void setChains(ArrayList<Chain> chains) {
        this.chains = chains;
    }

    public ArrayList<Chain> getChains() {
        return this.chains;
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
