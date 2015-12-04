package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RouteXmlParser extends BartXmlParser {
    private static final String ns = null;
    private static final String TAG = "RouteXmlParser";

    /**
     * Extract each Route entry in the XML
     */
    List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList Routes = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("routes")) {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String currName = parser.getName();
                    if (currName.equals("route")) {
                        Routes.add(readRoute(parser));
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }
        }
        return Routes;
    }

    private Route readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "route");
        String routeName = null;
        String abbreviation = null;
        String Id = null;
        String number = null;
        String color = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (name.equals("name")) {
                routeName = readName(parser);
            } else if (name.equals("abbr")) {
                abbreviation = readAbbr(parser);
            } else if (name.equals("routeID")) {
                Id = readRouteID(parser);
            } else if (name.equals("number")) {
                number = readNumber(parser);
            } else if (name.equals("color")) {
                color = readColor(parser);
            } else {
                skip(parser);
            }
        }
        return new Route(routeName, abbreviation, Id, number, color);
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

    private String readRouteID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "routeID");
        String routeID = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "routeID");
        return routeID;
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
}

