package com.handy.portal.setup;

import com.handy.portal.setup.step.AcceptTermsStep;
import com.handy.portal.setup.step.SetConfigurationStep;
import com.handy.portal.setup.step.SetProviderProfileStep;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                SetConfigurationStep.class,
                SetProviderProfileStep.class,
                AcceptTermsStep.class,
                SetupHandler.class,
        })
public class SetupModule
{
}
