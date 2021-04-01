package com.lubenard.ft_hangouts;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class SMSReceiverBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SMSReceiver", "Message received !");

        Bundle myBundle = intent.getExtras();
        SmsMessage [] messages = null;

        if (myBundle != null)
        {
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
                dbManager.saveNewMessage(dbManager.getContactIdFromPhoneNumber(messages[i].getOriginatingAddress()), messages[i].getMessageBody(),"FROM");
                String contactName = dbManager.getContactNameFromPhoneNumber(messages[i].getOriginatingAddress());
                sendNotificationWithQuickAnswer(context, (contactName == null) ? messages[i].getOriginatingAddress() : contactName, messages[i].getMessageBody(), R.drawable.baseline_chat_black_48);
            }
        }
    }

    public static void sendNotificationWithQuickAnswer(Context context, String title, String content, int drawable) {
        // Get the notification manager and build it
        PendingIntent pi = PendingIntent.getActivity(context, 1, new Intent(context, MessageFragment.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder permNotifBuilder = new NotificationCompat.Builder(context, "NORMAL_CHANNEL");
        permNotifBuilder.setSmallIcon(drawable)
                .setContentTitle(title)
                .setContentText(content)
                //.addAction(android.R.drawable.checkbox_on_background, context.getString(R.string.notif_choice_do_it), removedProtection)
                //.addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.notif_choice_dismiss), dismissedNotif)
                .setContentIntent(pi);
        mNotificationManager.notify(0, permNotifBuilder.build());
    }
}
