package com.handy.portal.data;

import android.content.Context;

import com.handy.portal.BuildConfig;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.core.BookingManager;
import com.handy.portal.core.BookingQuote;
import com.handy.portal.core.BookingRequest;
import com.handy.portal.core.BookingTransaction;
import com.handy.portal.core.User;
import com.handy.portal.core.UserManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
        {
            mixpanel = MixpanelAPI.getInstance(context, "864ccb52b900de546bb1bba717ab4fac");
        } else mixpanel = MixpanelAPI.getInstance(context, "5b31021d4a78ed7d57d9f19fd796f1cd");

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

    public void trackOnboardingShown()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "type", "default");
        mixpanel.track("Onboarding Show", props);
    }

    public void trackOnboardingActionLogin(final int page)
    {
        trackOnboardingActions("login", page);
    }

    public void trackOnboardingActionSkip(final int page)
    {
        trackOnboardingActions("skip", page);
    }

    private void trackOnboardingActions(final String action, final int page)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "type", "default");
        addProps(props, "action", action);
        addProps(props, "page", page);
        mixpanel.track("Onboarding Action", props);
    }

    public void trackPageLogin()
    {
        mixpanel.track("log in page view", null);
    }

    public void trackEventLoginSuccess(final LoginType type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "log_in_type", type.getValue());
        mixpanel.track("log in successful", props);
    }

    public void trackEventLoginFailure(final LoginType type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "log_in_type", type.getValue());
        mixpanel.track("log in failure", props);
    }

    public void trackEventWhenPage()
    {
        trackWhenPageEvents("when page");
    }

    public void trackEventWhenPageSubmitted()
    {
        trackWhenPageEvents("when page submitted");
    }

    public void trackEventPaymentPage()
    {
        final String event = "payment page";
        final Boolean called = calledMap.get(event);
        if (called != null && called) return;

        final JSONObject props = new JSONObject();
        addPaymentFlowProps(props);

        mixpanel.track(event, props);
        calledMap.put(event, true);
    }

    public void trackEventSubmitPayment()
    {
        final String event = "submit payment";
        final Boolean called = calledMap.get(event);
        if (called != null && called) return;

        final JSONObject props = new JSONObject();
        addSubmitPaymentFlowProps(props);

        mixpanel.track(event, props);
        calledMap.put(event, true);
    }

    public void trackEventBookingMade()
    {
        final String event = "booking made";
        final Boolean called = calledMap.get(event);
        if (called != null && called) return;

        final JSONObject props = new JSONObject();
        addBookingMadeFlowProps(props);

        mixpanel.track(event, props);
        calledMap.put(event, true);
    }

    public void trackEventYozioInstall(final HashMap<String, Object> metaData)
    {
        final JSONObject props = new JSONObject();
        addProps(props, metaData);
        mixpanel.track("Yozio Install", props);
    }

    public void trackEventYozioOpen(final HashMap<String, Object> metaData)
    {
        final JSONObject props = new JSONObject();
        addProps(props, metaData);
        mixpanel.track("Yozio Open", props);
    }

    public void trackEventFirstTimeUse()
    {
        mixpanel.track("first time use", null);
    }

    public void trackEventAppOpened(final boolean newOpen)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "new_open", newOpen);
        mixpanel.track("app had been opened", props);
    }

    public void trackEventHelpCenterOpened()
    {
        final JSONObject props = new JSONObject();
        mixpanel.track("ssc_open", props);
    }

    public void trackEventHelpCenterNavigation(final String location)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "selection", location);
        mixpanel.track("ssc_navigation_enter", props);
    }

    public void trackEventHelpCenterLeaf(final String nodeId, final String label)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "node_id", nodeId);
        addProps(props, "label", label);
        mixpanel.track("ssc_enter_leaf", props);
    }

    public void trackEventHelpCenterNeedHelpClicked(final String nodeId, final String label)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "node_id", nodeId);
        addProps(props, "label", label);
        mixpanel.track("ssc_still_need_help_clicked", props);
    }

    public void trackEventHelpCenterSubmitTicket(final String nodeId, final String label)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "node_id", nodeId);
        addProps(props, "label", label);
        mixpanel.track("ssc_submit_ticket", props);
    }

    public void trackEventHelpCenterDeepLinkClicked(final String nodeId, final String label)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "node_id", nodeId);
        addProps(props, "label", label);
        mixpanel.track("ssc_deep_link_clicked", props);
    }

    public void trackEventProRate(final ProRateEventType type, final int bookingId,
                                  final String proName, final int rating)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "dialog_event", type.getValue());
        addProps(props, "booking_id", bookingId);
        addProps(props, "provider_name", proName);
        addProps(props, "rating_range", "1 to 5");

        if (type == ProRateEventType.SUBMIT) addProps(props, "app_rating", rating);

        mixpanel.track("app pro rate event", props);
    }

    public void trackPageAddLaundryIntro(final LaundryEventSource source)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        mixpanel.track("show add laundry intro page", props);
    }

    public void trackPageAddLaundryConfirm(final LaundryEventSource source)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        mixpanel.track("show add laundry confirm page", props);
    }

    public void trackEventLaundryAdded(final LaundryEventSource source)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        mixpanel.track("submit add laundry confirm page", props);
    }

    public void trackPageScheduleLaundry(final LaundryEventSource source, final String type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        addProps(props, "type", type);
        mixpanel.track("show schedule laundry page", props);
    }

    public void trackEventLaundryScheduled(final LaundryEventSource source, final String type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        addProps(props, "type", type);
        mixpanel.track("submit schedule laundry page", props);
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

    private void trackWhenPageEvents(final String event)
    {
        final Boolean called = calledMap.get(event);
        if (called != null && called) return;

        final JSONObject props = new JSONObject();
        addWhenFlowProps(props);

        mixpanel.track(event, props);
        calledMap.put(event, true);
    }

    private void addWhenFlowProps(final JSONObject props)
    {
        final BookingRequest request = bookingManager.getCurrentRequest();
        String service = null, zip = null;

        if (request != null)
        {
            service = request.getUniq();
            zip = request.getZipCode();
        }

        addProps(props, "service", service);
        addProps(props, "booking_zipcode", zip);
    }

    private void addPaymentFlowProps(final JSONObject props)
    {
        addWhenFlowProps(props);

        final BookingRequest request = bookingManager.getCurrentRequest();
        final BookingQuote quote = bookingManager.getCurrentQuote();
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();

        String email = null;
        int bookingId = 0, repeatFreq = 0;
        float hours = 0, price = 0;
        boolean hasDynamicPricing = false, isRepeat = false;

        if (request != null) email = request.getEmail();

        if (quote != null)
        {
            bookingId = quote.getBookingId();
            hours = quote.getHours();
            hasDynamicPricing = quote.getSurgePriceTable() != null;
        }

        if (transaction != null)
        {
            repeatFreq = transaction.getRecurringFrequency();
            isRepeat = repeatFreq > 0;
            if (quote != null) price = quote.getPricing(hours, repeatFreq)[0];
        }

        addProps(props, "booking_id", bookingId);
        addProps(props, "hours", hours);
        addProps(props, "email", email);
        addProps(props, "price_before_discount", price);
        addProps(props, "repeat", isRepeat);
        addProps(props, "dynamic_price", hasDynamicPricing);
        if (repeatFreq > 0) addProps(props, "repeat_freq", repeatFreq);
    }

    private void addSubmitPaymentFlowProps(final JSONObject props)
    {
        addPaymentFlowProps(props);

        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        String cleaningExtras = null;
        boolean cleaningExtrasSelected = false;
        float hours = 0;


        if (transaction != null)
        {
            cleaningExtras = transaction.getExtraCleaningText();

            if (cleaningExtras != null)
            {
                final String[] extrasList = cleaningExtras.split(",");
                if (extrasList.length > 0) cleaningExtrasSelected = true;
            }

            hours = transaction.getExtraHours();
        }

        addProps(props, "cleaning_extras_tapped", cleaningExtrasSelected);
        if (cleaningExtrasSelected) addProps(props, "extra_hours", hours);
        if (cleaningExtrasSelected) addProps(props, "extras", cleaningExtras);
    }

    private void addBookingMadeFlowProps(final JSONObject props)
    {
        addSubmitPaymentFlowProps(props);

        final BookingQuote quote = bookingManager.getCurrentQuote();
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();

        float hourlyPrice = 0, totalPrice = 0;
        boolean isRepeating = false;

        if (quote != null) hourlyPrice = quote.getHourlyAmount();

        if (transaction != null)
        {
            isRepeating = transaction.getRecurringFrequency() > 0;

            final float hours = transaction.getHours() + transaction.getExtraHours();
            float[] pricing = new float[]{0, 0};

            if (quote != null)
                pricing = quote.getPricing(hours, transaction.getRecurringFrequency());
            if (pricing[0] == pricing[1]) totalPrice = pricing[0];
            else totalPrice = pricing[1];
        }

        addProps(props, "price_per_hour", hourlyPrice);
        addProps(props, "charge", totalPrice);
        addProps(props, "converted_to_repeat", isRepeating);
    }

//    @Subscribe
//    public final void userAuthUpdated(final UserLoggedInEvent event) {
//        setSuperProps();
//    }
//
//    @Subscribe
//    public final void bookingFlowCleared(final BookingFlowClearedEvent event) {
//        calledMap = new HashMap<>();
//        setSuperProps();
//    }

    public enum LoginType
    {
        EMAIL("email/password"), FACEBOOK("facebook");

        private String value;

        LoginType(final String value)
        {
            this.value = value;
        }

        String getValue()
        {
            return value;
        }
    }

    public enum ProRateEventType
    {
        SHOW("show"), SUBMIT("submit");

        private String value;

        ProRateEventType(final String value)
        {
            this.value = value;
        }

        String getValue()
        {
            return value;
        }
    }

    public enum LaundryEventSource
    {
        APP_OPEN("app_open");

        private String value;

        LaundryEventSource(final String value)
        {
            this.value = value;
        }

        String getValue()
        {
            return value;
        }
    }
}
