package com.lubenard.ft_hangouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ContactDetails extends Fragment {

    private int contactId = -1;
    private DbManager dbManager;
    private View view;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.contact_page_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbManager = new DbManager(getContext());
        this.view = view;

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        toolbar = view.findViewById(R.id.details_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get back to last fragment in the stack
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_edit_contact:
                    EditContact fragment = new EditContact();
                    Bundle args = new Bundle();
                    args.putInt("contactId", contactId);
                    fragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(android.R.id.content, fragment, null)
                            .addToBackStack(null).commit();
                    return true;
                case R.id.action_delete_contact:
                    new AlertDialog.Builder(getContext()).setTitle(R.string.alertdialog_delete_contact_title)
                            .setMessage(R.string.alertdialog_delete_contact_body)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dbManager.deleteContact(contactId);
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert).show();
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (contactId > 0) {
            ArrayList<String> contactDetails = dbManager.getContactDetail(contactId);
            TextView name = view.findViewById(R.id.contact_detail_name);
            TextView phoneNumber = view.findViewById(R.id.contact_detail_phone_number);
            TextView email = view.findViewById(R.id.contact_detail_email);
            TextView address = view.findViewById(R.id.contact_detail_address);
            TextView birthday = view.findViewById(R.id.contact_detail_birthday);

            // If the contact has a name, show it as Activity title.
            // Else, show the phoneNumber
            if (contactDetails.get(0) != null)
               toolbar.setTitle(contactDetails.get(0));
            else
                toolbar.setTitle(contactDetails.get(1));

            name.setText(contactDetails.get(0));
            phoneNumber.setText(contactDetails.get(1));
            email.setText(contactDetails.get(2));
            address.setText(contactDetails.get(3));
            birthday.setText(contactDetails.get(4));
        }
        else {
            // trigger error, show toast and exit
            Toast.makeText(getContext(), R.string.error_bad_id, Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }
}
