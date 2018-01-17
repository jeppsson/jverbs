package com.jeppsson.japaneseverbs.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jeppsson.japaneseverbs.DownloadTask;
import com.jeppsson.japaneseverbs.R;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final long AUTO_UPDATE_PERIOD = 30 * 24 * 60 * 60 * 1000L; // 30 days
    private static final String TAG_FRAGMENT = "VERB_LIST_FRAGMENT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new VerbListFragment(), TAG_FRAGMENT).commit();
        }

        checkAutoUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(SEARCH_SERVICE);
        if (searchManager != null) {
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setOnQueryTextListener(this);
        }
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

    @Override
    public boolean onQueryTextChange(String newText) {
        setQuery(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        setQuery(query);
        return false;
    }

    private void setQuery(String query) {
        VerbListFragment fragment = (VerbListFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (fragment != null) {
            fragment.setQuery(query);
        }
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
