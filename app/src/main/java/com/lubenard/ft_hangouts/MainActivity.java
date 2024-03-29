package com.lubenard.ft_hangouts;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.lubenard.ft_hangouts.UI.MainFragment;
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
                        onPermissionError.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
        }
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

    /**
     * Set correct theme + languages at app startup
     */
    private void checkConfig() {
        String theme_option = sharedPreferences.getString("ui_theme", "dark");
        Utils.applyTheme(theme_option);

        String language_option = sharedPreferences.getString("ui_language", "system");
        Utils.applyLanguage(this, language_option.toString());

        String header_option = sharedPreferences.getString("ui_header_color", "default");
        Utils.applyHeaderColor(this, header_option);
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
        checkOrRequestPerm(this, this, Manifest.permission.RECEIVE_SMS, () -> null, () -> {
            Toast.makeText(this, getString(R.string.no_access_to_send_sms), Toast.LENGTH_LONG).show();
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
