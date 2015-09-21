package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.Payment;
import com.handy.portal.ui.element.payments.PaymentsDetailListHeaderView;
import com.handy.portal.ui.element.payments.PaymentDetailExpandableListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PaymentsDetailFragment extends ActionBarFragment implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener
{
    @InjectView(R.id.payments_detail_list_view)
    PaymentDetailExpandableListView paymentDetailExpandableListView; //using ExpandableListView because it is the only ListView that offers group view support

    @InjectView(R.id.payment_details_list_header)
    PaymentsDetailListHeaderView paymentsDetailListHeaderView;

    NeoPaymentBatch neoPaymentBatch;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            this.neoPaymentBatch = (NeoPaymentBatch) getArguments().getSerializable(BundleKeys.PAYMENT_BATCH);
        }
        else
        {
            Crashlytics.logException(new Exception("Null arguments for class " + this.getClass().getName()));
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = inflater
                .inflate(R.layout.fragment_payments_detail, container, false);

        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        paymentDetailExpandableListView.setOnGroupClickListener(this);
        paymentDetailExpandableListView.setOnChildClickListener(this);
        paymentsDetailListHeaderView.updateDisplay(neoPaymentBatch);
        paymentDetailExpandableListView.updateData(neoPaymentBatch);
        for (int i = 0; i < neoPaymentBatch.getPaymentGroups().length; i++)
        {
            paymentDetailExpandableListView.expandGroup(i);

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.payments_details, true);//TODO: change to resource id
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
    {
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
    {
        Payment payment = (Payment) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
        String bookingId = payment.getBookingId();
        String bookingType = payment.getBookingType() != null ? payment.getBookingType().toUpperCase() : Booking.BookingType.BOOKING.toString();
        if (bookingId != null)
        {
            showBookingDetails(bookingId, bookingType);
        }
        return true;
    }

    private void showBookingDetails(String bookingId, String bookingType)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, bookingId);
        arguments.putString(BundleKeys.BOOKING_TYPE, bookingType);
        HandyEvent.NavigateToTab event = new HandyEvent.NavigateToTab(MainViewTab.DETAILS, arguments);
        bus.post(event);
    }
}
