package com.jeppsson.japaneseverbs;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jeppsson.japaneseverbs.db.Form;
import com.jeppsson.japaneseverbs.db.Verb;
import com.jeppsson.japaneseverbs.db.VerbBase;
import com.jeppsson.japaneseverbs.db.VerbDao;
import com.jeppsson.japaneseverbs.db.VerbDatabase;
import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadService extends IntentService {

    public static final String PREF_UPDATED = "updated";

    public DownloadService() {
        super(DownloadService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(getString(R.string.default_url));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            if (url.getHost().equals(urlConnection.getURL().getHost())) {

                CSVReader reader = new CSVReader(new InputStreamReader(in, "UTF-8"));

                VerbDao dao = VerbDatabase.getAppDatabase(this).verbDao();

                dao.clearVerbs();
                dao.clearForms();
                dao.clearBase();

                // Add verb forms to db
                List<Long> formIds = new ArrayList<>();
                String[] forms = reader.readNext();
                if (forms != null) {
                    for (int i = 0; i < forms.length; i = i + 4) {
                        Form form = new Form();
                        form.txt = forms[i];
                        form.dictionaryForm = i == 0;
                        long id = dao.insertForm(form);
                        formIds.add(id);
                    }
                } else {
                    return;
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
                    long baseId = dao.insertVerbBase(base);

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
                        dao.insertVerb(verb);
                    }

                    verbId++;
                }

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putLong(PREF_UPDATED, System.currentTimeMillis()).apply();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

