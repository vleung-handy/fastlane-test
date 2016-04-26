package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.handy.portal.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BookingResultBannerTextView extends TextView
{
    public static final int COMPLETED_JOB = 0;
    public static final int CUSTOMER_NO_SHOW = 1;
    public static final int CUSTOMER_CANCELLED = 2;
    public static final int CANCELLED_ON_CUSTOMER = 3;


    //Define the list of accepted constants
    @IntDef({COMPLETED_JOB, CUSTOMER_NO_SHOW, CUSTOMER_CANCELLED, CANCELLED_ON_CUSTOMER})
    //Tell the compiler not to store annotation data in the .class file
    @Retention(RetentionPolicy.SOURCE)
    @interface BookingResult {}

    public BookingResultBannerTextView(final Context context)
    {
        super(context);
    }

    public BookingResultBannerTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingResultBannerTextView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingResultBannerTextView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDisplay(@BookingResult int bookingResult)
    {
        Drawable icon = null;
        switch (bookingResult)
        {
            case COMPLETED_JOB:
                setText(R.string.completed_job_2);
                setTextColor(ContextCompat.getColor(getContext(), R.color.cleaner_green));
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cleaner_green_trans_10));
                icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green, null);
                break;
            case CUSTOMER_NO_SHOW:
                setText(R.string.customer_no_show);
                setTextColor(ContextCompat.getColor(getContext(), R.color.electrician_yellow));
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.electrician_yellow_trans_10));
                icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_exclaimation_yellow, null);
                break;
            case CUSTOMER_CANCELLED:
                setText(R.string.customer_cancelled);
                setTextColor(ContextCompat.getColor(getContext(), R.color.electrician_yellow));
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.electrician_yellow_trans_10));
                icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_exclaimation_yellow, null);
                break;
            case CANCELLED_ON_CUSTOMER:
                setText(R.string.cancelled_on_customer);
                setTextColor(ContextCompat.getColor(getContext(), R.color.plumber_red));
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.plumber_red_trans_10));
                icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_exclaimation_red, null);
                break;
        }

        if (icon != null)
        {
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
            setCompoundDrawables(icon, null, null, null);
        }
    }
}
