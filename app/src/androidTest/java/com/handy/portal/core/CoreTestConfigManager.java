package com.handy.portal.core;

import android.support.annotation.Nullable;

import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.data.DataManager;

import org.greenrobot.eventbus.EventBus;

public class CoreTestConfigManager extends ConfigManager {
    public CoreTestConfigManager(final DataManager dataManager, final EventBus bus) {
        super(dataManager, bus);
    }

    @Nullable
    @Override
    public ConfigurationResponse getConfigurationResponse() {
        ConfigurationResponse config = super.getConfigurationResponse();

        // Overriding the config params
        if (config != null) {
            config.setProfilePictureUploadEnabled(false);
            config.setProfilePictureEnabled(false);
        }

        return config;
    }
}
