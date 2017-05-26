package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.model.BookingTransactions;
import com.handy.portal.payments.model.Transaction;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingTransactionsWrapperFragment extends ActionBarFragment {
    @BindView(R.id.container)
    ViewGroup mContainer;
    @BindView(R.id.fetch_error_view)
    View mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mErrorText;

    @Inject
    PaymentsManager mPaymentsManager;

    private String mRequestedBookingId;
    private String mRequestedBookingType;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);

        Bundle arguments = getArguments();
        if (arguments == null) { return; }

        mRequestedBookingId = arguments.getString(BundleKeys.BOOKING_ID);
        mRequestedBookingType = arguments.getString(BundleKeys.BOOKING_TYPE);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_container, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestBookingPaymentDetails();
    }

    @OnClick(R.id.try_again_button)
    public void onClickRequestDetails() {
        requestBookingPaymentDetails();
    }

    private void onReceiveBookingDetailsSuccess(@NonNull BookingTransactions bookingTransactions) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Booking booking = bookingTransactions.getBooking();
        Transaction[] transactions = bookingTransactions.getTransactions();
        if (booking == null || transactions == null) {
            Crashlytics.log("Either booking or transactions is null in onReceiveBookingDetailsSuccess");
            onReceiveBookingDetailsError(null);
            return;
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(mContainer.getId(),
                BookingTransactionsFragment.newInstance(bookingTransactions)).commit();
    }

    private void onReceiveBookingDetailsError(@NonNull DataManager.DataManagerError error) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (!TextUtils.isNullOrEmpty(error.getMessage())) {
            mErrorText.setText(error.getMessage());
        }
        else {
            mErrorText.setText(R.string.error_fetching_connectivity_issue);
        }
        mFetchErrorView.setVisibility(View.VISIBLE);
    }

    private void requestBookingPaymentDetails() {
        mFetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));

        mPaymentsManager.onRequestBookingPaymentDetails(
                mRequestedBookingId,
                mRequestedBookingType,
                new FragmentSafeCallback<BookingTransactions>(this) {
                    @Override
                    public void onCallbackSuccess(BookingTransactions response) {
                        onReceiveBookingDetailsSuccess(response);
                    }

                    @Override
                    public void onCallbackError(DataManager.DataManagerError error) {
                        onReceiveBookingDetailsError(error);
                    }
                }
        );
    }
}
