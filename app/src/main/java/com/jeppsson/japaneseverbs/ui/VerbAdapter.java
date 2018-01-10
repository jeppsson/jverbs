package com.jeppsson.japaneseverbs.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jeppsson.japaneseverbs.R;
import com.jeppsson.japaneseverbs.databinding.VerbRowItemBinding;
import com.jeppsson.japaneseverbs.db.Verb3;

import java.util.List;
import java.util.Objects;

public class VerbAdapter extends RecyclerView.Adapter<VerbAdapter.VerbViewHolder> {

    @Nullable
    private final VerbClickCallback mVerbClickCallback;

    private List<Verb3> mVerbList;

    VerbAdapter(@Nullable VerbClickCallback verbClickCallback) {
        mVerbClickCallback = verbClickCallback;
    }

    void setVerbList(List<Verb3> verbList) {
        if (mVerbList == null) {
            mVerbList = verbList;
            notifyItemRangeInserted(0, verbList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mVerbList.size();
                }

                @Override
                public int getNewListSize() {
                    return verbList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mVerbList.get(oldItemPosition).id ==
                            verbList.get(newItemPosition).id;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Verb3 newVerb = verbList.get(newItemPosition);
                    Verb3 oldVerb = mVerbList.get(oldItemPosition);
                    return newVerb.id == oldVerb.id
                            && Objects.equals(newVerb.meaning, oldVerb.meaning)
                            && Objects.equals(newVerb.kanji, oldVerb.kanji)
                            && Objects.equals(newVerb.romanji, oldVerb.romanji);
                }
            });
            mVerbList = verbList;
            result.dispatchUpdatesTo(this);

        }
    }

    @Override
    public VerbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VerbRowItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.verb_row_item, parent, false);
        binding.setCallback(mVerbClickCallback);

        return new VerbViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(VerbViewHolder holder, int position) {
        holder.mBinding.setVerb(mVerbList.get(position));
    }

    @Override
    public int getItemCount() {
        return mVerbList == null ? 0 : mVerbList.size();
    }

    static class VerbViewHolder extends RecyclerView.ViewHolder {

        final VerbRowItemBinding mBinding;

        VerbViewHolder(VerbRowItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
