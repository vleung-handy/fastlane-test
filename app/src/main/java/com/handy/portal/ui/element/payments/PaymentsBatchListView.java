package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.handy.portal.R;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.ui.adapter.PaymentBatchListElementAdapter;

public final class PaymentsBatchListView extends ListView
{

    private PaymentsBatchListHeaderView paymentsBatchListHeaderView;

    public PaymentsBatchListView(final Context context)
    {
        super(context);
        init();
    }

    public PaymentsBatchListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PaymentsBatchListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public void init()
    {
        PaymentBatchListElementAdapter itemsAdapter = new PaymentBatchListElementAdapter(
                getContext(
                ));
        setAdapter(itemsAdapter);
        paymentsBatchListHeaderView = (PaymentsBatchListHeaderView) inflate(getContext(), R.layout.element_payments_current_week_header, null);
        addHeaderView(paymentsBatchListHeaderView);

    }

    public void updateData(PaymentBatches paymentBatches)
    {
        ((PaymentBatchListElementAdapter) ((HeaderViewListAdapter) getAdapter()).getWrappedAdapter()).setData(paymentBatches);
        paymentsBatchListHeaderView.updateDisplay(paymentBatches);
    }
}
