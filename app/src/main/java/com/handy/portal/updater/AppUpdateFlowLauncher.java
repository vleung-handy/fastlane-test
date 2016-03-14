package com.handy.portal.updater;

import android.support.annotation.NonNull;

/**
 * currently implemented by BaseActivity
 * and invoked by AppUpdateEventListener
 *
 * launches the UI stuff related to the app update flow
 */
public interface AppUpdateFlowLauncher
{
    void launchAppUpdater();
    void showAppUpdateFlowError(@NonNull String message);
    void launchEnableRequiredUpdateFlowApplicationIntent(@NonNull String packageName, String promptMessage);
}
