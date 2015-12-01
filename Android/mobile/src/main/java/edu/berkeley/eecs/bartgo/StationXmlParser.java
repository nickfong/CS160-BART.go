public class StationXmlParser {
    private static final String ns = null;

    public ArrayList<Station> parse(InputStream in) throws XmlPullParserException, IOException {
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
    private ArrayList<Station> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList stations = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("station")) {
                stations.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return stations;
    }

    private Station readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String abbreviation = null;
        String name = null;
        String address = null;
        String zip = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                title = readTitle(parser);
            } else if (name.equals("abbr")) {
                summary = readSummary(parser);
            } else if (name.equals("address")) {
                link = readLink(parser);
            } else if (name.equals("zipcode")) {
                zip = readLink(parser);
            else {
                skip(parser);
            }
        }
        return new Station(abbreviation, name, address, zipcode);
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
}

