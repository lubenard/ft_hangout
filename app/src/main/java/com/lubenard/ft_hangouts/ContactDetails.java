package com.lubenard.ft_hangouts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ContactDetails extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);

        DbManager dbManager = new DbManager(this);

        Intent intent = getIntent();
        int contactId = intent.getIntExtra("contactId", -1);

        if (contactId > 0) {
            ArrayList<String> contactDetails = dbManager.getContactDetail(contactId);
            TextView name = findViewById(R.id.contact_detail_name);
            TextView phoneNumber = findViewById(R.id.contact_detail_phone_number);
            TextView email = findViewById(R.id.contact_detail_email);
            TextView address = findViewById(R.id.contact_detail_address);
            TextView birthday = findViewById(R.id.contact_detail_birthday);

            name.setText(contactDetails.get(0));
            phoneNumber.setText(contactDetails.get(1));
            email.setText(contactDetails.get(2));
            address.setText(contactDetails.get(3));
            birthday.setText(contactDetails.get(4));
        }
        else {
            // trigger error, show toast and exit
            finish();
        }
    }
}
