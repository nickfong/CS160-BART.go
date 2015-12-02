package edu.berkeley.eecs.bartgo;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class LegsXmlTask extends AsyncTask<String, Void, String> {
    private final String TAG = "LegsXmlTask";

    @Override
    protected String doInBackground(String... urls) {
        try {
            return loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
            return "Connection error"; //getResources().getString(R.string.connection_error);
        } catch (XmlPullParserException e) {
            Log.e(TAG, String.valueOf(e));
            return "XML Error:" + e; //getResources().getString(R.string.xml_error);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
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
