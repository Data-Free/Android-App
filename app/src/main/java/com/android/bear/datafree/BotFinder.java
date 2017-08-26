package com.android.bear.datafree;

/**
 * Created by bear on 4/8/17.
 * Returns important info for bots when passed in name
 */

class BotFinder {
    BotFinder() { }

    //Given a String id, return proper key
    String getKey(String name) {

        if (name == null) { return "getKey(null)"; }
        String result = "";

        switch(name) {
            case "tutorial":
                result = "aa";
                break;
            case "updates":
                result = "ab";
                break;
            case "suggestions":
                result = "ac";
                break;
            case "wikipedia":
                result = "ad";
                break;
            case "urban_dic":
                result = "ae";
                break;
        }
        return result;
    }

    String getInfo(String name) {

        if (name == null) { return "getInfo(null)"; }
        String result = "";

        switch(name) {
            case "tutorial":
                result = "Learn how to use data free";
                break;
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

            default:
                result = "404: Not Found";
                break;
        }
        return result;
    }

    String getName(String name) {

        if (name == null) { return "getName(null)"; }
        String result = "";

        switch(name) {
            case "tutorial":
                result = "Tutorial";
                break;
            case "updates":
                result = "Updates";
                break;
            case "suggestions":
                result = "Suggest";
                break;
            case "wikipedia":
                result = "Wiki";
                break;
            case "urban_dic":
                result = "Urban Dic";
                break;
            default:
                result = "404: Not Found";
                break;
        }
        return result;
    }
}
