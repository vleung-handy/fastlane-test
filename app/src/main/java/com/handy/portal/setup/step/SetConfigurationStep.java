package com.handy.portal.setup.step;

import android.content.Context;

import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.flow.FlowStep;
import com.handy.portal.library.util.Utils;

import javax.inject.Inject;

public class SetConfigurationStep extends FlowStep {
    private final ConfigurationResponse mConfiguration;
    @Inject
    ConfigManager mConfigManager;

    public SetConfigurationStep(final Context context,
                                final ConfigurationResponse configuration) {
        Utils.inject(context, this);
        mConfiguration = configuration;
    }

    @Override
    public boolean shouldExecute() {
        return mConfiguration != null;
    }

    @Override
    public void execute() {
        mConfigManager.setConfigurationResponse(mConfiguration);
        complete();
    }
}
