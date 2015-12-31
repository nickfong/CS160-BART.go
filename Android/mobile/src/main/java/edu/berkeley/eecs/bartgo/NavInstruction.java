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
