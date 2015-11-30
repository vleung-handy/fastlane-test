package com.handy.portal.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.model.CheckoutRequest;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.ProBookingFeedback;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RateBookingDialogFragment extends InjectedDialogFragment
{
    @InjectView(R.id.rate_booking_comment_text)
    EditText mCommentText;

    @InjectView(R.id.rate_booking_rating_radiogroup)
    RadioGroup mRatingRadioGroup;

    @InjectView(R.id.rate_booking_title)
    TextView mRatingTitle;

    public static final String FRAGMENT_TAG = "fragment_dialog_rate_booking";

    private Booking mBooking;

    public static RateBookingDialogFragment newInstance(@NonNull Booking booking)
    {
        RateBookingDialogFragment rateBookingDialogFragment = new RateBookingDialogFragment();
        rateBookingDialogFragment.setResources(booking);
        return rateBookingDialogFragment;
    }

    private void setResources(Booking booking)
    {
        mBooking = booking;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_rate_booking, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //Fill in the name of the user associated with the booking to the question prompt
        mRatingTitle.setText(
                String.format(mRatingTitle.getText().toString(),
                        (mBooking.getUser() != null &&
                                mBooking.getUser().getFirstName() != null ?
                                mBooking.getUser().getFirstName() : "")
                )
        );
    }

    @OnClick(R.id.close_button)
    public void onCloseButtonClick()
    {
        dismiss();
    }

    @OnClick(R.id.rate_booking_confirm_checkout_button)
    public void onConfirmCheckoutButtonClick()
    {
        //Endpoint is expecting a rating of 1 - 5
        if (getBookingRatingScore() > 0)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            bus.post(new HandyEvent.RequestNotifyJobCheckOut(
                            getBookingId(),
                            new CheckoutRequest(
                                    getLocationData(),
                                    new ProBookingFeedback(getBookingRatingScore(), getBookingRatingComment())
                            )
                    )
            );
        }
        else
        {
            UIUtils.showToast(getContext(), getString(R.string.rate_booking_need_rating), Toast.LENGTH_SHORT);
        }
    }

    //when clicked on, close the dialog, the fragment will listen for the event to come back and transition correctly, if fails brings back
    @Subscribe
    public void onReceiveNotifyJobCheckOutSuccess(final HandyEvent.ReceiveNotifyJobCheckOutSuccess event)
    {
        if (!event.isAuto)
        {
            dismiss();
        }
    }

    private String getBookingId()
    {
        return mBooking.getId();
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getActivity());
    }

    private int getBookingRatingScore()
    {
        //Endpoint is expected a 1 indexed rating
        return 1 + UIUtils.indexOfCheckedRadioButton(mRatingRadioGroup);
    }

    private String getBookingRatingComment()
    {
        return (mCommentText.getText() != null ? mCommentText.getText().toString() : "");
    }
}
