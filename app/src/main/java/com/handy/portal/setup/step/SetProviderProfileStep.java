package com.handy.portal.setup.step;

import android.content.Context;

import com.handy.portal.flow.FlowStep;
import com.handy.portal.library.util.Utils;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ProviderProfile;

import javax.inject.Inject;

public class SetProviderProfileStep extends FlowStep
{
    @Inject
    ProviderManager mProviderManager;
    private ProviderProfile mProviderProfile;

    public SetProviderProfileStep(final Context context,
                                  final ProviderProfile providerProfile)
    {
        Utils.inject(context, this);
        mProviderProfile = providerProfile;
    }

    @Override
    public boolean shouldExecute()
    {
        return mProviderProfile != null;
    }

    @Override
    public void execute()
    {
        mProviderManager.setProviderProfile(mProviderProfile);
        complete();
    }
}