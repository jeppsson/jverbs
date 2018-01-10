package com.jeppsson.japaneseverbs.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jeppsson.japaneseverbs.DownloadTask;
import com.jeppsson.japaneseverbs.R;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final long AUTO_UPDATE_PERIOD = 30 * 24 * 60 * 60 * 1000L; // 30 days

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            VerbListFragment fragment = new VerbListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }

        checkAutoUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check:
                startActivity(new Intent(this, CheckActivity.class));
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkAutoUpdate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long updated = prefs.getLong(SettingsActivity.PREF_UPDATED, 0);
        long now = System.currentTimeMillis();

        if (updated + AUTO_UPDATE_PERIOD < now) {
            String urlPref = prefs.getString("pref_url", getString(R.string.default_url));
            try {
                new DownloadTask(this).execute(new URL(urlPref));
            } catch (MalformedURLException e) {
                Toast.makeText(this, "URL format wrong", Toast.LENGTH_LONG).show();
            }
        }
    }
}
