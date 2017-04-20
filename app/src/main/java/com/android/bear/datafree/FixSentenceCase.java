package com.android.bear.datafree;

import java.util.ArrayList;

/**
 * Created by bear on 4/20/17.
 */

class FixSentenceCase {

    FixSentenceCase() {};

    //---fixCapitalization-------------------------------------------------

     String fixCapitalization(String input) {
        ArrayList<String> sentences = breakToSentences(input);
        String fixed = "";

        for(int i=0; i<sentences.size(); i++) {
            fixed += capitalizeSentence(sentences.get(i));
        }

        return fixed;
    }

    //---capitalizeSentence------------------------------------------------

    // capitalizes the first letter in a string
    private static String capitalizeSentence(String input) {
        char[] charArray = input.toCharArray();
        boolean keepGoing = true;
        int i = 0;

        while(keepGoing && i<charArray.length) {
            if(isLetter(charArray[i])) {
                charArray[i] = capitalizeLetter(charArray[i]);
                keepGoing = false;
            }
            i++;
        }

        return new String(charArray);
    }

    //---isLetter----------------------------------------------------------

    // returns true if char is a letter
    private static boolean isLetter(char character) {
        int input = (int)character;
        if((input>=65 && input<=90) || (input>=97 && input<=122)) {
            return true;
        }
        return false;
    }

    //---capitalizeLetter--------------------------------------------------

    // returns upper case version of char if lowercase
    private static char capitalizeLetter(char input) {
        if(input>=97 && input<=122) {
            return (char) (input - 32);
        }
        return input;
    }

    //---breakToSentences--------------------------------------------------

    // returns a list of sentences
    private static ArrayList<String> breakToSentences(String input) {
        ArrayList<String> sentences = new ArrayList<String>();
        char[] charArray = input.toCharArray();

        int endOfLastSentence = 0;
        String newSentence = "";

        // searches for sentence ending punctuation
        for(int i=0; i<charArray.length-1; i++) {
            if(isEndChar(charArray[i])) {

                if(charArray[i+1] == ' ' || isQuoteChar(charArray[i+1])) {
                    //found end of sentence
                    newSentence = input.substring(endOfLastSentence, i+1);
                    sentences.add(newSentence);
                    newSentence = "";
                    endOfLastSentence = i+1;
                }
            }
        }
        // add last sentence
        newSentence = input.substring(endOfLastSentence);
        sentences.add(newSentence);

        return sentences;
    }

    //---isQuoteChar-------------------------------------------------------

    // returns true if char = ' or "
    private static boolean isQuoteChar(char input) {
        switch(input) {
            case '\'':
            case '"':
                return true;
        }
        return false;
    }
    //---isEndChar---------------------------------------------------------

    // returns true if char = . or ! or ?
    private static boolean isEndChar(char input) {
        switch(input) {
            case '.':
            case '?':
            case '!':
                return true;
        }
        return false;
    }

}
