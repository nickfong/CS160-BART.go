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
 * Created by patrick on 11/20/15
 */
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