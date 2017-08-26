package com.android.bear.datafree;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    //---DECLARE VARIABLES--------------------------------------------------------------------------

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
    String serverNumber = "";
    ArrayList<ContentPackage> incomingPackages = new ArrayList<ContentPackage>();

    //---- User Input -----
    EditText input;
    String toServer = "";   //final String that gets texted to the server
    String botKey = "aa";   //first 2 chars of toServer to identify which bot requested

    //---- Buttons and User UI -----
    Button sendButton;
    final int numberOfButtons = 5;
    Button[] botButtons;
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
    VerifiedNumbersSingleton verifiedNumbers = VerifiedNumbersSingleton.getInstance();


    //---ON START-----------------------------------------------------------------------------------

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
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
        buttonArray = new String[numberOfButtons];    //length = number of bots
        buttonArray[0] = "suggestions";
        buttonArray[1] = "wikipedia";
        buttonArray[2] = "urban_dic";

        //set up buttons
        sendButton = (Button) findViewById(R.id.send);
        botButtons = createBotButtons(numberOfButtons);
        infoBox = (TextView) findViewById(R.id.infoBox);
        messageDisplay = (TextView) findViewById(R.id.messageDisplay);

        //---

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateScreen();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

        });

        //---- Memory ----
        SharedPreferences memory = PreferenceManager.getDefaultSharedPreferences(this);

        // set up server Number
        serverNumber = memory.getString("serverNumber", "");
        verifiedNumbers.setServerNumber(serverNumber, this);


        updateButtonColors(botButtons[0]);
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

    //---MENU BUTTONS-------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_number:
                createPopUp(this, 0, "New Server Number\ncurrent: " + serverNumber, "5551119999");
                return true;
        }
        return false;
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


        // update send button color
        if (input.getText().toString().length() > 0) {
            sendButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        } else {
            sendButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.darkGray), PorterDuff.Mode.MULTIPLY);

        }
    }



    //---CONCERNING MESSAGES------------------------------------------------------------------------

    //Sends SMS Declaration Request to Data Free Server
    public void onSendClick(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {

            // check if there is anything to send
            if (input.getText().toString().length() < 1) {
                return;
            }

            // SEND SMS TO SERVER

            // only try to send if serverNumber is a real number
            if(serverNumber.length() == 12) {
                // 1) create packageSlot in a non active package index
                int packageSlot;

                // 2) find first non-active package
                boolean found = false;
                int nonActiveIndex = -1;
                for (int i = nonActiveIndex + 1; i < incomingPackages.size() && !found; i++) {
                    if (!incomingPackages.get(i).active) {
                        found = true;
                        nonActiveIndex = i;
                    }
                }

                // 3) set packageSlot to empty/non-active spot
                if (found) {
                    packageSlot = nonActiveIndex;
                } else {
                    packageSlot = incomingPackages.size();
                }

                // 4) determine bot case from bot
                String botCase = "c"; // CHANGE THIS

                // 5) format text: { bk r p ...
                toServer = "{" + botKey + botCase + packageSlot + input.getText().toString();

                smsManager.sendTextMessage(serverNumber, null, toServer, null, null);
                Toast.makeText(this, "Request Declaration sent!", Toast.LENGTH_SHORT).show();

                //clear text in Edit text
                input.setText("");
            } else {
                toast("Please input a server number!");
            }
        }
    }

    //send Content Request to Data Free Server
    public void sendContentRequestText(String botKey, String botCase, String packageSlot, String content) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            // SEND SMS TO SERVER

            // format: bk + r + i + ...
            toServer = botKey + botCase + packageSlot + content;
            smsManager.sendTextMessage(serverNumber, null, toServer, null, null);
            Toast.makeText(this, "Content Request sent!", Toast.LENGTH_SHORT).show();
        }

    }

    // gets called when a new message comes in
    // puts message in correct package, or creates new package from packageSlot
    public void refreshSmsInbox(String smsMessage) {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

        //check whether incoming sms is a header, content
        if(smsMessage.substring(0,1).contains("{")) {

            // declaration text = { + bk + r + p + si + ...
            //                    0   12   3   4   56
            // { + botkey + botCase + packageSlot + size
            String bK = smsMessage.substring(1,3);
            String kS = smsMessage.substring(5,7);
            String request = smsMessage.substring(7);

            String botCase = smsMessage.substring(3,4);
            String packageSlot = smsMessage.substring(4,5);
            String content = smsMessage.substring(7);


            // create new package in correct spot
            if(incomingPackages.size() <= Integer.parseInt(packageSlot)) {
                incomingPackages.add(new ContentPackage(bK, kS, request));
            } else {
                incomingPackages.set(Integer.parseInt(packageSlot), new ContentPackage(bK, kS, request));
            }

            // send confirmation text
            sendContentRequestText(bK, botCase, packageSlot, content);

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
            int packageSlot = Integer.parseInt(smsMessage.substring(0,1));
            smsMessage += " "; // add back the space that twilio formatting deletes
            incomingPackages.get(packageSlot).addMessage(smsMessage);
            //---------


            // display how many received/expected messages
            messageDisplay.setText(arrayHandler.findPercentFull(incomingPackages.get(packageSlot).contents));

            // if the entire package has been received, display it
            if(arrayHandler.checkFull(incomingPackages.get(packageSlot).contents)) {

                String fullAnswer = ArrayHandler.createString(incomingPackages.get(packageSlot).contents);

                fullAnswer = huffDecoder.decode(fullAnswer, wordList);
                fullAnswer = fixCapitals.fixCapitalization(fullAnswer);
                messageDisplay.setText(fullAnswer);

                // set package to be complete
                incomingPackages.get(packageSlot).complete();
            }


        }
    }

    //---BOT BUTTONS--------------------------------------------------------------------------------


    // createBotButtons
    public Button[] createBotButtons(int numberOfButtons) {

        Button[] botButtons = new Button[numberOfButtons];

        // loop through and create buttons
        for (int i = 0; i < numberOfButtons; i++) {

            // create Button
            final Button newButton = new Button(this);
            newButton.setText(botFinder.getName(buttonArray[i]));
            newButton.setTag(i);

            // assign button to proper view
            LinearLayout ll = (LinearLayout) findViewById(R.id.bot_button_layout);
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
            ll.addView(newButton, lp);


            // add on click functionality
            newButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    // select correct bot
                    int tag = Integer.parseInt(v.getTag().toString());
                    currentBotIndex = tag;
                    botKey = botFinder.getKey(buttonArray[currentBotIndex]);

                    // assign button colors
                    updateButtonColors(newButton);
                }
            });


            // add button to botButtons
            botButtons[i] = newButton;
        }
        return botButtons;
    }

    //
    public void updateButtonColors(Button b) {
        deselectButtonColors(botButtons);
        b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        b.setTextColor(ContextCompat.getColor(this, R.color.white));
        updateScreen();
    }


    // deselect button colors
    public void deselectButtonColors(Button[] buttons) {

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].getBackground().setColorFilter(ContextCompat.getColor(this, R.color.lightGray), PorterDuff.Mode.MULTIPLY);
            buttons[i].setTextColor(ContextCompat.getColor(this, R.color.darkGray));

        }
    }

    //---Memory-------------------------------------------------------------------------------------

    // saves a value to memory at label
    public void saveToMemory(String label, String value) {
        SharedPreferences memory = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editMemory = memory.edit();
        editMemory.putString(label, value).apply();
    }

    // creates a pop up, commits answer to memory
    public void createPopUp(final Context context, int action, String message, String hint) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        //final TextView prompt = new TextView(context);
        //prompt.setText(message);
        final EditText et = new EditText(context);
        et.setHint(hint);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);
        alertDialogBuilder.setTitle(message);
        // set dialog message

        if(action == 0) {
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // check if number is valid
                    if(("+1" + et.getText().toString()).length() == 12) {
                        serverNumber = "+1" + et.getText().toString();
                        verifiedNumbers.setServerNumber(serverNumber, context);
                        saveToMemory("serverNumber", serverNumber);
                        toast(verifiedNumbers.getNumber());
                    } else {
                        toast("Number not added");
                    }

                }
            });
        }


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }


    //---UTILITIES----------------------------------------------------------------------------------

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}