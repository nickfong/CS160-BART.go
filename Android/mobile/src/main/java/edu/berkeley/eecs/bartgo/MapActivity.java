package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MapActivity extends Activity implements OnMapReadyCallback {

    ////////////////////////////////////////////////////////////////////////////////
    // GLOBAL VARS
    ////////////////////////////////////////////////////////////////////////////////
    // TextView testTV1;  // FOR TESTING
    // TextView testTV2;  // FOR TESTING
//    Intent mapIntent = null;
    protected final static String TAG_DEBUG = "tag_debug";
    HashMap<String, LatLng> stationHashMap;


    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // if (mapIntent == null) {
        setStations();
        // }
        // displayTestVals();  // FOR TESTING

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);
        Log.d(TAG_DEBUG, "******* onCreate called!");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        setStations();
//    }

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
    // SAVED-INSTANCE STATE HANDLING
    ////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////
    // MAP GENERATION
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap map) {
        setStations();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(37.732026, -122.183038), (float) 9.5));

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        Set<Map.Entry<String, LatLng>> entries = stationHashMap.entrySet();
        Iterator<Map.Entry<String, LatLng>> iter = entries.iterator();

        for (int i = 0; i < stationHashMap.size(); i++) {
            Map.Entry<String, LatLng> entry = iter.next();
            final LatLng val = entry.getValue();
            String stationName = entry.getKey();

            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bart_blueback_png16))
                    /*.anchor(0.0f, 1.0f)*/ // Anchors the marker on the bottom left
                    .position(val)
                    .title(stationName + " BART")
                    .snippet("<Insert additional station info here!>")
                    .draggable(true));
            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    // Simulate long click
                    Intent startNavIntent = new Intent(getBaseContext(), NavActivity.class);
                    // startNavIntent.setFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    // dummy origin data
                    startNavIntent.putExtra("originLatLng", new LatLng(37.875173, -122.260172));
                    startNavIntent.putExtra("destLatLng", val);
                    startActivity(startNavIntent);
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    // Do nothing
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    // Do nothing
                }
            });
        }

    }

    ////////////////////////////////////////////////////////////////////////////////
    // LAT-LON RETRIEVAL                                         (HELPER METHODS) //
    ////////////////////////////////////////////////////////////////////////////////
    /* Retrieves and sets station-latlng HashMap. */
    public void  setStations() {
        Intent i = getIntent();
        // Intent i = mapIntent;
        stationHashMap = (HashMap<String, LatLng>) i.getSerializableExtra("stationsLatLngMap");
        if (stationHashMap == null) {
            Log.d(TAG_DEBUG, "******* stationHashMap:  NULL!");
        } else {
            Log.d(TAG_DEBUG, "******* stationHashMap:  NOT null! It's " + stationHashMap.toString());
        }
    }

    /* Returns station names as a String[]. */
    public String[] getStations() {
        return (String[]) stationHashMap.keySet().toArray();
    }

    /* Returns station latitutde-longitude as a LatLng. */
    public LatLng getStationLatLng(String name) {
        return stationHashMap.get(name);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // FOR DEBUGGING/TESTING
    ////////////////////////////////////////////////////////////////////////////////
    /* Displays test strings at top of screen. */
    /*
    public void displayTestVals() {
        testTV1 = (TextView) findViewById(R.id.testOut1);
        testTV2 = (TextView) findViewById(R.id.testOut2);

        String oakLatLng = getStationLatLng("12th St. Oakland City Center").toString();
        String missionLatLng = getStationLatLng("16th St. Mission").toString();

        testTV1.setText("12th St. Oakland City Center LatLng:  " + oakLatLng);
        testTV2.setText("16th St. Mission LatLng:  " + missionLatLng);
    }
    */
}
