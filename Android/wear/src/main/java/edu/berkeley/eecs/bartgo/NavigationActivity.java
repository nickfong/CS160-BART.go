package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Daiwei Liu on 12/1/15
 */
public class NavigationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        TextView direction = (TextView) findViewById(R.id.navigationText);
        direction.setText(getIntent().getStringExtra(DisplayActivity.NAV_EXTRA));
        direction.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                finish();
            }
        });
    }
}
