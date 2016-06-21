package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.Payment;
import com.handy.portal.payments.model.PaymentGroup;
import com.handy.portal.payments.ui.element.PaymentDetailExpandableListView;
import com.handy.portal.payments.ui.element.PaymentsDetailListHeaderView;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class PaymentsDetailFragment extends ActionBarFragment implements ExpandableListView.OnChildClickListener
{
    @Bind(R.id.payments_detail_list_view)
    PaymentDetailExpandableListView paymentDetailExpandableListView; //using ExpandableListView because it is the only ListView that offers group view support
    @Bind(R.id.payment_details_list_header)
    PaymentsDetailListHeaderView paymentsDetailListHeaderView;

    private NeoPaymentBatch neoPaymentBatch;
    private View fragmentView;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.PAYMENTS;
    }

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
        if (fragmentView == null)
        {
            fragmentView = inflater
                    .inflate(R.layout.fragment_payments_detail, container, false);
        }
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
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
        setActionBar(R.string.payments_details, true);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
    {
        final ExpandableListAdapter parentListAdapter = parent.getExpandableListAdapter();

        final PaymentGroup paymentGroup = (PaymentGroup) parentListAdapter.getGroup(groupPosition);
        bus.post(new LogEvent.AddLogEvent(
                new PaymentsLog.DetailSelected(paymentGroup.getMachineName())));

        final Payment payment = (Payment) parentListAdapter.getChild(groupPosition, childPosition);

        if (payment.getBookingId() == null || payment.getBookingType() == null) { return false; }

        showBookingDetails(payment.getBookingId(), payment.getBookingType());
        return true;
    }

    private void showBookingDetails(String bookingId, String bookingType)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, bookingId);
        arguments.putString(BundleKeys.BOOKING_TYPE, bookingType);
        NavigationEvent.NavigateToPage event =
                new NavigationEvent.NavigateToPage(MainViewPage.JOB_PAYMENT_DETAILS, arguments, true);
        bus.post(event);
    }
}
