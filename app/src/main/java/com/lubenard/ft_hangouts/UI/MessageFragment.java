package com.lubenard.ft_hangouts.UI;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.lubenard.ft_hangouts.Custom_Objects.ContactModel;
import com.lubenard.ft_hangouts.Custom_Objects.CustomMessageListAdapter;
import com.lubenard.ft_hangouts.DbManager;
import com.lubenard.ft_hangouts.MainActivity;
import com.lubenard.ft_hangouts.Custom_Objects.MessageModel;
import com.lubenard.ft_hangouts.BroadcastReceiver.ProgrammedMessage;
import com.lubenard.ft_hangouts.R;
import com.lubenard.ft_hangouts.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class MessageFragment extends Fragment {

    private static int contactId;
    private static DbManager dbManager;

    private static ArrayList<MessageModel> dataModels;
    private static CustomMessageListAdapter adapter;
    private static ListView listView;
    private ContactModel contactDetails;
    private boolean hasAuthToSensSms = false;

    private static Boolean isUserOnMessageFragment = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        dbManager = new DbManager(getContext());

        if (contactId == -1)
            getActivity().setTitle(R.string.message_new_contact_title);
        else {
            contactDetails = dbManager.getContactDetail(contactId);
            getActivity().setTitle(contactDetails.getName());
        }

        EditText messageContent = view.findViewById(R.id.editTextMessageContent);
        ImageButton sendMessage = view.findViewById(R.id.sendMessageImageButton);
        listView = view.findViewById(R.id.message_list);

        dataModels = new ArrayList<>();

        sendMessage.setOnClickListener(view12 -> {
            if (!messageContent.getText().equals("")) {
                if (hasAuthToSensSms) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contactDetails.getPhoneNumber(), null, messageContent.getText().toString(), null, null);
                    Toast.makeText(getContext(), R.string.toast_sms_sent, Toast.LENGTH_SHORT).show();
                    dbManager.saveNewMessage(contactId, messageContent.getText().toString(), "TO");
                    messageContent.setText("");
                    updateMessageList(getContext());
                } else
                    Toast.makeText(getContext(), getContext().getString(R.string.no_access_to_send_sms), Toast.LENGTH_SHORT).show();
            }
        });

        sendMessage.setOnLongClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.send_programmed_message);
            final View customLayout = getLayoutInflater().inflate(R.layout.programmed_message_time_picker, null);
            final TimePicker timePicker = customLayout.findViewById(R.id.daily_reminder_timePicker);
            timePicker.setIs24HourView(true);
            builder.setView(customLayout);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                int hour, minute;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }
                else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                Intent intent = new Intent(getContext(), ProgrammedMessage.class);
                intent.putExtra("contactId", contactId);
                intent.putExtra("receiverNum", contactDetails.getPhoneNumber());
                intent.putExtra("message", messageContent.getText().toString());
                messageContent.setText("");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);
                AlarmManager am = (AlarmManager)getContext().getSystemService(Activity.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(getContext(), getString(R.string.message_delivered_at) + hour + "h" + minute + "mn", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton(android.R.string.cancel,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });
    }

    public static boolean getIsUserOnMessageFragment() {
        return isUserOnMessageFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        isUserOnMessageFragment = true;
        MainActivity.checkOrRequestPerm(getActivity(), getContext(), Manifest.permission.SEND_SMS, () -> {
            hasAuthToSensSms = true;
            return true;
        }, () -> {
            hasAuthToSensSms = false;
            Toast.makeText(getContext(), getContext().getString(R.string.no_access_to_send_sms), Toast.LENGTH_SHORT).show();
            return null;
        });
        updateMessageList(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        isUserOnMessageFragment = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_message_contact, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_call_contact:
                if (Utils.checkExistantPhoneNumnber(contactDetails.getPhoneNumber())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contactDetails.getPhoneNumber()));
                    getContext().startActivity(intent);
                } else
                    Toast.makeText(getContext(), R.string.impossible_call_no_phone_number, Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the message list
     * @param context
     */
    public static void updateMessageList(Context context) {
        dataModels.clear();
        LinkedHashMap<Integer, MessageModel> contactsdatas = dbManager.getAllMessageFromContactId(contactId);
        for (LinkedHashMap.Entry<Integer, MessageModel> oneElemDatas : contactsdatas.entrySet()) {
            dataModels.add(oneElemDatas.getValue());
        }
        adapter = new CustomMessageListAdapter(dataModels, context);
        listView.setAdapter(adapter);
    }
}
