package com.android.bear.datafree;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by bear on 5/13/17.
 */


class VerifiedNumbersSingleton {

    private static VerifiedNumbersSingleton instance = null;
    private String serverNumber; // have this be determined from memory

    private VerifiedNumbersSingleton() {
        // Exists only to defeat instantiation.
    }

    static VerifiedNumbersSingleton getInstance() {
        if(instance == null) {
            instance = new VerifiedNumbersSingleton();
        }
        return instance;
    }

    //---Managing Number----------------------------------------------------------------------------

    void setServerNumber(String newNum, Context context) {
        if(newNum.length() == 12) { // 12 = "+15551119999"
            serverNumber = newNum;
        } else {
            Toast.makeText(context, "Please input proper number", Toast.LENGTH_LONG).show();
        }
    }

    String getNumber() {
        return serverNumber;
    }

    //---Validity Checks----------------------------------------------------------------------------

    // returns true if input string is from the right number
    boolean isValid(String input) {
        return (serverNumber != null && serverNumber.equals(input));
    }

    //returns true if input has been formatted properly
    boolean isValidMessage(String input) {
        return (checkHeader(input) || checkContent(input));
    }

    //header = {xx...
    // { bk r i si ...
    boolean checkHeader(String message) {
        char[] charArray = message.toCharArray();

        //true if first char is '{' and next two are lowercase
        if(charArray[0]!='{') {
            return false; // {
        } else if(!Character.isLowerCase(charArray[1])) {
            return false; // botkey
        } else if(!Character.isLowerCase(charArray[2])) {
            return false; // botkey
        } else if(!Character.isLetter(charArray[3])) {
            return false; // botCase
        } else if(!Character.isDigit(charArray[4])) {
            return false; // instance
        } else if(!Character.isLetter(charArray[5])) {
            return false; // size
        } else if(!Character.isLetter(charArray[6])) {
            return false; // size
        }
        return true;
    }

    //content = xx...
    // i si ...
    boolean checkContent(String message) {
        char[] charArray = message.toCharArray();

        //true if the first two characters are lowercase letters
        if(!Character.isDigit(charArray[0])) {
            return false; // instance
        } else if(!Character.isLowerCase(charArray[1])) {
            return false; // index
        } else if(!Character.isLowerCase(charArray[2])) {
            return false; // index
        }
        return true;
    }
}
