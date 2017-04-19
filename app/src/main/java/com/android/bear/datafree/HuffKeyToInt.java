package com.android.bear.datafree;

/**
 * Created by bear on 4/17/17.
 */

public class HuffKeyToInt {
    // returns an int value corresponding to huffKey
    int convert(String huffKey) {
        int keySize = findKeySize(huffKey);
        String pureKey; // used to take huffKey without punctuation
        int result;

        if(keySize == -1) {

            // if keySize == -1, then word doesn't exit in wordList
            // flag it with -1
            result = -1;
        } else {

            // if keySize != -1, then word exists!
            // this means we can convert the key to an int index
            pureKey = huffKey.substring(0, keySize);
            result = keyToInt(pureKey);
        }

        return result;
    }

    //---keyToInt----------------------------

    //takes in a key and returns int value
    int keyToInt(String pureKey) {
        char[] charArray = pureKey.toCharArray();
        int result = 0;
        int scale = getScale(charArray);
        int keyValue = getValue(charArray);

        if(charArray.length == 2) {
            result = (scale-1)*2703 + keyValue;
        } else {
            result = scale*2703 + keyValue;
        }

        return result;
    }

    //---getValue----------------------------

    // converts the two alphabetical chars into an int
    int getValue(char[] key) {
        int resultValue = 0;
        if(key.length == 2) {
            resultValue += convert10sDigit(key[0]);
            resultValue += convert1sDigit(key[1]);
            return resultValue;
        }
        // if here, length = 3 -> ignore first char
        resultValue += convert10sDigit(key[1]);
        resultValue += convert1sDigit(key[2]);
        return resultValue;
    }

    //---getScale------------------------------------------
    // converts char[0] into an int and returns it
    int getScale(char[] key) {
        // if key has 2 entries, then the scale of 1 is implied
        if(key.length == 2) {
            return 1;
        }
        return ((int) key[0]) - 49;
    }

    //---findKeySize--------------------------

    // returns size of Key by inspecting first char
    int findKeySize(String huffKey) {

        int keySize = -1; // if first char isn't a letter or num, flag it
        char firstChar = huffKey.charAt(0);

        if(isLetter(firstChar)) {
            // if letter: keySize = 2 | [ab]
            keySize = 2;

        } else if(is2through9(firstChar)) {
            // if number (2-9), keySize = 3 | [3ab]
            keySize = 3;

        }
        return keySize;
    }

    //---convert10sDigit-----------------------------------

    // converts the 10s digit of key and returns int value
    int convert10sDigit(char digitKey) {

        int letter = (int) digitKey;
        int result = 0;
        if(letter >= 97) {
            // if letter is lowercase, convert to decimal
            // [a,z] = [0, 25]
            return (letter-97)*52;
        }
        // else, uppercase
        // [A,Z] = [26, 51]
        return (letter - 39)*52;
    }

    //---convert1sDigit-----------------------------------

    // converts 1s digit of key and returns int value
    int convert1sDigit(char digitKey) {

        int letter = (int) digitKey;
        int result = 0;

        // check if lowercase
        if(letter >= 97) {
            // [a,z] = [0, 25]
            return (letter-97);
        }
        // else, uppercase
        // [A,Z] = [26, 51]
        return (letter - 39);
    }


    //---isLetter----------------------------------------

    // returns true if char is a letter
    boolean isLetter(char character) {
        int input = (int)character;
        if((input>=65 && input<=90) || (input>=97 && input<=122)) {
            return true;
        }
        return false;
    }

    //---is2through9------------------------------------

    // returns true is char is a number [2-9]
    boolean is2through9(char character) {
        int input = (int)character;
        if(input>=50 && input<=57) {
            return true;
        }
        return false;
    }
}
