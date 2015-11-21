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
    public HashMap<String, LatLng> getStationLatLng() {
        // Hardcoded dummy data
        LatLng oak12thSt = new LatLng(37.803664, -122.271604);
        LatLng mission16thSt = new LatLng(37.765062, -122.419694);

        HashMap<String,LatLng> stationMap = new HashMap<>();
        stationMap.put("12th St. Oakland City Center", oak12thSt);
        stationMap.put("16th St. Mission", mission16thSt);

        return stationMap;
    }
    public void onClickMapTab(View view) {
        Intent intentMapTab = new Intent(this, MapActivity.class);
        intentMapTab.putExtra("stationsLatLngMap", getStationLatLng());
        startActivity(intentMapTab);
    }
}
