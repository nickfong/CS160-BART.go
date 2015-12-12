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

