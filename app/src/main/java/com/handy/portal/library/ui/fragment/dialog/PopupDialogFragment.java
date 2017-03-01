package com.handy.portal.library.ui.fragment.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.handy.portal.R;

import butterknife.ButterKnife;

/**
 * dialog fragment that slides down from the top to the center
 */
public abstract class PopupDialogFragment extends InjectedDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation_slide_down_up_from_top;
        return dialog;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }
}

