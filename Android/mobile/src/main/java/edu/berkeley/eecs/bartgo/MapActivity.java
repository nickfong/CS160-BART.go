package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Set;

public class MapActivity extends Activity {

    TextView testTV1;
    TextView testTV2;
    HashMap<String, LatLng> stationHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getAndSetStations();
        displayTestVals();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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

    /* Returns station names as a Set<String>. */
    public Set<String>  getAndSetStations() {
        Intent i = getIntent();
        stationHashMap = (HashMap<String, LatLng>) i.getSerializableExtra("stationsLatLngMap");
        return stationHashMap.keySet();
    }

    /* Returns station latitutde-longitude as a LatLng. */
    public LatLng getStationLatLng(String name) {
        return stationHashMap.get(name);
    }

    public void displayTestVals() {
        testTV1 = (TextView) findViewById(R.id.testOut1);
        testTV2 = (TextView) findViewById(R.id.testOut2);

        String oakLatLng = getStationLatLng("12th St. Oakland City Center").toString();
        String missionLatLng = getStationLatLng("16th St. Mission").toString();

        testTV1.setText("12th St. Oakland City Center LatLng:  " + oakLatLng);
        testTV2.setText("16th St. Mission LatLng:  " + missionLatLng);

    }
}
