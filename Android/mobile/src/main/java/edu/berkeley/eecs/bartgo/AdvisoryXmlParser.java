package edu.berkeley.eecs.bartgo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdvisoryXmlParser {
    private static final String ns = null;
    private static final String TAG = "AdvisoryXmlParser";

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
     * Extract each Advisory entry in the XML
     */
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList advisories = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("bsa")) {
                Log.i(TAG, "Calling readEntry");
                advisories.add(readEntry(parser));
                /*
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String currName = parser.getName();
                    if (currName.equals("Advisory")) {
                        Advisorys.add(readEntry(parser));
                    } else {
                        Log.i(TAG, "Found " + currName + " instead of a Advisory");
                        skip(parser);
                    }
                }
                */
            } else {
                Log.i(TAG, "Found " + name + " instead of advisories");
                skip(parser);
            }
        }
        return advisories;
    }

    private Advisory readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "At top of readEntry");
        parser.require(XmlPullParser.START_TAG, ns, "bsa");
        String id = null;
        String type = null;
        String description = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (name.equals("bsa")) {
                skip(parser);//TODO FIXME
//                id = readId(parser);
            } else if (name.equals("type")) {
                type= readType(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else {
                Log.i(TAG, "Found " + name + " instead of anything in bsa");
                skip(parser);
            }
        }
        return new Advisory(id, type, description);
    }

    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "bsa");
        String id = "";
        String tag = parser.getName();
        if (tag.equals("id")) {
            id = parser.getAttributeValue(null, "id");
        }
        return id;
    }

    private String readType(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "type");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "type");
        return type;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
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

