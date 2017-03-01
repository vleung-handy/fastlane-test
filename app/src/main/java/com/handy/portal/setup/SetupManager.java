package com.handy.portal.setup;

import com.handy.portal.data.DataManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class SetupManager {
    private final EventBus mBus;
    private final DataManager mDataManager;

    @Inject
    public SetupManager(final EventBus bus, final DataManager dataManager) {
        mBus = bus;
        mDataManager = dataManager;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestSetupData(final SetupEvent.RequestSetupData event) {
        mDataManager.getSetupData(new DataManager.Callback<SetupData>() {
            @Override
            public void onSuccess(final SetupData setupData) {
                mBus.post(new SetupEvent.ReceiveSetupDataSuccess(setupData));
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new SetupEvent.ReceiveSetupDataError(error));
            }
        });
    }
}
