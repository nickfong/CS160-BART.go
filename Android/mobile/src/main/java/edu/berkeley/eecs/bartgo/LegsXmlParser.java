package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LegsXmlParser extends BartXmlParser {
    private static final String ns = null;
    private static final String TAG = "LegsXmlParser";

    /**
     * Extract each legs entry in the XML
     */
    List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
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
                parser.require(XmlPullParser.START_TAG, ns, "schedule");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String currName = parser.getName();
                    if (currName.equals("request")) {
                        parser.require(XmlPullParser.START_TAG, ns, "request");
                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }
                            String localName = parser.getName();
                            if (localName.equals("trip")) {
                                parser.require(XmlPullParser.START_TAG, ns, "trip");
                                String currTrip = readTrip(parser);
                                parser.require(XmlPullParser.END_TAG, ns, "trip");
                                if (!legs.contains(currTrip)) {
                                    legs.add(currTrip);
                                }
                            } else {
                                skip(parser);
                            }
                        }
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }
        }
        return legs;
    }


    private String readTrip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
//        parser.require(XmlPullParser.END_TAG, ns, "trip");
        return legs;
    }

    private String readLeg(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "leg");
        String order = parser.getAttributeValue(null, "order");
        String origin = parser.getAttributeValue(null, "origin");
        String destination = parser.getAttributeValue(null, "destination");
        String line = parser.getAttributeValue(null, "line");
        String head = parser.getAttributeValue(null, "trainHeadStation");

        String currLeg = origin + ":" + destination+ ":" + head;

        skip(parser);

//        parser.require(XmlPullParser.END_TAG, ns, "leg");

        return currLeg;
    }
}

