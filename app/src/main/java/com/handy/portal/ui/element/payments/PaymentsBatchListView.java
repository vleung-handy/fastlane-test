package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.ui.widget.InfiniteScrollListView;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import java.util.Date;

import javax.inject.Inject;

public final class PaymentsBatchListView extends InfiniteScrollListView implements AdapterView.OnItemClickListener
{
    @Inject
    Bus mBus;
    @Inject
    EventLogFactory mEventLogFactory;

    private PaymentsBatchListHeaderView paymentsBatchListHeaderView;
    private TextView footerView;
    private OnDataItemClickListener onDataItemClickListener; //TODO: WIP. refine

    /*
    we need dataItemClick listener because the lists header data is linked to adapter data
    should set OnDataItemClickListener instead of OnItemClickListener
     */

    public PaymentsBatchListView(final Context context)
    {
        super(context);
        Utils.inject(context, this);

    }

    public PaymentsBatchListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        Utils.inject(context, this);
    }

    public PaymentsBatchListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        Utils.inject(context, this);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    public void init()
    {
        PaymentBatchListAdapter itemsAdapter = new PaymentBatchListAdapter(getContext());
        paymentsBatchListHeaderView = (PaymentsBatchListHeaderView) LayoutInflater.from(getContext()).inflate(R.layout.element_payments_batch_list_current_week_header, null);
        addHeaderView(paymentsBatchListHeaderView);

        footerView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.element_payments_batch_list_footer, null);
        addFooterView(footerView, null, false);

        setAdapter(itemsAdapter);

        setOnItemClickListener(this);
        paymentsBatchListHeaderView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!getWrappedAdapter().isDataEmpty())
                {
                    notifyDataItemClickListener(getWrappedAdapter().getDataItem(0));
                    mBus.post(new LogEvent.AddLogEvent(
                            mEventLogFactory.createPaymentBatchSelectedLog(true, 1)));
                }
            }
        });
    }

    public void clear()
    {
        getWrappedAdapter().clear();
    }

    public interface OnDataItemClickListener
    { //TODO: put this somewhere else and make type generic?
        void onDataItemClicked(PaymentBatch paymentBatch);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        mBus.post(new LogEvent.AddLogEvent(mEventLogFactory.createPaymentBatchSelectedLog(false,
                position + 1))); // index needs to be one based
        PaymentBatch paymentBatch = getWrappedAdapter().getDataItem(position);
        notifyDataItemClickListener(paymentBatch);
    }

    private void notifyDataItemClickListener(PaymentBatch paymentBatch)
    {
        if (onDataItemClickListener != null)
        {
            onDataItemClickListener.onDataItemClicked(paymentBatch);
        }
    }

    public void setOnDataItemClickListener(OnDataItemClickListener onDataItemClickListener)
    {
        this.onDataItemClickListener = onDataItemClickListener;
    }

    public void showFooter(int stringResourceId)
    {
        setFooterVisible(true);
        setFooterText(stringResourceId);
    }

    public void setFooterText(int resourceId)
    {
        footerView.setText(resourceId);
    }

    public void setFooterVisible(boolean visible)
    {
        footerView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void appendData(PaymentBatches paymentBatches, Date requestStartDate)
    {
        if (getWrappedAdapter().isDataEmpty())
        {
            if (paymentBatches.getNeoPaymentBatches().length == 0)
            {
                Crashlytics.logException(new Exception("No non-legacy payment batches received! Expecting at least one (first entry should be the current week's payment batch)"));
                return;
            }
            NeoPaymentBatch neoPaymentBatch = paymentBatches.getNeoPaymentBatches()[0];
            // Set Header List View
            paymentsBatchListHeaderView.updateDisplay(neoPaymentBatch);
            Integer year = DateTimeUtils.getYearInt(neoPaymentBatch.getEndDate());
            getWrappedAdapter().setCurrentYear(year);
        }
        getWrappedAdapter().appendData(paymentBatches, requestStartDate);
    }

    public PaymentBatchListAdapter getWrappedAdapter()
    {
        return getAdapter() == null ? null : ((PaymentBatchListAdapter) ((HeaderViewListAdapter) getAdapter()).getWrappedAdapter());
    }

    public boolean shouldRequestMoreData()
    {
        return getWrappedAdapter().shouldRequestMoreData();
    }

    public boolean isDataEmpty()
    {
        return getWrappedAdapter() == null || getWrappedAdapter().isDataEmpty();
    }

    public Date getNextRequestEndDate()
    {
        return getWrappedAdapter().getNextRequestEndDate();
    }

}
