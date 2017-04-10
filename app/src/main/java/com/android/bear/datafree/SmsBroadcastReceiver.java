package com.android.bear.datafree;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by bear on 3/28/17.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    public VerifiedNumbers checkNumbers = new VerifiedNumbers();

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        //Toast.makeText(context, "Message Received!", Toast.LENGTH_SHORT).show();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            String address = "";
            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                String smsBody = smsMessage.getMessageBody();

                //Use Address to check whether or not its a twilio number
                address = smsMessage.getOriginatingAddress();

                //smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody;
            }

            //only read the sms if it's a verified Data Free number
            if(checkNumbers.isValid(address)) {
                MainActivity inst = MainActivity.instance();
                inst.updateInbox(smsMessageStr);
            }
        }
    }
}