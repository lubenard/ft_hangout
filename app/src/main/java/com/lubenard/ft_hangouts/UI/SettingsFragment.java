package com.lubenard.ft_hangouts.UI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;

import com.lubenard.ft_hangouts.DbManager;
import com.lubenard.ft_hangouts.MainActivity;
import com.lubenard.ft_hangouts.R;
import com.lubenard.ft_hangouts.Utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Settings page.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = "SettingsActivity";
    private static Activity activity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_activity, rootKey);
        activity = getActivity();
        activity.setTitle(R.string.action_settings);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Language change listener
        final Preference language = findPreference("ui_language");
        language.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.d(TAG, "Language value has changed for " + newValue);
            Utils.applyLanguage(getContext(), newValue.toString());
            restartActivity();
            return true;
        });

        // Theme change listener
        final Preference theme = findPreference("ui_theme");
        theme.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.d(TAG, "Theme value has changed for " + newValue);
            Utils.applyTheme(newValue.toString());
            return true;
        });

        // Theme change listener
        final Preference headerColor = findPreference("ui_header_color");
        headerColor.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.d(TAG, "Theme value has changed for " + newValue);
            Utils.applyHeaderColor(getActivity(), newValue.toString());
            return true;
        });

        // feedback preference click listener
        Preference integrate_system_contacts = findPreference("tweaks_integrate_system_contacts");
        integrate_system_contacts.setOnPreferenceClickListener(preference -> {
            MainActivity.checkOrRequestPerm(getActivity(), getContext(), Manifest.permission.READ_CONTACTS, () -> {
                integrateSystemContacts();
                return true;
            }, () -> {
                Toast.makeText(getContext(), R.string.permission_denied_red_contacts, Toast.LENGTH_LONG).show();
                return false;
            });
            return true;
        });

        // feedback preference click listener
        Preference myPref = findPreference("other_feedback");
        myPref.setOnPreferenceClickListener(preference -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","lubenard@student.42.fr", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_app));
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
            return true;
        });
    }

    /**
     * Integrate the system contact by questionning the android API
     */
    private void integrateSystemContacts() {
        String contactName;
        String phoneNumber;
        String email;
        InputStream contactImageIStream;
        Bitmap bitmapContactImage;
        String iconImage;
        DbManager dbManager = new DbManager(getContext());
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                phoneNumber = null;
                email = null;
                iconImage = null;
                contactImageIStream = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id)));

                if (contactImageIStream != null) {
                    bitmapContactImage = BitmapFactory.decodeStream(contactImageIStream);

                    iconImage = getContext().getFilesDir().getAbsolutePath() + id  + ".jpg";
                    try {
                        bitmapContactImage.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(iconImage));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                    if (pCur != null && pCur.moveToFirst()) {
                        phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    pCur.close();
                }
                // get the user's email address
                Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                if (ce != null && ce.moveToFirst()) {
                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    ce.close();
                }
                dbManager.createNewContact(contactName, (phoneNumber != null) ? phoneNumber : "", (email != null) ? email : "", null, null, iconImage, "SYSTEM");
            }
        }
        if (cur != null)
            cur.close();
        Toast.makeText(getContext(), getContext().getString(R.string.all_system_contacts_imported), Toast.LENGTH_SHORT).show();
    }

    /**
     * Restart activity
     */
    public static void restartActivity() {
        activity.recreate();
    }
}
