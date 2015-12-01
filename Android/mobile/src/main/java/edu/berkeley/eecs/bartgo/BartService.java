package edu.berkeley.eecs.bartgo;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class BartService {
    private final String TAG = "BartService";
    private HashMap<Integer, Route> routes;

    /**
     * The BartService constructor calls populateStations() to get an ArrayList
     * of Station objects corresponding to all stations that currently exist in
     * the BART system.  The constructor should be called to ensure that valid
     * stations are listed in the application.
     * @return an ArrayList of Station objects corresponding to all valid
     *         stations
     */
    public ArrayList<Station> BartService() {
        this.routes = populateRoutes();
        return populateStations();
    }

    /**
     * generateApiCall generates the proper API URL for a given command and
     * optional arguments
     * @param command is the API command to be passed to the API
     * @param arguments is an optional ArrayList of arguments to command
     * @return a String that is a valid API call for the given command and
     *         arguments
     */
    private String generateApiCall(String command, ArrayList<String> arguments) {
        String prefix = "http://api.bart.gov/api/stn.aspx?cmd=";
        String suffix = "&key=" + PrivateConstants.BART_API_KEY;
        String call = prefix + command;
        if (arguments != null) {
            for(int i = 0; i < arguments.size(); i++) {
                call += "&" + arguments.get(i);
            }
        }
        return call + suffix;
    }

    /**
     * populateStations() queries the BART API and returns an ArrayList of
     * Station objects
     * @return an ArrayList of Station objects corresponding to all valid
     *         stations
     */
    public ArrayList<Station> populateStations() {
        ArrayList<Station> stations = new ArrayList<Station>();
        String call = generateApiCall("stns", null);
        try {
            String result = new StationXmlTask().execute(call).get();
            String[] stationStrings = result.split("\n");
            for (String station : stationStrings) {
                String[] stationString = station.split(";");
                String abbreviation = stationString[0];
                String name = stationString[1];
                String address = stationString[2];
                String zip = stationString[3];
                stations.add(new Station(abbreviation, name, address, zip));
                Log.i(TAG, "New station object with station " + station);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "XmlTask execution from populateStations was interupted: " + e);
        } catch (ExecutionException e) {
            Log.e(TAG, "XmlTask execution from populateStations failed: " + e);
        }
        return stations;
    }

    private HashMap<Integer, Route> populateRoutes() {
        String call = generateApiCall("routes", null);
        HashMap<Integer, Route> routes = new HashMap<>();
        //TODO parse call
        return routes;
    }

    /**
     * Generate a trip between two stations
     * @param startStation is a Station object corresponding to the starting
     *        station of the user
     * @param destinationStation is a Station object corresponding to the
     *        destination station of the user
     * @param time is a String, formatted h:mm+am|pm specifying what time the
     *        earliest trip should be
     * @return a valid Trip corresponding to a Trip between startStation and
     *         destinationStation
     */
    public Trip generateTrip(Station startStation, Station destinationStation, String time) {
        ArrayList<String> callArgs = new ArrayList<>();
        callArgs.add("orig=" + startStation.getAbbreviation());
        callArgs.add("dest=" + destinationStation.getAbbreviation());
        String call = generateApiCall("fare", callArgs);
        //TODO parse call and fix call to Trip constructor
        Trip trip = new Trip(startStation, destinationStation, 0.0f);

        ArrayList<Legs> legs = generateLegs(startStation, destinationStation, time);
        trip.setLegs(legs);

        return trip;
    }

    public void softRefresh(Trip trip, String time) {
        //TODO Update the trip
    }

    public void hardRefresh(Trip trip, String time) {
        //TODO Update the trip
    }

    private ArrayList<Legs> generateLegs(Station startStation, Station destinationStation, String time) {
        ArrayList<String> callArgs = new ArrayList<>();
        callArgs.add("orig=" + startStation.getAbbreviation());
        callArgs.add("dest=" + destinationStation.getAbbreviation());
        callArgs.add(time != null ? "time=" + time : "time=now");
        callArgs.add("b=0");    //get 0 trips before the current time
        callArgs.add("a=4");    //get 0 trips before the current time
        String call = generateApiCall("depart", callArgs);

        ArrayList<Legs> legs = new ArrayList<>();
        //TODO parse call
        //TODO remove duplicate legs

        return legs;
    }

    private void getDepartureTimes(Trip trip) {
        Station station = trip.getStartingStation();
        ArrayList<String> callArgs = new ArrayList<>();
        callArgs.add("orig=" + station.getAbbreviation());
        String call = generateApiCall("etd", callArgs);
        //TODO parse call


    }

    private ArrayList<Integer> getCrowding(Legs l) {//Station station, String route, String id) {
        ArrayList<Leg> legs = l.getLegs();
        ArrayList<String> callArgs = new ArrayList<>();
        String idString = "";
        for(int i = 0; i < legs.size(); i++) {
            assert legs.get(i).route.Id.length() == 2;
            //TODO assert that the train ID is proper length too

            String station = legs.get(i).startStation.getAbbreviation();
            String route = legs.get(i).route.Id;
            String id = "";
            idString += "Id" + i+1 + "=" + station + route + id + "&";
        }
        /* Remove trailing & from string */
        if (idString.endsWith("&")) {
            idString = idString.substring(0, idString.length()-1);
        }
        callArgs.add(idString);
        String call = generateApiCall("load", callArgs);
        //TODO parse call
        return null;
    }

    public ArrayList<Advisory> getCurrentAdvisories() {
        String call = generateApiCall("bsa", null);
        ArrayList<Advisory> advisories = new ArrayList<>();
        //TODO parse call

        return advisories;
    }
}
