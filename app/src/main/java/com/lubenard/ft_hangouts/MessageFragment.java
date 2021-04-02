package com.lubenard.ft_hangouts;

import android.Manifest;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MessageFragment extends Fragment {

    private int contactId;
    private DbManager dbManager;
    private View view;
    private Toolbar toolbar;

    private ArrayList<MessageModel> dataModels;
    private CustomMessageListAdapter adapter;
    private ListView listView;
    private ImageButton sendMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText messageContent = view.findViewById(R.id.editTextMessageContent);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager = new DbManager(getContext());
        this.view = view;

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        dataModels = new ArrayList<>();

        listView = view.findViewById(R.id.message_list);

        sendMessage = view.findViewById(R.id.sendMessageImageButton);

        if (contactId == -1)
            toolbar.setTitle(R.string.message_new_contact_title);
        else {
            ArrayList<String> contactDetails = dbManager.getContactDetail(contactId);
            getActivity().setTitle(contactDetails.get(0));
        }

        /*toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_call:

                    return true;
                default:
                    return false;
            }
        });*/

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.checkOrRequestPerm(getActivity(), getContext(), Manifest.permission.SEND_SMS, () -> {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("(555) 521-5556", null, messageContent.getText().toString(), null, null);
                    Toast.makeText(getContext(), R.string.toast_sms_sent, Toast.LENGTH_LONG).show();
                    long insertedMessageId = dbManager.saveNewMessage(contactId, messageContent.getText().toString(), "TO");

                    // We do not update all messages. If there is too much messages, that could take a while
                    //dataModels.add(new MessageModel(insertedMessageId, contactId, messageContent.getText().toString(), "TO"));
                    //adapter.notifyDataSetChanged();
                    messageContent.setText("");
                    updateMessageList();
                    return null;
                }, () -> {
                    Toast.makeText(getContext(), getContext().getString(R.string.no_access_to_send_sms), Toast.LENGTH_SHORT).show();
                    return null;
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMessageList();
    }

    private void updateMessageList() {
        dataModels.clear();
        LinkedHashMap<Integer, MessageModel> contactsdatas = dbManager.getAllMessageFromContactId(contactId);
        for (LinkedHashMap.Entry<Integer, MessageModel> oneElemDatas : contactsdatas.entrySet()) {
            dataModels.add(oneElemDatas.getValue());
        }
        adapter = new CustomMessageListAdapter(dataModels, getContext());
        listView.setAdapter(adapter);
    }
}
