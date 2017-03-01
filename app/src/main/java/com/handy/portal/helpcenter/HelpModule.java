package com.handy.portal.helpcenter;

import com.handy.portal.helpcenter.ui.fragment.HelpWebViewFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                HelpWebViewFragment.class,
        })
public final class HelpModule {
}
