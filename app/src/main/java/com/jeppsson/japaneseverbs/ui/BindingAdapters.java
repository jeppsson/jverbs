package com.jeppsson.japaneseverbs.ui;

import android.databinding.BindingAdapter;
import android.view.View;

public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showGone(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("visibleHide")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}