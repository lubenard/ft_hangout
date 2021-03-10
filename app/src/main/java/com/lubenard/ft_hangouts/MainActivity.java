package com.lubenard.ft_hangouts;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ContactModel> dataModels;
    private DbManager dbManager;
    private CustomListAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewContact();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        listView = findViewById(R.id.main_list);

        dataModels = new ArrayList<>();

        dbManager = new DbManager(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactModel dataModel= dataModels.get(i);
                Log.d("ONCLICK", "Element " + dataModel.getName());
                Intent intent = new Intent(getApplicationContext(), ContactDetails.class);
                intent.putExtra("contactId", dataModel.getId());
                startActivity(intent);
            }

        });
    }

    private void updateContactList() {
        LinkedHashMap<Integer, ContactModel> contactsdatas = dbManager.getAllContactsForMainList();
        for (LinkedHashMap.Entry<Integer, ContactModel> oneElemDatas : contactsdatas.entrySet()) {
            dataModels.add(oneElemDatas.getValue());
        }
        adapter = new CustomListAdapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);
    }

    // Start the EditContact activity, with no user datas to load
    private void createNewContact() {
        Intent intent = new Intent(this, EditContact.class);
        intent.putExtra("contactId", -1);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Launch settings page
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment(), null)
                    .addToBackStack(null).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataModels.clear();
        updateContactList();
    }
}