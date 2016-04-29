package com.handy.portal.ui.fragment;

import android.os.CountDownTimer;
import android.text.format.DateUtils;

import com.handy.portal.R;
import com.handy.portal.util.DateTimeUtils;

import java.util.Date;


public class TimerActionBarFragment extends ActionBarFragment
{
    private static final long TIMER_START_INTERVAL = DateUtils.HOUR_IN_MILLIS * 3;

    private CountDownTimer mCounter;

    @Override
    public void onPause()
    {
        super.onPause();
        if (mCounter != null)
        { mCounter.cancel(); }
    }

    protected void setTimerIfNeeded(Date startDate, Date endDate)
    {
        if (mCounter != null) { mCounter.cancel(); } // cancel the previous counter

        if (DateTimeUtils.isTimeWithinXMillisecondsFromNow(startDate,
                TIMER_START_INTERVAL))
        {
            mCounter = DateTimeUtils.setActionBarCountdownTimer(getContext(), getActionBar(),
                    startDate.getTime() - System.currentTimeMillis(),
                    R.string.start_timer_lowercase_formatted);
        }
        else if (DateTimeUtils.isTimeWithinXMillisecondsFromNow(endDate,
                endDate.getTime() - startDate.getTime()))
        {
            mCounter = DateTimeUtils.setActionBarCountdownTimer(getContext(), getActionBar(),
                    endDate.getTime() - System.currentTimeMillis(),
                    R.string.end_timer_lowercase_formatted);
        }
        else if (System.currentTimeMillis() < startDate.getTime())
        {
            // More than 3 hours before booking start
            setActionBarTitle(R.string.your_job);
        }
        else
        {
            setActionBarTitle(R.string.time_expired);
        }
    }
}
