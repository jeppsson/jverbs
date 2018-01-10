package com.jeppsson.japaneseverbs.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeppsson.japaneseverbs.R;
import com.jeppsson.japaneseverbs.databinding.FragmentCheckBinding;
import com.jeppsson.japaneseverbs.db.Verb2;
import com.jeppsson.japaneseverbs.db.VerbDao;
import com.jeppsson.japaneseverbs.db.VerbDatabase;

import java.util.List;
import java.util.Random;

public class CheckFragment extends Fragment implements CheckClickCallback {

    private FragmentCheckBinding mBinding;
    private VerbViewModel mModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_check, container, false);
        mBinding.setCheckCallback(this);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mModel = ViewModelProviders.of(this).get(VerbViewModel.class);
        mModel.verbs.observe(this, verbs -> {
            if (verbs != null && verbs.size() > 0) {
                mBinding.setVerb(mModel.getVerb());
            }
        });

        mBinding.setStarted(mModel.started);
        mBinding.setAnswerPending(mModel.answerPending);
    }

    @Override
    public void onStartClick() {
        mModel.next();
        mBinding.setStarted(true);
        mBinding.setAnswerPending(true);
        mBinding.setVerb(mModel.getVerb());
    }

    @Override
    public void onVerbClick() {
        if (!mBinding.getAnswerPending()) {
            mModel.next();
        } else {
            mModel.answerPending = false;
        }

        mBinding.setAnswerPending(mModel.answerPending);
        mBinding.setVerb(mModel.getVerb());
    }

    private static class VerbViewModel extends AndroidViewModel {

        private final Random mRandom = new Random();
        private int mVerbId;

        private final LiveData<List<Verb2>> verbs;
        private boolean started;
        private boolean answerPending = true;

        public VerbViewModel(@NonNull Application application) {
            super(application);

            VerbDao dao = VerbDatabase.getAppDatabase(getApplication()).verbDao();

            verbs = dao.loadAllVerbs();
        }

        void next() {
            started = true;
            answerPending = true;
            List<Verb2> verbList = verbs.getValue();
            mVerbId = mRandom.nextInt(verbList.size());
        }

        Verb2 getVerb() {
            List<Verb2> verbList = verbs.getValue();
            return verbList.get(mVerbId);
        }
    }
}
