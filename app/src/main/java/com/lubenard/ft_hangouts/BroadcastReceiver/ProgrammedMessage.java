package com.lubenard.ft_hangouts.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.lubenard.ft_hangouts.DbManager;
import com.lubenard.ft_hangouts.R;
import com.lubenard.ft_hangouts.UI.MessageFragment;

public class ProgrammedMessage extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int contactId = intent.getIntExtra("contactId", -1);
        String receiverNum = intent.getStringExtra("receiverNum");
        String message = intent.getStringExtra("message");

        if (receiverNum != null && message != null && contactId != -1) {
            DbManager dbManager = new DbManager(context);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(receiverNum, null, message, null, null);
            Toast.makeText(context, R.string.toast_sms_sent, Toast.LENGTH_LONG).show();
            dbManager.saveNewMessage(contactId, message, "TO");
            if (MessageFragment.getIsUserOnMessageFragment()) {
                MessageFragment.updateMessageList(context);
            }
        }
    }
}
