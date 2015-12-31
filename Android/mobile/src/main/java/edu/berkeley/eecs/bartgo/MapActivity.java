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

/**
 * THIS ACTIVITY IS DEPRECATED!
 *
 * Its features have been incorporated into MainActivity.
 */

package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

import edu.berkeley.eecs.bartgo.NavActivity;
import edu.berkeley.eecs.bartgo.R;

public class MapActivity extends Activity implements OnMapReadyCallback {

    ////////////////////////////////////////////////////////////////////////////////
    // GLOBAL VARS (GENERAL)
    ////////////////////////////////////////////////////////////////////////////////
    protected final static String TAG_DEBUG = "tag_debug";  // Was used for Log.d()
    HashMap<String, LatLng> stationLatLngMap;





    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Retrieve stations-latlng hashmap
        setStations();

        // Generate map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);
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





    ////////////////////////////////////////////////////////////////////////////////
    // MAP GENERATION
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Generates a scaled bitmap icon  from an R.drawable element.
     *
     * @param   resId   The image id to be used as the icon.  Of the form
     *                  "R.drawable.image_name"
     * @param   scale   The "down-scaling" factor.  Ie. width and height are scaled
     *                  by a factor of 1 / scale.
     * @return          The scaled bitmap.
     */
    public Bitmap generateIcon(int resId, int scale) {
        Bitmap b = BitmapFactory.decodeResource(getResources(), resId);
        Bitmap bScaled = Bitmap.createScaledBitmap(b, b.getWidth() / scale, b.getHeight() / scale, false);
        return bScaled;
    }

    /**
     * Sets map camera zoom and places markers upon the given map's readiness.
     * Tap: display station name and station details.
     * Long-Tap: launch turn-by-turn navigation (NavActivity) to selected station.
     *
     * TODO--INTEGRATION:  POPULATE STATION DETAILS WITH RELEVANT INFO AS DECIDED BY GROUP
     * TODO--INTEGRATION:  REPLACE DUMMY ORIGIN LAT/LNG DATA (SEE onMarkerDragStart())
     * TODO                WITH ACTUAL CURRENT POS CALCULATED IN PATRICK'S MAIN ACTIVITY.
     *
     * @param   map     The GoogleMap instance to display.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        // Set camera zoom
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(37.804697, -122.201255), (float) 9.5));

        // Iterate through all stations in the stationLatLngMap,
        // generating a Marker for each
        Set<Map.Entry<String, LatLng>> entries = stationLatLngMap.entrySet();
        Iterator<Map.Entry<String, LatLng>> iter = entries.iterator();

        for (int i = 0; i < stationLatLngMap.size(); i++) {
            Map.Entry<String, LatLng> entry = iter.next();
            LatLng val = entry.getValue();
            String stationName = entry.getKey();

            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(generateIcon(R.drawable.marker_bartgo_logo_round, 2)))
                    .anchor(0.5f, 1.0f) /*Anchors the marker on the bottom center */
                    .position(val)
                    .title(stationName + " BART")
                    .snippet("<Insert additional station info here!>")
                    .draggable(true));
            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                // Simulate long-click functionality
                @Override
                public void onMarkerDragStart(Marker marker) {
                    Intent startNavIntent = new Intent(getBaseContext(), NavActivity.class);
                    // dummy origin data
                    startNavIntent.putExtra("origLat", 37.875173);
                    startNavIntent.putExtra("origLng", -122.260172);

                    // Retrieve destination based on marker being long-tapped on
                    String stationKey = marker.getTitle();
                    int len = stationKey.length();
                    stationKey = stationKey.substring(0, len - 5);
                    LatLng stationLatLng = getStationLatLng(stationKey);

                    startNavIntent.putExtra("destLat", stationLatLng.latitude);
                    startNavIntent.putExtra("destLng", stationLatLng.longitude);

                    startActivity(startNavIntent);
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    // Nothing special to do
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    // Do special to do
                }
            });
        }

    }





    ////////////////////////////////////////////////////////////////////////////////
    // LAT-LON RETRIEVAL                                         (HELPER METHODS) //
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Retrieves and sets global station-latlng HashMap.
     */
    public void  setStations() {
        Intent i = getIntent();
        stationLatLngMap = (HashMap<String, LatLng>) i.getSerializableExtra("stationsLatLngMap");
    }

    /**
     * Returns an array containing all station names.
     *
     * @return          A String[] of all station names, retrieved from global HashMap.
     */
    public String[] getStations() {
        return (String[]) stationLatLngMap.keySet().toArray();
    }

    /**
     * Returns a station latitutde-longitude coordinates
     *
     * @param   name    The station's name.
     * @return          The station's latitude and longitude, as a LatLng.
     */
    public LatLng getStationLatLng(String name) {
        return stationLatLngMap.get(name);
    }
}
