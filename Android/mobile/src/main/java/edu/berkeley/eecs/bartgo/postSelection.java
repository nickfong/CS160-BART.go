package edu.berkeley.eecs.bartgo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class postSelection extends Activity {
    ////////////////////////////////////////////////////////////////////////////////
    // GLOBAL VARS
    ////////////////////////////////////////////////////////////////////////////////
    BartService mBService;
    List<Integer> trainList;
    Trip mTrip;
    String trainString;
    static final String TAG_DEBUG = "tag_debug";
    static final String noneRunningMsg = "None currently running.";
    static final String noneRunningMsgAbbrev = "N/A";
    static final String noneRunningMsgSchedInfo = "Trains usually run 4/6/8 AM - 1 AM\n(Weekdays/Sat./Sun. respectively)";
    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS (GENERAL)
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting menu bar properties
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_post_selection);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#1e2a37"));

        // Capturing UI Views
        final TextView destinationString = (TextView) findViewById(R.id.selectedDestination);
        final TextView fareString = (TextView) findViewById(R.id.costOfFare);
        final TextView fareString2 = (TextView) findViewById(R.id.costOfFare2);
        final TextView lineString = (TextView) findViewById(R.id.boundTrain);
        final TextView etaString = (TextView) findViewById(R.id.etaText);
        final TextView arrivingIn = (TextView) findViewById(R.id.arriving);
        final TextView minutesRemaining = (TextView) findViewById(R.id.mainBox);
        final TextView arrivingUnits = (TextView) findViewById(R.id.arriving_units);
        final Button startButton = (Button) findViewById(R.id.startButton);
        final Switch turnByTurnSwitch = (Switch) findViewById(R.id.turnbyturnSwitch);
        Intent intent = getIntent();

        String destinationSelected =  intent.getStringExtra("destName");
        destinationString.setText(destinationSelected);

        setUpperBG(destinationSelected);

        String trainArrivalTime = initializeTrip(destinationSelected);
        if (trainArrivalTime != null) {
            etaString.setText("Train arrival: " + trainArrivalTime);
        } else {
            etaString.setText("Train arrival:  " + noneRunningMsgAbbrev);
        }
        float fare = mTrip.getFare();
        DecimalFormat decim = new DecimalFormat("0.00");
        fareString.setText("One-way:  $" + decim.format(fare));
        fareString2.setText("Round-trip:  $" + decim.format(2*fare));
        String boundTrain = mBService.getNextDepartureDestination(mTrip);
        lineString.setText(boundTrain + " Train");
        if ((trainList != null) && (trainList.size() != 0)) {
            minutesRemaining.setText(trainList.get(0) + "");
            setStartButtonListener(startButton, turnByTurnSwitch);
        } else {
            arrivingIn.setVisibility(View.INVISIBLE);
            minutesRemaining.setText(noneRunningMsg);
            minutesRemaining.setTextSize((float) 24);
            minutesRemaining.setTypeface(minutesRemaining.getTypeface(), Typeface.ITALIC);
            arrivingUnits.setVisibility(View.INVISIBLE);

            startButton.setClickable(false);
            startButton.setText(noneRunningMsgSchedInfo);
        }
        trainString = prepareTrains();

    }

    public void setStartButtonListener(final Button startButton, final Switch turnByTurnSwitch) {
        // Create OnClickListener for StartButton
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                Intent toNavOrNotToNavIntent = new Intent(getBaseContext(), NavActivity.class);

                // Pass origin/destination/destination name extras
                Double origLat = i.getDoubleExtra("origLat", 999999);
                Double origLng = i.getDoubleExtra("origLng", 999999);
                Double destLat = i.getDoubleExtra("destLat", 999999);
                Double destLng = i.getDoubleExtra("destLng", 999999);
                String destName = i.getStringExtra("destName");

                toNavOrNotToNavIntent.putExtra("origLat", origLat);
                toNavOrNotToNavIntent.putExtra("origLng", origLng);
                toNavOrNotToNavIntent.putExtra("destLat", destLat);
                toNavOrNotToNavIntent.putExtra("destLng", destLng);
                toNavOrNotToNavIntent.putExtra("destName", destName);
                toNavOrNotToNavIntent.putExtra("isChecked", turnByTurnSwitch.isChecked());
                toNavOrNotToNavIntent.putExtra("trains", trainString);
                startActivity(toNavOrNotToNavIntent);
            }
        });
    }

    // Sets the background image at the top of the postSelection view
    // Different image for each station

    public void setUpperBG(String destination){
        ImageView upperBG = (ImageView) findViewById(R.id.upperBG);

        switch(destation) {
            case "12th St. Oakland City Center":
                upperBG.setImageResource(R.drawable.twelvestoakland);
                break;
            case "16th St. Mission":
                upperBG.setImageResource(R.drawable.embarcadero);
                break;
            case "19th St. Oakland":
                upperBG.setImageResource(R.drawable.nineteenoak);
                break;
            case "24th St. Mission":
                upperBG.setImageResource(R.drawable.twentyfourmission);
                break;
            case "Ashby":
                upperBG.setImageResource(R.drawable.ashby);
                break;
            case "Balboa Park":
                upperBG.setImageResource(R.drawable.balboapark);
                break;
            case "Bay Fair":
                upperBG.setImageResource(R.drawable.bayfair);
                break;
            case "Castro Valley":
                upperBG.setImageResource(R.drawable.castrovalley);
                break;
            case "Civic Center/UN Plaza":
                upperBG.setImageResource(R.drawable.civiccenter);
                break;
            case "Coliseum":
                upperBG.setImageResource(R.drawable.coliseum);
                break;
            case "Colma":
                upperBG.setImageResource(R.drawable.colma);
                break;
            case "Concord":
                upperBG.setImageResource(R.drawable.concord);
                break;
            case "Daly City":
                upperBG.setImageResource(R.drawable.dalycity);
                break;
            case "Downtown Berkeley":
                upperBG.setImageResource(R.drawable.downtownberk);
                break;
            case "Dublin/Pleasanton":
                upperBG.setImageResource(R.drawable.pleasanton);
                break;
            case "El Cerrito del Norte":
                upperBG.setImageResource(R.drawable.elcerritodelnorte);
                break;
            case "El Cerrito Plaza":
                upperBG.setImageResource(R.drawable.elcerritoplaza);
                break;
            case "Embarcadero":
                upperBG.setImageResource(R.drawable.embarcadero);
                break;
            case "Fremont":
                upperBG.setImageResource(R.drawable.fremont);
                break;
            case "Fruitvale":
                upperBG.setImageResource(R.drawable.fruitvale);
                break;
            case "Glen Park":
                upperBG.setImageResource(R.drawable.glenpark);
                break;
            case "Hayward":
                upperBG.setImageResource(R.drawable.hayward);
                break;
            case "Lafayette":
                upperBG.setImageResource(R.drawable.lafayette);
                break;
            case "Lake Merritt":
                upperBG.setImageResource(R.drawable.lakemerritt);
                break;
            case "MacArthur":
                upperBG.setImageResource(R.drawable.macarthur);
                break;
            case "Millbrae":
                upperBG.setImageResource(R.drawable.millbrae);
                break;
            case "Montgomery St.":
                upperBG.setImageResource(R.drawable.montgomery);
                break;
            case "North Berkeley":
                upperBG.setImageResource(R.drawable.northberk);
                break;
            case "North Concord/Martinez":
                upperBG.setImageResource(R.drawable.northconcord);
                break;
            case "Oakland Int'l Airport":
                upperBG.setImageResource(R.drawable.oakairport);
                break;
            case "Orinda":
                upperBG.setImageResource(R.drawable.orinda);
                break;
            case "Pittsburg/Bay Point":
                upperBG.setImageResource(R.drawable.pbaypoint);
                break;
            case "Pleasant Hill/Contra Costa Centre":
                upperBG.setImageResource(R.drawable.pleasanthill);
                break;
            case "Powell St.":
                upperBG.setImageResource(R.drawable.powell);
                break;
            case "Richmond":
                upperBG.setImageResource(R.drawable.embarcadero);
                break;
            case "Rockridge":
                upperBG.setImageResource(R.drawable.rockridge);
                break;
            case "San Bruno":
                upperBG.setImageResource(R.drawable.embarcadero);
                break;
            case "San Francisco Int'l Airport":
                upperBG.setImageResource(R.drawable.sfo);
                break;
            case "San Leandro":
                upperBG.setImageResource(R.drawable.sanleandro);
                break;
            case "South Hayward":
                upperBG.setImageResource(R.drawable.hayward);
                break;
            case "South San Francisco":
                upperBG.setImageResource(R.drawable.southsf);
                break;
            case "Union City":
                upperBG.setImageResource(R.drawable.embarcadero);
                break;
            case "Walnut Creek":
                upperBG.setImageResource(R.drawable.walnutcreek);
                break;
            case "West Dublin/Pleasanton":
                upperBG.setImageResource(R.drawable.embarcadero);
                break;
            case "West Oakland":
                upperBG.setImageResource(R.drawable.westoak);
                break;
            default:
                upperBG.setImageResource(R.drawable.embarcadero);
                break;
        }

    }
    // Initializes the trip and returns the next train's arrival time in the format h:mm
    public String initializeTrip(String dest) {
        mBService = new BartService();
        Station destStation = mBService.lookupStationByName(dest);
        Station origStation = mBService.lookupStationByAbbreviation(getIntent().getStringExtra("origStation"));
        DateFormat df = new SimpleDateFormat("hh:mma", Locale.US);
        DateFormat df2 = new SimpleDateFormat("h:mm a", Locale.US);
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date now = cal.getTime();
        mTrip = mBService.generateTrip(origStation, destStation, df.format(now));
        Log.d(TAG_DEBUG, "***** mTrip: " + mTrip);
        mBService.updateDepartureTimes(mTrip);
        trainList = mBService.getNextDepartureTimes(mTrip);  // if no legs found, returns an empty ArrayList
        if (trainList.size() != 0) {
            cal.add(Calendar.MINUTE, trainList.get(0));
            return df2.format(cal.getTime()); // next train time
        } else {
            return null;
        }
    }

//    public Leg findLeg() {
//        // bestTrain is the train headed towards the trip destination
//        // arriving at the origin station the soonest. For simplicity's
//        // sake, the app will only display the trains in the same list
//        // as bestTrain
//        ArrayList<Legs> legs = mTrip.getLegs();
//        int bestIndex = 0;
//        int bestTrain = 999999;
//        for (int i = 0; i < legs.size(); i++) {
//            Leg michawk = legs.get(i).getLegs().get(0);
//            if (michawk.trains != null) {
//                int firstTrain = michawk.trains.get(0);
//                if (firstTrain < bestTrain) {
//                    bestTrain = firstTrain;
//                    bestIndex = i;
//                }
//            }
//        }
//        return legs.get(bestIndex).getLegs().get(0);
//    }

    public String prepareTrains() {
        int numTrains = trainList.size();
        Integer[] trainArray = trainList.toArray(new Integer[numTrains]);
        long currMillis = new java.util.Date().getTime();
        String trainTimes = "";
        for (int i = 0; i < trainArray.length; i++) {
            long temp = currMillis + (long) (trainArray[i]*60000);
            trainTimes += temp + " ";
        }
        return trainTimes;
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
