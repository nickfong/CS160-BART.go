package edu.berkeley.eecs.bartgo;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class TrainXmlTask extends BartXmlTask {
    private final String TAG = "TrainXmlTask";

    String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        TrainXmlParser trainParser = new TrainXmlParser();
        ArrayList<String> trains = null;

        try {
            stream = downloadUrl(urlString);
            trains = (ArrayList)trainParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        String estimate = "";
        for (String s : trains) {
            estimate += s + ";";
        }

        if (estimate.equals("")) {
            return "";
        }
        return estimate.substring(0, estimate.length() - 1);
    }
}
