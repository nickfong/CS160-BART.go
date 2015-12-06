package edu.berkeley.eecs.bartgo;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class StationXmlTask extends BartXmlTask {
    private final String TAG = "StationXmlTask";

    String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        StationXmlParser stationParser = new StationXmlParser();
        ArrayList<Station> stations = null;

        try {
            stream = downloadUrl(urlString);
            stations = (ArrayList)stationParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        String station_string = "";
        for (Station station : stations) {
            station_string += station.getAbbreviation() + ";" + station.getName() + ";" + station.getAddress() + ";" + station.getZip() + ";" + station.getLatitude() + ";" + station.getLatitude() +  "\n";
        }
        return station_string;
    }
}
