package com.jeppsson.japaneseverbs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.jeppsson.japaneseverbs.db.Form;
import com.jeppsson.japaneseverbs.db.Verb;
import com.jeppsson.japaneseverbs.db.VerbBase;
import com.jeppsson.japaneseverbs.db.VerbDao;
import com.jeppsson.japaneseverbs.db.VerbDatabase;
import com.jeppsson.japaneseverbs.ui.SettingsActivity;
import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadTask extends AsyncTask<URL, Void, Void> {

    private final VerbDao mDao;
    private final SharedPreferences mPrefs;

    public DownloadTask(Context context) {
        mDao = VerbDatabase.getAppDatabase(context).verbDao();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected Void doInBackground(URL... urls) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = urls[0];
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            if (url.getHost().equals(urlConnection.getURL().getHost())) {

                CSVReader reader = new CSVReader(new InputStreamReader(in, "UTF-8"));

                mDao.clearVerbs();
                mDao.clearForms();
                mDao.clearBase();

                // Add verb forms to db
                List<Long> formIds = new ArrayList<>();
                String[] forms = reader.readNext();
                if (forms != null) {
                    for (int i = 0; i < forms.length; i = i + 4) {
                        Form form = new Form();
                        form.txt = forms[i];
                        form.dictionaryForm = i == 0;
                        long id = mDao.insertForm(form);
                        formIds.add(id);
                    }
                } else {
                    return null;
                }

                // Add verbs
                int verbId = 0;
                String[] verbs;
                while ((verbs = reader.readNext()) != null) {
                    if (verbs.length < forms.length) {
                        continue;
                    }

                    VerbBase base = new VerbBase();
                    base.verbId = verbId;
                    long baseId = mDao.insertVerbBase(base);

                    for (int i = 0; i < verbs.length; i = i + 4) {
                        if (TextUtils.isEmpty(verbs[i])) {
                            continue;
                        }

                        Verb verb = new Verb();
                        verb.meaning = verbs[i];
                        verb.formId = formIds.get(i / 4);
                        verb.verbBaseId = baseId;

                        verb.kanji = verbs[i + 1];
                        verb.furigana = verbs[i + 2];
                        verb.romanji = verbs[i + 3];
                        mDao.insertVerb(verb);
                    }

                    verbId++;
                }

                mPrefs.edit().putLong(SettingsActivity.PREF_UPDATED, System.currentTimeMillis()).apply();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }
}

