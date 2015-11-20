package com.handy.portal.ui.fragment.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RateBookingDialogFragment extends InjectedDialogFragment //TODO: consolidate some of this logic with other dialog fragments
{

    @InjectView(R.id.rate_booking_confirm_checkout_button)
    protected Button confirmCheckoutButton;

    @InjectView(R.id.rate_booking_comment_text)
    protected EditText commentText;

    @InjectView(R.id.rate_booking_rating_radiogroup)
    protected RadioGroup ratingRadioGroup;

    @InjectView(R.id.rate_booking_title)
    protected TextView ratingTitle;

    public static final String FRAGMENT_TAG = "fragment_dialog_rate_booking";

    private Booking booking;

    public static RateBookingDialogFragment newInstance(Booking booking)
    {
        RateBookingDialogFragment rateBookingDialogFragment = new RateBookingDialogFragment();
        rateBookingDialogFragment.setResources(booking);
        return rateBookingDialogFragment;
    }

    private void setResources(Booking booking)
    {
        this.booking = booking;
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
        ratingTitle.setText(
                String.format(ratingTitle.getText().toString(),
                        (booking.getUser() != null && booking.getUser().getFirstName() != null ?
                                booking.getUser().getFirstName() : ""))
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
        //TODO: Is the endpoint expecting 0 or 1 indexed ratings?
        if (getBookingRatingIndex() >= 0)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            bus.post(new HandyEvent.RequestNotifyJobCheckOut(
                            getBookingId(),
                            new CheckoutRequest(
                                    getLocationData(),
                                    new ProBookingFeedback(getBookingRatingIndex(), getBookingRatingComment())
                            )
                    )
            );
        }
        else
        {
            showToast(getString(R.string.rate_booking_need_rating), Toast.LENGTH_SHORT);
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

    //TODO: Move to some util or parent class like we do for the other showtoast
    protected void showToast(String message, int length)
    {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private String getBookingId()
    {
        return booking.getId();
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getActivity());
    }

    private int getBookingRatingIndex()
    {
        return UIUtils.indexOfCheckedRadioButton(ratingRadioGroup);
    }

    private String getBookingRatingComment()
    {
        return (commentText.getText() != null ? commentText.getText().toString() : "");
    }
}
