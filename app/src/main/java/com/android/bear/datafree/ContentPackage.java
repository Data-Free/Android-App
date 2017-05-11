package com.android.bear.datafree;

import android.widget.Toast;

/**
 * Created by bear on 5/10/17.
 */

class ContentPackage {

    private KeyConverter keyConvert = new KeyConverter();

    int botCase, botKey;
    String request;
    String[] contents;
    boolean active;

    ContentPackage(String bK, String kS, String contentRequest) {
        botKey = keyConvert.keyToInt(bK);
        contents = new String[keyConvert.keyToInt(kS)];
        request = contentRequest;
        active = true;
    }

    //---addMessage---------------------------------------------------------------------------------

    // adds a message to contents[]
    void addMessage(String message) {
        // format: i si ... (instance + index + content)
        int index = keyConvert.keyToInt(message.substring(1,3));
        contents[index] = message.substring(3);
    }

    //---complete-----------------------------------------------------------------------------------

    // call to flag package as inactive
    void complete() {
        active = false;
    }
}
