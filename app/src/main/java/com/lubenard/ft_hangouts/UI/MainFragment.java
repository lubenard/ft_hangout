package com.lubenard.ft_hangouts.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.lubenard.ft_hangouts.Custom_Objects.ContactModel;
import com.lubenard.ft_hangouts.Custom_Objects.CustomListAdapter;
import com.lubenard.ft_hangouts.DbManager;
import com.lubenard.ft_hangouts.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainFragment extends Fragment {

    private ArrayList<ContactModel> dataModels;
    private DbManager dbManager;
    private CustomListAdapter adapter;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private Context context;
    private FragmentManager fragmentManager;
    private int sorting_value;

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

        listView = view.findViewById(R.id.main_list);

        dataModels = new ArrayList<>();

        context = getContext();

        dbManager = new DbManager(context);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        fragmentManager = getActivity().getSupportFragmentManager();

        ImageButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view13 -> createNewContact());

        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            ContactModel dataModel = dataModels.get(i);
            ContactDetails fragment = new ContactDetails();
            Bundle args = new Bundle();
            args.putInt("contactId", dataModel.getId());
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment, null)
                    .addToBackStack(null).commit();
        });

        listView.setOnItemLongClickListener((adapterView, view12, i, l) -> {
            ContactModel dataModel = dataModels.get(i);
            new AlertDialog.Builder(context).setTitle(R.string.alertdialog_delete_contact_title)
                    .setMessage(R.string.alertdialog_delete_contact_body)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        dbManager.deleteContact(dataModel.getId());
                        fragmentManager.popBackStackImmediate();
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
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment(), null)
                        .addToBackStack(null).commit();
                return true;
            case R.id.action_filter:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.filter_by);
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_filter_alertdialog, null);
                builder.setView(customLayout);
                CheckBox system_contacts = customLayout.findViewById(R.id.show_system_contacts);
                CheckBox internal_contacts = customLayout.findViewById(R.id.show_internal_contacts);

                system_contacts.setChecked(sharedPreferences.getBoolean("sort_filter_system_contacts", true));
                internal_contacts.setChecked(sharedPreferences.getBoolean("sort_filter_internal_contacts", true));

                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    sorting_value = 0;
                    if (internal_contacts.isChecked())
                        sorting_value += 1;
                    if (system_contacts.isChecked())
                        sorting_value += 2;
                    sharedPreferences.edit().putInt("sort_filter_value", sorting_value).apply();
                    sharedPreferences.edit().putBoolean("sort_filter_system_contacts", system_contacts.isChecked()).apply();
                    sharedPreferences.edit().putBoolean("sort_filter_internal_contacts", internal_contacts.isChecked()).apply();
                    updateContactList();
                });
                builder.setNegativeButton(android.R.string.cancel,null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the contact list
     */
    private void updateContactList() {
        dataModels.clear();
        LinkedHashMap<Integer, ContactModel> contactsdatas = dbManager.getAllContactsForMainList(sorting_value);
        for (LinkedHashMap.Entry<Integer, ContactModel> oneElemDatas : contactsdatas.entrySet()) {
            dataModels.add(oneElemDatas.getValue());
        }
        adapter = new CustomListAdapter(dataModels, context, getActivity());
        listView.setAdapter(adapter);
    }

    /**
     * Start the EditContact activity, with no user datas to load
     */
    private void createNewContact() {
        EditContact fragment = new EditContact();
        Bundle args = new Bundle();
        args.putInt("contactId", -1);
        fragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment, null)
                .addToBackStack(null).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContactList();
    }
}