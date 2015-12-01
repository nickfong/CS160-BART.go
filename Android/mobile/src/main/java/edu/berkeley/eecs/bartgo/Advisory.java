package edu.berkeley.eecs.bartgo;

public class Advisory {
    protected String date;
    protected String time;
    protected String id;
    protected String type;
    protected String text;

    public Advisory(String date, String time, String id, String type, String text) {
        this.date = date;
        this.time = time;
        this.id = id;
        this.type = type;
        this.text = text;
    }
}
