package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.library.ui.widget.InfiniteScrollListView;
import com.handy.portal.library.util.Utils;
import com.squareup.otto.Bus;

import java.util.Date;

import javax.inject.Inject;

public final class PaymentsBatchListView extends InfiniteScrollListView implements AdapterView.OnItemClickListener
{
    @Inject
    Bus mBus;

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

        footerView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.element_infinite_scrolling_list_footer, null);
        addFooterView(footerView, null, false);
        setAdapter(itemsAdapter);
        setOnItemClickListener(this);
        // Override the StickyListHeaderView not setting these correctly
        ColorDrawable divider = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.list_divider));
        getWrappedList().setDivider(divider);
        getWrappedList().setDividerHeight(1);
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
        final boolean isCurrentWeek = (position == 0);
        mBus.post(new LogEvent.AddLogEvent(new PaymentsLog.BatchSelected(isCurrentWeek, position + 1))); // index needs to be one based
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
        getWrappedAdapter().appendData(paymentBatches, requestStartDate);
    }

    public PaymentBatchListAdapter getWrappedAdapter()
    {
        return (PaymentBatchListAdapter) getAdapter();
    }

    public boolean shouldRequestMoreData()
    {
        return getWrappedAdapter().shouldRequestMoreData();
    }

    public boolean isDataEmpty()
    {
        return getWrappedAdapter().isDataEmpty();
    }

    public Date getNextRequestEndDate()
    {
        return getWrappedAdapter().getNextRequestEndDate();
    }

}
