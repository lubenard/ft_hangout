package com.lubenard.ft_hangouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class EditContact extends Fragment {

    private int contactId;
    private DbManager dbManager;
    private View view;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.edit_contact_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbManager = new DbManager(getContext());
        this.view = view;

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        toolbar = view.findViewById(R.id.edit_contact_toolbar);

        if (contactId == -1)
            toolbar.setTitle(R.string.app_create_contact_name);
        else {
            toolbar.setTitle(R.string.app_edit_contact_name);
            ArrayList<String> contactDetails = dbManager.getContactDetail(contactId);
            // Load datas into the fields
            ((EditText)view.findViewById(R.id.editTextTextPersonName)).setText(contactDetails.get(0));
            ((EditText)view.findViewById(R.id.editTextPhone)).setText(contactDetails.get(1));
            ((EditText)view.findViewById(R.id.editTextTextEmailAddress)).setText(contactDetails.get(2));
            ((EditText)view.findViewById(R.id.editTextTextPostalAddress)).setText(contactDetails.get(3));
            ((EditText)view.findViewById(R.id.editTextBirthday)).setText(contactDetails.get(4));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get back to last fragment in the stack
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_validate:
                    // TODO: see if with API we can insert contact into system contacts
                    // For now, just register into own contacts
                    String name = ((EditText)view.findViewById(R.id.editTextTextPersonName)).getText().toString();
                    String phoneNumber = ((EditText)view.findViewById(R.id.editTextPhone)).getText().toString();
                    String email = ((EditText)view.findViewById(R.id.editTextTextEmailAddress)).getText().toString();
                    String address = ((EditText)view.findViewById(R.id.editTextTextPostalAddress)).getText().toString();
                    String birthday = ((EditText)view.findViewById(R.id.editTextBirthday)).getText().toString();

                    if (contactId == -1)
                        dbManager.createNewContact(name, phoneNumber, email, address, birthday);
                    else
                        dbManager.updateContact(contactId, name, phoneNumber, email, address, birthday);

                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                    return true;
                default:
                    return false;
            }
        });
    }
}
