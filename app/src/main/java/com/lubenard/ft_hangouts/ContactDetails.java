package com.lubenard.ft_hangouts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lubenard.ft_hangouts.Utils.Utils;

import java.util.ArrayList;

public class ContactDetails extends Fragment {

    private int contactId = -1;
    private DbManager dbManager;
    private ArrayList<String> contactDetails;
    private Context context;
    private Activity activity;
    private FragmentManager fragmentManager;
    private TextView name;
    private TextView phoneNumber;
    private TextView email;
    private TextView address;
    private TextView birthday;
    private ImageView icon;
    private ImageButton favButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.contact_page_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        activity = getActivity();
        fragmentManager = getActivity().getSupportFragmentManager();

        dbManager = new DbManager(context);

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        activity.setTitle("");

        ((AppCompatActivity)activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = view.findViewById(R.id.contact_detail_name);
        phoneNumber = view.findViewById(R.id.contact_detail_phone_number);
        email = view.findViewById(R.id.contact_detail_email);
        address = view.findViewById(R.id.contact_detail_address);
        birthday = view.findViewById(R.id.contact_detail_birthday);
        icon = view.findViewById(R.id.contact_detail_icon);
        favButton = view.findViewById(R.id.image_button_fav_contact);

        Button callContact = view.findViewById(R.id.details_call);
        Button messageContact = view.findViewById(R.id.details_message);

        callContact.setOnClickListener(view13 -> {
            if (Utils.checkExistantPhoneNumnber(contactDetails.get(1))) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contactDetails.get(1)));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, R.string.impossible_call_no_phone_number, Toast.LENGTH_LONG).show();
            }
        });

        messageContact.setOnClickListener(view12 -> {
            if (Utils.checkExistantPhoneNumnber(contactDetails.get(1))) {
                MessageFragment fragment = new MessageFragment();
                Bundle args = new Bundle();
                args.putInt("contactId", contactId);
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, fragment, null)
                        .addToBackStack(null).commit();
            } else
                Toast.makeText(context, R.string.impossible_message_no_phone_number, Toast.LENGTH_LONG).show();
        });

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
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, fragment, null)
                        .addToBackStack(null).commit();
                return true;
            case R.id.action_delete_contact:
                new AlertDialog.Builder(context).setTitle(R.string.alertdialog_delete_contact_title)
                        .setMessage(R.string.alertdialog_delete_contact_body)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            dbManager.deleteContact(contactId);
                            fragmentManager.popBackStackImmediate();
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

            name.setText(contactDetails.get(0));
            phoneNumber.setText(contactDetails.get(1));
            email.setText(contactDetails.get(2));
            address.setText(contactDetails.get(3));
            birthday.setText(contactDetails.get(4));

            if (contactDetails.get(5) != null)
                icon.setImageDrawable(Drawable.createFromPath(contactDetails.get(5)));
            else
                icon.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_help));

            favButton.setBackgroundResource(contactDetails.get(6).equals("1") ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
        }
        else {
            // Trigger error, show toast and exit
            Toast.makeText(context, R.string.error_bad_id, Toast.LENGTH_SHORT).show();
            fragmentManager.popBackStackImmediate();
        }
    }
}
