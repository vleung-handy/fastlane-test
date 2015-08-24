package com.handy.portal.manager;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.action.CustomDeepLinkAction;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.actions.DeepLinkAction;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

/**
 * Created by cdavis on 8/10/15.
 */
public class UrbanAirshipManager
{
    private final Bus bus;
    private final DataManager dataManager;
    private final PrefsManager prefsManager;
    private final Application associatedApplication;
    private final CustomDeepLinkAction customDeepLinkAction;

    public UrbanAirshipManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager, final Application associatedApplication, final CustomDeepLinkAction customDeepLinkAction)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
        this.associatedApplication = associatedApplication;
        this.customDeepLinkAction = customDeepLinkAction;
    }

    @Subscribe
    public void onStartUrbanAirship(HandyEvent.StartUrbanAirship event)
    {
        startUrbanAirship();
    }

    protected void startUrbanAirship()
    {
        if(UAirship.isTakingOff() || UAirship.isFlying())
        {
            Crashlytics.log("startUrbanAirship : Alreadying taking off or flying, aborting subsequent startup");
            return;
        }

        final AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(associatedApplication.getApplicationContext());
        options.inProduction = !BuildConfig.DEBUG;

        UAirship.takeOff(associatedApplication, options, new UAirship.OnReadyCallback()
        {
            @Override
            public void onAirshipReady(final UAirship airship)
            {
                final DefaultNotificationFactory defaultNotificationFactory =
                        new DefaultNotificationFactory(associatedApplication.getApplicationContext());

                defaultNotificationFactory.setColor(associatedApplication.getApplicationContext().getResources().getColor(R.color.handy_blue));
                defaultNotificationFactory.setSmallIconId(R.drawable.ic_notification);

                airship.getPushManager().setNotificationFactory(defaultNotificationFactory);
                airship.getPushManager().setPushEnabled(true);
                airship.getPushManager().setUserNotificationsEnabled(true); //notifications the user can see as opposed to background data pushes

                //Setup an alias linking this user's id to a UA alias
                //We may not have a cached provider id when a user first logs in, possible race condition, but the UrbanAirshipManager will hear the ProviderIdUpdated event and update accordingly
                String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
                if (providerId != null)
                {
                    setAlias(providerId);
                }

               //Override the default action otherwise it tries to openurl all of our deep links
                //Init the deep link listener, must be done after takeoff
                UAirship.shared().getActionRegistry().getEntry(DeepLinkAction.DEFAULT_REGISTRY_NAME).setDefaultAction(customDeepLinkAction);
            }
        });
    }

    //Update our alias to match the provider id
    @Subscribe
    public void onProviderIdUpdated(HandyEvent.ProviderIdUpdated event)
    {
        setAlias(event.providerId);
    }

    private void setAlias(String aliasId)
    {
        if(UAirship.isFlying() && aliasId != null && !aliasId.isEmpty())
        {
            UAirship.shared().getPushManager().setAlias(aliasId);
        }
    }

}