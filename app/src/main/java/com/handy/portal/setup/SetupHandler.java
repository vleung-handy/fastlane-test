package com.handy.portal.setup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handy.portal.flow.Flow;
import com.handy.portal.library.util.Utils;
import com.handy.portal.setup.step.AcceptTermsStep;
import com.handy.portal.setup.step.AppUpdateStep;
import com.handy.portal.setup.step.SetConfigurationStep;
import com.handy.portal.setup.step.SetProviderProfileStep;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class SetupHandler {
    @Inject
    EventBus bus;

    private final Callback mSetupHandlerCallback;
    private final Context mContext;
    private Flow mSetupFlow;

    public SetupHandler(@NonNull final Callback setupHandlerCallback,
                        @NonNull final Context context) {
        Utils.inject(context, this);
        bus.register(this);
        mSetupHandlerCallback = setupHandlerCallback;
        mContext = context;
    }

    public void start() {
        bus.post(new SetupEvent.RequestSetupData());
    }

    public boolean isOngoing() {
        return mSetupFlow != null && !mSetupFlow.isComplete();
    }

    @Subscribe
    public void onReceiveSetupDataSuccess(final SetupEvent.ReceiveSetupDataSuccess event) {
        final SetupData setupData = event.getSetupData();
        mSetupFlow = new Flow()
                .addStep(new AppUpdateStep()) // this does NOTHING for now
                .addStep(new AcceptTermsStep(mContext,
                        setupData.getTermsDetails()))
                .addStep(new SetConfigurationStep(mContext,
                        setupData.getConfigurationResponse()))
                .addStep(new SetProviderProfileStep(mContext,
                        setupData.getProviderProfile()))
                .setOnFlowCompleteListener(new Flow.OnFlowCompleteListener() {
                    @Override
                    public void onFlowComplete() {
                        mSetupHandlerCallback.onSetupComplete(setupData);
                    }
                })
                .start();
        bus.unregister(SetupHandler.this);
    }

    @Subscribe
    public void onReceiveSetupDataError(final SetupEvent.ReceiveSetupDataError event) {
        bus.unregister(this);
        mSetupHandlerCallback.onSetupFailure();
    }

    public interface Callback {
        void onSetupComplete(final SetupData setupData);

        void onSetupFailure();
    }
}
