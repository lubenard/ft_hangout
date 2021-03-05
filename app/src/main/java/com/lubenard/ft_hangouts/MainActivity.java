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

public class MainActivity extends AppCompatActivity {

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

        ArrayList<ContactModel> dataModels;
        ListView listView;
        CustomListAdapter adapter;

        listView = findViewById(R.id.main_list);

        dataModels = new ArrayList<>();

        dataModels.add(new ContactModel("Toto", "Android 1.0", null));
        dataModels.add(new ContactModel("Luc", "Android 1.1", null));
        dataModels.add(new ContactModel("Cupcake", "Android 1.5", null));
        dataModels.add(new ContactModel("My best friend","Android 1.6",null));
        dataModels.add(new ContactModel("Carl", "Android 2.0", null));
        dataModels.add(new ContactModel("David", "Android 2.2", null));
        dataModels.add(new ContactModel("Amber", "Android 2.3",null));
        dataModels.add(new ContactModel("Clara","Android 3.0", null));
        dataModels.add(new ContactModel("Jeremy", "Android 4.0", null));
        dataModels.add(new ContactModel("Jennifer", "Android 4.2", null));
        dataModels.add(new ContactModel("Karl", "Android 4.4", null));
        dataModels.add(new ContactModel("Boss","Android 5.0",null));
        dataModels.add(new ContactModel("Marie", "Android 6.0", null));

        adapter = new CustomListAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactModel dataModel= dataModels.get(i);
                Log.d("ONCLICK", "Element " + dataModel.getName());
            }

        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}