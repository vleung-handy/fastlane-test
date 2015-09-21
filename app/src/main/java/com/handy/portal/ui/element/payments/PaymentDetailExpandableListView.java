package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.ui.adapter.PaymentDetailExpandableListAdapter;

public class PaymentDetailExpandableListView extends ExpandableListView
{

    public PaymentDetailExpandableListView(final Context context)
    {
        super(context);
    }

    public PaymentDetailExpandableListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PaymentDetailExpandableListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    public void updateData(NeoPaymentBatch neoPaymentBatch)
    {
        PaymentDetailExpandableListAdapter itemsAdapter = new PaymentDetailExpandableListAdapter(
                neoPaymentBatch);
        setAdapter(itemsAdapter);


    }

}
