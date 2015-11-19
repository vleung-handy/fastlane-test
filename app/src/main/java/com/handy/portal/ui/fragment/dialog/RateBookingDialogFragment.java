package com.handy.portal.ui.fragment.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.model.LocationData;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

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


    @InjectView(R.id.rating_button_1)
    RadioButton mRating1;
    @InjectView(R.id.rating_button_2)
    RadioButton mRating2;
    @InjectView(R.id.rating_button_3)
    RadioButton mRating3;
    @InjectView(R.id.rating_button_4)
    RadioButton mRating4;
    @InjectView(R.id.rating_button_5)
    RadioButton mRating5;


    public static final String FRAGMENT_TAG = "fragment_dialog_rate_booking";

    private Booking booking;
    private LocationData locationData;

    private static final int NUM_RATING_BUTTONS = 5;

    public static RateBookingDialogFragment newInstance(Booking booking, LocationData locationData)
    {
        RateBookingDialogFragment rateBookingDialogFragment = new RateBookingDialogFragment();
        rateBookingDialogFragment.setResources(booking, locationData);
        return rateBookingDialogFragment;
    }

    private void setResources(Booking booking, LocationData locationData)
    {
        this.booking = booking;
        this.locationData = locationData;
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
        confirmCheckoutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //bus.post(new HandyEvent.NavigateToTab(MainViewTab.SELECT_PAYMENT_METHOD, null, TransitionStyle.REFRESH_TAB));
                //RateBookingDialogFragment.this.dismiss();

                //System.out.println("Index : " + getBookingRatingIndex());

                //TODO: Is the endpoint expecting 0 or 1 indexed ratings?

                bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                bus.post(new HandyEvent.RequestNotifyJobCheckOut(
                        getBookingId(),
                        getLocationData(),
                        getBookingRatingIndex(),
                        getBookingRatingComment()));
            }
        });

        //Fill in the name of the user associated with the booking to the question prompt
        ratingTitle.setText(String.format(ratingTitle.getText().toString(),
                        (booking.getUser() != null && booking.getUser().getFirstName() != null ?
                                booking.getUser().getFirstName() : ""))
        )
        ;
    }

    //when clicked on, close the dialog, the fragment will listen for the event to come back and transition correctly, if fails brings back

    @Subscribe
    public void onReceiveNotifyJobCheckOutSuccess(final HandyEvent.ReceiveNotifyJobCheckOutSuccess event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

            //return to schedule page
            returnToTab(MainViewTab.SCHEDULED_JOBS, booking.getStartDate().getTime(), TransitionStyle.REFRESH_TAB);

            showToast(getString(R.string.check_out_success), Toast.LENGTH_LONG);

            dismiss();
        }
    }

    @Subscribe
    public void onReceiveNotifyJobCheckOutError(final HandyEvent.ReceiveNotifyJobCheckOutError event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            //handleNotifyCheckOutError(event);
        }
    }

    protected void showToast(String message, int length)
    {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    private void returnToTab(MainViewTab targetTab, long epochTime, TransitionStyle transitionStyle)
    {
        //Return to available jobs with success
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new HandyEvent.NavigateToTab(targetTab, arguments, transitionStyle));
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
        return ratingRadioGroup.indexOfChild(ratingRadioGroup.findViewById(ratingRadioGroup.getCheckedRadioButtonId()));
    }

    private String getBookingRatingComment()
    {
        return (commentText.getText() != null ? commentText.getText().toString() : "");
    }
}
