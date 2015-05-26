package com.handy.portal.data;

import android.content.Context;

import com.handy.portal.core.BookingManager;
import com.handy.portal.core.User;
import com.handy.portal.core.UserManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

public class Mixpanel
{
    private MixpanelAPI mixpanel;
    private UserManager userManager;
    private BookingManager bookingManager;
    private Bus bus;
    private HashMap<String, Boolean> calledMap;

    @Inject
    public Mixpanel(final Context context, final UserManager userManager,
                    final BookingManager bookingManager, final Bus bus)
    {
        String mixpanelApiKey = PropertiesReader.getConfigProperties(context).getProperty("mixpanel_api_key");
        this.mixpanel = MixpanelAPI.getInstance(context, mixpanelApiKey);
        this.userManager = userManager;
        this.bookingManager = bookingManager;
        this.bus = bus;
        this.bus.register(this);
        this.calledMap = new HashMap<>();

        setSuperProps();
    }

    public void flush()
    {
        mixpanel.flush();
    }

    private void setSuperProps()
    {
        mixpanel.clearSuperProperties();

        final JSONObject props = new JSONObject();
        addProps(props, "mobile", true);
        addProps(props, "client", "android");
        addProps(props, "impersonating", false);

        final User user = userManager.getCurrentUser();

        if (user == null) addProps(props, "user_logged_in", false);
        else
        {
            addProps(props, "user_logged_in", true);
            addProps(props, "name", user.getFullName());
            addProps(props, "email", user.getEmail());
            addProps(props, "user_id", user.getId());


            final User.Analytics analytics = user.getAnalytics();

            if (analytics != null)
            {
                addProps(props, "last_booking_end", analytics.getLastBookingEnd());
                addProps(props, "partner", analytics.getPartner());
                addProps(props, "bookings", analytics.getBookings());
                addProps(props, "past_bookings_count", analytics.getPastBookings());
                addProps(props, "upcoming_bookings_count", analytics.getUpcomingBookings());
                addProps(props, "total_bookings_count", analytics.getTotalBookings());
                addProps(props, "recurring_bookings_count", analytics.getRecurringBookings());
                addProps(props, "provider", analytics.isProvider());
                addProps(props, "vip", analytics.isVip());
                addProps(props, "facebook_login", analytics.isFacebookLogin());
            }
        }
        mixpanel.registerSuperProperties(props);
    }

    public void trackEventAppOpened(final boolean newOpen)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "new_open", newOpen);
        mixpanel.track("app had been opened", props);
    }

    private void addProps(final JSONObject object, final String key, final Object value)
    {
        try
        {
            object.put(key, value);
        } catch (final JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void addProps(final JSONObject object, final HashMap<String, Object> props)
    {
        try
        {
            for (final String key : props.keySet()) object.put(key, props.get(key));
        } catch (final JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

}
