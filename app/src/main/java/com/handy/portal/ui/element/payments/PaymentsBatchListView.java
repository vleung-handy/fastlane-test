package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.ui.adapter.PaymentBatchListAdapter;

import java.util.Date;

public final class PaymentsBatchListView extends ListView
{

    private PaymentsBatchListHeaderView paymentsBatchListHeaderView;
    private TextView footerView;

    public PaymentsBatchListView(final Context context)
    {
        super(context);
    }

    public PaymentsBatchListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PaymentsBatchListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    public void init()
    {
        PaymentBatchListAdapter itemsAdapter = new PaymentBatchListAdapter(
                getContext(
                ));
        setAdapter(itemsAdapter);
        paymentsBatchListHeaderView = (PaymentsBatchListHeaderView) inflate(getContext(), R.layout.element_payments_batch_current_week_header, null);
        addHeaderView(paymentsBatchListHeaderView);

        footerView = (TextView) inflate(getContext(), R.layout.element_payments_batch_list_footer, null);
        addFooterView(footerView);
    }

    public void setFooterText(int resourceId)
    {
        footerView.setText(resourceId);
    }

    public void setFooterText(String text)
    {
        footerView.setText(text);
    }

    public void setFooterVisible(boolean visible)
    {
        footerView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void clear()
    {
        getWrappedAdapter().clear();
    }

    public void appendData(PaymentBatches paymentBatches, Date requestStartDate)
    {
        boolean needToUpdateHeader = getWrappedAdapter().isEmpty(); //update header if list was initially empty
        getWrappedAdapter().appendData(paymentBatches, requestStartDate);
        if (needToUpdateHeader) paymentsBatchListHeaderView.updateDisplay(paymentBatches);
    }

    private PaymentBatchListAdapter getWrappedAdapter()
    {
        return getAdapter() == null ? null : ((PaymentBatchListAdapter) ((HeaderViewListAdapter) getAdapter()).getWrappedAdapter());
    }

    public boolean shouldRequestMoreData()
    {
        return getWrappedAdapter().shouldRequestMoreData();
    }

    public boolean isEmpty()
    {
        return getWrappedAdapter() == null || getWrappedAdapter().getCount() == 0;
    }

    public Date getOldestDate()
    {
        return getWrappedAdapter().getOldestDate();
    }

}
