package com.handy.portal.setup;

import com.handy.portal.setup.step.SetConfigurationStep;
import com.handy.portal.setup.step.SetProviderProfileStep;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                SetConfigurationStep.class,
                SetProviderProfileStep.class,
        })
public class SetupModule
{
}
