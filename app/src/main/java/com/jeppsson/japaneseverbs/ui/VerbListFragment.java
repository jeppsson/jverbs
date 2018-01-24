package com.jeppsson.japaneseverbs.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeppsson.japaneseverbs.R;
import com.jeppsson.japaneseverbs.databinding.FragmentVerbListBinding;
import com.jeppsson.japaneseverbs.db.Verb3;
import com.jeppsson.japaneseverbs.db.VerbDao;
import com.jeppsson.japaneseverbs.db.VerbDatabase;

import java.util.List;

public class VerbListFragment extends Fragment {

    private FragmentVerbListBinding mBinding;
    private VerbAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_verb_list, container, false);

        mAdapter = new VerbAdapter(mVerbClickCallback);

        mBinding.verbList.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MeaningListViewModel model = ViewModelProviders.of(this).get(MeaningListViewModel.class);
        model.getMeanings().observe(this, meanings -> {
            if (meanings != null) {
                mBinding.setIsLoading(false);
                mAdapter.setVerbList(meanings);
            } else {
                mBinding.setIsLoading(true);
            }
        });
    }

    public void setQuery(String query) {
        MeaningListViewModel model = ViewModelProviders.of(this).get(MeaningListViewModel.class);
        model.setQuery(query);
    }

    private final VerbClickCallback mVerbClickCallback = new VerbClickCallback() {
        @Override
        public void onClick(long id) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                Intent intent = new Intent(getContext(), VerbDialogActivity.class);
                intent.putExtra(VerbDialogActivity.EXTRA_VERB_ID, id);
                startActivity(intent);
            }
        }
    };

    private static class MeaningListViewModel extends AndroidViewModel {

        private final MediatorLiveData<List<Verb3>> mMeanings;

        public MeaningListViewModel(@NonNull Application application) {
            super(application);

            mMeanings = new MediatorLiveData<>();

            VerbDao dao = VerbDatabase.getAppDatabase(getApplication()).verbDao();

            mMeanings.addSource(dao.loadDictionaryVerbs("%"), mMeanings::setValue);
        }

        LiveData<List<Verb3>> getMeanings() {
            return mMeanings;
        }

        void setQuery(String query) {
            VerbDao dao = VerbDatabase.getAppDatabase(getApplication()).verbDao();

            mMeanings.addSource(dao.loadDictionaryVerbs('%' + query + '%'), mMeanings::setValue);
        }
    }
}
