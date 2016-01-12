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

import android.content.Intent;
import android.os.Vibrator;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * A Listener class which broadcasts custom messages sent from Mobile.  This currently includes
 * user ETA updates and train ETA updates.
 */
public class WatchListenerService extends WearableListenerService {
    private static final String REFRESH_DATA = "/refresh_data";
    private static final String NEW_TRAINS = "/new_trains";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // If a REFRESH_DATA (updated user eta) message is received
        if( messageEvent.getPath().equalsIgnoreCase( REFRESH_DATA ) ) {
            // Extract message data, and create an intent with said data as an extra
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent i = new Intent("refresh");
            i.putExtra("msg", value);
            // Broadcast intent (to the BroadcastReceiver in DisplayActivity)
            sendBroadcast(i);
        // Else if a NEW_TRAINS (updated train eta) message is received
        } else if (messageEvent.getPath().equalsIgnoreCase( NEW_TRAINS)) {
            // Broadcast a "close" intent.  (Motivation unclear.)
            sendBroadcast(new Intent("close"));
            // Extract message data
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            // Vibrate watch for 500 millis
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            v.vibrate(500);
            // Prepare intent to launch display activity, adding message data as an extra
            Intent intent = new Intent(this, DisplayActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("start", value);
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}