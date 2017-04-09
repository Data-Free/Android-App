package com.android.bear.datafree;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
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

    //---- User Input -----!
    EditText input;
    String toServer = "";   //final String that gets texted to the server
    String botKey = "aa";   //first 2 chars of toServer to identify which bot requested

    //---- Buttons and User UI -----!
    Button button0, button1, button2;
    TextView infoBox;           //displays info for currently selected bot
    TextView messageDisplay;    //test variable to display message

    //---- Bot State Machine -----!
    String[] buttonArray;       //stores names of bots so BotFinder.java can use them
    int currentBotIndex = 0;    //which bot is selected in buttonArray

    //---- Classes -----!
    BotFinder botFinder = new BotFinder();  //pass in bot name, get important info
                                            //key, name, info
    KeyConverter keyChange = new KeyConverter();    //performs useful functions on
                                                    //botKeys, indexes
    ArrayHandler arrayHandler = new ArrayHandler(); //useful functions on arrays


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

    public void refreshSmsInbox(String smsMessage) {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

        //check whether incoming sms is a header, content, or end sms
        if(smsMessage.substring(0,1).contains("{")) {
            //check if it is a header text

            //<Debug tools>
            //String display = smsMessage.substring(39,41);
            //display = display + ": " + keyChange.keyToInt(display);
            //messageDisplay.setText(display);

            //create new array the size of the number of incoming sms packages
            messageArray = new String[keyChange.keyToInt(smsMessage.substring(1,3))];
        } else if(smsMessage.substring(0,1).contains("}")) {

        } else {
            //Add message to content string and display it
            incomingContent = incomingContent +" " + smsMessage;
            arrayAdapter.clear();
            arrayAdapter.add(incomingContent);

            //get sms index and use it to put String in proper place
            int mIndex = keyChange.keyToInt(smsMessage.substring(0,2));
            messageArray[mIndex] = smsMessage.substring(2);

            if(arrayHandler.checkFull(messageArray)) {
                messageDisplay.setText(ArrayHandler.createString(messageArray));
            }
        }
    }

    public void updateInbox(String smsMessageStr) {
        //arrayAdapter.add(smsMessageStr);
        refreshSmsInbox(smsMessageStr);
    }

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

    //Sends SMS to Data Free Server
    public void onSendClick(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            //Send sms to server
            toServer = botKey + input.getText().toString();
            smsManager.sendTextMessage("+15555555555", null, toServer, null, null);
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();

            //clear text in Edit text
            input.setText("");
        }
    }

    public void updateScreen() {
        //update bot names

        //update botInfo
        infoBox.setText(botFinder.getInfo(buttonArray[currentBotIndex]));
    }

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