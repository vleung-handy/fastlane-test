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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * dialog fragment that slides up from the bottom.
 * has a dismiss button and a confirm button
 *
 * shown when we want to confirm a booking action
 */
public abstract class ConfirmBookingActionDialogFragment extends DialogFragment
{
    @Bind(R.id.confirm_booking_action_button)
    Button mConfirmBookingActionButton;

    protected Booking mBooking;

    /**
     *
     * @param inflater
     * @param container
     * @return the view that will be stuffed inside confirm_booking_action_content of this fragment's view
     */
    protected abstract View getBookingActionContentView(LayoutInflater inflater, ViewGroup container);
    protected abstract void onConfirmBookingActionButtonClicked();
    protected abstract int getConfirmButtonBackgroundResourceId();
    protected abstract String getConfirmButtonText();

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(getActivity(), this);
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
        dialog.setCanceledOnTouchOutside(cancelDialogOnTouchOutside());
        return dialog;
    }

    public boolean cancelDialogOnTouchOutside()
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
        View view = inflater.inflate(R.layout.fragment_dialog_confirm_booking_action, container, false);
        ScrollView confirmBookingActionContentLayout = (ScrollView) view.findViewById(R.id.confirm_booking_action_content);
        confirmBookingActionContentLayout.addView(getBookingActionContentView(inflater, container));
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mConfirmBookingActionButton.setBackgroundResource(getConfirmButtonBackgroundResourceId());
        mConfirmBookingActionButton.setText(getConfirmButtonText());
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

    @OnClick(R.id.confirm_booking_action_button)
    public void onConfirmButtonClicked()
    {
        onConfirmBookingActionButtonClicked();
    }

    @OnClick(R.id.confirm_booking_action_dismiss_button)
    public void onDismissButtonClicked()
    {
        dismiss();
    }
}
