package com.handy.portal.ui.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;
import com.handy.portal.model.CheckoutRequest;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.ProBookingFeedback;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_rate_booking, container, false);
        ButterKnife.inject(this, view);

        mBooking = null;
        if (getArguments() != null && getArguments().containsKey(BundleKeys.BOOKING))
        {
            mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        }

        if (mBooking == null)
        {
            Crashlytics.logException(new Exception("No valid booking passed to RateBookingDialogFragment, aborting rating"));
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

        return view;
    }

    @OnClick(R.id.close_button)
    public void onCloseButtonClick()
    {
        dismiss();
    }

    @OnClick(R.id.rate_booking_confirm_checkout_button)
    public void onConfirmCheckoutButtonClick()
    {
        Address address = mBooking.getAddress();
        if (address != null)
        {
            bus.post(new BookingEvent.RequestNearbyBookings(mBooking.getRegionId(),
                    address.getLatitude(), address.getLongitude()));
        }
        //Endpoint is expecting a rating of 1 - 5
//        if (getBookingRatingScore() > 0)
//        {
//            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
//            bus.post(new HandyEvent.RequestNotifyJobCheckOut(
//                            getBookingId(),
//                            new CheckoutRequest(
//                                    getLocationData(),
//                                    new ProBookingFeedback(getBookingRatingScore(), getBookingRatingComment())
//                            )
//                    )
//            );
//        }
//        else
//        {
//            UIUtils.showToast(getContext(), getString(R.string.rate_booking_need_rating), Toast.LENGTH_SHORT);
//        }
    }

    //when clicked on, close the dialog, the fragment will listen for the event to come back and transition correctly, if fails brings back
    @Subscribe
    public void onReceiveNotifyJobCheckOutSuccess(final HandyEvent.ReceiveNotifyJobCheckOutSuccess event)
    {
        if (!event.isAutoCheckIn)
        {
            Address address = mBooking.getAddress();
            if (address != null)
            {
                bus.post(new BookingEvent.RequestNearbyBookings(5, 40.7400016784668, -73.99299621582031));
            }
            else
            {
                dismiss();
            }
        }
    }

    @Subscribe
    public void onReceiveNearbyBookingsSuccess(final BookingEvent.ReceiveNearbyBookingsSuccess event)
    {
        if (event.getBookings().size() < 1) { return; }

        Address address = mBooking.getAddress();
        if (address != null)
        {
            Bundle args = new Bundle();
            args.putSerializable(BundleKeys.BOOKINGS, new ArrayList<>(event.getBookings()));
            args.putParcelable(BundleKeys.MAP_CENTER,
                    new LatLng(address.getLatitude(), address.getLongitude()));
            bus.post(new HandyEvent.NavigateToTab(MainViewTab.NEARBY_JOBS, args));
        }
        dismiss();
    }

    @Subscribe
    public void onReceiveNearbyBookingsError(final BookingEvent.ReceiveNearbyBookingsError event)
    {
        dismiss();
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
