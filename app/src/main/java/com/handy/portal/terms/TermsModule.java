package com.handy.portal.terms;

import com.handy.portal.data.DataManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                TermsActivity.class,
                TermsFragment.class,
        })
public final class TermsModule {
    @Provides
    @Singleton
    final TermsManager provideTermsManager(final EventBus bus,
                                           final DataManager dataManager) {
        return new TermsManager(bus, dataManager);
    }
}
