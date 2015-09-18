package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.ui.adapter.PaymentDetailExpandableListAdapter;
import com.handy.portal.ui.element.payments.PaymentsDetailListHeaderView;

public class PaymentDetailExandableListView extends ExpandableListView
{

    PaymentsDetailListHeaderView headerView;

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

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        //TODO: initialize adapter
//        headerView = (PaymentsDetailHeaderView)inflate(getContext(), R.layout.element_payment_details_list_header, null);
//        this.addHeaderView(headerView);
//        setAdapter(new PaymentDetailExpandableListAdapter());
    }


    public void updateData(NeoPaymentBatch neoPaymentBatch)//TODO: make adapter init separate
    {
//        headerView.updateDisplay(neoPaymentBatch);
//        ((PaymentDetailExpandableListAdapter) getAdapter()).setData(neoPaymentBatch);
        PaymentDetailExpandableListAdapter itemsAdapter = new PaymentDetailExpandableListAdapter(
                neoPaymentBatch);
        setAdapter(itemsAdapter);


    }
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//    {
//        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
//                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
//        ViewGroup.LayoutParams params = getLayoutParams();
//        params.height = getMeasuredHeight();
//    }

}
