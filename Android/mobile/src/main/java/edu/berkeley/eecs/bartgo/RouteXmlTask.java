package edu.berkeley.eecs.bartgo;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class RouteXmlTask extends BartXmlTask {
    private final String TAG = "RouteXmlTask";

    String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        RouteXmlParser RouteParser = new RouteXmlParser();
        ArrayList<Route> routes = null;

        try {
            stream = downloadUrl(urlString);
            routes = (ArrayList)RouteParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        String route_string = "";
        for (Route route : routes) {
            route_string += route.name + ";" + route.abbreviation + ";" + route.Id + ";" + route.number + ";" + route.color + "\n";
        }
        return route_string;
    }
}
