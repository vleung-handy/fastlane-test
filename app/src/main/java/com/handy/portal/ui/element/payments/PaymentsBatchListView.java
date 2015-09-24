package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HeaderViewListAdapter;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.ui.widget.InfiniteScrollListView;

import java.util.Date;

public final class PaymentsBatchListView extends InfiniteScrollListView
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

    public void setHeaderViewClickListener(OnClickListener onClickListener) //TODO: not ideal. refactor this
    {
        paymentsBatchListHeaderView.setOnClickListener(onClickListener);
    }

    public void init()
    {
        PaymentBatchListAdapter itemsAdapter = new PaymentBatchListAdapter(
                getContext(
                ));
        setAdapter(itemsAdapter);
        paymentsBatchListHeaderView = (PaymentsBatchListHeaderView) inflate(getContext(), R.layout.element_payments_batch_list_current_week_header, null);
        addHeaderView(paymentsBatchListHeaderView);

        footerView = (TextView) inflate(getContext(), R.layout.element_payments_batch_list_footer, null);
        addFooterView(footerView);
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
        if(getWrappedAdapter().isDataEmpty())
        {
            paymentsBatchListHeaderView.updateDisplay(paymentBatches);
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

    public Date getOldestDate()
    {
        return getWrappedAdapter().getOldestDate();
    }

}
