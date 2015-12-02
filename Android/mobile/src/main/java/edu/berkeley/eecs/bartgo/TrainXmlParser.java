package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TrainXmlParser {
    private static final String ns = null;
    private static final String TAG = "TrainXmlParser";

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
     * Extract each train entry in the XML
     */
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList estimates = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("station")) {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String currName = parser.getName();
                    if (currName.equals("etd")) {
                        estimates.add(readEtd(parser));
                    } else {
                        Log.i(TAG, "Found " + currName + " instead of etd");
                        skip(parser);
                    }
                }
            } else {
                Log.i(TAG, "Found " + name + " instead of station");
                skip(parser);
            }
        }
        return estimates;
    }

    private String readEtd(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "At top of readEtd");
        parser.require(XmlPullParser.START_TAG, ns, "etd");
        String destination = null;
        String abbreviation = null;
        String estimates = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("destination")) {
                destination = readDestination(parser);
            } else if (name.equals("abbreviation")) {
                abbreviation = readAbbr(parser);
            } else if (name.equals("estimate")) {
                estimates += readEstimate(parser) + ",";
            } else {
                skip(parser);
            }
        }
        return abbreviation + ":" + estimates;
    }

    private String readDestination(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "destination");
        String destination = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "destination");
        return destination;
    }

    private String readAbbr(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "abbreviation");
        String abbreviation = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "abbreviation");
        return abbreviation;
    }

    private String readEstimate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "estimate");
        String estimate = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("minutes")) {
                estimate = readMinutes(parser);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "estimate");
        return estimate;
    }

    private String readMinutes(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "minutes");
        String minutes = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "minutes");
        return minutes;
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

