package edu.berkeley.eecs.bartgo;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DisplayActivity extends WearableActivity {
    public static final String NAV_EXTRA = "NAV_DIRECTION";

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BroadcastReceiver mReceiver;
    private DismissOverlayView mDismissOverlay;
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

                // Obtain the DismissOverlayView element
                mDismissOverlay = (DismissOverlayView) stub.findViewById(R.id.dismiss_overlay);
                mDismissOverlay.setIntroText(R.string.long_press_intro);
                mDismissOverlay.showIntroIfNecessary();

                mPacingView = (PacingView) stub.findViewById(R.id.pacingView);

                long currMillis = new java.util.Date().getTime();
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

                    public void onSwipeRight() {
                        mDismissOverlay.show();
                    }

                    /* --- For Patrick ---
                     * Just put the current navigation direction into the intend and start the
                     * navigation activity.
                     * You need to supply the correct current direction though. (although I think
                     * that should be handled on the mobile side) */
                    public void onSwipeLeft() {
                        Intent intent = new Intent(mContext, NavigationActivity.class);
                        intent.putExtra(NAV_EXTRA, "Go straight, and turn left at Fulton/Bancroft");
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
