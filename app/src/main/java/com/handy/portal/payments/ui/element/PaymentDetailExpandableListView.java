package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.ui.adapter.PaymentDetailExpandableListAdapter;
import com.handy.portal.payments.viewmodel.PaymentDetailHeaderViewModel;

public class PaymentDetailExpandableListView extends ExpandableListView {
    /**
     * to be initialized here and added as the header view so it can scroll along
     */
    private PaymentsDetailListHeaderView mPaymentsDetailListHeaderView;

    /**
     * to be initialized here and added as the footer view so it can scroll along
     */
    private Button mPaymentSupportButton;

    public PaymentDetailExpandableListView(final Context context) {
        super(context);
        init();
    }

    public PaymentDetailExpandableListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaymentDetailExpandableListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        mPaymentsDetailListHeaderView = (PaymentsDetailListHeaderView)
                inflate(getContext(), R.layout.element_payment_details_list_header, null);
        addHeaderView(mPaymentsDetailListHeaderView);

        mPaymentSupportButton = (Button) inflate(getContext(),
                R.layout.element_payment_support_button, null);
        //hacky - need to wrap payment support inside this layout to set margins because AbsList.LayoutParams doesn't have margins
        LinearLayout expandableListFooterView = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        int defaultMargin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        int defaultMarginHalf = getResources().getDimensionPixelSize(R.dimen.default_margin_half);
        layoutParams.setMargins(defaultMargin, defaultMarginHalf, defaultMargin, defaultMargin);
        mPaymentSupportButton.setLayoutParams(layoutParams);
        expandableListFooterView.addView(mPaymentSupportButton);
        addFooterView(expandableListFooterView);
    }

    @NonNull
    public PaymentsDetailListHeaderView getHeaderView() {
        return mPaymentsDetailListHeaderView;
    }

    @NonNull
    public Button getPaymentSupportButton() {
        return mPaymentSupportButton;
    }

    public void updateData(@NonNull NeoPaymentBatch neoPaymentBatch, boolean cashOutButtonVisible) {
        PaymentDetailExpandableListAdapter itemsAdapter = new PaymentDetailExpandableListAdapter(
                neoPaymentBatch);
        setAdapter(itemsAdapter);

        PaymentDetailHeaderViewModel paymentDetailHeaderViewModel
                = new PaymentDetailHeaderViewModel(neoPaymentBatch, cashOutButtonVisible);
        mPaymentsDetailListHeaderView.updateDisplay(paymentDetailHeaderViewModel);
        /*
        the visibility of the payment support button is not updated here
        because it depends on things this view is not responsible for (the callback)
         */
    }
}
