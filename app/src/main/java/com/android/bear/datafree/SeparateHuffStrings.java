package com.android.bear.datafree;

import java.util.ArrayList;

/**
 * Created by bear on 4/17/17.
 */

public class SeparateHuffStrings {

    // returns an arraylist after separating the message
    ArrayList<String> separate(String encodedMessage) {

        //return this
        ArrayList<String> separatedMessage = new ArrayList<String>();
        char[] charArray = encodedMessage.toCharArray();
        int keySize = 0;

        // ADD KEYS LOOP
        for(int i=0; i<charArray.length; i+=keySize) {

            char firstDigit = charArray[i]; //first digit of key determines size
            keySize = 0; // use to separate huffKey
            String huffKey = ""; // add to separatedMessage as new entry

            keySize = determineKeySize(firstDigit, i, charArray);

            // now that keySize has been determined, add keys accordingly
            if(keySize == 1) {

                // if keySize == 1,
                // add firstDigit to previous entry in separatedMessage
                // unless there is no previous entry, then create one

                if(separatedMessage.size()==0) {
                    separatedMessage.add("" + firstDigit);
                } else {
                    int priorKey = separatedMessage.size() - 1;
                    String updateMessage = separatedMessage.get(priorKey);
                    updateMessage += firstDigit;
                    separatedMessage.set(priorKey, updateMessage);
                }
            } else {

                //add new entry
                huffKey = encodedMessage.substring(i, i+keySize);
                separatedMessage.add(huffKey);
            }

            // END ADD KEYS LOOP
        }
        // END SEPARATE FUNCTION
        return separatedMessage;
    }

    //---determineKeySize------------------------------

    private int determineKeySize(char firstDigit, int i, char[] charArray) {
        int size = 0;
        if(isLetter(firstDigit)) {
            //if first digit is a letter, then key size is 2
            size = 2;

        } else if(is2through9(firstDigit)) {
            // if first digit is a number 2-9, then key size is 3
            size = 3;

        } else if(firstDigit == '0') {
            // if first digit is zero, then this isn't a normal key
            // key will go on until you reach a space
            int nextSpace = findNextSpace(i, charArray);
            size = nextSpace - i;

        } else {
            // if none of the above, then it is a literal character
            // this character is a remnant of the previous key
            // flag it with size 1 so it can be added to previous key
            size = 1;
        }
        return size;
    }

    //---findNextSpace----------------------------------

    // returns the next index of the next space
    private int findNextSpace(int start, char[] message) {
        for(int j = start+1; j<message.length; j++) {
            if(message[j] == ' ') {
                return j; //
            }
        }
        return -1;
    }

    //---isLetter----------------------------------------

    // returns true if char is a letter
    private boolean isLetter(char character) {
        int input = (int)character;
        if((input>=65 && input<=90) || (input>=97 && input<=122)) {
            return true;
        }
        return false;
    }

    //---is2through9------------------------------------

    // returns true is char is a number [2-9]
    private boolean is2through9(char character) {
        int input = (int)character;
        if(input>=50 && input<=57) {
            return true;
        }
        return false;
    }

    //--END PROGRAM------------------
}
