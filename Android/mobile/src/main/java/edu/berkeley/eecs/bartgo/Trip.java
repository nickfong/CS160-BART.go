/**
 * Copyright (C) 2015
 * Nicholas Fong, Daiwei Liu, Krystyn Neisess, Patrick Sun, Michael Xu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 */

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
