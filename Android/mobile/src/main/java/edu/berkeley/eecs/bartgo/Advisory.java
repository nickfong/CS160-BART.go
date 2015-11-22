package edu.berkeley.eecs.bartgo;

public class Advisory {
    String date;
    String time;
    String id;
    String type;
    String text;

    public Advisory(String date, String time, String id, String type, String text) {
        this.date = date;
        this.time = time;
        this.id = id;
        this.type = type;
        this.text = text;
    }
}
