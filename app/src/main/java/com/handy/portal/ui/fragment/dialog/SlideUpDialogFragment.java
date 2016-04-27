package com.handy.portal.ui.fragment.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * dialog fragment that slides up from the bottom
 * is dismissable by clicking outside
 */
public abstract class SlideUpDialogFragment extends DialogFragment
{
    /**
     *
     * @param inflater
     * @param container
     * @return the view that will be stuffed inside confirm_booking_action_content of this fragment's view
     */
    protected abstract View inflateContentView(LayoutInflater inflater, ViewGroup container);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.getAttributes().windowAnimations = R.style.dialog_animation_slide_up_down_from_bottom;

        /* TODO the line below won't actually work for now because we are making the layout full screen
         * as a hacky fix for the weird resize animation
         * (the layout overflows at the bottom and then it gets redrawn so that it's not)
         * seen when cancellation policy is shown in confirm claim
         * */
        dialog.setCanceledOnTouchOutside(cancelDialogOnTouchOutside());
        return dialog;
    }

    protected boolean cancelDialogOnTouchOutside()
    {
        return true;
    }

    /**
     * creates the view with the subclass's specific content layout
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_slide_up, container, false);
        LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.fragment_dialog_slide_up_content_layout);
        contentLayout.addView(inflateContentView(inflater, container));
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        wlp.gravity = Gravity.BOTTOM;
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * TODO need this because we're making the layout full screen instead of half screen
     * as a hacky fix for the weird resize animation seen
     * (the layout overflows at the bottom and then it gets redrawn so that it's not)
     * when cancellation policy screen is shown
     * in the confirm claim dialog
     */
    @OnClick(R.id.fragment_dialog_slide_up_transparent_background_layout)
    public void onTransparentBackgroundClicked()
    {
        dismiss();
    }
}
