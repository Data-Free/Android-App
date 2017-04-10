package com.android.bear.datafree;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by bear on 4/10/17.
 */

public class VerifiedNumbers {

    static String[] numbers = new String[1];

    void VerifiedNumbers() {}

    boolean isValid(String input) {
        switch(input) {
            case "+15555555555":
                return true;
        }
        return false;
    }
}
