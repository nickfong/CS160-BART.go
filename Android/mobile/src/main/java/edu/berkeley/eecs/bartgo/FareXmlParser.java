package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FareXmlParser {
    private static final String ns = null;
    private static final String TAG = "FareXmlParser";

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
     * Extract each fare entry in the XML
     */
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList fares = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            String name = parser.getName();
            if (name.equals("trip")) {
                fares.add(readEntry(parser));
            } else {
                Log.i(TAG, "Found " + name + " instead of trip");
                skip(parser);
            }
        }
        return fares;
    }

    private String readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        String fare = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (name.equals("fare")) {
                fare = readFare(parser);
            } else {
                skip(parser);
            }
        }
        return fare;
    }

    private String readFare(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "fare");
        String fare = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "fare");
        return fare;
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

