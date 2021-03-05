package com.lubenard.ft_hangouts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditContact extends AppCompatActivity {

    int contactId;
    DbManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        dbManager = new DbManager(this);

        Intent intent = getIntent();
        contactId = intent.getIntExtra("contactId", -1);

        if (contactId == -1)
            setTitle(R.string.app_create_contact_name);
        else
            setTitle(R.string.app_edit_contact_name);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_validate) {
            // TODO: see if with API we can insert contact into system contacts
            // For now, just register into own contacts
            String name = ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString();
            String phoneNumber = ((EditText)findViewById(R.id.editTextPhone)).getText().toString();
            String email = ((EditText)findViewById(R.id.editTextTextEmailAddress)).getText().toString();
            String address = ((EditText)findViewById(R.id.editTextTextPostalAddress)).getText().toString();
            String birthday = ((EditText)findViewById(R.id.editTextBirthday)).getText().toString();

            if (contactId == -1)
                dbManager.createNewContact(name, phoneNumber, email, address, birthday);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
