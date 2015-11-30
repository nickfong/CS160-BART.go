package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity implements OnMapReadyCallback {
    BartService mBService;
    boolean mBound = false;

    static String currList = "Favorites";
    HashMap<String, Integer> fullHash = new HashMap<String, Integer>();
    ArrayList<String> allStationsList;
    ArrayList<String> favoritesList;
    ArrayList<Integer> favoritesImageList;
    HashMap<Integer, String> allStationsHash = new HashMap<Integer, String>();
    HashMap<Integer, String> favoritesHash = new HashMap<Integer, String>();

    HashMap<String, LatLng> stationLatLngMap;

    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS (GENERAL)
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#1e2a37"));

        final ListView listView = (ListView) findViewById(R.id.listView);
        final MapFragment mapFrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag));
        final FrameLayout mapFrame = (FrameLayout) findViewById(R.id.mapFrame);
        final RelativeLayout mapFrameWrap = (RelativeLayout) findViewById(R.id.mapFrameWrap);
        final Button allStationsButton = (Button) findViewById(R.id.allStations);
        final Button favoritesButton = (Button) findViewById(R.id.favoritesButton);
        final Button mapButton = (Button) findViewById(R.id.mapButton);
        final TextView underlineFavorites = (TextView) findViewById(R.id.underlineFavorites);
        final TextView underlineAll = (TextView) findViewById(R.id.underlineAll);
        final TextView underlineMap = (TextView) findViewById(R.id.underlineMap);

        underlineAll.setBackgroundColor(Color.parseColor("#2C3E50"));
        underlineMap.setBackgroundColor(Color.parseColor("#2C3E50"));
        allStationsButton.setTextColor(Color.parseColor("#95A5A6"));
        mapButton.setTextColor(Color.parseColor("#95A5A6"));

        createAllStationsHash();
        createFavoritesHash();
        listView.setAdapter(setFavoriteStations());
        stationLatLngMap = getStationLatLngMap();

        // Generate mapFragment for Map tab
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        // Generate Spinners and OnClickListeners
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dest = "";
                if (MainActivity.currList == "All") {
                    dest = allStationsHash.get(position);
                }
                else {
                    dest = favoritesHash.get(position);
                }

                // Prepare extras from data (ie. the selected station)
                LatLng destLatLng = stationLatLngMap.get(dest);
                Double destLat = destLatLng.latitude;
                Double destLng = destLatLng.longitude;
                // Dummy origin data
                Double origLat = 37.875173;
                Double origLng = -122.260172;

                // Create post-selection intent and put extras
                Intent postSelection = new Intent();
                postSelection.setClass(view.getContext(), postSelection.class);
                postSelection.putExtra("destName", dest);
                postSelection.putExtra("destLat", destLat);
                postSelection.putExtra("destLng", destLng);
                postSelection.putExtra("origLat", origLat);
                postSelection.putExtra("origLng", origLng);
                startActivityForResult(postSelection, 1);
            }
        });

        allStationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currList = "All";
                listView.setAdapter(setAllStations());
                // mapFrag.getView().setVisibility(View.INVISIBLE);
//                mapFrame.setVisibility(View.INVISIBLE);
                mapFrameWrap.setVisibility(View.INVISIBLE);
                underlineAll.setBackgroundColor(Color.parseColor("#F39C12"));
                underlineMap.setBackgroundColor(Color.parseColor("#2C3E50"));
                underlineFavorites.setBackgroundColor(Color.parseColor("#2C3E50"));
                allStationsButton.setTextColor(Color.parseColor("#F39C12"));
                favoritesButton.setTextColor(Color.parseColor("#95A5A6"));
                mapButton.setTextColor(Color.parseColor("#95A5A6"));
            }
        });

        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currList = "Favorites";
                listView.setAdapter(setFavoriteStations());
//                mapFrag.getView().setVisibility(View.INVISIBLE);
//                mapFrame.setVisibility(View.INVISIBLE);
                mapFrameWrap.setVisibility(View.INVISIBLE);
                underlineFavorites.setBackgroundColor(Color.parseColor("#F39C12"));
                underlineAll.setBackgroundColor(Color.parseColor("#2C3E50"));
                underlineMap.setBackgroundColor(Color.parseColor("#2C3E50"));
                favoritesButton.setTextColor(Color.parseColor("#F39C12"));
                allStationsButton.setTextColor(Color.parseColor("#95A5A6"));
                mapButton.setTextColor(Color.parseColor("#95A5A6"));
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setAdapter(clearStations());
//                mapFrag.getView().setVisibility(View.VISIBLE);
//                mapFrame.setVisibility(View.VISIBLE);
                mapFrameWrap.setVisibility(View.VISIBLE);
                underlineMap.setBackgroundColor(Color.parseColor("#F39C12"));
                underlineAll.setBackgroundColor(Color.parseColor("#2C3E50"));
                underlineFavorites.setBackgroundColor(Color.parseColor("#2C3E50"));
                mapButton.setTextColor(Color.parseColor("#F39C12"));
                favoritesButton.setTextColor(Color.parseColor("#95A5A6"));
                allStationsButton.setTextColor(Color.parseColor("#95A5A6"));
