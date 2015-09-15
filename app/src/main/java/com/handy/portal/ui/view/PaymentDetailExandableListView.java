package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.ui.adapter.PaymentDetailExpandableListAdapter;

public class PaymentDetailExandableListView extends ExpandableListView
{

    public PaymentDetailExandableListView(final Context context)
    {
        super(context);
    }

    public PaymentDetailExandableListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PaymentDetailExandableListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void populateList(NeoPaymentBatch neoPaymentBatch)
    {
        PaymentDetailExpandableListAdapter itemsAdapter = new PaymentDetailExpandableListAdapter(
                neoPaymentBatch);
        setAdapter(itemsAdapter);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

}
