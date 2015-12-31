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

public class Leg {
    protected Station startStation;
    protected Station endStation;
    protected String trainDestination;
    protected ArrayList<Integer> trains;
//    protected Route route;
//    protected int startingTrainId;

    public Leg(Station startStation, Station endStation, String trainDestination/*, Route route, int startingTrainId*/) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.trainDestination = trainDestination;
//        this.route = route;
//        this.trains = null;
//        this.startingTrainId = startingTrainId;
    }

    public void setTrains(ArrayList<Integer> trains) {
        this.trains = trains;
    }
}
