package com.lubenard.ft_hangouts;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.lubenard.ft_hangouts.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {
    private String lastPauseTime;
    private SharedPreferences sharedPreferences;
    private static Callable onPermissionSuccess;
    private static Callable onPermissionError;

    /**
     * Check the requested permission, and if not already gave, ask for it
     * @param permRequired
     * @return
     */
    public static boolean checkOrRequestPerm(Activity activity, Context context, String permRequired, Callable onSucess, Callable onError) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permRequired) == PackageManager.PERMISSION_GRANTED) {
                try {
                    onSucess.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (activity.shouldShowRequestPermissionRationale(permRequired)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                try {
                    onError.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.alertdialog_perm_not_granted_title))
                //   .setMessage(context.getResources().getString(R.string.alertdialog_perm_not_granted_desc)).setPositiveButton(context.getResources().getString(R.));
                return false;
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                onPermissionSuccess = onSucess;
                onPermissionError = onError;
                activity.requestPermissions(new String[]{permRequired}, 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    try {
                        onPermissionSuccess.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    try {
                        onPermissionSuccess.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    private void createNotifChannel() {
        if (!sharedPreferences.getBoolean("has_notif_channel_created", false)) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("NORMAL_CHANNEL",
                        getString(R.string.notif_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(getString(R.string.notif_normal_channel_desc));
                // Do not show badge
                channel.setShowBadge(false);
                mNotificationManager.createNotificationChannel(channel);
                sharedPreferences.edit().putBoolean("has_notif_channel_created", true).apply();
            }
        }
    }

    private void checkConfig() {
        String theme_option = sharedPreferences.getString("ui_theme", "dark");
        switch (theme_option) {
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

        String language_option = sharedPreferences.getString("ui_language", "system");
        switch (language_option) {
            case "en":
                Utils.setAppLocale(this, "en-us");
                break;
            case "fr":
                Utils.setAppLocale(this, "fr");
                break;
            case "system":
                Utils.setAppLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage());
                break;
        }

        String header_option = sharedPreferences.getString("ui_header_color", "default");
        switch (header_option.toString()) {
            case "default":
                this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6200EE")));
                break;
            case "white":
                this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
                break;
            case "black":
                this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                break;
            case "blue":
                this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3366FF")));
                break;
            case "red":
                this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6666")));
                break;
            case "green":
                this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33CCgit 33")));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getSupportFragmentManager().popBackStackImmediate();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkConfig();
        createNotifChannel();
        checkOrRequestPerm(this, this, Manifest.permission.RECEIVE_SMS, () -> {
            return null;
        }, () -> {
            Toast.makeText(this, getString(R.string.no_access_to_send_sms), Toast.LENGTH_SHORT).show();
            return null;
        });

        // Then switch to the main Fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new MainFragment());
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastPauseTime = new SimpleDateFormat("hh:mm:ss").format(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastPauseTime != null)
            Toast.makeText(this, getString(R.string.lastPause) + lastPauseTime, Toast.LENGTH_SHORT).show();
    }
}
