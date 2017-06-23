package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.ui.widget.InfiniteScrollListView;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import javax.inject.Inject;

public final class PaymentsBatchListView extends InfiniteScrollListView implements AdapterView.OnItemClickListener {
    @Inject
    EventBus mBus;

    private TextView footerView;
    private OnDataItemClickListener onDataItemClickListener; //TODO: WIP. refine

    /*
    we need dataItemClick listener because the lists header data is linked to adapter data
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

        footerView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.element_infinite_scrolling_list_footer, null);
        addFooterView(footerView, null, false);
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

    public interface OnDataItemClickListener {
        //PaymentBatch does not denote whether it is the current week batch
        void onDataItemClicked(PaymentBatch paymentBatch, boolean isCurrentWeekBatch);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final boolean isCurrentWeek =
                getWrappedAdapter().getViewTypeForPosition(position)
                        == PaymentBatchListAdapter.VIEW_TYPE_CURRENT_WEEK_BATCH;
        mBus.post(new LogEvent.AddLogEvent(new PaymentsLog.BatchSelected(isCurrentWeek, position + 1))); // index needs to be one based
        PaymentBatch paymentBatch = getWrappedAdapter().getDataItem(position);
        notifyDataItemClickListener(paymentBatch, isCurrentWeek);
    }

    private void notifyDataItemClickListener(PaymentBatch paymentBatch, boolean isCurrentWeekBatch) {
        if (onDataItemClickListener != null) {
            onDataItemClickListener.onDataItemClicked(paymentBatch, isCurrentWeekBatch);
        }
    }

    public void setOnDataItemClickListener(OnDataItemClickListener onDataItemClickListener) {
        this.onDataItemClickListener = onDataItemClickListener;
    }

    public void showFooter(int stringResourceId) {
        setFooterVisible(true);
        setFooterText(stringResourceId);
    }

    public void setFooterText(int resourceId) {
        footerView.setText(resourceId);
    }

    public void setFooterVisible(boolean visible) {
        footerView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void appendData(PaymentBatches paymentBatches, Date requestStartDate) {
        getWrappedAdapter().appendData(paymentBatches, requestStartDate);
        initialRequest = false;
    }

    public PaymentBatchListAdapter getWrappedAdapter() {
        return (PaymentBatchListAdapter) getAdapter();
    }

    boolean initialRequest = true;

    public boolean shouldRequestMoreData() {

        //fixme test only remove

        return initialRequest;
//        return getWrappedAdapter().shouldRequestMoreData();
    }

    public boolean isDataEmpty() {
        return getWrappedAdapter().isDataEmpty();
    }

    public Date getNextRequestEndDate() {
        return getWrappedAdapter().getNextRequestEndDate();
    }

    /**
     * the cash out dialog fragment needs to be launched by a fragment
     * so that callbacks can be properly handled
     */
    public void setCashOutButtonClickListener(OnClickListener cashOutButtonClickedListener)
    {
        getWrappedAdapter().setCashOutButtonClickedListener(cashOutButtonClickedListener);
    }

    public void setDailyCashOutListeners(CompoundButton.OnCheckedChangeListener onCheckedChangeListener,
                                         OnClickListener onClickListener) {
        getWrappedAdapter().setDailyCashOutListeners(onCheckedChangeListener, onClickListener);
    }
}
