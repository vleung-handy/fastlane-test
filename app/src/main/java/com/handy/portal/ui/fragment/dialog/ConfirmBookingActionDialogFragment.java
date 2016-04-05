package com.handy.portal.ui.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * dialog fragment that slides up from the bottom.
 * has a dismiss button and a confirm button.
 */
public abstract class ConfirmBookingActionDialogFragment extends DialogFragment
{
    @Bind(R.id.confirm_booking_action_content)
    protected LinearLayout mConfirmBookingActionContent;
//    @Bind(R.id.confirm_booking_action_button)
//    TextView mConfirmButton;

    protected Booking mBooking;

    public abstract View getBookingActionContentView(LayoutInflater inflater, ViewGroup container);
//    public abstract String getConfirmButtonText();

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.getAttributes().windowAnimations = R.style.dialog_animation_slide_up_down_from_bottom;
        Drawable background = new ColorDrawable(Color.BLACK);
        background.setAlpha(130);
        window.setBackgroundDrawable(background);
        return dialog;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_confirm_booking_action, container, false);
        LinearLayout confirmBookingActionContentLayout = (LinearLayout) view.findViewById(R.id.confirm_booking_action_content);
        confirmBookingActionContentLayout.addView(getBookingActionContentView(inflater, container));
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
//        mConfirmButton.setText(getConfirmButtonText());
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

    public void confirmBookingActionButtonClicked()
    {
        Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        if(getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
        dismiss();
    }

    @OnClick(R.id.confirm_booking_action_dismiss_button)
    public void closeDialog()
    {
        dismiss();
    }
}
