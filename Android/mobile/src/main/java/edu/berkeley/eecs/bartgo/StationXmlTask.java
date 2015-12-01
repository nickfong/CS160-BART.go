package edu.berkeley.eecs.bartgo;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class StationXmlTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        try {
            return loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
            return "Connection error"; //getResources().getString(R.string.connection_error);
        } catch (XmlPullParserException e) {
            return "XML Error"; //getResources().getString(R.string.xml_error);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        /*
        setContentView(R.layout.main);
        // Displays the HTML string in the UI via a WebView
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadData(result, "text/html", null);
        */
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
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
            station_string += station.getAbbreviation() + ";" + station.getName() + ";" + station.getAddress() + ";" + station.getZip() + "\n";
        }
        return station_string;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
