package com.android.bear.datafree;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by bear on 4/10/17.
 */

public class VerifiedNumbers {

    static String[] numbers = new String[1];

    VerifiedNumbers() {}

    //Determine whether a number is valid
    boolean isValid(String input) {
        switch(input) {
            case "+15555555555":
                return true;
        }
        return false;
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
