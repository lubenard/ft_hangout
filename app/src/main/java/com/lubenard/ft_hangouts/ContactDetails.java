package com.lubenard.ft_hangouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ContactDetails extends AppCompatActivity {

    private int contactId = -1;
    private DbManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);

        dbManager = new DbManager(this);

        Intent intent = getIntent();
        contactId = intent.getIntExtra("contactId", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit_contact:
                Intent intent = new Intent(getApplicationContext(), EditContact.class);
                intent.putExtra("contactId", contactId);
                startActivity(intent);
                break;
            case R.id.action_delete_contact:
                new AlertDialog.Builder(this).setTitle(R.string.alertdialog_delete_contact_title)
                        .setMessage(R.string.alertdialog_delete_contact_body)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbManager.deleteContact(contactId);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
