package com.jeppsson.japaneseverbs.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jeppsson.japaneseverbs.R;
import com.jeppsson.japaneseverbs.db.Verb2;
import com.jeppsson.japaneseverbs.db.VerbDao;
import com.jeppsson.japaneseverbs.db.VerbDatabase;

import java.util.List;

public class VerbDialogActivity extends AppCompatActivity implements View.OnClickListener {

    static final String EXTRA_VERB_ID = "verbs.intent.extra.VERB_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verb);

        findViewById(R.id.btn_verb_dialog_ok).setOnClickListener(this);

        long verbId = getIntent().getLongExtra(EXTRA_VERB_ID, 0);

        VerbViewModel model = ViewModelProviders.of(this, new VerbViewModel.VerbViewModelFactory(getApplication(), verbId)).get(VerbViewModel.class);
        model.getMeanings().observe(this, meanings -> {
            if (meanings != null) {
                setTitle(meanings.get(0).meaning);

                StringBuilder sb = new StringBuilder();
                for (Verb2 verb : meanings) {
                    sb.append(verb.kanji).append(" ")
                            .append(verb.romanji).append(" ")
                            .append(verb.furigana).append('\n');
                }
                TextView txtVerbs = findViewById(R.id.verbs);
                txtVerbs.setText(sb);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verb_dialog_ok:
                finish();
                break;
        }
    }

    private static class VerbViewModel extends AndroidViewModel {

        private final LiveData<List<Verb2>> mMeanings;

        VerbViewModel(@NonNull Application application, long verbId) {
            super(application);

            VerbDao dao = VerbDatabase.getAppDatabase(getApplication()).verbDao();

            mMeanings = dao.loadVerb(verbId);
        }

        LiveData<List<Verb2>> getMeanings() {
            return mMeanings;
        }

        public static class VerbViewModelFactory extends ViewModelProvider.NewInstanceFactory {
            private Application mApplication;
            private long mVerbId;


            VerbViewModelFactory(Application application, long verbId) {
                mApplication = application;
                mVerbId = verbId;
            }

            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new VerbViewModel(mApplication, mVerbId);
            }
        }
    }
}
