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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Variables
    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    // for sending messages
    EditText input;
    SmsManager smsManager = SmsManager.getDefault();

    //Buttons
    Button button0, button1, button2;
    //info box
    TextView infoBox;

    //content is the string that stores incoming text messages
    String content = "";
    //toServer is final string that gets sent to the server
    String toServer = "";
    // botKey is used as the first two digits in sms so server knows what bot to use
    String botKey = "aa";

    //Store the names of bots into buttonArray so BotFinder.java can use them
    String[] buttonArray;
    int currentBotIndex = 0;

    BotFinder botFinder = new BotFinder();

    static MainActivity inst;

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

        //set up button names
        button0.setText(botFinder.getName(buttonArray[0]));
        button1.setText(botFinder.getName(buttonArray[1]));
        button2.setText(botFinder.getName(buttonArray[2]));
    }

    public void refreshSmsInbox(String smsMessage) {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

        //Add message to content string
        content = content +" " + smsMessage;

        //display
        arrayAdapter.clear();
        arrayAdapter.add(content);

        //Adds rest of inbox
        /*
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
        */
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