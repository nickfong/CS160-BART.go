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
