package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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
    protected final static String TAG_DEBUG = "tag_debug";
    public ArrayList<String> navInstructions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
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


    protected class NavTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... myurl) {
            InputStream is = null;
            String jsonString = null;
            try {
                URL url = new URL(myurl[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                // Starts the query
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
        protected void onPostExecute(String result) {/* Do nothing. */}
    }

    public void runDemoNav() {
        String testUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=37.877262,-122.259311&destination=37.869944,-122.268142&mode=walking&key=AIzaSyBTE2CQqCh-0LXhMyuWS8csfkyhQgF4n4c";
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

        // Parse json file!
        JSONObject query = null;
        try {
            // Retrieve initial JSON Object representing entire query
            query = new JSONObject(jsonResultString);
            // Log.d(TAG_DEBUG, "******* Printing json object:\n" + query.toString());
            Log.d(TAG_DEBUG, "******* MEEP!  Parsing JSON, 1 success.");
            // Retrieve the "routes" JSON Array
            JSONArray routes = query.getJSONArray("routes");
            // Log.d(TAG_DEBUG, "******* Printing json routes object:\n" + routes.toString());
            Log.d(TAG_DEBUG, "******* MEEP!  Parsing JSON, 2 successes.");
            // Retrieve the first route in "routes" array
            JSONObject route = routes.getJSONObject(0);
            Log.d(TAG_DEBUG, "******* MEEP!  Parsing JSON, 2.5 successes.");
            // Retrieve the "legs" JSON Object from "routes" object
            JSONArray legs = route.getJSONArray("legs");
            Log.d(TAG_DEBUG, "******* MEEP!  Parsing JSON, 3 successes.");
            // Retrieve the first leg of the "legs" array
            JSONObject leg = legs.getJSONObject(0);
            // Retrieve the "steps" JSON Array of the leg object
            JSONArray steps = leg.getJSONArray("steps");
            Log.d(TAG_DEBUG, "******* MEEP!  Parsing JSON, 4 successes.");

            // Iterate through, retrieve "html_instructions" JSON Object from each
            int len = steps.length();
            StringBuilder sbNav = new StringBuilder();
            navInstructions = new ArrayList<>();
            Log.d(TAG_DEBUG, "******* MEEP!  Parsing JSON, 5 successes.");

            // Access each element in the steps array, extract "html_instructions" string
            for (int i = 0; i < len; i++) {
                // Retrieve ith step in steps
                JSONObject step = steps.getJSONObject(i);
                String instruct = step.getString("html_instructions");
                sbNav.append(instruct + "<br>");
                navInstructions.add(instruct);
            }
            String nav = sbNav.toString();
            Log.d(TAG_DEBUG, "******* MEEP!  Parsing JSON, 6 successes.");

            TextView tv = (TextView) findViewById(R.id.testNavOut);
            tv.setText(Html.fromHtml(nav));
        } catch (JSONException e) {
            Log.e("Error", "JSONException while parsing DirectionsAPI JSON!");
            return;
        }
    }
}
