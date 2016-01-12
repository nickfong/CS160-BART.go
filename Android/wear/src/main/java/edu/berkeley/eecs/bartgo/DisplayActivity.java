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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * An Activity class orchestrating Activity-launching and the reception of messages broadcasted
 * from the WatchListenerService class.
 */
public class DisplayActivity extends WearableActivity {
    public static final String NAV_EXTRA = "NAV_DIRECTION";
    private static final long[] mVibrationPattern = {0, 500, 50, 300};
    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BroadcastReceiver mReceiver;
    private DismissOverlayView mDismissOverlay;
    private PacingView mPacingView;
    private Context mContext = this;
    private Vibrator mVibrator;

    private boolean navEnabled = false; // @Patrick: please set this var according to Mobile's msg




    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS (GENERAL)
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set initial views
        setContentView(R.layout.activity_main);
        registerReceiver(closeCurrent, new IntentFilter("close"));
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            // Set layout listener, and prepare display resources
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mDismissOverlay = (DismissOverlayView) stub.findViewById(R.id.dismiss_overlay);
                mDismissOverlay.setIntroText(R.string.long_press_intro);
                mDismissOverlay.showIntroIfNecessary();

                mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

                mPacingView = (PacingView) stub.findViewById(R.id.pacingView);
                long currMillis = new java.util.Date().getTime();
                Intent intent = getIntent();
                String fullMessage = intent.getStringExtra("start");
                String trainTimes = fullMessage.substring(0, fullMessage.length()-1);
                char navSwitch = fullMessage.charAt(fullMessage.length()-1);
                if (navSwitch == '1') {
                    navEnabled = true;
                }

                // Prepare train times
                String[] bartStringTimes = trainTimes.split(" ");
                int numTrains = bartStringTimes.length;
                long[] bartTimes = new long[numTrains];
                Log.d("DisplayActivity", "bartStringTimes is " + Arrays.toString(bartStringTimes));
                if (bartStringTimes[0].equals("")) {
                    bartTimes[0] = 0; // error case
                    Toast.makeText(DisplayActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < numTrains; i++) {
                        Log.d("DisplayActivity", "parsing index: " + i);
                        bartTimes[i] = Long.parseLong(bartStringTimes[i], 10);
                    }
                }

                // Set PacingView data; update PacingView UI
                mPacingView.setDepartureTimes(bartTimes);
                mPacingView.updateArrivalTime(currMillis + 300000); // TODO: Still fake data here

                // Set PacingView swipe-listener.
                mPacingView.setOnTouchListener(new OnSwipeTouchListener(mContext) {
                    public void onSwipeBottom() {
                        int retVal = mPacingView.onSwipeDown();
                        if (retVal != 0) {
                            Toast.makeText(DisplayActivity.this, "Previous Train", Toast.LENGTH_SHORT).show();
                            if (retVal == 2) {
                                mVibrator.vibrate(mVibrationPattern, -1);
                                Log.d("DisplayActivity", "WATCH VIBRATED");
                            }
                        } else {
                            Toast.makeText(DisplayActivity.this, "No More", Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onSwipeTop() {
                        int retVal = mPacingView.onSwipeUp();
                        if (retVal != 0) {
                            Toast.makeText(DisplayActivity.this, "Next Train", Toast.LENGTH_SHORT).show();
                            if (retVal == 2) {
                                mVibrator.vibrate(mVibrationPattern, -1);
                                Log.d("DisplayActivity", "WATCH VIBRATED");
                            }
                        } else {
                            Toast.makeText(DisplayActivity.this, "No More", Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onSwipeRight() {
                        mDismissOverlay.show();
                    }

                    // TODO:  Replace fake data with live directions.
                    public void onSwipeLeft() {
                        if (!navEnabled) return;
                        Intent intent = new Intent(mContext, NavigationActivity.class);
                        //fake data
                        String[] directions = new String[3];
                        directions[0] = "Go straight";
                        directions[1] = "Turn left";
                        directions[2] = "stop";
                        intent.putExtra(NAV_EXTRA, directions);
                        startActivity(intent);
                    }
                });
            }
        });

        // Fake advisory notification.
        // TODO:  Trigger notification with genuine advisory.
        onReceiveNewAdvisory("Elevator out of service"); //example
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("refresh");

        // A BroadcastReceiver listening for user eta updates.
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract user ETA data message from intent
                String receivedMsg = intent.getStringExtra("msg");
                long eta = Long.parseLong(receivedMsg, 10);
                // vibrate if updating arrival time with new eta triggered a background color change.
                if (mPacingView.updateArrivalTime(eta)) {
                    mVibrator.vibrate(mVibrationPattern, -1);
                    Log.d("DisplayActivity", "WATCH VIBRATED");
                }
                Log.d("Updated with new ETA ", eta + "");
            }
        };
        // Register receiver
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister receiver
        this.unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(closeCurrent);
    }


    ////////////////////////////////////////////////////////////////////////////////
    // MESSAGE RECEPTION HELPER METHODS AND OBJECTS
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * When called, triggers notification dialog, containing the specified text, in the Wear UI.
     *
     * @param content       The String to be displayed as the advisory text.
     */
    public void onReceiveNewAdvisory(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Advisory")
                .setMessage(content)
                .setNeutralButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing. Just wait for dismiss
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Allows new routes to be received during the current DisplayActivity.
     */
    private final BroadcastReceiver closeCurrent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
