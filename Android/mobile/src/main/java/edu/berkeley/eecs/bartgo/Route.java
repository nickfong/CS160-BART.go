package edu.berkeley.eecs.bartgo;

public class Route {
    protected String name;
    protected String abbreviation;
    protected String Id;
    protected String number;
    protected String color;

    public Route(String name, String abbreviation, String Id, String number, String color) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.Id = Id;
        this.number = number;
        this.color = color;
    }
}
