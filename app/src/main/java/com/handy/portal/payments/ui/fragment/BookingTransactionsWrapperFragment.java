package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.Transaction;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.util.TextUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingTransactionsWrapperFragment extends ActionBarFragment
{
    @Bind(R.id.container)
    ViewGroup mContainer;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mErrorText;

    private String mRequestedBookingId;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);

        Bundle arguments = getArguments();
        if (arguments == null) { return; }

        mRequestedBookingId = arguments.getString(BundleKeys.BOOKING_ID);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_loading_container, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        requestBookingPaymentDetails(mRequestedBookingId);
        Log.d("xizz", "Booking id: " + mRequestedBookingId);
    }

    @OnClick(R.id.try_again_button)
    public void onClickRequestDetails()
    {
        requestBookingPaymentDetails(mRequestedBookingId);
    }

    @Subscribe
    public void onReceiveBookingDetailsSuccess(PaymentEvent.ReceiveBookingPaymentDetailsSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Booking booking = event.mBookingTransactions.getBooking();
        Transaction[] transactions = event.mBookingTransactions.getTransactions();
        if (booking == null || transactions == null)
        {
            Crashlytics.log("Either booking or transactions is null in onReceiveBookingDetailsSuccess");
            onReceiveBookingDetailsError(null);
            return;
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(mContainer.getId(),
                BookingTransactionsFragment.newInstance(event.mBookingTransactions)).commit();
    }

    @Subscribe
    public void onReceiveBookingDetailsError(PaymentEvent.ReceiveBookingPaymentDetailsError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (event != null && event.error != null && TextUtils.isNullOrEmpty(event.error.getMessage()))
        {
            mErrorText.setText(event.error.getMessage());
        }
        else
        {
            mErrorText.setText(R.string.error_fetching_connectivity_issue);
        }
        mFetchErrorView.setVisibility(View.VISIBLE);
    }

    private void requestBookingPaymentDetails(String bookingId)
    {
        mFetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new PaymentEvent.RequestBookingPaymentDetails(bookingId));
    }
}
