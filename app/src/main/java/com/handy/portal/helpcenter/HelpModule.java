package com.handy.portal.helpcenter;

import com.handy.portal.data.DataManager;
import com.handy.portal.helpcenter.helpcontact.ui.fragment.HelpContactFragment;
import com.handy.portal.helpcenter.ui.fragment.HelpFragment;
import com.handy.portal.helpcenter.ui.fragment.HelpWebViewFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library=true,
        complete=false,
        injects = {
        HelpFragment.class,
        HelpWebViewFragment.class,
        HelpContactFragment.class,
})
public final class HelpModule
{
    @Provides
    @Singleton
    final HelpManager provideHelpManager(final Bus bus,
                                         final DataManager dataManager)
    {
        return new HelpManager(bus, dataManager);
    }
}