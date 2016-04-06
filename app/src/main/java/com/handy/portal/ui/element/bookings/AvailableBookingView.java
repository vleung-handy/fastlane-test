package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.handy.portal.R;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.fragment.booking.BookingMapFragment;
import com.handy.portal.ui.view.InjectedBusView;
import com.handy.portal.ui.view.MapPlaceholderView;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AvailableBookingView extends InjectedBusView
{
    @Inject
    PrefsManager mPrefsManager;
    @Bind(R.id.map_view)
    ViewGroup mMapView;
    @Bind(R.id.job_location_text)
    TextView mJobLocationText;
    @Bind(R.id.job_date_text)
    TextView mJobDateText;
    @Bind(R.id.job_time_text)
    TextView mJobTimeText;
    @Bind(R.id.job_payment_text)
    TextView mJobPaymentText;
    @Bind(R.id.job_payment_bonus_text)
    TextView mJobPaymentBonusText;
    @Bind(R.id.job_number_text)
    TextView mJobNumberText;

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;

    public AvailableBookingView(final Context context, @NonNull Booking booking, String source,
                                Bundle sourceExtras)
    {
        super(context);
        init();
        setBooking(booking, source, sourceExtras);
    }

    public AvailableBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public AvailableBookingView(final Context context, final AttributeSet attrs,
                                final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AvailableBookingView(final Context context, final AttributeSet attrs,
                                final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setBooking(@NonNull Booking booking, String source, Bundle sourceExtras)
    {
        mBooking = booking;
        mSource = source;
        mSourceExtras = sourceExtras;

        initMapLayout();

        mJobLocationText.setText(mBooking.getLocationName());
        Address address = mBooking.getAddress();
        if (address != null)
        {
            mJobLocationText.setText(mBooking.getAddress().getShortRegion());
        }

        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        String formattedDate = DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER.format(startDate);
        String formattedTime = DateTimeUtils.formatDateTo12HourClock(startDate) + " "
                + getResources().getString(R.string.dash) + " "
                + DateTimeUtils.formatDateTo12HourClock(endDate);

        mJobDateText.setText(getPrependByStartDate(startDate) + formattedDate);
        mJobTimeText.setText(formattedTime.toUpperCase());
        mJobNumberText.setText(getResources().getString(R.string.job_number_formatted, mBooking.getId()));

        PaymentInfo paymentInfo = mBooking.getPaymentToProvider();
        if (paymentInfo != null)
        {
            String paymentText = paymentInfo.getCurrencySymbol() +
                    TextUtils.DECIMAL_FORMAT_TWO_ZERO.format(paymentInfo.getAdjustedAmount());
            mJobPaymentText.setText(paymentText);
        }

        PaymentInfo bonusInfo = mBooking.getBonusPaymentToProvider();
        if (bonusInfo != null && bonusInfo.getAdjustedAmount() > 0)
        {
            String bonusText = getResources().getString(R.string.bonus_payment_value,
                    bonusInfo.getCurrencySymbol() +
                            TextUtils.DECIMAL_FORMAT_TWO_ZERO.format(bonusInfo.getAdjustedAmount()));
            mJobPaymentBonusText.setText(bonusText);
        }
    }

    @OnClick(R.id.booking_claim)
    public void claim()
    {
        requestClaimJob(mBooking);
    }

    @Subscribe
    public void onReceiveZipClusterPolygonsSuccess(
            final BookingEvent.ReceiveZipClusterPolygonsSuccess event)
    {
        Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(getLoggedInUserId());
        BookingMapFragment fragment = BookingMapFragment.newInstance(
                mBooking,
                mSource,
                bookingStatus,
                event.zipClusterPolygons
        );
        FragmentTransaction transaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
        transaction.replace(mMapView.getId(), fragment).commit();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_available_booking, this);
        ButterKnife.bind(this);
        Utils.inject(getContext(), this);
    }

    private void requestClaimJob(Booking booking)
    {
        mBus.post(new LogEvent.AddLogEvent(
                new AvailableJobsLog.ClaimSubmitted(booking, mSource, mSourceExtras, 0.0f)));
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new HandyEvent.RequestClaimJob(booking, mSource, mSourceExtras));
    }

    //returns a today or tomorrow prepend as needed
    private String getPrependByStartDate(Date bookingStartDate)
    {
        String prepend = "";

        Calendar calendar = Calendar.getInstance();

        Date currentTime = calendar.getTime();

        if (DateTimeUtils.equalCalendarDates(currentTime, bookingStartDate))
        {
            prepend = (getContext().getString(R.string.today) + ", ");
        }

        calendar.add(Calendar.DATE, 1);
        Date tomorrowTime = calendar.getTime();
        if (DateTimeUtils.equalCalendarDates(tomorrowTime, bookingStartDate))
        {
            prepend = (getContext().getString(R.string.tomorrow) + ", ");
        }

        return prepend;
    }

    private void initMapLayout()
    {
        //show either the real map or a placeholder image depending on if we have google play services
        Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(getLoggedInUserId());
        if (ConnectionResult.SUCCESS ==
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()))
        {
            final String zipClusterId = mBooking.getZipClusterId();
            if (zipClusterId != null)
            {
                requestZipClusterPolygons(zipClusterId);
            }
            else
            {
                BookingMapFragment fragment = BookingMapFragment.newInstance(
                        mBooking,
                        mSource,
                        bookingStatus
                );
                FragmentTransaction transaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(mMapView.getId(), fragment).commit();
            }
        }
        else
        {
            UIUtils.replaceView(mMapView, new MapPlaceholderView(getContext()));
        }
    }

    private void requestZipClusterPolygons(final String zipClusterId)
    {
        mBus.post(new BookingEvent.RequestZipClusterPolygons(zipClusterId));
    }

    private String getLoggedInUserId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }
}
