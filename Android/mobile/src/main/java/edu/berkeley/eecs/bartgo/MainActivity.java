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
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;

public class MainActivity extends Activity {
    BartService mBService;
    boolean mBound = false;

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

        /*
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        LatLng  = new LatLng(,);
        */


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

        /*
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        stationMap.put("", );
        */

        return stationMap;
    }
    public void onClickMapTab(View view) {
        Intent intentMapTab = new Intent(this, MapActivity.class);
        intentMapTab.putExtra("stationsLatLngMap", getStationLatLngMap());
        startActivity(intentMapTab);
    }
}
