package com.lubenard.ft_hangouts;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String lastPauseTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
