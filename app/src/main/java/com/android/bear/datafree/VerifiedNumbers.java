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
        //change this to check whether formatting is right -> more flexible
        if(input.equals("Hello from Twilio!")) {
            return false;
        }
        return true;
    }
}
