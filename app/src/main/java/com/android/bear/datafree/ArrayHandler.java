package com.android.bear.datafree;

/**
 * Created by bear on 4/9/17.
 * Program does useful checks on arrays
 */

class ArrayHandler {
    ArrayHandler() {}

    //checks if an array has no blank slots
    boolean checkFull(String[] array) {
        for(int i=0; i<array.length; i++) {
            if(array[i]==null) {
                return false;
            }
        }
        return true;
    }

    //compiles an array into a string
    public static String createString(String[] array) {
        String result = "";
        for(int i=0; i<array.length; i++) {
            result += array[i];
        }
        return result;
    }
}
