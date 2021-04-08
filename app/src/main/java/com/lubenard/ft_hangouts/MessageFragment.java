package com.lubenard.ft_hangouts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.lubenard.ft_hangouts.Utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MessageFragment extends Fragment {

    private static int contactId;
    private static DbManager dbManager;

    private static ArrayList<MessageModel> dataModels;
    private static CustomMessageListAdapter adapter;
    private static ListView listView;
    private ArrayList<String> contactDetails;

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
            getActivity().setTitle(contactDetails.get(0));
        }

        EditText messageContent = view.findViewById(R.id.editTextMessageContent);
        ImageButton sendMessage = view.findViewById(R.id.sendMessageImageButton);
        listView = view.findViewById(R.id.message_list);

        dataModels = new ArrayList<>();

        sendMessage.setOnClickListener(view1 -> MainActivity.checkOrRequestPerm(getActivity(), getContext(), Manifest.permission.SEND_SMS, () -> {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contactDetails.get(0), null, messageContent.getText().toString(), null, null);
            Toast.makeText(getContext(), R.string.toast_sms_sent, Toast.LENGTH_LONG).show();
            dbManager.saveNewMessage(contactId, messageContent.getText().toString(), "TO");
            messageContent.setText("");
            updateMessageList(getContext());
            return null;
        }, () -> {
            Toast.makeText(getContext(), getContext().getString(R.string.no_access_to_send_sms), Toast.LENGTH_SHORT).show();
            return null;
        }));
    }

    public static boolean getIsUserOnMessageFragment() {
        return isUserOnMessageFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        isUserOnMessageFragment = true;
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
                if (Utils.checkExistantPhoneNumnber(contactDetails.get(1))) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contactDetails.get(1)));
                    getContext().startActivity(intent);
                } else
                    Toast.makeText(getContext(), R.string.impossible_call_no_phone_number, Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
