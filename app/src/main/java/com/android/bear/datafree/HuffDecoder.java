package com.android.bear.datafree;

import java.util.ArrayList;

/**
 * Created by bear on 4/17/17.
 */

class HuffDecoder {

    // helper classes
    SeparateHuffStrings separate = new SeparateHuffStrings();
    HuffKeyToInt keyToInt = new HuffKeyToInt();
    //IntToWord intToWord = new IntToWord();

    public HuffDecoder() {};

    // returns decoded version
    public String decode(String encoded, ArrayList<String> wordList) {

        String decodedMessage = "";

        //separate into array of keys
        ArrayList<String> keys = separate.separate(encoded);

        // DECODE LOOP-------
        for(int i=0; i<keys.size(); i++) {
            String currentKey = keys.get(i);
            String currentWord = "";

            int wordIndex = keyToInt.convert(currentKey);

            // CONVERT LOGIC -----
            if(wordIndex != -1) {
                // if wordIndex wasn't flagged as -1, then it is a word!
                // find the word
                currentWord = wordList.get(wordIndex);

                // WORK IN PROGRESS ------------------------------
                //------------------------------------------------
                // add punctuation that was at end of key
                // currentWord += getPostPunctuation(currentKey)
                currentWord += getPunctuation(currentKey);
                currentWord += " ";

            } else {
                // if wordIndex has been flagged as -1, it isn't a word...
                // check if a word or special char or an unknown word

                if(currentKey.charAt(0) == '0' ) {
                    // if CurrentKey[0] = '0' -> unknown word
                    // chop off first and last char
                    currentWord = currentKey.substring(1, currentKey.length());

                } else {
                    // not a word, just special characters
                    currentWord = currentKey;
                }

            } // END CONVERT LOGIC ----

            decodedMessage += currentWord;
        } // END DECODE LOOP-------
        return decodedMessage;
    }

    //---getPunctuation----------------------------------
    private String getPunctuation(String input) {
        int keySize = 0;
        char firstChar = input.charAt(0);
        String punctuation = "";


        // determine key size
        if(isLetter(firstChar)) {
            keySize = 2;
        } else if(is2through9(firstChar)) {
            keySize = 3;
        }

        punctuation = input.substring(keySize);
        return punctuation;
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

}
