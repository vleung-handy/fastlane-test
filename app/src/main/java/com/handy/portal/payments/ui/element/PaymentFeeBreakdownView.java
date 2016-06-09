package com.handy.portal.payments.ui.element;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.payments.model.Payment;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentFeeBreakdownView extends FrameLayout
{
    @Bind(R.id.fee_breakdown_month_text)
    TextView mFeeBreakdownMonthText;
    @Bind(R.id.fee_breakdown_day_text)
    TextView mFeeBreakdownDayText;
    @Bind(R.id.fee_breakdown_reason_text)
    TextView mFeeBreakdownReasonText;
    @Bind(R.id.fee_breakdown_description_text)
    TextView mFeeBreakdownDescriptionText;
    @Bind(R.id.fee_breakdown_amount_text)
    TextView mFeeBreakdownAmountText;

    public PaymentFeeBreakdownView(final Context context)
    {
        super(context);
        init();
    }

    public PaymentFeeBreakdownView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PaymentFeeBreakdownView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaymentFeeBreakdownView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_fee_breakdown, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(Payment payment)
    {
        Date date = payment.getDate();

        mFeeBreakdownMonthText.setText(DateTimeUtils.getMonthShortName(date));
        mFeeBreakdownDayText.setText(String.valueOf(DateTimeUtils.getDayOfMonth(date)));
        mFeeBreakdownReasonText.setText(payment.getTitle());
        mFeeBreakdownDescriptionText.setText(payment.getSubTitle());
        mFeeBreakdownAmountText.setText(CurrencyUtils.formatPriceWithCents(payment.getAmount(), payment.getCurrencySymbol()));
    }
}
