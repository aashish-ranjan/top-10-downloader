package com.example.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private final String DESIRED_IMAGE_HEIGHT = "53";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        boolean inEntryTag = false;
        String textValue = "";
        String tagName;
        FeedEntry currentApplication = null;
        boolean foundImage = false;

        try {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = parserFactory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                tagName = xmlPullParser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntryTag = true;
                            currentApplication = new FeedEntry();
                        }
                        else if(inEntryTag && "image".equalsIgnoreCase(tagName)) {
                            String imageHeight = xmlPullParser.getAttributeValue(null, "height");
                            if(imageHeight != null) {
                                foundImage = DESIRED_IMAGE_HEIGHT.equalsIgnoreCase(imageHeight);
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xmlPullParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (inEntryTag) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                applications.add(currentApplication);
                                inEntryTag = false;
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentApplication.setName(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentApplication.setSummary(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentApplication.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentApplication.setReleaseDate(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                if(foundImage) {
                                currentApplication.setImageUrl(textValue);
                                }
                            }
                        }
                        break;

                    default:
                        //do nothing
                }
                eventType = xmlPullParser.next();
            }
//            for(FeedEntry app: applications) {
//                Log.d(TAG, "**********************************");
//                Log.d(TAG, app.toString());
//            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
