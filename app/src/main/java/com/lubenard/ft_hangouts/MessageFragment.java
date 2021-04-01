package com.lubenard.ft_hangouts;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.lubenard.ft_hangouts.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static android.app.Activity.RESULT_OK;

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

        dbManager = new DbManager(getContext());
        this.view = view;

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        dataModels = new ArrayList<>();

        listView = view.findViewById(R.id.message_list);

        toolbar = view.findViewById(R.id.message_toolbar);

        sendMessage = view.findViewById(R.id.sendMessageImageButton);

        /*if (contactId == -1)
            toolbar.setTitle(R.string.app_create_contact_name);
        else {
            toolbar.setTitle(R.string.app_edit_contact_name);
            ArrayList<String> contactDetails = dbManager.getContactDetail(contactId);
        }*/

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get back to last fragment in the stack
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

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
                long insertedMessageId = dbManager.saveNewMessage(contactId, messageContent.getText().toString(), "TO");

                // We do not update all messages. If there is too much messages, that could take a while
                dataModels.add(new MessageModel(insertedMessageId, contactId, messageContent.getText().toString(), "TO"));
                adapter.notifyDataSetChanged();
                messageContent.setText("");
                //updateMessageList();
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
