package com.handy.portal.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public abstract class ConfirmBookingActionDialogFragment extends SlideUpDialogFragment
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
    protected abstract View inflateBookingActionContentView(LayoutInflater inflater, ViewGroup container);

    protected final View inflateContentView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_confirm_booking_action, container, false);
    }

    protected abstract void onConfirmBookingActionButtonClicked();
    protected abstract int getConfirmButtonBackgroundResourceId();
    protected abstract String getConfirmButtonText();

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(getActivity(), this);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING); //should not be null
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
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ScrollView confirmBookingActionContentLayout = (ScrollView) view.findViewById(R.id.confirm_booking_action_content);
        confirmBookingActionContentLayout.addView(inflateBookingActionContentView(inflater, container));
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
