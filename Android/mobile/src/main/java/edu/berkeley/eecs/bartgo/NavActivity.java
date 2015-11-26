package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NavActivity extends Activity {

    ////////////////////////////////////////////////////////////////////////////////
    //GLOBAL VARS
    ////////////////////////////////////////////////////////////////////////////////
    protected final static String TAG_DEBUG = "tag_debug";  // For Log.d()
    public ArrayList<NavInstruction> navInstructions = null;





    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS (GENERAL)
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        // Run sample navigation directions display
        runDemoNav();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    ////////////////////////////////////////////////////////////////////////////////
    // DIRECTIONS QUERY CLASS
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * A subclass for asynchronously handling navigation querying, as this may
     * potentially be a time consuming task not appropriate for executing on the
     * main UI thread.
     */
    protected class NavTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... myurl) {
            InputStream is = null;
            String jsonString = null;
            try {
                // Set up Http connection
                URL url = new URL(myurl[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                // Start the query
                conn.connect();
                is = conn.getInputStream();
                BufferedReader buffReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                // Convert the InputStream into a string
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = buffReader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                buffReader.close();
                jsonString = sb.toString();

                return jsonString;

            } catch (IOException e) {
                Log.d(TAG_DEBUG, "***** MEEP IO Exception at doInBackground");
            } finally {
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    Log.d(TAG_DEBUG, "***** MEEP IO Exception at doInBackground finally");
                }

                return jsonString;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Nothing special to do here.
        }
    }





    ////////////////////////////////////////////////////////////////////////////////
    // QUERYING FOR DIRECTIONS
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Retrieves the start and destination LatLng objects (passed as extras from
     * the MapActivity), and generates the appropriate query url for the Google
     * Maps Directions API.  This query assumes walking as the user's mode of transit.
     *
     * @return          The query URL, as a String.
     */
    private String generateNavQueryUrlString () {
        Intent i = getIntent();
        String oLat = String.valueOf(i.getDoubleExtra("origLat", 999999));
        String oLng = String.valueOf(i.getDoubleExtra("origLng", 999999));
        String dLat = String.valueOf(i.getDoubleExtra("destLat", 999999));
        String dLng = String.valueOf(i.getDoubleExtra("destLng", 999999));

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + oLat + "," + oLng + "&destination=" + dLat + "," + dLng + "&mode=walking&key=AIzaSyBTE2CQqCh-0LXhMyuWS8csfkyhQgF4n4c";
        return url;
    }

    /**
     * Queries Google Maps Direction API, parses JSON data, and stores directions
     * into two structures:
     *      1) A single Html-formatted string, for displaying as a demo.
     *         (The setting of a TextView to display this is not intended to ship--
     *         it is merely a demonstration of functionality for dev purposes.)
     *      2) An ArrayList<NavInstruction>, where NavInstruction is a custom data
     *         structure storing pertinent navigation instruction information.
     *         See NavInstruction.java for more information.
     */
    public void runDemoNav() {
        // Execute query
        String testUrl = generateNavQueryUrlString();
        String jsonResultString = null;
        try {
            jsonResultString = new NavTask().execute(testUrl).get();
        } catch (ExecutionException e) {
            Log.e("Error", "ExecutionException while querying DirectionsAPI!");
        } catch (InterruptedException e) {
            Log.e("Error", "InterruptedException while querying DirectionsAPI!");
        }

        if (jsonResultString == null) {
            Log.e("Error", "Unable to retrieve JSON file!");
            return;
        }

        // Parse JSON file!
        JSONObject query = null;
        try {
            // Retrieve initial JSON Object representing entire query
            query = new JSONObject(jsonResultString);
            // Retrieve the "routes" JSON Array
            JSONArray routes = query.getJSONArray("routes");
            // Retrieve the first route in "routes" array
            JSONObject route = routes.getJSONObject(0);
            // Retrieve the "legs" JSON Object from "routes" object
            JSONArray legs = route.getJSONArray("legs");
            // Retrieve the first leg of the "legs" array
            JSONObject leg = legs.getJSONObject(0);
            // Retrieve the "steps" JSON Array of the leg object
            JSONArray steps = leg.getJSONArray("steps");

            // Iterate through each navigational step,
            // extracting instruction information from each
            int len = steps.length();
            StringBuilder sbNav = new StringBuilder();
            navInstructions = new ArrayList<>();

            for (int i = 0; i < len; i++) {
                // Retrieve ith step in steps
                JSONObject step = steps.getJSONObject(i);
                String instructText = step.getString("html_instructions");
                String instructDist=  step.getJSONObject("distance").getString("text");
                String instructionDur = step.getJSONObject("duration").getString("text");

                // Add to demo string
                sbNav.append(instructText +  " (" + instructDist + ", " + instructionDur + ")<br>");

                // Add to custom data structure
                NavInstruction navInst = new NavInstruction(instructText, instructDist, instructionDur);
                navInstructions.add(navInst);
            }

            // Set demo TextView
            String nav = sbNav.toString();
            TextView tv = (TextView) findViewById(R.id.testNavOut);
            tv.setText(Html.fromHtml(nav));
        } catch (JSONException e) {
            Log.e("Error", "JSONException while parsing DirectionsAPI JSON!");
            return;
        }
    }
}
