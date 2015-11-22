package edu.berkeley.eecs.bartgo;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BartService {
    private final static String key = "Q7VS-PJD5-9K8T-DWE9";

    /**
     * generateApiCall generates the proper API URL for a given command and optional arguments
     * @param command is the API command to be passed to the API
     * @param arguments is an optional ArrayList of arguments to command
     * @return a String that is a valid API call for the given command and arguments
     */
    private String generateApiCall(String command, ArrayList<String> arguments) {
        String prefix = "http://api.bart.gov/api/stn.aspx?cmd=";
        String suffix = "&key=" + key;
        String call = prefix + command;
        if (arguments != null) {
            for(int i = 0; i < arguments.size(); i++) {
                call += "&" + arguments.get(i);
            }
        }
        return call + suffix;
    }

    public ArrayList<Station> BartService() {
        return populateStations();
    }

    /**
     * The BartService constructor queries the BART API and returns an ArrayList of Station objects
     * @return an ArrayList of Station objects corresponding to all valid stations
     */
    public ArrayList<Station> populateStations() {
        ArrayList<Station> stations = new ArrayList<Station>();
        String call = generateApiCall("stns", null);
        //TODO parse call
        return stations;
    }

    /**
     * Generate a trip between two stations
     * @param startStation is a Station object corresponding to the starting station of the user
     * @param destinationStation is a Station object corresponding to the destination station of the user
     * @param time is a String, formatted h:mm+am|pm specifying what time the earliest trip should be
     * @return a valid Trip corresponding to a Trip between startStation and destinationStation
     */
    public Trip generateTrip(Station startStation, Station destinationStation, String time) {
        ArrayList<String> callArgs = new ArrayList<>();
        callArgs.add("orig=" + startStation.getAbbreviation());
        callArgs.add("dest=" + destinationStation.getAbbreviation());
        String call = generateApiCall("fare", callArgs);
        //TODO parse call and fix call to Trip constructor
        Trip trip = new Trip(startStation, destinationStation, 0.0f);

        ArrayList<Chain> chains = generateChains(startStation, destinationStation, time);
        trip.setChains(chains);

        return trip;
    }

    public Trip refreshTrip(Trip trip, String time) {
        //TODO Update the trip
        return trip;
    }

    private ArrayList<Chain> generateChains(Station startStation, Station destinationStation, String time) {
        ArrayList<String> callArgs = new ArrayList<>();
        callArgs.add("orig=" + startStation.getAbbreviation());
        callArgs.add("dest=" + destinationStation.getAbbreviation());
        callArgs.add(time != null ? "time=" + time : "time=now");
        callArgs.add("b=0");    //get 0 trips before the current time
        callArgs.add("a=4");    //get 0 trips before the current time
        String call = generateApiCall("depart", callArgs);

        ArrayList<Chain> chains = new ArrayList<>();
        //TODO parse call and fix call to Trip constructor

        return chains;
    }

    public ArrayList<Advisory> getCurrentAdvisories() {
        String call = generateApiCall("bsa", null);
        ArrayList<Advisory> advisories = new ArrayList<>();
        //TODO parse call

        return advisories;
    }
}
