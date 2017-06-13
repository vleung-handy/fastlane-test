package com.handy.portal.library.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.handy.portal.R;

public abstract class FullScreenDialogFragment extends InjectedDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null && getWindowAnimationResourceId() > 0) {
            dialog.getWindow().setWindowAnimations(getWindowAnimationResourceId());
        }
        return dialog;
    }

    protected int getWindowAnimationResourceId() {
        return R.style.dialog_animation_slide_down_up_from_top;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialog);
    }
}
