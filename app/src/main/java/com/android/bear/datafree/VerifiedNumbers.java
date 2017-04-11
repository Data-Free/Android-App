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
        if(checkHeader(input)) {
            return true;
        } else if(checkContent(input)) {
            return true;
        }
        return false;
    }

    //header = {xx...
    boolean checkHeader(String message) {
        char[] charArray = message.toCharArray();

        //true if first char is '{' and next two are lowercase
        if(charArray[0]!='{') {
            return false;
        } else if(!Character.isLowerCase(charArray[1])) {
            return false;
        } else if(!Character.isLowerCase(charArray[2])) {
            return false;
        }
        return true;
    }

    //content = xx...
    boolean checkContent(String message) {
        char[] charArray = message.toCharArray();

        //true if the first two characters are lowercase letters
        if(!Character.isLowerCase(charArray[0])) {
            return false;
        } else if(!Character.isLowerCase(charArray[1])) {
            return false;
        }
        return true;
    }
}
