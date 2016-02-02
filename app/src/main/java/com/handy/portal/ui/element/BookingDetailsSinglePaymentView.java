package com.handy.portal.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailsSinglePaymentView extends FrameLayout
{
    @Bind(R.id.booking_entry_payment_text)
    TextView mDollars;
    @Bind(R.id.booking_entry_cents_text)
    TextView mCents;


    public enum Size
    {
        SMALL, MEDIUM
    }

    public BookingDetailsSinglePaymentView(final Context context)
    {
        super(context);
    }

    public BookingDetailsSinglePaymentView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsSinglePaymentView(final Context context, final AttributeSet attrs,
                                           final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingDetailsSinglePaymentView(final Context context, final AttributeSet attrs,
                                           final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(final PaymentInfo paymentInfo, final Size size)
    {
        removeAllViews();
        final int layoutId = size == Size.SMALL ? R.layout.element_payment_amount_small :
                R.layout.element_payment_amount;
        final View view =
                LayoutInflater.from(getContext()).inflate(layoutId, this);
        ButterKnife.bind(this, view);
        UIUtils.setPaymentInfo(mDollars, mCents, paymentInfo,
                getContext().getString(R.string.payment_value));
    }
}
