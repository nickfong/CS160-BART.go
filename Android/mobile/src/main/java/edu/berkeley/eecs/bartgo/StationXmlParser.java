package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StationXmlParser {
    private static final String ns = null;
    private static final String TAG = "StationXmlParser";

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Extract each station entry in the XML
     */
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "At top of readFeed");
        ArrayList stations = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            Log.i(TAG, "At top of loop");
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                Log.i(TAG, "Continuing");
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("stations")) {
                Log.i(TAG, "Found stations tag");
                stations.add(readEntry(parser));
            } else {
                Log.i(TAG, "Found " + name + " instead of a station");
                skip(parser);
            }
        }
        return stations;
    }

    private Station readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "At top of readEntry");
        parser.require(XmlPullParser.START_TAG, ns, "stations");
        String abbreviation = null;
        String stationName = null;
        String address = null;
        String zip = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("station")) {
                Log.i(TAG, "Found station");
                parser.require(XmlPullParser.START_TAG, ns, "station");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (name.equals("name")) {
                        stationName = readName(parser);
                    } else if (name.equals("abbr")) {
                        abbreviation = readAbbr(parser);
                    } else if (name.equals("address")) {
                        address = readAddress(parser);
                    } else if (name.equals("zipcode")) {
                        zip = readZipcode(parser);
                    } else {
                        Log.i(TAG, "Found " + name);
                        skip(parser);
                    }
                }
            }
            else {
                skip(parser);
            }
        }
        return new Station(abbreviation, stationName, address, zip);
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

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}

