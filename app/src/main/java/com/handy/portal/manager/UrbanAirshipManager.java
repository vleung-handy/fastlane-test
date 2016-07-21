package com.handy.portal.manager;

import android.app.Application;
import android.app.Notification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.action.CustomDeepLinkAction;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.push.PushActionConstants;
import com.handy.portal.push.PushActionWidgets;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.actions.DeepLinkAction;
import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.DefaultNotificationFactory;
import com.urbanairship.push.notifications.NotificationFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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


//                    final DefaultNotificationFactory defaultNotificationFactory =
//                            new DefaultNotificationFactory(associatedApplication.getApplicationContext());
//
//                    defaultNotificationFactory.setColor(ContextCompat.getColor(associatedApplication.getApplicationContext(), R.color.handy_blue));
//                    defaultNotificationFactory.setSmallIconId(R.drawable.ic_notification);

                    NotificationFactory notificationFactory = getNotificationFactory(associatedApplication);
                    airship.getPushManager().setNotificationFactory(notificationFactory);
                    airship.getPushManager().setPushEnabled(true);

                    airship.getPushManager().setUserNotificationsEnabled(true); //notifications the user can see as opposed to background data pushes
                    setNotificationActionButtons(airship);
                    //Setup a named user linking this user's id to a UA named user
                    //We may not have a cached provider id when a user first logs in, possible race condition, but the UrbanAirshipManager will hear the ProviderIdUpdated event and update accordingly
                    String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
                    if (providerId != null)
                    {
                        setUniqueIdentifiers(providerId);
                    }

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

    private void setUniqueIdentifiers(String id)
    {
        if (UAirship.isFlying() && id != null && !id.isEmpty())
        {
            //Keep alias around for backwards compatibility until
            //named user is backfilled by UrbanAirship
            UAirship.shared().getPushManager().setAlias(id);
            UAirship.shared().getPushManager().getNamedUser().setId(id);
        }
    }

    private void setNotificationActionButtons(final UAirship airship)
    {
        airship.getPushManager()
                .addNotificationActionButtonGroup(PushActionConstants.ACTION_GROUP_CONTACT, PushActionWidgets.createContactActionButtonGroup())
        ;

        airship.getPushManager().addNotificationActionButtonGroup(PushActionConstants.ACTION_GROUP_OMW, PushActionWidgets.createOnMyWayActionButtonGroup());
    }

    private NotificationFactory getNotificationFactory(final Application application)
    {
        final DefaultNotificationFactory defaultNotificationFactory =
                new DefaultNotificationFactory(application) {
                    @Override
                    public Notification createNotification(@NonNull final PushMessage message, final int notificationId)
                    {
                        Notification notification = super.createNotification(message, notificationId);
                        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR; //prevents user from swiping it away
                        return notification;
                    }

                    @Override
                    protected NotificationCompat.Builder createNotificationBuilder(@NonNull final PushMessage message, final int notificationId, @Nullable final NotificationCompat.Style defaultStyle)
                    {
                        return super.createNotificationBuilder(message, notificationId, defaultStyle);
                    }

                    @Override
                    public int getNextId(@NonNull final PushMessage pushMessage)
                    {
                        //only one notification per booking id, for quick hack
                        //TODO might want >1 notification/booking
                        final String bookingId = pushMessage.getPushBundle().getString("booking_id", "");
                        try
                        {
                            int id = Integer.parseInt(bookingId);
                            return id;
                        }
                        catch (Exception e)
                        {
                            return super.getNextId(pushMessage);
                        }
                    }
                };

        defaultNotificationFactory.setColor(application.getResources()
                .getColor(R.color.handy_blue));
        defaultNotificationFactory.setSmallIconId(R.drawable.ic_notification);

        return defaultNotificationFactory;
    }

}
