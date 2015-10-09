package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.PaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.ui.widget.InfiniteScrollListView;

import java.util.Date;

public final class PaymentsBatchListView extends InfiniteScrollListView implements AdapterView.OnItemClickListener
{

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
