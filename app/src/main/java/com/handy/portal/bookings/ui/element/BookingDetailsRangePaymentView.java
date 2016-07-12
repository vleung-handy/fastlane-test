package com.handy.portal.bookings.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.payments.model.PaymentInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingDetailsRangePaymentView extends FrameLayout
{
    @BindView(R.id.minimum_payment)
    BookingDetailsSinglePaymentView mMinimumPayment;
    @BindView(R.id.maximum_payment)
    BookingDetailsSinglePaymentView mMaximumPayment;

    public BookingDetailsRangePaymentView(final Context context)
    {
        super(context);
    }

    public BookingDetailsRangePaymentView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsRangePaymentView(final Context context, final AttributeSet attrs,
                                          final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingDetailsRangePaymentView(final Context context, final AttributeSet attrs,
                                          final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(final PaymentInfo minimumPaymentInfo, final PaymentInfo maximumPaymentInfo)
    {
        removeAllViews();
        final View view =
                LayoutInflater.from(getContext()).inflate(R.layout.element_payment_range, this);
        ButterKnife.bind(this, view);
        mMinimumPayment.init(minimumPaymentInfo, BookingDetailsSinglePaymentView.Size.SMALL);
        mMaximumPayment.init(maximumPaymentInfo, BookingDetailsSinglePaymentView.Size.SMALL);
    }
}
