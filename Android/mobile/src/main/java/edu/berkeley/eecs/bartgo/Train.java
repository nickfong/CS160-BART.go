package edu.berkeley.eecs.bartgo;

public class Train {
    /**
     *  A train is a specific train within a leg of trains on a trip
     */
    private Station trainDestination;
    private Station embarkationStation;
    private Station debarkationStation;
    private int arrivalMinutes;
    private int crowding;
    private int id;

    public Train(int arrivalMinutes, int crowding, Station trainDestination, Station embarkationStation, Station debarkationStation, int id) {
        this.arrivalMinutes = arrivalMinutes;
        this.crowding = crowding;
        this.trainDestination = trainDestination;
        this.embarkationStation = embarkationStation;
        this.debarkationStation = debarkationStation;
        this.id = id;
    }

    public void setArrivalMinutes(int minutes) {
        this.arrivalMinutes = minutes;
    }

    public int getArrivalMinutes() {
        return this.arrivalMinutes;
    }

    public Station getDestination() {
        return this.trainDestination;
    }

    public Station getEmbarkationStation() {
        return this.embarkationStation;
    }

    public Station getDebarkation() {
        return this.debarkationStation;
    }

    public int getId() {
        return this.id;
    }

    public int getCrowding() {
        return this.crowding;
    }

}

