package com.lubenard.ft_hangouts;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.lubenard.ft_hangouts.Utils.Utils;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class EditContact extends Fragment {

    private int contactId;
    private DbManager dbManager;
    private View view;
    private Toolbar toolbar;
    private ImageButton imagePicker;
    private String iconImage = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.edit_contact_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbManager = new DbManager(getContext());
        this.view = view;

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        toolbar = view.findViewById(R.id.edit_contact_toolbar);
        imagePicker = view.findViewById(R.id.imagePicker);

        if (contactId == -1)
            toolbar.setTitle(R.string.app_create_contact_name);
        else {
            toolbar.setTitle(R.string.app_edit_contact_name);
            ArrayList<String> contactDetails = dbManager.getContactDetail(contactId);
            // Load datas into the fields
            ((EditText)view.findViewById(R.id.editTextTextPersonName)).setText(contactDetails.get(0));
            ((EditText)view.findViewById(R.id.editTextPhone)).setText(contactDetails.get(1));
            ((EditText)view.findViewById(R.id.editTextTextEmailAddress)).setText(contactDetails.get(2));
            ((EditText)view.findViewById(R.id.editTextTextPostalAddress)).setText(contactDetails.get(3));
            ((EditText)view.findViewById(R.id.editTextBirthday)).setText(contactDetails.get(4));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get back to last fragment in the stack
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_validate:
                    // TODO: see if with API we can insert contact into system contacts
                    // For now, just register into own contacts
                    String name = ((EditText)view.findViewById(R.id.editTextTextPersonName)).getText().toString();
                    String phoneNumber = ((EditText)view.findViewById(R.id.editTextPhone)).getText().toString();
                    String email = ((EditText)view.findViewById(R.id.editTextTextEmailAddress)).getText().toString();
                    String address = ((EditText)view.findViewById(R.id.editTextTextPostalAddress)).getText().toString();
                    String birthday = ((EditText)view.findViewById(R.id.editTextBirthday)).getText().toString();

                    if (contactId == -1)
                        dbManager.createNewContact(name, phoneNumber, email, address, birthday, iconImage);
                    else
                        dbManager.updateContact(contactId, name, phoneNumber, email, address, birthday, iconImage);

                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                    return true;
                default:
                    return false;
            }
        });

        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String pickTitle = "Select or take a new Picture";
                Intent chooserIntent = Intent.createChooser(intent, pickTitle);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePhotoIntent });
                startActivityForResult(Intent.createChooser(chooserIntent,"Select Picture"), 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("EditContact", "OnActivityResult launched, datas are: " + resultCode + " datas = " + data + " data content " + data.getData());

        if (resultCode  == RESULT_OK && data != null && data.getData() != null) {
            String filename;

            if (contactId > 0)
                filename = "/" + contactId;
            else
                filename = "/image.jpg";

            Log.d("Activity", "Result is okay, nice");
            Uri selectedImageUri = data.getData();
            Log.d("Activity", "is null ? " + data.getData());
            Utils.writeFileOnInternalStorage(getContext(), filename, selectedImageUri);
            Log.d("Activity", "file dir = " + getContext().getFilesDir().getAbsolutePath() + filename);
            File file = new File(getContext().getFilesDir().getAbsolutePath() + filename);

            if (file.exists())
                Log.d("Activity", "the file exist");
            else
                Log.d("Activity", "the file does not exist");

            iconImage = getContext().getFilesDir().getAbsolutePath() + filename;
            imagePicker.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagePicker.setImageDrawable(Drawable.createFromPath(getContext().getFilesDir().getAbsolutePath() + filename));
        }
    }
}
