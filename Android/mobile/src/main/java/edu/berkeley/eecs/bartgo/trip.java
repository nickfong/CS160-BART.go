package edu.berkeley.eecs.bartgo;

public class trip {
    /**
     *  A trip is a journey from one station to another.  It has a set fare.
     *
     *  Initially, the trip doesn't have any chains.  Chains are populated
     *  with the setChains method.
     */
    private station startingStation;
    private station destinationStation;
    private float fare;
    private chain[] chains;

    public trip(station start, station end, float fare) {
        this.startingStation = start;
        this.destinationStation = end;
        this.fare = fare;
        this.chains = null;
    }

    protected void setChains(chain[] chains) {
        this.chains = chains;
    }

    public chain[] getChains() {
        return this.chains;
    }
}
