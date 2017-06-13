package com.handy.portal.library.ui.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handy.portal.R;

/**
 * copied from consumer app
 *
 * child classes must call super.onCreateView()
 * before calling showProgressSpinner and hideProgressSpinner
 */
public abstract class ProgressSpinnerFragment extends InjectedFragment {

    private ProgressBar mProgressSpinner;
    private View mOverlay;

    @CallSuper
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_progress_spinner, container, false);
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
        mOverlay = view.findViewById(R.id.progress_overlay);
        return view;
    }

    protected void showProgressSpinner(boolean isBlocking) {
        if (isBlocking) {
            mOverlay.bringToFront();
            mOverlay.setVisibility(View.VISIBLE);
        }
        mProgressSpinner.bringToFront();
        mProgressSpinner.setVisibility(View.VISIBLE);
    }

    protected void showProgressSpinner() {
        showProgressSpinner(false);
    }

    protected void hideProgressSpinner() {
        mProgressSpinner.setVisibility(View.GONE);
        mOverlay.setVisibility(View.GONE);
    }
}
