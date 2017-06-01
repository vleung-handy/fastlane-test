package com.handy.portal.availability;

import com.handy.portal.availability.fragment.EditAvailableHoursFragment;
import com.handy.portal.availability.fragment.EditWeeklyAdhocAvailableHoursFragment;
import com.handy.portal.availability.manager.AvailabilityManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.data.DataManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                EditAvailableHoursFragment.class,
                EditWeeklyAdhocAvailableHoursFragment.class,
        })
public class AvailabilityModule {
    @Provides
    @Singleton
    AvailabilityManager provideAvailabilityManager(
            final EventBus bus,
            final DataManager dataManager,
            final ProviderManager providerManager
            ) {
        return new AvailabilityManager(bus, dataManager, providerManager);
    }
}
