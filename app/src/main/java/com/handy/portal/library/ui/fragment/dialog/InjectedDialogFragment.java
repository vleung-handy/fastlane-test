package com.handy.portal.library.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.library.util.Utils;

import butterknife.BindView;

public class InjectedDialogFragment extends DialogFragment {
    @Nullable
    @BindView(R.id.loading_overlay)
    View mLoadingOverlay;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.inject(getActivity(), this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(STYLE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_slide_down_up_from_top;
        return dialog;
    }

    protected void showLoadingOverlay() {
        if (mLoadingOverlay != null) {
            mLoadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    protected void hideLoadingOverlay() {
        if (mLoadingOverlay != null) {
            mLoadingOverlay.setVisibility(View.GONE);
        }
    }
}
