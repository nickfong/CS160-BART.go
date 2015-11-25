package edu.berkeley.eecs.bartgo;

/**
 * A simple class for organizing data pertinent to navigation instructions.
 * Created by Krystyn on 11/25/2015.
 */

public class NavInstruction {
    String textInstruction = "";
    String distance = "-1";
    String duration = "-1";

    /* Empty constructor. */
    NavInstruction() {
        textInstruction = null;
        distance = "-1";
        duration = "-1";
    }

    /* Constructor which populates NaveInstruction instance
     * according to the instruction text, distance, and duration
     * String arguments.
     */
    NavInstruction(String text, String dist, String dur) {
        textInstruction = text;
        distance = dist;
        duration = dur;
    }

    /* Convenient get-methods to retrieve each field. */
    public String getText() {
        return textInstruction;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }
}
