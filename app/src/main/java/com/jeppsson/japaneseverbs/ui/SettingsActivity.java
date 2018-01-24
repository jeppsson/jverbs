package com.jeppsson.japaneseverbs.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.jeppsson.japaneseverbs.DownloadService;
import com.jeppsson.japaneseverbs.R;

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
                    getActivity().startService(new Intent(getActivity(), DownloadService.class));
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
