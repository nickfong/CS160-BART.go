package edu.berkeley.eecs.bartgo;

/**
 * Created by patrick on 11/20/15.
 */
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WatchListenerService extends WearableListenerService {
    private static final String REFRESH_DATA = "/refresh_data";
    private static final String NEW_TRAINS = "/new_trains";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( REFRESH_DATA ) ) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent i = new Intent("refresh");
            i.putExtra("msg", value);
            sendBroadcast(i);
        } else if (messageEvent.getPath().equalsIgnoreCase( NEW_TRAINS)) {
            sendBroadcast(new Intent("close"));
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            v.vibrate(500);
            Intent intent = new Intent(this, DisplayActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("start", value);
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}
