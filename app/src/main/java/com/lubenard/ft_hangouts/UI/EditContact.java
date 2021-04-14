package com.lubenard.ft_hangouts.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.lubenard.ft_hangouts.Custom_Objects.ContactModel;
import com.lubenard.ft_hangouts.DbManager;
import com.lubenard.ft_hangouts.MainActivity;
import com.lubenard.ft_hangouts.R;
import com.lubenard.ft_hangouts.Utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class EditContact extends Fragment {

    private int contactId;
    private DbManager dbManager;
    private ImageButton imagePicker;
    private String iconImage = null;
    private Activity activity;
    private Context context;

    private EditText personName;
    private EditText phone;
    private EditText emailAddress;
    private EditText postalAddress;
    private EditText birthdayDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.edit_contact_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        context = getContext();
        dbManager = new DbManager(context);

        Bundle bundle = this.getArguments();
        contactId = bundle.getInt("contactId", -1);

        ((AppCompatActivity)activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePicker = view.findViewById(R.id.imagePicker);
        personName = view.findViewById(R.id.editTextTextPersonName);
        phone = view.findViewById(R.id.editTextPhone);
        emailAddress = view.findViewById(R.id.editTextTextEmailAddress);
        postalAddress = view.findViewById(R.id.editTextTextPostalAddress);
        birthdayDate = view.findViewById(R.id.editTextBirthday);

        if (contactId == -1)
            activity.setTitle(R.string.app_create_contact_name);
        else {
            activity.setTitle(R.string.app_edit_contact_name);
            ContactModel contactDetails = dbManager.getContactDetail(contactId);
            // Load datas into the fields
            personName.setText(contactDetails.getName());
            phone.setText(contactDetails.getPhoneNumber());
            emailAddress.setText(contactDetails.getEmail());
            postalAddress.setText(contactDetails.getAddress());
            birthdayDate.setText(contactDetails.getBirthday());
        }

        imagePicker.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            String pickTitle = getString(R.string.select_or_take_picture);
            Intent chooserIntent = Intent.createChooser(intent, pickTitle);
            MainActivity.checkOrRequestPerm(activity, context, Manifest.permission.CAMERA, () -> {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePhotoIntent });
                startActivityForResult(Intent.createChooser(chooserIntent, getString(R.string.select_picture)), 1);
                return null;

            }, () -> {
                Toast.makeText(context, context.getString(R.string.allow_camera), Toast.LENGTH_LONG).show();
                startActivityForResult(Intent.createChooser(chooserIntent, getString(R.string.select_picture)), 1);
                return null;
            });
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_contact, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_validate:
                // TODO: see if with API we can insert contact into system contacts
                // For now, just register into own contacts
                String name = personName.getText().toString();
                String phoneNumber = phone.getText().toString();
                String email = emailAddress.getText().toString();
                String address = postalAddress.getText().toString();
                String birthday = birthdayDate.getText().toString();

                if (name.isEmpty() && phoneNumber.isEmpty() && email.isEmpty()) {
                    Toast.makeText(context, R.string.cannot_save_contact, Toast.LENGTH_LONG).show();
                    return false;
                }

                if (name.isEmpty() && !phoneNumber.isEmpty())
                    name = phoneNumber;
                else if (name.isEmpty() && phoneNumber.isEmpty() && !email.isEmpty())
                    name = email;

                if (contactId == -1)
                    dbManager.createNewContact(name, phoneNumber, email, address, birthday, iconImage, "INTERNAL");
                else
                    dbManager.updateContact(contactId, name, phoneNumber, email, address, birthday, iconImage);

                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;
            default:
                return false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filename;
        if (resultCode == RESULT_OK && data != null && data.getData() == null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            filename = new SimpleDateFormat("/dd-MM-yyyy_HH-mm-ss").format(new Date()) + ".jpg";

            iconImage = context.getFilesDir().getAbsolutePath() + filename;

            try {
                photo.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(iconImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
            imagePicker.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagePicker.setImageBitmap(photo);
        }
        if (resultCode  == RESULT_OK && data != null && data.getData() != null) {
            filename = new SimpleDateFormat("/dd-MM-yyyy_HH-mm-ss").format(new Date()) + ".jpg";

            Uri selectedImageUri = data.getData();

            iconImage = context.getFilesDir().getAbsolutePath() + filename;

            Utils.writeFileOnInternalStorage(context, filename, selectedImageUri);

            imagePicker.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagePicker.setImageDrawable(Drawable.createFromPath(context.getFilesDir().getAbsolutePath() + filename));
        }
    }
}
