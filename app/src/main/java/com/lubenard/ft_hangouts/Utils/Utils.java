package com.lubenard.ft_hangouts.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lubenard.ft_hangouts.SettingsFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class Utils {

    public static void applyTheme(String newValue) {
        switch (newValue) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "white":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "battery_saver":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public static void applyLanguage(Context context, String newValue) {
        switch (newValue) {
            case "en":
                setAppLocale(context,"en-us");
                break;
            case "fr":
                setAppLocale(context, "fr");
                break;
            case "system":
                setAppLocale(context, Resources.getSystem().getConfiguration().locale.getLanguage());
                break;
        }
    }

    public static void applyHeaderColor(Activity activity, String newValue) {
        switch (newValue) {
            case "default":
                ((AppCompatActivity)activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6200EE")));
                break;
            case "black":
                ((AppCompatActivity)activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                break;
            case "blue":
                ((AppCompatActivity)activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3366FF")));
                break;
            case "red":
                ((AppCompatActivity)activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6666")));
                break;
            case "green":
                ((AppCompatActivity)activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33CC33")));
                break;
        }
    }

    public static boolean checkExistantPhoneNumnber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return true;
        }
        return false;
    }

    // Very useful https://mkyong.com/java/how-to-write-to-file-in-java-fileoutputstream-example/
    public static void writeFileOnInternalStorage(Context mcoContext, String sFileName, Uri datasUri) {
        try {
            File file = new File(mcoContext.getFilesDir(), sFileName);
            FileOutputStream fop = new FileOutputStream(file);
            InputStream inputStream = mcoContext.getContentResolver().openInputStream(datasUri);
            while (inputStream.available() > 0)
                fop.write(inputStream.read());
            fop.close();
            Log.d("Utils", "wrote file to " + file.getAbsolutePath());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static final void setAppLocale(Context context, String localeCode) {
        Locale myLocale = new Locale(localeCode);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
