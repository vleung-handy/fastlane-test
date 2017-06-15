package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.ui.widget.InfiniteScrollListView;
import com.handy.portal.library.util.Utils;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;

public final class PaymentsBatchListView extends InfiniteScrollListView implements AdapterView.OnItemClickListener {

    private TextView mFooterView;
    private OnDataItemClickListener mOnDataItemClickListener;

    /*
    we need dataItemClick listener because the lists header data is linked to adapter data.
    should set OnDataItemClickListener instead of OnItemClickListener
     */

    public PaymentsBatchListView(final Context context) {
        super(context);
        Utils.inject(context, this);

    }

    public PaymentsBatchListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        Utils.inject(context, this);
    }

    public PaymentsBatchListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        Utils.inject(context, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void init() {
        PaymentBatchListAdapter itemsAdapter = new PaymentBatchListAdapter(getContext());

        mFooterView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.element_infinite_scrolling_list_footer, null);
        addFooterView(mFooterView, null, false);
        setAdapter(itemsAdapter);
        setOnItemClickListener(this);
        // Override the StickyListHeaderView not setting these correctly
        ColorDrawable divider = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.list_divider));
        getWrappedList().setDivider(divider);
        getWrappedList().setDividerHeight(1);
    }

    public void clear() {
        getWrappedAdapter().clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final boolean isCurrentWeek =
                getWrappedAdapter().getViewTypeForPosition(position)
                        == PaymentBatchListAdapter.VIEW_TYPE_CURRENT_WEEK_BATCH;
        PaymentBatch paymentBatch = getWrappedAdapter().getDataItem(position);
        notifyDataItemClickListener(paymentBatch, isCurrentWeek, position);
    }

    private void notifyDataItemClickListener(PaymentBatch paymentBatch,
                                             boolean isCurrentWeekBatch,
                                             int listIndex) {
        if (mOnDataItemClickListener != null) {
            mOnDataItemClickListener.onDataItemClicked(paymentBatch, isCurrentWeekBatch, listIndex);
        }
    }

    public void setOnDataItemClickListener(OnDataItemClickListener onDataItemClickListener) {
        this.mOnDataItemClickListener = onDataItemClickListener;
    }

    public void showFooter(int stringResourceId) {
        showFooter(stringResourceId, null);
    }

    public void showFooter(int stringResourceId,
                           @Nullable OnClickListener onClickListener) {
        setFooterVisible(true);
        mFooterView.setText(stringResourceId);
        mFooterView.setOnClickListener(onClickListener);
    }

    public void setFooterVisible(boolean visible) {
        mFooterView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void appendData(@NonNull PaymentBatches paymentBatches) {
        getWrappedAdapter().appendData(paymentBatches);
    }

    @NonNull
    public PaymentBatchListAdapter getWrappedAdapter() {
        return (PaymentBatchListAdapter) getAdapter();
    }

    /**
     * the cash out dialog fragment needs to be launched by a fragment
     * so that callbacks can be properly handled
     */
    public void setCashOutButtonClickListener(OnClickListener cashOutButtonClickedListener)
    {
        getWrappedAdapter().setCashOutButtonClickedListener(cashOutButtonClickedListener);
    }

    public interface OnDataItemClickListener {
        /**
         * @param paymentBatch
         * @param isCurrentWeekBatch needed because the given PaymentBatch does not denote
         *                           whether it is the current week batch
         * @param listIndex          currently used for logging purposes only
         */
        void onDataItemClicked(PaymentBatch paymentBatch,
                               boolean isCurrentWeekBatch,
                               int listIndex);
    }
}
