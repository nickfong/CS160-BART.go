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

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Daiwei Liu on 12/1/15
 */
public class NavigationActivity extends Activity {
    private String[] directions;
    private TextView direction;
    private int currentDirectionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        direction = (TextView) findViewById(R.id.navigationText);
        directions = getIntent().getStringArrayExtra(DisplayActivity.NAV_EXTRA);
        currentDirectionIndex = 0;
        direction.setText(directions[currentDirectionIndex]);
        direction.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                finish();
            }

            public void onSwipeBottom() {
                if (currentDirectionIndex > 0) {
                    currentDirectionIndex -= 1;
                    direction.setText(directions[currentDirectionIndex]);
                } else {
                    Toast.makeText(NavigationActivity.this, "No Previous Direction", Toast.LENGTH_SHORT).show();
                }
            }

            public void onSwipeTop() {
                if (currentDirectionIndex < directions.length - 1) {
                    currentDirectionIndex += 1;
                    direction.setText(directions[currentDirectionIndex]);
                } else {
                    Toast.makeText(NavigationActivity.this, "END", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
