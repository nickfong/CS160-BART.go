package edu.berkeley.eecs.bartgo;

/**
 * A simple class for organizing data pertinent to navigation instructions.
 * Each NavInstruction stores the following data as Strings:
 *      Text instruction
 *          Ex:  "Head <b>south</b> on <b>Euclid Ave</b> toward <b>Hearst Ave</b>"
 *          Notice that this string contains HTML formatting tags.  To properly display,
 *          rather than setting a TextView to this string directly
 *          (eg. view.setText(your_text_instruction)), instead use Html.fromHtml
 *          (eg. view.setText(Html.fromHtml(your_text_instruction))).
 *      Instruction distance  (ie. physical distance until the next instruction)
 *          Ex. "92 ft"
 *      Instruction duration (ie. projected time duration until completion)
 *          Ex. "1 min"
 */

public class NavInstruction {
    String textInstruction = "";
    String distance = "-1";
    String duration = "-1";

    ////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Empty constructor.
     */
    NavInstruction() {
        textInstruction = null;
        distance = "-1";
        duration = "-1";
    }

    /**
     *Constructor which populates NaveInstruction instance according to the
     * instruction text, distance, and duration String arguments.
     */
    NavInstruction(String text, String dist, String dur) {
        textInstruction = text;
        distance = dist;
        duration = dur;
    }





    ////////////////////////////////////////////////////////////////////////////////
    // GET-METHODS
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * A collection of convenient get-methods to retrieve each field.
     */

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
