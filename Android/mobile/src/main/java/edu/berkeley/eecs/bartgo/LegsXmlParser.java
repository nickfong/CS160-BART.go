package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LegsXmlParser {
    private static final String ns = null;
    private static final String TAG = "LegsXmlParser";

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
     * Extract each legs entry in the XML
     */
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<String> legs = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("schedule")) {
                Log.i(TAG, "Found a schedule");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String currName = parser.getName();
                    if (currName.equals("request")) {
                        Log.i(TAG, "Found a request");
                        parser.require(XmlPullParser.START_TAG, ns, "request");
                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }
                            String localName = parser.getName();
                            if (localName.equals("trip")) {
                                Log.i(TAG, "Found a trip");
                                String currTrip = readEntry(parser);
                                if (!legs.contains(currTrip)) {
                                    legs.add(currTrip); 
                                }
                            } else {
                                Log.i(TAG, "Found " + currName + " instead of trip");
                                skip(parser);
                            }
                        }
                    } else {
                        Log.i(TAG, "Found " + name + " instead of request");
                        skip(parser);
                    }
                }
            } else {
                Log.i(TAG, "Found " + name + " instead of schedule");
                skip(parser);
            }
        }
        return legs;
    }


    private String readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "At top of readEntry");
        parser.require(XmlPullParser.START_TAG, ns, "trip");
        String legs = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("leg")) {
                legs += readLeg(parser) + ";";
            } else {
                skip(parser);
            }
        }
        return legs;
    }

    private String readLeg(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "At top of readLeg");
        parser.require(XmlPullParser.START_TAG, ns, "leg");
        String order = parser.getAttributeValue(null, "order");
        String origin = parser.getAttributeValue(null, "origin");
        String destination = parser.getAttributeValue(null, "destination");
        String line = parser.getAttributeValue(null, "line");
        String head = parser.getAttributeValue(null, "trainHeadStation");

        String currLeg = origin + ":" + destination+ ":" + head;

        parser.require(XmlPullParser.END_TAG, ns, "leg");

        return currLeg;
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

