package com.handy.portal.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.ui.element.BookingDetailsSinglePaymentView.Size;

public class BookingDetailsPaymentView extends FrameLayout
{
    public BookingDetailsPaymentView(final Context context)
    {
        super(context);
    }

    public BookingDetailsPaymentView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsPaymentView(final Context context, final AttributeSet attrs,
                                     final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingDetailsPaymentView(final Context context, final AttributeSet attrs,
                                     final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(final Booking booking)
    {
        removeAllViews();
        float minimumHours = booking.getMinimumHours();
        float hours = booking.getHours();
        if (minimumHours > 0 && minimumHours < hours)
        {
            addBookingDetailsRangePaymentView(booking.getHourlyRate(), minimumHours, hours);
        }
        else
        {
            final BookingDetailsSinglePaymentView view =
                    new BookingDetailsSinglePaymentView(getContext());
            view.init(booking.getPaymentToProvider(), Size.MEDIUM);
            addView(view);
        }
    }

    @NonNull
    private void addBookingDetailsRangePaymentView(
            final PaymentInfo hourlyRate, final float minimumHours, final float hours)
    {
        final BookingDetailsRangePaymentView view =
                new BookingDetailsRangePaymentView(getContext());
        final PaymentInfo minimumPaymentInfo = new PaymentInfo.Builder()
                .withAmount((int) (hourlyRate.getAmount() * minimumHours))
                .withCurrencySymbol(hourlyRate.getCurrencySymbol())
                .build();
        final PaymentInfo maximumPaymentInfo = new PaymentInfo.Builder()
                .withAmount((int) (hourlyRate.getAmount() * hours))
                .withCurrencySymbol(hourlyRate.getCurrencySymbol())
                .build();
        view.init(minimumPaymentInfo, maximumPaymentInfo);
        addView(view);
    }
}
