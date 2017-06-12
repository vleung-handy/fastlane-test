package com.handy.portal.payments.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.ui.element.PaymentsBatchListHeaderView;
import com.handy.portal.payments.ui.element.PaymentsBatchListItemView;
import com.handy.portal.payments.viewmodel.PaymentBatchListHeaderViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class PaymentBatchListAdapter extends ArrayAdapter<PaymentBatch> implements StickyListHeadersAdapter //TODO: THIS IS GROSS, NEED TO REFACTOR THIS COMPLETELY!
{
    @Inject
    EventBus mBus;

    @Inject
    ConfigManager mConfigManager;

    public static final int DAYS_TO_REQUEST_PER_BATCH = 28;
    private Date nextRequestEndDate;
    private View.OnClickListener mOnCashOutButtonClickedListener;

    //TODO: we don't need to keep track of oldest date when we can use new pagination API that allows us to get the N next batches

    public PaymentBatchListAdapter(Context context) {
        super(context, R.layout.element_payments_batch_list_entry, 0);
        Utils.inject(context, this);
        resetMetadata();
    }

    private void resetMetadata() {
        nextRequestEndDate = new Date();
    }

    public void clear() {
        resetMetadata();
        super.clear();
    }

    public boolean shouldRequestMoreData() {
        return nextRequestEndDate != null;
    }

    public Date getNextRequestEndDate() {
        return nextRequestEndDate;
    }

    public void appendData(PaymentBatches paymentBatches, Date requestStartDate) //this should also be called if paymentBatch is empty
    {
        addAll(paymentBatches.getAggregateBatchList());
        updateOldestDate(requestStartDate);
        notifyDataSetChanged();
    }

    public boolean canAppendBatch(Date batchRequestEndDate) //TODO: do something more elegant
    {
        return nextRequestEndDate != null && batchRequestEndDate.equals(nextRequestEndDate); //compares the exact time
    }

    private void updateOldestDate(Date requestStartDate) {
        if (nextRequestEndDate != null) {
            final Calendar lowerBoundPaymentRequestDate = Calendar.getInstance();
            lowerBoundPaymentRequestDate.set(2013, 9, 23); // No payments precede Oct 23, 2013

            Date newDate = new Date(requestStartDate.getTime() - 1);
            Calendar newDateCalendar = Calendar.getInstance();
            newDateCalendar.setTime(requestStartDate);

            nextRequestEndDate = newDateCalendar.before(lowerBoundPaymentRequestDate)
                    ? null : newDate;
        }
    }

    @Override
    public boolean areAllItemsEnabled() //supposed to fix (only in Android <5.0) issue in which dividers for disabled items are invisible
    {
        return true;
    }

    public boolean isDataEmpty() //check if underlying data is empty (this counts ones not displayed)
    {
        return getDataItemsCount() == 0;
    }

    public int getDataItemsCount() //get count of underlying data objects, not view
    {
        return super.getCount();
    }

    public PaymentBatch getDataItem(int position) {
        return super.getItem(position);
    }

    @Override
    public boolean isEnabled(int position) {
        // Setting disabled state via setEnabled(false)
        return true;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * the cash out dialog fragment needs to be launched by a fragment
     * so that callbacks can be properly handled
     * @param onClickListener
     */
    public void setCashOutButtonClickedListener(View.OnClickListener onClickListener)
    {
        mOnCashOutButtonClickedListener = onClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        PaymentBatch paymentBatch = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (position == 0 && paymentBatch instanceof NeoPaymentBatch) {
            if (convertView == null || !(convertView instanceof PaymentsBatchListHeaderView)) {
                v = inflater.inflate(R.layout.element_payments_batch_list_current_week_header, parent, false);
                v.findViewById(R.id.payments_current_week_remaining_fees_row).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mBus.post(new NavigationEvent.NavigateToPage(MainViewPage.OUTSTANDING_FEES, true));
                        mBus.post(new LogEvent.AddLogEvent(new PaymentsLog.FeeDetailSelected()));
                    }
                });
            }
            else {
                v = convertView;
            }

            PaymentsBatchListHeaderView paymentsBatchListHeaderView
                    = ((PaymentsBatchListHeaderView) v);

            PaymentBatchListHeaderViewModel paymentBatchListHeaderViewModel
                    = new PaymentBatchListHeaderViewModel((NeoPaymentBatch) paymentBatch,
                    mConfigManager.getConfigurationResponse().isDailyProPaymentsEnabled());

            paymentsBatchListHeaderView.updateDisplay(paymentBatchListHeaderViewModel);

            mBus.post(new LogEvent.AddLogEvent(new PaymentsLog.PageShown(
                    paymentBatchListHeaderViewModel.shouldShowCashOutButton(),
                    paymentBatchListHeaderViewModel.shouldEnableCashOutButton()
                    )));

            paymentsBatchListHeaderView.setOnCashOutButtonClickedListener(mOnCashOutButtonClickedListener);
        }
        else {
            if (convertView == null || !(convertView instanceof PaymentsBatchListItemView)) {
                v = inflater.inflate(R.layout.element_payments_batch_list_entry, parent, false);
            }
            else {
                v = convertView;
            }

            ((PaymentsBatchListItemView) v).updateDisplay(paymentBatch);
        }

        return v;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        View v;
        PaymentBatch paymentBatch = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (convertView == null) {
            v = inflater.inflate(R.layout.element_payment_list_section_header, parent, false);
        }
        else {
            v = convertView;
        }

        String year = DateTimeUtils.getYear(paymentBatch.getEffectiveDate());
        ((TextView) v.findViewById(R.id.payment_list_section_header_text)).setText(year);

        return v;
    }

    @Override
    public long getHeaderId(int position) {
        PaymentBatch paymentBatch = getItem(position);
        return DateTimeUtils.getYearInt(paymentBatch.getEffectiveDate());
    }
}
