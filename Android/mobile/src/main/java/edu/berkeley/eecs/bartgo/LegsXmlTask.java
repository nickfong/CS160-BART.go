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

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class LegsXmlTask extends BartXmlTask {
    private final String TAG = "LegsXmlTask";

    String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        LegsXmlParser legsParser = new LegsXmlParser();
        ArrayList<String> legs = null;

        try {
            stream = downloadUrl(urlString);
            legs = (ArrayList)legsParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        String output_string = "";
        for (String l : legs) {
            output_string += l + "\n";
        }
        return output_string;
    }
}