//                onClickMapTab(mapButton);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BartService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BartService.LocalBinder binder = (BartService.LocalBinder) service;
            BartService mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /* Returns a HashMap between station names and
     * latitude-longitude coordinates.
     *
     * TODO--INTEGRATION:  CHANGE TO ACCEPT INPUT FROM NICK'S API DATA
     *
     * @return          A HashMap<String, LatLng> station mapping
     */
    public HashMap<String, LatLng> getStationLatLngMap() {
        HashMap<String,LatLng> stationMap = new HashMap<>();

        // Hardcoded station data
        LatLng oak12thSt = new LatLng(37.803664, -122.271604);
        LatLng mission16thSt = new LatLng(37.765062, -122.419694);
        LatLng oak19thSt = new LatLng(37.80787, -122.269029);
        LatLng mission24thSt = new LatLng(37.752254, -122.418466);
        LatLng ashby  = new LatLng(37.853024, -122.26978);
        LatLng balboaPark = new LatLng(37.72198087, -122.4474142);
        LatLng bayFair = new LatLng(37.697185, -122.126871);
        LatLng castroValley = new LatLng(37.690754, -122.075567);
        LatLng civicUN = new LatLng (37.779528, -122.413756);
        LatLng coliseum = new LatLng(37.754006, -122.197273);
        LatLng colma = new LatLng(37.684638, -122.466233);
        LatLng concord = new LatLng(37.973737, -122.029095);
        LatLng dalyCity = new LatLng(37.70612055, -122.4690807);
        LatLng downtownBerk = new LatLng(37.869867, -122.268045);
        LatLng dublinPleas = new LatLng(37.701695, -121.900367);
        LatLng ecDelNorte = new LatLng(37.925655, -122.317269);
        LatLng ecPlaza = new LatLng(37.9030588, -122.2992715);
        LatLng embarcadero = new LatLng(37.792976, -122.396742);
        LatLng fremont = new LatLng(37.557355, -121.9764);
        LatLng fruitvale = new LatLng(37.774963, -122.224274);
        LatLng glenPark= new LatLng(37.732921, -122.434092);
        LatLng hayward = new LatLng(37.670399, -122.087967);
        LatLng lafayette = new LatLng(37.893394, -122.123801);
        LatLng lakeMerritt = new LatLng(37.797484, -122.265609);
        LatLng macArthur = new LatLng(37.828415, -122.267227);
        LatLng millbrae = new LatLng(37.599787, -122.38666);
        LatLng montgomery = new LatLng(37.789256, -122.401407);
        LatLng nBerk = new LatLng(37.87404, -122.283451);
        LatLng nConcord = new LatLng(38.003275, -122.024597);
        LatLng oakAir = new LatLng(37.71297174, -122.21244024);
        LatLng orinda = new LatLng(37.87836087, -122.1837911);
        LatLng pittsBay = new LatLng(38.018914, -121.945154);
        LatLng pHilCC = new LatLng(37.928403, -122.056013);
        LatLng powell = new LatLng(37.784991, -122.406857);
        LatLng richmond = new LatLng(37.936887, -122.353165);
        LatLng rockridge = new LatLng(37.844601, -122.251793);
        LatLng sanBruno = new LatLng(37.637753, -122.416038);
        LatLng sfAir = new LatLng(37.616035, -122.392612);
        LatLng sanLeandro = new LatLng(37.72261921, -122.1613112);
        LatLng sHayward = new LatLng(37.63479954, -122.0575506);
        LatLng sSF = new LatLng(37.664174, -122.444116);
        LatLng unionCity = new LatLng(37.591208, -122.017867);
        LatLng walCreek = new LatLng(37.905628, -122.067423);
        LatLng wDublinPleas = new LatLng(37.699759, -121.928099);
        LatLng wOak = new LatLng(37.80467476, -122.2945822);

        stationMap.put("12th St. Oakland City Center", oak12thSt);
        stationMap.put("16th St. Mission", mission16thSt);
        stationMap.put("19th St. Oakland", oak19thSt);
        stationMap.put("24th St. Mission", mission24thSt);
        stationMap.put("Ashby", ashby);
        stationMap.put("Balboa Park", balboaPark);
        stationMap.put("Bay Fair", bayFair);
        stationMap.put("Castro Valley", castroValley);
        stationMap.put("Civic Center/UN Plaza", civicUN);
        stationMap.put("Coliseum", coliseum);
        stationMap.put("Colma", colma);
        stationMap.put("Concord", concord);
        stationMap.put("Daly City", dalyCity);
        stationMap.put("Downtown Berkeley", downtownBerk);
        stationMap.put("Dublin/Pleasanton", dublinPleas);
        stationMap.put("El Cerrito del Norte", ecDelNorte);
        stationMap.put("El Cerrito Plaza", ecPlaza);
        stationMap.put("Embarcadero", embarcadero);
        stationMap.put("Fremont", fremont);
        stationMap.put("Fruitvale", fruitvale);
        stationMap.put("Glen Park", glenPark);
        stationMap.put("Hayward", hayward);
        stationMap.put("Lafayette", lafayette);
        stationMap.put("Lake Merritt", lakeMerritt);
        stationMap.put("MacArthur", macArthur);
        stationMap.put("Millbrae", millbrae);
        stationMap.put("Montgomery St.", montgomery);
        stationMap.put("North Berkeley", nBerk);
        stationMap.put("North Concord/Martinez", nConcord);
        stationMap.put("Oakland Int'l Airport", oakAir);
        stationMap.put("Orinda", orinda);
        stationMap.put("Pittsburg/Bay Point", pittsBay);
        stationMap.put("Pleasant Hill/Contra Costa Centre", pHilCC);
        stationMap.put("Powell St.", powell);
        stationMap.put("Richmond", richmond);
        stationMap.put("Rockridge", rockridge);
        stationMap.put("San Bruno", sanBruno);
        stationMap.put("San Francisco Int'l Airport", sfAir);
        stationMap.put("San Leandro", sanLeandro);
        stationMap.put("South Hayward", sHayward);
        stationMap.put("South San Francisco", sSF);
        stationMap.put("Union City", unionCity);
        stationMap.put("Walnut Creek", walCreek);
        stationMap.put("West Dublin/Pleasanton", wDublinPleas);
        stationMap.put("West Oakland", wOak);

        return stationMap;
    }

    /**
     * Transition to MapActivity, which displays the interactive station
     * selection map, upon the user tapping on the maps tab.
     */
    public void onClickMapTab(View view) {
        Intent intentMapTab = new Intent(this, MapActivity.class);
        intentMapTab.putExtra("stationsLatLngMap", stationLatLngMap);
        startActivity(intentMapTab);
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
    // MAP GENERATION:  LAT-LON RETRIEVAL                        (HELPER METHODS) //
    ////////////////////////////////////////////////////////////////////////////////
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







    ////////////////////////////////////////////////////////////////////////////////
    // MOBILE UI:  CUSTOM LIST GENERATOR
    ////////////////////////////////////////////////////////////////////////////////
    public class CustomList extends ArrayAdapter<String> {

        private final Activity context;
        private final ArrayList<String> web;
        private final ArrayList<Integer> imageId;
        public CustomList (Activity context,
                           ArrayList<String> web, ArrayList<Integer> imageId) {
            super(context, R.layout.simple_list_item_1, web);
            this.context = context;
            this.web = web;
            this.imageId = imageId;

        }
        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.simple_list_item_1, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

            final ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            txtTitle.setText(web.get(position));

            imageView.setImageResource(imageId.get(position));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currList == "All") {
                        boolean notStarred = true;
                        boolean imgChanged = false;
                        String temp = "";
                        for (String station : favoritesList) {
                            if (allStationsHash.get(position) == station) {
                                temp = station;
                                imageView.setImageResource(R.drawable.graystar);
                                imgChanged = true;
                                notStarred = false;
                            }
                        }
                        if (temp != "") {
                            favoritesList.remove(temp);
                        }
                        if (imgChanged == false && notStarred) {
                            favoritesList.add(allStationsHash.get(position));
                            imageView.setImageResource(R.drawable.star);
                            imgChanged = true;
                        }
                    }
                }
            });
            return rowView;
        }
    }






    ////////////////////////////////////////////////////////////////////////////////
    // MOBILE UI:  CREATION METHODS
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * TODO--INTEGRATION:  CHANGE TO ACCEPT INPUT FROM NICK'S API DATA
     *
     */
    public void createAllStationsHash() {
        // Hard-coded station data
        allStationsList =  new ArrayList<>(45);
        allStationsList.add("12th St. Oakland City Center");
        allStationsList.add("16th St. Mission");
        allStationsList.add("19th St. Oakland");
        allStationsList.add("24th St. Mission");
        allStationsList.add("Ashby");
        allStationsList.add("Balboa Park");
        allStationsList.add("Bay Fair");
        allStationsList.add("Castro Valley");
        allStationsList.add("Civic Center/UN Plaza");
        allStationsList.add("Coliseum");
        allStationsList.add("Colma");
        allStationsList.add("Concord");
        allStationsList.add("Daly City");
        allStationsList.add("Downtown Berkeley");
        allStationsList.add("Dublin/Pleasanton");
        allStationsList.add("El Cerrito del Norte");
        allStationsList.add("El Cerrito Plaza");
        allStationsList.add("Embarcadero");
        allStationsList.add("Fremont");
        allStationsList.add("Fruitvale");
        allStationsList.add("Glen Park");
        allStationsList.add("Hayward");
        allStationsList.add("Lafayette");
        allStationsList.add("Lake Merritt");
        allStationsList.add("MacArthur");
        allStationsList.add("Millbrae");
        allStationsList.add("Montgomery St.");
        allStationsList.add("North Berkeley");
        allStationsList.add("North Concord/Martinez");
        allStationsList.add("Oakland Int'l Airport");
        allStationsList.add("Orinda");
        allStationsList.add("Pittsburg/Bay Point");
        allStationsList.add("Pleasant Hill/Contra Costa Centre");
        allStationsList.add("Powell St.");
        allStationsList.add("Richmond");
        allStationsList.add("Rockridge");
        allStationsList.add("San Bruno");
        allStationsList.add("San Francisco Int'l Airport");
        allStationsList.add("San Leandro");
        allStationsList.add("South Hayward");
        allStationsList.add("South San Francisco");
        allStationsList.add("Union City");
        allStationsList.add("Walnut Creek");
        allStationsList.add("West Dublin/Pleasanton");
        allStationsList.add("West Oakland");
        Collections.sort(allStationsList);
        int count = 0;
        for (String station:allStationsList) {
            allStationsHash.put(count, station);
            fullHash.put(station, count);
            count++;
        }
    }

    public void createFavoritesHash() {
        // Hardcoded (intended as default?) data
        favoritesList =  new ArrayList<>(45);
        favoritesList.add("12th St. Oakland City Center");
        favoritesList.add("Civic Center/UN Plaza");
        favoritesList.add("Coliseum");
        favoritesList.add("Embarcadero");
        favoritesList.add("Montgomery St.");
        favoritesList.add("Rockridge");
        favoritesList.add("San Francisco Int'l Airport");
        favoritesList.add("Walnut Creek");
        favoritesList.add("West Dublin/Pleasanton");
        Collections.sort(favoritesList);
        int count = 0;
        for (String station:favoritesList) {
            favoritesHash.put(count, station);
            count++;
        }
    }

    public CustomList setAllStations(){
        favoritesImageList = new ArrayList<Integer>(Collections.nCopies(45, R.drawable.graystar));
        Collections.sort(favoritesList);
        int count = 0;
        for (String station:favoritesList) {
            favoritesHash.put(count, station);
            count++;
        }
        for (String station:favoritesHash.values()) {
            favoritesImageList.set(fullHash.get(station), R.drawable.star);
        }

        CustomList adapter = new CustomList(MainActivity.this, allStationsList, favoritesImageList);
        return adapter;
    }

    public ArrayAdapter<String> setFavoriteStations(){
        favoritesImageList = new ArrayList<Integer>(favoritesList.size());
        Collections.sort(favoritesList);
        int count = 0;
        while (count < favoritesList.size()){
            favoritesImageList.add(R.drawable.star);
            count++;
        }
        count = 0;
        for (String station:favoritesList) {
            favoritesHash.put(count, station);
            count++;
        }

        CustomList adapter = new CustomList(MainActivity.this, favoritesList, favoritesImageList);
        return adapter;
    }

    public ArrayAdapter<String> clearStations(){
        ArrayList<String> list =  new ArrayList<>(1);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, list);
        return listAdapter;
    }

}
