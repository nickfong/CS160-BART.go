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
        ArrayList legs = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("schedule")) {
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
                                legs.add(readEntry(parser));
                            } else {
                                Log.i(TAG, "Found " + currName + " instead of a legs");
                                skip(parser);
                            }
                        }
                    } else {
                        Log.i(TAG, "Found " + name + " instead of legs");
                        skip(parser);
                    }
                }
            } else {
                Log.i(TAG, "Found " + name + " instead of legs");
                skip(parser);
            }
        }
        return legs;
    }

    private legs readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "At top of readEntry");
        parser.require(XmlPullParser.START_TAG, ns, "trip");
        String legsName = null;
        String abbreviation = null;
        String Id = null;
        String number = null;
        String color = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (name.equals("name")) {
                legsName = readName(parser);
            } else if (name.equals("abbr")) {
                abbreviation = readAbbr(parser);
            } else if (name.equals("legsID")) {
                Id = readlegsID(parser);
            } else if (name.equals("number")) {
                number = readNumber(parser);
            } else if (name.equals("color")) {
                color = readColor(parser);
            } else {
                skip(parser);
            }
        }
        return new legs(legsName, abbreviation, Id, number, color);
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

    private String readlegsID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "legsID");
        String legsID = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "legsID");
        return legsID;
    }

    private String readNumber(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "number");
        String number = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "number");
        return number;
    }

    private String readColor(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "color");
        String color = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "color");
        return color;
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

