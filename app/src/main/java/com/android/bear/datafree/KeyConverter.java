package com.android.bear.datafree;

/**
 * Created by bear on 4/8/17.
 * Used to convert between index and bot keys (strings)
 */

class KeyConverter {
    KeyConverter() {}

    //takes in a 2 char string and returns decimal equivalent
    int keyToInt(String input) {
        // a = 0, z = 25
        char[] charArray = input.toCharArray();

        //Convert 10's digit into an int
        int d1 = (int) charArray[0];
        d1 -= 97;
        d1 *= 26;

        //Convert 1's digit into an int
        int d2 = (int) charArray[1];
        d2 -= 97;

        return d1 + d2;
    }

    String intToKey(int input) {
        String result = "";
        return result;
    }
}
