package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class MainActivity extends Activity {
    BartService mBService;
    boolean mBound = false;

    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS (GENERAL)
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        // Hardcoded dummy data
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
        stationMap.put("Montgomery", montgomery);
        stationMap.put("North Berkeley", nBerk);
        stationMap.put("North Concord", nConcord);
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
     *
     * TODO--INTEGRATION:  SET THE "MAP" TAB TO BE CLICKED TO BE THE VIEW
     * TODO                REPRESENTING SAID TAB IN MICHAEL'S MOBILE UI.
     */
    public void onClickMapTab(View view) {
        Intent intentMapTab = new Intent(this, MapActivity.class);
        intentMapTab.putExtra("stationsLatLngMap", getStationLatLngMap());
        startActivity(intentMapTab);
    }
}
