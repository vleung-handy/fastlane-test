package com.handy.portal.setup.step;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.flow.FlowStep;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.model.ConfigurationResponse;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class PrefetchBookingDataStep extends FlowStep
{
    @Inject
    ConfigManager mConfigManager;

    @Inject
    EventBus mBus;

    public PrefetchBookingDataStep(final Context context)
    {
        Utils.inject(context, this);
    }

    @Override
    public boolean shouldExecute()
    {
        return true;
    }

    @Override
    public void execute()
    {
        //don't block, just a blind fetch to decrease time to live data
        ConfigurationResponse configurationResponse = mConfigManager.getConfigurationResponse();
        if (configurationResponse != null) //this can't be null unless something crazy has happend, but still
        {
            prefetchAvailableJobs(configurationResponse);
            prefetchScheduledJobs();
            prefetchRequestedJobs(configurationResponse);
        }

        complete();
    }

    private void prefetchAvailableJobs(@NonNull ConfigurationResponse configurationResponse)
    {
        //if config response took a while this may be late so allow cached
        mBus.post(new HandyEvent.RequestAvailableBookings(
                DateTimeUtils.generateDatesFromToday(configurationResponse.getNumberOfDaysForAvailableJobs()), true));
    }

    private void prefetchScheduledJobs()
    {
        //if config response took a while this may be late so allow cached
        mBus.post(new HandyEvent.RequestScheduledBookings(
                DateTimeUtils.generateDatesFromToday(ScheduledBookingsFragment.SCHEDULED_REQUEST_NUM_DAYS), true));
    }

    private void prefetchRequestedJobs(@NonNull ConfigurationResponse configurationResponse)
    {
        //if config response took a while this may be late so allow cached
        mBus.post(new HandyEvent.RequestProRequestedJobs(
                DateTimeUtils.generateDatesFromToday(configurationResponse.getNumberOfDaysForRequestedJobs()), true));
    }

}
