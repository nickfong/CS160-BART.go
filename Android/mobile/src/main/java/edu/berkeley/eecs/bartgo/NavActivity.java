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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

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

/**
 * An Activity classes which interfaces with the Google Maps Directions API to generate
 * turn-by-turn naviagtion directions.
 *
 * This class also handles Mobile-side processes in the Mobile-Wear communication framework,
 * as the last activity to be launched before transitioning to Wear.
 */
public class NavActivity extends Activity {
    protected final static String TAG_DEBUG = "tag_debug";  // For Log.d()
    private Intent i;
    public ArrayList<NavInstruction> navInstructions = null;
    private GoogleApiClient mApiClient;

    private double currLat;
    private double currLong;
    private double trainLat;
    private double trainLong;

    private final String REFRESH_DATA = "/refresh_data";
    private final String NEW_TRAINS = "/new_trains";
    private final int fetchInterval = 5000; // Update interval in milliseconds
    private Handler handler = new Handler();




    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS (GENERAL)
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set menu bar properties
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nav);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#1e2a37"));
        Intent intent = getIntent();
        trainLat = intent.getDoubleExtra("origLat", 999999);
        trainLong = intent.getDoubleExtra("origLng", 999999);

        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mApiClient);
                        if (mLastLocation != null) {
                            currLat = mLastLocation.getLatitude();
                            currLong = mLastLocation.getLongitude();
                        }
                        // Fetches intent from postSelection to retrieve the string of train times in millis
                        String trainTimes = getIntent().getStringExtra("trains");
                        // Initiates watch message interaction.  Sends string of train times to watch
                        initialize(trainTimes);
                        handler.removeCallbacks(updater);
                        // periodically sends user eta to station to watch every 5 seconds.
                        updater.run();
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
        mApiClient.connect();
        // Query for navigation if toggle switch was checked.
        // Else, notice that the navInstructions ArrayList remains null.
        // As such, a null check on it can also be used to determine whether or not
        //   navigation has been requested.
        i = getIntent();

        if (i.getBooleanExtra("isChecked", false)) {
            runNav();
        }
        Log.d(TAG_DEBUG, "******* What is navInstructions?  It is " + navInstructions);
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
        String oLat = String.valueOf(i.getDoubleExtra("origLat", 999999));
        String oLng = String.valueOf(i.getDoubleExtra("origLng", 999999));
        String dLat = String.valueOf(i.getDoubleExtra("destLat", 999999));
        String dLng = String.valueOf(i.getDoubleExtra("destLng", 999999));

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + oLat + "," + oLng + "&destination=" + dLat + "," + dLng + "&mode=walking&key=" + PrivateConstants.GOOGLE_API_SERVER_KEY;
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
    public void runNav() {
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
            // String nav = sbNav.toString();
            // TextView tv = (TextView) findViewById(R.id.testNavOut);
            // tv.setText(Html.fromHtml(nav));
        } catch (JSONException e) {
            Log.e("Error", "JSONException while parsing DirectionsAPI JSON!");
            return;
        }
    }




    ////////////////////////////////////////////////////////////////////////////////
    // MOBILE-WEAR COMMUNICATION FRAMEWORK
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * A Periodic Runnable which sends user ETA updates to the watch.  Updates are based on
     * the user's location and distance covered within the last update interval.
     */
    public Runnable updater = new Runnable() {

        @Override
        public void run() {
            // Retrieve last location's lat and lng
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            if (mLastLocation != null) {
                currLat = mLastLocation.getLatitude();
                currLong = mLastLocation.getLongitude();
                // Calcualate distance to nearest BART station
                double distance = getDistance(currLat, currLong);
//                double pace = (prevDist - distance) / fetchInterval;
//                double millisRemaining = distance / pace;
                long arrival = new java.util.Date().getTime();
//                if (millisRemaining > 0) {
//                    arrival += (long) millisRemaining;
//                } else {
                // arrival is the project arrival time at the station in millis assuming an average walking speed
                arrival += (long) (distance / 0.0014); // avg walking velocity == 0.0014 m / ms
//                }
                // a string of the arrival time is sent to wear
                sendMessage(REFRESH_DATA, arrival + "");
//                prevDist = distance;
            }
            // controls looping after specified delay interval.
            handler.postDelayed(updater, fetchInterval);
        }
    };

    /**
     * Sends a message to Wear with the specified path and data.
     *
     * @param path      The message path.
     * @param text      The message data as a String.
     */
    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }

    /**
     * Initializes the Wear interaction.  Also checks whether turn-by-turn navigation instructions
     * were requested, and appends this information to the train ETA update message sent.
     *
     * @param trains    The String of train times (in millis since the Unix epoch) to be sent
     *                  to Wear.
     */
    public void initialize(String trains) {
        if (navInstructions != null) {
            trains += "1";
        } else {
            trains += "0";
        }
        sendMessage(NEW_TRAINS, trains);
    }

    /**
     * Returns the distance, in meters, to the BART station nearest to the specified location.
     *
     * @param latitude      Latitude, as a double.
     * @param longitude     Longitude, as a double.
     * @return              The distance to the nearest station, in meters.
     */
    public double getDistance(Double latitude, Double longitude) {
        Location stationLoc = new Location("Station");
        stationLoc.setLatitude(trainLat);
        stationLoc.setLongitude(trainLong);
        Location userLoc = new Location("User");
        userLoc.setLatitude(latitude);
        userLoc.setLongitude(longitude);
        return (double) userLoc.distanceTo(stationLoc);
    }

    // Deprecated getDistance method.
        // public double getDistance(Double latitude, Double longitude) {
        // double trainLat2 = Math.toRadians(trainLat);
        // double currLat2 = Math.toRadians(latitude);
        // double deltaLong = Math.toRadians(trainLong - longitude);
        // double deltaLat = Math.toRadians(trainLat - latitude);
        // double radius = 6371000;
        // double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(currLat2) * Math.cos(trainLat2) * Math.sin(deltaLong/2) * Math.sin(deltaLong/2);
        // double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        // return radius * c;
    // }

}
