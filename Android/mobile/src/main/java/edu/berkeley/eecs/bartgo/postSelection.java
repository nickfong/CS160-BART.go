package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class postSelection extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_post_selection);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#1e2a37"));

        final TextView destinationString = (TextView) findViewById(R.id.selectedDestination);
        final Button startButton = (Button) findViewById(R.id.startButton);
        final Switch turnByTurnSwitch = (Switch) findViewById(R.id.turnbyturnSwitch);

        String destinationSelected =  getIntent().getStringExtra("destName");
        destinationString.setText(destinationSelected);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                String destName = i.getStringExtra("destName");

                Intent toNavOrNotToNavIntent;
                if (turnByTurnSwitch.isChecked()) {
                    // Create NavActivity intent
                    toNavOrNotToNavIntent = new Intent(getBaseContext(), NavActivity.class);
                    // Pass origin/destination extras
                    Double origLat = i.getDoubleExtra("origLat", 999999);
                    Double origLng = i.getDoubleExtra("origLng", 999999);
                    Double destLat = i.getDoubleExtra("destLat", 999999);
                    Double destLng = i.getDoubleExtra("destLng", 999999);

                    toNavOrNotToNavIntent.putExtra("origLat", origLat);
                    toNavOrNotToNavIntent.putExtra("origLng", origLng);
                    toNavOrNotToNavIntent.putExtra("destLat", destLat);
                    toNavOrNotToNavIntent.putExtra("destLng", destLng);
                    toNavOrNotToNavIntent.putExtra("destName", destName);
                } else {
                    // Create NoNavActivity intent
                    toNavOrNotToNavIntent = new Intent(getBaseContext(), NoNavActivity.class);
                    toNavOrNotToNavIntent.putExtra("destName", destName);
                }
                startActivity(toNavOrNotToNavIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_selection, menu);
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
}
