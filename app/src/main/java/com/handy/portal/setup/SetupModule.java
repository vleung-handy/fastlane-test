package com.handy.portal.setup;

import com.handy.portal.setup.step.AcceptTermsStep;
import com.handy.portal.setup.step.SetConfigurationStep;
import com.handy.portal.setup.step.SetProviderProfileStep;
import com.handy.portal.ui.activity.BaseActivity;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                SetConfigurationStep.class,
                SetProviderProfileStep.class,
                AcceptTermsStep.class,
                BaseActivity.SetupHandler.class,
        })
public class SetupModule
{
}
