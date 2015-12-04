package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FareXmlParser extends BartXmlParser {
    private static final String ns = null;
    private static final String TAG = "FareXmlParser";

    /**
     * Extract each fare entry in the XML
     */
    List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList fares = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            String name = parser.getName();
            if (name.equals("trip")) {
                fares.add(readTrip(parser));
            } else {
                skip(parser);
            }
        }
        return fares;
    }

    private String readTrip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
}

