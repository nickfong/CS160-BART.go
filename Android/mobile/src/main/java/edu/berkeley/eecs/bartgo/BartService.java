package edu.berkeley.eecs.bartgo;


/* This service should be bound */
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class BartService extends Service {
    private final String TAG = "BartService";
    private HashMap<Integer, Route> routes;
    private final IBinder mBinder = new LocalBinder();

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
    private String generateApiCall(String verb, String command, ArrayList<String> arguments) {
        String prefix = "http://api.bart.gov/api/" + verb + ".aspx?cmd=";
        String suffix = "&key=" + PrivateConstants.BART_API_KEY;
        String call = prefix + command;
        if (arguments != null) {
            for(int i = 0; i < arguments.size(); i++) {
                call += "&" + arguments.get(i);
            }
        }
        Log.i(TAG, "API Call is: " + call + suffix + "<");
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
        String call = generateApiCall("stn", "stns", null);
        try {
            String result = new StationXmlTask().execute(call).get();
            Log.i(TAG, "Result of API call is: " + result + "<");
            String[] stationStrings = result.split("\n");
            for (String station : stationStrings) {
                Log.i(TAG, "Got a station: " + station);
                String[] stationString = station.split(";");
                if (stationString.length == 4) {
                    String abbreviation = stationString[0];
                    String name = stationString[1];
                    String address = stationString[2];
                    String zip = stationString[3];
                    stations.add(new Station(abbreviation, name, address, zip));
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "XmlTask execution from populateStations was interupted: " + e);
        } catch (ExecutionException e) {
            Log.e(TAG, "XmlTask execution from populateStations failed: " + e);
        }
        Log.i(TAG, "Found " + stations.size() + " stations.  Returning.");
        return stations;
    }

    private HashMap<Integer, Route> populateRoutes() {
        String call = generateApiCall("route", "routes", null);
        HashMap<Integer, Route> routes = new HashMap<>();
        try {
            String result = new RouteXmlTask().execute(call).get();
            Log.i(TAG, "Result of API call is: " + result + "<");
            String[] routeStrings = result.split("\n");
            for (String route : routeStrings) {
                Log.i(TAG, "Got a route: " + route);
                String[] routeString = route.split(";");
                if (routeString.length == 5) {
                    String name = routeString[0];
                    String abbreviation = routeString[1];
                    String id = routeString[2];
                    String number = routeString[3];
                    String color = routeString[4];
                    routes.put(new Integer(number), new Route(name, abbreviation, id, number, color));
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "XmlTask execution from populateroutes was interupted: " + e);
        } catch (ExecutionException e) {
            Log.e(TAG, "XmlTask execution from populateroutes failed: " + e);
        }
        Log.i(TAG, "Found " + routes.size() + " routes");
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
        String call = generateApiCall("sched", "fare", callArgs);
        float fare = 0.0f;
        try {
            String result = new FareXmlTask().execute(call).get();
            Log.i(TAG, "Result of API call is: " + result + "<");
            fare = Float.parseFloat(result);
        } catch (InterruptedException e) {
            Log.e(TAG, "XmlTask execution from populateroutes was interupted: " + e);
        } catch (ExecutionException e) {
            Log.e(TAG, "XmlTask execution from populateroutes failed: " + e);
        }
        Log.i(TAG, "Found a fare of $" + fare);

        Trip trip = new Trip(startStation, destinationStation, fare);

        //TODO Implement generateLegs
        //ArrayList<Legs> legs = generateLegs(startStation, destinationStation, time);
        //trip.setLegs(legs);

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
        String call = generateApiCall("sched", "depart", callArgs);

        ArrayList<Legs> legs = new ArrayList<>();

        try {
            String result = new DepartXmlTask().execute(call).get();
            Log.i(TAG, "Result of API call is: " + result + "<");
            String[] routeStrings = result.split("\n");
            for (String route : routeStrings) {
                Log.i(TAG, "Got a route: " + route);
                String[] routeString = route.split(";");
                if (routeString.length == 5) {
                    String name = routeString[0];
                    String abbreviation = routeString[1];
                    String id = routeString[2];
                    String number = routeString[3];
                    String color = routeString[4];
                    routes.put(new Integer(number), new Route(name, abbreviation, id, number, color));
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "XmlTask execution from populateroutes was interupted: " + e);
        } catch (ExecutionException e) {
            Log.e(TAG, "XmlTask execution from populateroutes failed: " + e);
        }
        Log.i(TAG, "Found " + routes.size() + " routes");

        //TODO parse call
        //TODO remove duplicate legs

        return legs;
    }

    private void getDepartureTimes(Trip trip) {
        Station station = trip.getStartingStation();
        ArrayList<String> callArgs = new ArrayList<>();
        callArgs.add("orig=" + station.getAbbreviation());
        String call = generateApiCall("etd", "etd", callArgs);
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
        String call = generateApiCall("sched", "load", callArgs);
        //TODO parse call
        return null;
    }

    public ArrayList<Advisory> getCurrentAdvisories() {
        String call = generateApiCall("bsa", "bsa", null);
        ArrayList<Advisory> advisories = new ArrayList<>();
        try {
            String result = new AdvisoryXmlTask().execute(call).get();
            Log.i(TAG, "Result of API call is: " + result + "<");
            String[] advisoryStrings = result.split("\n");
            for (String advisory : advisoryStrings) {
                Log.i(TAG, "Got an advisory: " + advisory);
                String[] advisoryString = advisory.split(";");
                if (advisoryString.length == 3) {
                    String id = advisoryString[0];
                    String type = advisoryString[1];
                    String description= advisoryString[2];
                    advisories.add(new Advisory(id, type, description));
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "XmlTask execution from populateAdvisories was interupted: " + e);
        } catch (ExecutionException e) {
            Log.e(TAG, "XmlTask execution from populateAdvisories failed: " + e);
        }
        Log.i(TAG, "Found " + advisories.size() + " advisories.  Returning.");
        return advisories;
    }

    public class LocalBinder extends Binder {
        BartService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BartService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        ArrayList<Station> stations = populateStations();
        populateRoutes();
        getCurrentAdvisories();
        generateTrip(stations.get(0), stations.get(1), "foo");
        return mBinder;
    }

}
