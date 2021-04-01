package com.lubenard.ft_hangouts;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainFragment extends Fragment {

    private ArrayList<ContactModel> dataModels;
    private DbManager dbManager;
    private CustomListAdapter adapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.app_title);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactModel dataModel = dataModels.get(i);
                Log.d("ONCLICK", "Element " + dataModel.getId());
                ContactDetails fragment = new ContactDetails();
                Bundle args = new Bundle();
                args.putInt("contactId", dataModel.getId());
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, fragment, null)
                        .addToBackStack(null).commit();
            }
        });

        Toolbar toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    // Navigate to settings screen
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(android.R.id.content, new SettingsFragment(), null)
                            .addToBackStack(null).commit();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void updateContactList() {
        dataModels.clear();
        LinkedHashMap<Integer, ContactModel> contactsdatas = dbManager.getAllContactsForMainList();
        for (LinkedHashMap.Entry<Integer, ContactModel> oneElemDatas : contactsdatas.entrySet()) {
            dataModels.add(oneElemDatas.getValue());
        }
        adapter = new CustomListAdapter(dataModels, getContext());
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