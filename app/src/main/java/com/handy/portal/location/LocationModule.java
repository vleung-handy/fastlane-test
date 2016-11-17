package com.handy.portal.location;

import android.content.Context;

import com.handy.portal.data.DataManager;
import com.handy.portal.location.manager.LocationManager;
import com.handy.portal.location.scheduler.LocationScheduleService;
import com.handy.portal.location.scheduler.geofences.handler.BookingGeofenceScheduleHandler;
import com.handy.portal.location.scheduler.tracking.handler.LocationTrackingScheduleHandler;
import com.handy.portal.location.ui.LocationPermissionsBlockerDialogFragment;
import com.handy.portal.location.ui.LocationSettingsBlockerDialogFragment;
import com.handy.portal.manager.ProviderManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                LocationSettingsBlockerDialogFragment.class,
                LocationTrackingScheduleHandler.class,
                BookingGeofenceScheduleHandler.class,
                LocationScheduleService.class,
                LocationPingService.class,
                LocationPermissionsBlockerDialogFragment.class,
        })
public final class LocationModule
{
    @Provides
    @Singleton
    final LocationManager provideLocationManager(
            final Context context,
            final EventBus bus,
            final DataManager dataManager,
            final ProviderManager providerManager)
    {
        return new LocationManager(context, bus, dataManager, providerManager);
    }
}
