package com.jeppsson.japaneseverbs.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeppsson.japaneseverbs.DownloadTask;
import com.jeppsson.japaneseverbs.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREF_UPDATED = "updated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            // Refresh
            Preference refreshPreference = findPreference("pref_refresh");
            refreshPreference.setOnPreferenceClickListener(this);
            setUpdatedSummary(refreshPreference, getPreferenceManager().getSharedPreferences());
        }

        @Override
        public void onStart() {
            super.onStart();

            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();

            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "pref_refresh":
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String urlPref = sharedPref.getString("pref_url", getString(R.string.default_url));
                    try {
                        new DownloadTask(getActivity()).execute(new URL(urlPref));
                        return true;
                    } catch (MalformedURLException e) {
                        Toast.makeText(getActivity(), "URL format wrong", Toast.LENGTH_LONG).show();
                    }
                    return true;
            }

            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (PREF_UPDATED.equals(key)) {
                Preference refreshPreference = findPreference("pref_refresh");
                setUpdatedSummary(refreshPreference, sharedPreferences);
            }
        }

        private static void setUpdatedSummary(Preference preference, SharedPreferences sharedPreferences) {
            long updated = sharedPreferences.getLong(PREF_UPDATED, 0);
            preference.setSummary("Last updated: " + SimpleDateFormat.getDateTimeInstance().format(updated));
        }
    }
}
