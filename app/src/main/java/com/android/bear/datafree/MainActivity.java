package com.android.bear.datafree;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //---- Reading SMS -----!
    static MainActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<>();
    SmsManager smsManager = SmsManager.getDefault();
    ListView messages;
    ArrayAdapter arrayAdapter;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    String incomingContent = "";    //stores incoming sms from server
    String[] messageArray;          //array that stores all incoming sms in proper order
                                    //uses indexKeys to order correctly

    ArrayList<ContentPackage> incomingPackages = new ArrayList<ContentPackage>();

    //---- User Input -----
    EditText input;
    String toServer = "";   //final String that gets texted to the server
    String botKey = "aa";   //first 2 chars of toServer to identify which bot requested

    //---- Buttons and User UI -----
    Button button0, button1, button2;
    TextView infoBox;           //displays info for currently selected bot
    TextView messageDisplay;    //test variable to display message

    //---- Bot State Machine -----
    String[] buttonArray;       //stores names of bots so BotFinder.java can use them
    int currentBotIndex = 0;    //which bot is selected in buttonArray

    //---- Word List ----
    String filePath = "google-10000-english-usa.txt";
    ArrayList<String> wordList;


    //---- Classes -----
    BotFinder botFinder = new BotFinder();  //pass in bot name, get important info
                                            //key, name, info
    KeyConverter keyChange = new KeyConverter();    //performs useful functions on
                                                    //botKeys, indexes
    ArrayHandler arrayHandler = new ArrayHandler(); //useful functions on arrays
    HuffDecoder huffDecoder = new HuffDecoder();    //decodes sms into regular text
    FixSentenceCase fixCapitals = new FixSentenceCase();


    //End of declaring variables
    //----------------------------------------------------------------------

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set up sms reading
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages = (ListView) findViewById(R.id.messages);
        input = (EditText) findViewById(R.id.input);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        messages.setAdapter(arrayAdapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            //refreshSmsInbox();
        }

        wordList = new ArrayList<String>();
        //---createWordList---------
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(filePath)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                wordList.add(mLine);

            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        //------------

        //set up buttonArray
        buttonArray = new String[4];    //length = number of bots
        buttonArray[0] = "suggestions";
        buttonArray[1] = "wikipedia";
        buttonArray[2] = "urban_dic";

        //set up buttons
        button0 = (Button) findViewById(R.id.bot_button0);
        button1 = (Button) findViewById(R.id.bot_button1);
        button2 = (Button) findViewById(R.id.bot_button2);
        infoBox = (TextView) findViewById(R.id.infoBox);
        messageDisplay = (TextView) findViewById(R.id.messageDisplay);

        //set up button names
        button0.setText(botFinder.getName(buttonArray[0]));
        button1.setText(botFinder.getName(buttonArray[1]));
        button2.setText(botFinder.getName(buttonArray[2]));

        updateScreen();
    }

    //---GET PERMISSIONS----------------------------------------------------------------------------

    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                //refreshSmsInbox();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    //---UPDATE SCREEN------------------------------------------------------------------------------

    // gets called when app receives a new, valid text
    public void updateInbox(String smsMessageStr) {
        //arrayAdapter.add(smsMessageStr);
        refreshSmsInbox(smsMessageStr);
    }

    public void updateScreen() {
        //update bot names

        //update botInfo
        infoBox.setText(botFinder.getInfo(buttonArray[currentBotIndex]));
    }

    //---CONCERNING MESSAGES------------------------------------------------------------------------

    //Sends SMS Declaration Request to Data Free Server
    public void onSendClick(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            //Send sms to server

            // CREATE INSTANCE
            int instance;

            //SOMETHING IS FUCKY


            // find first non-active package
            // loop through
            boolean found = false;
            int nonActiveIndex = -1;
            for(int i=nonActiveIndex+1; i<incomingPackages.size() && !found; i++) {
                if(!incomingPackages.get(i).active) {
                    found = true;
                    nonActiveIndex = i;
                }
            }
            if(found) {
                instance = nonActiveIndex;
            } else {
                instance = incomingPackages.size();
            }


            //instance = incomingPackages.size();

            String botCase = "c"; // CHANGE THIS

            // format: { bk r i ...
            toServer = "{" + botKey + botCase + instance + input.getText().toString();
            smsManager.sendTextMessage("+15555555555", null, toServer, null, null);
            Toast.makeText(this, "Request Declaration sent!", Toast.LENGTH_SHORT).show();

            //clear text in Edit text
            input.setText("");
        }
    }

    //send Content Request to Data Free Server
    public void sendContentRequestText(String botKey, String botCase, String instance, String content) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            //Send sms to server

            // format: bk + r + i + ...
            toServer = botKey + botCase + instance + content;
            smsManager.sendTextMessage("+15555555555", null, toServer, null, null);
            Toast.makeText(this, "Content Request sent!", Toast.LENGTH_SHORT).show();
        }

    }

    // gets called when a new message comes in
    public void refreshSmsInbox(String smsMessage) {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

        //check whether incoming sms is a header, content
        if(smsMessage.substring(0,1).contains("{")) {

            // declaration text = { + bk + r + i + si + ...
            //                    0   12   3   4   56
            // { + botkey + botCase + instance + size
            String bK = smsMessage.substring(1,3);
            String kS = smsMessage.substring(5,7);
            String request = smsMessage.substring(7);

            String botCase = smsMessage.substring(3,4);
            String instance = smsMessage.substring(4,5);
            String content = smsMessage.substring(7);

            /*
            // FIX THIS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // create new package in correct spot
            if(incomingPackages.size() >= keyChange.keyToInt(instance)) {
                incomingPackages.add(new ContentPackage(bK, kS, request));
            } else {
                incomingPackages.set(keyChange.keyToInt(instance),new ContentPackage(bK, kS, request));
            }
*/
            incomingPackages.add(new ContentPackage(bK, kS, request));



            // send confirmation text
            sendContentRequestText(bK, botCase, instance, content);

        } else {
            //Add message to content string and display it
            // content text = i + si + ...
            //                0 + 12 + ...

            //---add to screen for debug----
            incomingContent = incomingContent + smsMessage;
            arrayAdapter.clear();
            arrayAdapter.add(incomingContent);
            //----

            // add to package
            int instance = Integer.parseInt(smsMessage.substring(0,1));
            smsMessage += " "; // add back the space that twilio formatting deletes
            incomingPackages.get(instance).addMessage(smsMessage);
            //---------


            // display how many received/expected messages
            messageDisplay.setText(arrayHandler.findPercentFull(incomingPackages.get(instance).contents));

            // if the entire package has been received, display it
            if(arrayHandler.checkFull(incomingPackages.get(instance).contents)) {

                String fullAnswer = ArrayHandler.createString(incomingPackages.get(instance).contents);
                fullAnswer = huffDecoder.decode(fullAnswer, wordList);
                fullAnswer = fixCapitals.fixCapitalization(fullAnswer);
                messageDisplay.setText(fullAnswer);

                // set package to be complete
                incomingPackages.get(instance).complete();
            }


        }
    }

    //---BOT BUTTONS--------------------------------------------------------------------------------
    //Change
    public void onClick_B0(View view) {
        currentBotIndex = 0;
        botKey = botFinder.getKey(buttonArray[0]);
        updateScreen();
    }
    public void onClick_B1(View view) {
        currentBotIndex = 1;
        botKey = botFinder.getKey(buttonArray[1]);
        updateScreen();
    }
    public void onClick_B2(View view) {
        currentBotIndex = 2;
        botKey = botFinder.getKey(buttonArray[2]);
        updateScreen();
    }
}