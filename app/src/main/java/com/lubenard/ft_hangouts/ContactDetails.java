package com.lubenard.ft_hangouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ContactDetails extends Fragment {

    private int contactId = -1;
    private DbManager dbManager;
    private View view;
    ArrayList<String> contactDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.contact_page_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbManager = new DbManager(getContext());
        this.view = view;

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button callContact = view.findViewById(R.id.details_call);
        callContact.setOnClickListener(view13 -> {
            if (contactDetails.get(1) != null && !contactDetails.get(1).isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contactDetails.get(1)));
                getContext().startActivity(intent);
            } else {
                Toast.makeText(getContext(), R.string.impossible_call_no_phone_number, Toast.LENGTH_LONG).show();
            }
        });

        Button messageContact = view.findViewById(R.id.details_message);
        messageContact.setOnClickListener(view12 -> {
            MessageFragment fragment = new MessageFragment();
            Bundle args = new Bundle();
            args.putInt("contactId", contactId);
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment, null)
                    .addToBackStack(null).commit();
        });

        ImageButton favButton = view.findViewById(R.id.image_button_fav_contact);

        favButton.setOnClickListener(view1 -> {
            if (contactDetails.get(6).equals("1")) {
                favButton.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                contactDetails.set(6, "0");

            } else {
                favButton.setBackgroundResource(R.drawable.baseline_favorite_24);
                contactDetails.set(6, "1");
            }
            dbManager.updateContactIsFavourite(contactId, contactDetails.get(6));
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_details, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (contactId > 0) {
            contactDetails = dbManager.getContactDetail(contactId);
            TextView name = view.findViewById(R.id.contact_detail_name);
            TextView phoneNumber = view.findViewById(R.id.contact_detail_phone_number);
            TextView email = view.findViewById(R.id.contact_detail_email);
            TextView address = view.findViewById(R.id.contact_detail_address);
            TextView birthday = view.findViewById(R.id.contact_detail_birthday);
            ImageView icon = view.findViewById(R.id.contact_detail_icon);

            getActivity().setTitle("");

            name.setText(contactDetails.get(0));
            phoneNumber.setText(contactDetails.get(1));
            email.setText(contactDetails.get(2));
            address.setText(contactDetails.get(3));
            birthday.setText(contactDetails.get(4));

            if (contactDetails.get(5) != null)
                icon.setImageDrawable(Drawable.createFromPath(contactDetails.get(5)));
            else
                icon.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.ic_menu_help));

            ImageButton favButton = view.findViewById(R.id.image_button_fav_contact);

             if (contactDetails.get(6).equals("1")) {
                favButton.setBackgroundResource(R.drawable.baseline_favorite_24);
            } else {
                favButton.setBackgroundResource(R.drawable.baseline_favorite_border_24);
            }
        }
        else {
            // trigger error, show toast and exit
            Toast.makeText(getContext(), R.string.error_bad_id, Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }
}
