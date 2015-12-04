package edu.berkeley.eecs.bartgo;

import java.util.ArrayList;

public class Legs {
    /**
     *  A leg is a specific trip that includes one or more trains
     */
    private ArrayList<Leg> legs;

    public Legs(ArrayList<Leg> legs)  {
        this.legs = legs;
    }

    public ArrayList<Leg> getLegs() {
        return this.legs;
    }

}
