package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StationXmlParser extends BartXmlParser {
    private static final String ns = null;
    private static final String TAG = "StationXmlParser";

    /**
     * Extract each station entry in the XML
     */
    List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList stations = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("stations")) {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String currName = parser.getName();
                    if (currName.equals("station")) {
                        stations.add(readStation(parser));
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }
        }
        return stations;
    }

    private Station readStation(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "station");
        String abbreviation = null;
        String stationName = null;
        String address = null;
        String zip = null;
        String latitude = null;
        String longitude = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (name.equals("name")) {
                stationName = readName(parser);
            } else if (name.equals("abbr")) {
                abbreviation = readAbbr(parser);
            } else if (name.equals("address")) {
                address = readAddress(parser);
            } else if (name.equals("zipcode")) {
                zip = readZipcode(parser);
            } else if (name.equals("gtfs_latitude")) {
                latitude = readLatitude(parser);
            } else if (name.equals("gtfs_longitude")) {
                longitude = readLongitude(parser);
            } else {
                skip(parser);
            }
        }
        return new Station(abbreviation, stationName, address, zip, latitude, longitude);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readAbbr(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "abbr");
        String abbr = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "abbr");
        return abbr;
    }

    private String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "address");
        String address = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "address");
        return address;
    }

    private String readZipcode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "zipcode");
        String zipcode = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "zipcode");
        return zipcode;
    }

    private String readLatitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "gtfs_latitude");
        String latitude = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "gtfs_latitude");
        return latitude;
    }

    private String readLongitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "gtfs_longitude");
        String longitude = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "gtfs_longitude");
        return longitude;
    }

}

