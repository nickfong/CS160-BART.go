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

public class Station {
    private String abbreviation;
    private String name;
    private String address;
    private String zip;
    private String latitude;
    private String longitude;

    public Station(String abbreviation, String name, String address, String zip, String latitude, String longitude) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getAbbreviation() { return this.abbreviation; }

    public String getName() { return this.name; }

    public String getAddress() {
        return this.address;
    }

    public String getZip() {
        return this.zip;
    }

    public String getLatitude() { return this.latitude; }

    public String getLongitude() {return this.longitude; }

}

