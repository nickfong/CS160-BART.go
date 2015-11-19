package edu.berkeley.eecs.bartgo;

public class chain {
    /**
     *  A chain is a specific trip that includes one or more trains
     */
    private train[] trains;

    public chain(trip currentTrip, train[] trains)  {
        this.trains = trains;
    }

    public train[] getTrains() {
        return this.trains;
    }

}
