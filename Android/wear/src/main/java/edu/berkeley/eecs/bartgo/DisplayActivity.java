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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(closeCurrent, new IntentFilter("close"));
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
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
                mPacingView.setDepartureTimes(bartTimes);
                mPacingView.updateArrivalTime(currMillis + 300000); //@Patrick: still fake data here
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

                    /* --- For Patrick ---
                     * Just put the current navigation direction into the intend and start the
                     * navigation activity.
                     * You need to supply the correct current direction though. (although I think
                     * that should be handled on the mobile side) */
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
        onReceiveNewAdvisory("Elevator out of service"); //example
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("refresh");

        // A BroadcastReceiver listening for user eta updates.
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
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
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //unregister our receiver
        this.unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(closeCurrent);
    }

    /* --- For Patrick ---
     * Once you receive a new advisory just call this method with the content of the advisory,
     * This method would pop up a dialog alert on the UI*/
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

    // Allow new routes to be received during current DisplayActivity
    private final BroadcastReceiver closeCurrent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
