package com.handy.portal.core.manager;

import android.app.Application;
import android.support.v4.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.data.DataManager;
import com.handy.portal.deeplink.CustomDeepLinkAction;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.actions.DeepLinkAction;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by cdavis on 8/10/15.
 */
public class UrbanAirshipManager
{
    private final EventBus bus;
    private final DataManager dataManager;
    private final PrefsManager prefsManager;
    private final Application associatedApplication;
    private final CustomDeepLinkAction customDeepLinkAction;

    public UrbanAirshipManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager, final Application associatedApplication, final CustomDeepLinkAction customDeepLinkAction)
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
        if (UAirship.isTakingOff() || UAirship.isFlying())
        {
            Crashlytics.log("startUrbanAirship : Alreadying taking off or flying, aborting subsequent startup");
            return;
        }

        final AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(associatedApplication.getApplicationContext());
        options.inProduction = !BuildConfig.DEBUG;

        try
        {
            UAirship.takeOff(associatedApplication, options, new UAirship.OnReadyCallback()
            {
                @Override
                public void onAirshipReady(final UAirship airship)
                {
                    final DefaultNotificationFactory defaultNotificationFactory =
                            new DefaultNotificationFactory(associatedApplication.getApplicationContext());

                    defaultNotificationFactory.setColor(ContextCompat.getColor(associatedApplication.getApplicationContext(), R.color.handy_blue));
                    defaultNotificationFactory.setSmallIconId(R.drawable.ic_notification);

                    airship.getPushManager().setNotificationFactory(defaultNotificationFactory);
                    airship.getPushManager().setPushEnabled(true);
                    airship.getPushManager().setUserNotificationsEnabled(true); //notifications the user can see as opposed to background data pushes

                    //Setup a named user linking this user's id to a UA named user
                    //We may not have a cached provider id when a user first logs in, possible race condition, but the UrbanAirshipManager will hear the ProviderIdUpdated event and update accordingly
                    String providerId = prefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID, null);
                    setUniqueIdentifiers(providerId);

                    //Override the default action otherwise it tries to openurl all of our deep links
                    //Init the deep link listener, must be done after takeoff
                    UAirship.shared().getActionRegistry().getEntry(DeepLinkAction.DEFAULT_REGISTRY_NAME).setDefaultAction(customDeepLinkAction);
                }
            });
        }
        catch (IllegalStateException | IllegalArgumentException e)
        {
            Crashlytics.logException(e);
        }
    }

    //Update our alias to match the provider id
    @Subscribe
    public void onProviderIdUpdated(HandyEvent.ProviderIdUpdated event)
    {
        setUniqueIdentifiers(event.providerId);
    }

    @Subscribe
    public void onUserLoggedOut(final HandyEvent.UserLoggedOut event)
    {
        setUniqueIdentifiers(null);
    }

    private void setUniqueIdentifiers(String id)
    {
        if (UAirship.isFlying())
        {
            //Keep alias around for backwards compatibility until
            //named user is backfilled by UrbanAirship
            UAirship.shared().getPushManager().setAlias(id);
            UAirship.shared().getPushManager().getNamedUser().setId(id);
        }
    }

}
