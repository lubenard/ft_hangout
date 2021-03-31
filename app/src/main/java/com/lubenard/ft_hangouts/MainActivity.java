package com.lubenard.ft_hangouts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String lastPauseTime;
    private SharedPreferences sharedPreferences;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        createNotifChannel();

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
