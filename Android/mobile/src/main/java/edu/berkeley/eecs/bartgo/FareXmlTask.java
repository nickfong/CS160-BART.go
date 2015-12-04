package edu.berkeley.eecs.bartgo;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class FareXmlTask extends BartXmlTask {
    private final String TAG = "FareXmlTask";

    String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        FareXmlParser fareParser = new FareXmlParser();
        ArrayList<String> fares = null;

        try {
            stream = downloadUrl(urlString);
            fares = (ArrayList)fareParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        String fare_string = "";
        for (String fare : fares) {
            fare_string += fare;
        }
        return fare_string;
    }
}
