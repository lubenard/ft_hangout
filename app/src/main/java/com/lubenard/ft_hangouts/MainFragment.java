package com.lubenard.ft_hangouts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainFragment extends Fragment {

    private ArrayList<ContactModel> dataModels;
    private DbManager dbManager;
    private CustomListAdapter adapter;
    private ListView listView;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.app_title);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewContact();
            }
        });

        listView = view.findViewById(R.id.main_list);

        dataModels = new ArrayList<>();

        dbManager = new DbManager(getContext());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            ContactModel dataModel = dataModels.get(i);
            Log.d("ONCLICK", "Element " + dataModel.getId());
            ContactDetails fragment = new ContactDetails();
            Bundle args = new Bundle();
            args.putInt("contactId", dataModel.getId());
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment, null)
                    .addToBackStack(null).commit();
        });

        listView.setOnItemLongClickListener((adapterView, view12, i, l) -> {
            ContactModel dataModel = dataModels.get(i);
            new AlertDialog.Builder(getContext()).setTitle(R.string.alertdialog_delete_contact_title)
                    .setMessage(R.string.alertdialog_delete_contact_body)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbManager.deleteContact(dataModel.getId());
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
            return false;
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment(), null)
                        .addToBackStack(null).commit();
                return true;
            case R.id.action_filter:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Filter by");
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_filter_alertdialog, null);
                builder.setView(customLayout);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.edit().putBoolean("sort_filter_system_contacts", ((CheckBox)customLayout.findViewById(R.id.show_system_contacts)).isChecked()).apply();
                        sharedPreferences.edit().putBoolean("sort_filter_internal_contacts", ((CheckBox)customLayout.findViewById(R.id.show_internal_contacts)).isChecked()).apply();
                        sharedPreferences.edit().putBoolean("sort_filter_fav_contacts", ((CheckBox)customLayout.findViewById(R.id.show_fav_contacts)).isChecked()).apply();
                        updateContactList();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel,null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateContactList() {
        dataModels.clear();
        LinkedHashMap<Integer, ContactModel> contactsdatas = dbManager.getAllContactsForMainList(sharedPreferences.getBoolean("sort_filter_internal_contacts", true),
                sharedPreferences.getBoolean("sort_filter_system_contacts",
                sharedPreferences.getBoolean("sort_filter_fav_contacts", true)), true);
        for (LinkedHashMap.Entry<Integer, ContactModel> oneElemDatas : contactsdatas.entrySet()) {
            dataModels.add(oneElemDatas.getValue());
        }
        adapter = new CustomListAdapter(dataModels, getContext(), getActivity());
        listView.setAdapter(adapter);
    }

    // Start the EditContact activity, with no user datas to load
    private void createNewContact() {
        EditContact fragment = new EditContact();
        Bundle args = new Bundle();
        args.putInt("contactId", -1);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment, null)
                .addToBackStack(null).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContactList();
    }
}