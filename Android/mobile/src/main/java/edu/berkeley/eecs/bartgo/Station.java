package edu.berkeley.eecs.bartgo;

public class Station {
    private String abbreviation;
    private String name;
    private String address;
    private String zip;

    public station(String abbreviation, String name, String address, String zip) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.address = address;
        this.zip = zip;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String retZip() {
        return this.zip;
    }

}

