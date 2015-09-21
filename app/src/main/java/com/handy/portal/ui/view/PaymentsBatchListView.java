package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.handy.portal.R;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.ui.adapter.PaymentBatchElementAdapter;
import com.handy.portal.ui.element.payments.PaymentsBatchListHeaderView;

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
        PaymentBatchElementAdapter itemsAdapter = new PaymentBatchElementAdapter(
                getContext(
                ));
        setAdapter(itemsAdapter);
        paymentsBatchListHeaderView = (PaymentsBatchListHeaderView) inflate(getContext(), R.layout.element_payments_current_week_header, null);
        addHeaderView(paymentsBatchListHeaderView);

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//    {
//        int heightMeasureSpec_custom = View.MeasureSpec.makeMeasureSpec(
//                Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
//        ViewGroup.LayoutParams params = getLayoutParams();
//        params.height = getMeasuredHeight();
//    }
    public void updateData(PaymentBatches paymentBatches)
    {
        ((PaymentBatchElementAdapter)((HeaderViewListAdapter)getAdapter()).getWrappedAdapter()).setData(paymentBatches);
        paymentsBatchListHeaderView.updateDisplay(paymentBatches);
    }
}
