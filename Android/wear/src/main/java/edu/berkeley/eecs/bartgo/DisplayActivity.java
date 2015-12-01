package edu.berkeley.eecs.bartgo;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.Toast;

public class DisplayActivity extends WearableActivity {

    private BroadcastReceiver mReceiver;
    private PacingView mPacingView;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(closeCurrent, new IntentFilter("close"));
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mPacingView = (PacingView) stub.findViewById(R.id.pacingView);

                long currMillis = new java.util.Date().getTime();
//                long[] bartTimes = new long[5];
//                bartTimes[0] = currMillis + 120000; // departs in 2 min
//                bartTimes[1] = currMillis + 240000; // departs in 4 min
//                bartTimes[2] = currMillis + 420000; // departs in 7 min
//                bartTimes[3] = currMillis + 900000; // departs in 15 min
//                bartTimes[4] = currMillis + 1140000; // departs in 19 min
                Intent intent = getIntent();
                String trainTimes = intent.getStringExtra("start");
                String[] bartStringTimes = trainTimes.split(" ");
                int numTrains = bartStringTimes.length;
                long[] bartTimes = new long[numTrains];
                for (int i = 0; i < numTrains; i++) {
                    bartTimes[i] = Long.parseLong(bartStringTimes[i], 10);
                }
                mPacingView.setDepartureTimes(bartTimes);
                mPacingView.updateArrivalTime(currMillis + 300000);
                mPacingView.setOnTouchListener(new OnSwipeTouchListener(mContext) {
                    public void onSwipeBottom() {
                        Toast.makeText(DisplayActivity.this, "Previous Train", Toast.LENGTH_SHORT).show();
                        mPacingView.onSwipeUp();
                    }

                    public void onSwipeTop() {
                        Toast.makeText(DisplayActivity.this, "Next Train", Toast.LENGTH_SHORT).show();
                        mPacingView.onSwipeDown();
                    }
                });
            }
        });
        createAdvisoryNotification();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("refresh");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String receivedMsg = intent.getStringExtra("msg");
                long eta = Long.parseLong(receivedMsg, 10);
                mPacingView.updateArrivalTime(eta);
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
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(closeCurrent);
    }

    public void createAdvisoryNotification() {
        // Build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.alert)
                .setContentTitle("BART Advisory")
                .setContentText("Elevator out of service");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationBuilder.setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());
        Log.v("MainActivity", "notified");
    }

    // Allow new routes to be received during current DisplayActivity
    private final BroadcastReceiver closeCurrent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
