package com.android.bear.datafree;

/**
 * Created by bear on 4/8/17.
 */

public class BotFinder {
    public BotFinder() { }

    //Given a String id, return proper key
    public String getKey(String name) {
        String result = "";
        switch(name) {
            case "updates":
                result = "aa";
                break;
            case "suggestions":
                result = "ab";
                break;
            case "wikipedia":
                result = "ac";
                break;
            case "urban_dic":
                result = "ad";
                break;
        }
        return result;
    }

    public String getInfo(String name) {
        String result = "";
        switch(name) {
            case "updates":
                result = "This bot provides updates for the app";
                break;
            case "suggestions":
                result = "Send your bot suggestions to the Data Free team!";
                break;
            case "wikipedia":
                result = "View wikipedia articles";
                break;
            case "urban_dic":
                result = "Ever wonder what something really means?\nFind " +
                        "out now on urban dictionary!";
                break;
        }
        return result;
    }

    public String getName(String name) {
        String result = "";
        switch(name) {
            case "updates":
                result = "updates";
                break;
            case "suggestions":
                result = "suggest";
                break;
            case "wikipedia":
                result = "Wiki";
                break;
            case "urban_dic":
                result = "Urban Dic";
                break;
        }
        return result;
    }
}
