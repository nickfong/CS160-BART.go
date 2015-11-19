package edu.berkeley.eecs.bartgo;

public class train {
    /**
     *  A train is a specific train within a chain of trains on a trip
     */
    private station trainDestination;
    private station embarkationStation;
    private station debarkationStation;
    private float arrivalMinutes;
    private int crowding;
    private int id;

    public train(float arrivalMinutes, int crowding, station trainDestination, station embarkationStation, station debarkationStation, int id) {
        this.arrivalMinutes = arrivalMinutes;
        this.crowding = crowding;
        this.trainDestination = trainDestination;
        this.embarkationStation = embarkationStation;
        this.debarkationStation = debarkationStation;
        this.id = id;
    }

    public float updateArrivalTime() {
        //TODO
        return -1.0f;
    }

    public station getDestination() {
        return this.trainDestination;
    }

    public station getEmbarkationStation() {
        return this.embarkationStation;
    }

    public station getDebarkation() {
        return this.debarkationStation;
    }

    public int getId() {
        return this.id;
    }

    public int getCrowding() {
        return this.crowding;
    }

    public float getArrivalTime() {
        updateArrivalTime();
        return this.arrivalMinutes;
    }
}

