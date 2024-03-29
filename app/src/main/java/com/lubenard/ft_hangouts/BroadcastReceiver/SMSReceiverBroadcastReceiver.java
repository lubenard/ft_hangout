package com.lubenard.ft_hangouts.BroadcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.lubenard.ft_hangouts.DbManager;
import com.lubenard.ft_hangouts.R;
import com.lubenard.ft_hangouts.UI.MessageFragment;

public class SMSReceiverBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Bundle myBundle = intent.getExtras();
        SmsMessage [] messages;

        if (myBundle != null) {
            Object[] pdus = (Object[]) myBundle.get("pdus");
            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                }
                else
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                DbManager dbManager = new DbManager(context);

                String contactName = dbManager.getContactNameFromPhoneNumber(messages[i].getOriginatingAddress());
                if (preferences.getBoolean("tweaks_create_new_contact_when_unknown_number", false) && contactName == null) {
                    dbManager.createNewContact(messages[i].getOriginatingAddress(), messages[i].getOriginatingAddress(), null, null, null, null, "INTERNAL");
                }

                dbManager.saveNewMessage(dbManager.getContactIdFromPhoneNumber(messages[i].getOriginatingAddress()), messages[i].getMessageBody(),"FROM");
                sendNotification(context, (contactName == null) ? messages[i].getOriginatingAddress() : contactName, messages[i].getMessageBody(), R.drawable.baseline_chat_black_48);
                if (MessageFragment.getIsUserOnMessageFragment()) {
                    MessageFragment.updateMessageList(context);
                }
            }
        }
    }

    /**
     * Send a notification when user receive a message
     * @param context
     * @param title Title of the notification
     * @param content body of the notification
     * @param drawable icon of the notification
     */
    public static void sendNotification(Context context, String title, String content, int drawable) {
        // Get the notification manager and build it
        PendingIntent pi = PendingIntent.getActivity(context, 1, new Intent(context, MessageFragment.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder permNotifBuilder = new NotificationCompat.Builder(context, "NORMAL_CHANNEL");
        permNotifBuilder.setSmallIcon(drawable)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pi);
        mNotificationManager.notify(0, permNotifBuilder.build());
    }
}
