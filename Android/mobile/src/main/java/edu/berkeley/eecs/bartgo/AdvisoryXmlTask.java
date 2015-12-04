package edu.berkeley.eecs.bartgo;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class AdvisoryXmlTask extends BartXmlTask {
    private final String TAG = "advisoryXmlTask";

    String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        AdvisoryXmlParser advisoryParser = new AdvisoryXmlParser();
        ArrayList<Advisory> advisories= null;

        try {
            stream = downloadUrl(urlString);
            advisories = (ArrayList)advisoryParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        String advisory_string = "";
        for (Advisory advisory : advisories) {
            advisory_string += advisory.id + ";" + advisory.type + ";" + advisory.description + "\n";
        }
        return advisory_string;
    }
}
