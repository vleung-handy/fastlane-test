package com.handy.portal.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.Pair;

import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.PropertiesReader;
import com.handy.portal.ui.activity.BaseActivity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

/**
 * Created by cdavis on 4/20/15.
 */

public final class NavigationManager
{

    //String consts
    private static final String WEB_AUTH_TOKEN = "slt=";
    private static final String WEB_PARAM_TOKEN = "?";
    private static final String WEB_ADDITIONAL_PARAM_TOKEN = "&";
    private static final String WEB_PARAM_DISABLE_MOBILE_SPLASH = "&disable_mobile_splash=1";

    //Injected params
    private UserManager userManager;
    private DataManager dataManager;
    private DataManagerErrorHandler dataManagerErrorHandler;

    //Valid Action Ids
    private static final String ACTION_ID_SERVICES = "services";
    private static final String ACTION_ID_RESCHEDULE = "reschedule_booking";
    private static final String ACTION_ID_CANCEL_BOOKING = "cancel_booking";
    private static final String ACTION_ID_RATE_PRO = "rate_confirmation";
    private static final String ACTION_ID_REQUEST_PRO = "request_pro";
    private static final String ACTION_ID_GO_TO_MY_BOOKINGS = "go_to_my_bookings";
    private static final String ACTION_ID_GO_TO_MY_PROFILE = "go_to_my_profile";
    private static final String ACTION_ID_GO_TO_REFERRALS = "go_to_referrals";
    private static final String ACTION_ID_EDIT_ENTRY_INFO = "edit_entry_info";
    private static final String ACTION_ID_EDIT_NOTE_TO_PRO = "edit_note_to_pro";
    private static final String ACTION_ID_EDIT_HOURS = "edit_hours";
    private static final String ACTION_ID_EDIT_FREQUENCY = "edit_frequency";
    private static final String ACTION_ID_GET_INVOICE = "get_invoice";
    private static final String ACTION_ID_ATTACH_LAUNDRY = "attach_laundry";
    private static final String ACTION_ID_SKIP_LAUNDRY = "skip_laundry";
    private static final String ACTION_ID_SCHEDULE_PICKUP = "schedule_pickup";
    private static final String ACTION_ID_SCHEDULE_DELIVERY = "schedule_delivery";
    private static final String ACTION_ID_SCHEDULE_CONFLICT = "schedule_conflict";
    private static final String ACTION_ID_MOVING_HELP = "book_moving_help";

    //Valid Deeplink ids
    private static final String DEEP_LINK_ID_SERVICES = "services/";
    private static final String DEEP_LINK_ID_PROFILE = "profile/";
    private static final String DEEP_LINK_ID_BOOKINGS = "bookings/";
    private static final String DEEP_LINK_ID_PROMOTIONS = "promotions/";
    private static final String DEEP_LINK_ID_BOOKINGS_RESCHEDULE = "bookings/reschedule";
    private static final String DEEP_LINK_ID_BOOKINGS_CANCEL = "bookings/cancel";

    //Action Id to Deeplink Id Mapping
    public static final Map<String, String> ACTION_ID_TO_DEEP_LINK_ID;

    static
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(ACTION_ID_SERVICES, DEEP_LINK_ID_SERVICES);
        map.put(ACTION_ID_GO_TO_MY_PROFILE, DEEP_LINK_ID_PROFILE);
        map.put(ACTION_ID_GO_TO_MY_BOOKINGS, DEEP_LINK_ID_BOOKINGS);
        map.put(ACTION_ID_RATE_PRO, DEEP_LINK_ID_SERVICES); //HACK: The services page will auto detect if a rating is required and direct user to that flow
        map.put(ACTION_ID_RESCHEDULE, DEEP_LINK_ID_BOOKINGS_RESCHEDULE);
        map.put(ACTION_ID_CANCEL_BOOKING, DEEP_LINK_ID_BOOKINGS_CANCEL);
        ACTION_ID_TO_DEEP_LINK_ID = Collections.unmodifiableMap(map);
    }

    //Member fields
    private final Properties config;
    private final Context context;


    //Move somewhere else

    public static final String PARAM_BOOKING_ID = "booking_id";


    ///////
    //Public
    ///////

    @Inject
    public NavigationManager(Context context, UserManager userManager, DataManager dataManager, DataManagerErrorHandler dataManagerErrorHandler)
    {
        this.config = PropertiesReader.getProperties(context, "config.properties");
        this.context = context;
        this.userManager = userManager;
        this.dataManager = dataManager;
        this.dataManagerErrorHandler = dataManagerErrorHandler;
    }

    //Use navigation data to attempt to navigate to a deep link, using web url as fallback if deeplink is not mapped

//    public Boolean navigateTo(CTANavigationData navData)
//    {
//        return navigateTo(navData, new HashMap<String, String>());
//    }
//
//    public Boolean navigateTo(CTANavigationData navData, Map<String, String> params)
//    {
//        Boolean success = false;
//
//        String deepLinkId = actionIdToDeepLinkId(navData.navigationActionId);
//        String constructedUrl = constructWebUrlFromNavData(navData);
//
//        //Deep links have priority over web links
//        if(validateDeepLink(deepLinkId))
//        {
//            navigateToDeepLink(deepLinkId, params);
//            success = true;
//        }
//        else if(validateUrl(constructedUrl))
//        {
//            navigateToWeb(constructedUrl);
//            success = true;
//        }
//
//        return success;
//    }
//
//    //Want to remove this once and make this functionality generic
//    //Handle splash screen deep links, these may require additional callback functionality
//    public void handleSplashScreenLaunch(Intent splashScreenIntent, BaseActivity callingActivity)
//    {
//        final String action = splashScreenIntent.getAction();
//        final Uri data = splashScreenIntent.getData();
//
//        if (!action.equals("android.intent.action.VIEW") || !data.getScheme().equals("handy")) {
//            openServiceCategoriesActivity();
//            return;
//        }
//
//        final String deepLinkId = data.getHost() + data.getPath();
//
//        HashMap<String, String> params = new HashMap<String, String>();
//        if(deepLinkId.equals(DEEP_LINK_ID_BOOKINGS_RESCHEDULE) || deepLinkId.equals(DEEP_LINK_ID_BOOKINGS_CANCEL))
//        {
//            String bookingId = data.getQueryParameter("booking_id");
//            params.put(PARAM_BOOKING_ID, (bookingId != null ? bookingId : ""));
//        }
//
//        navigateToDeepLink(deepLinkId, params);
//    }
//
//    ///////
//    //Private
//    ///////
//
//    ///////
//    //Web
//    ///////
//
//    private String constructWebUrlFromNavData(CTANavigationData data)
//    {
//        String baseUrl =  this.dataManager.getBaseUrl();
//        String separatorCharacter = (data.nodeContentWebUrl.contains(WEB_PARAM_TOKEN) ? WEB_ADDITIONAL_PARAM_TOKEN : WEB_PARAM_TOKEN);
//
//        String[] tokens = data.nodeContentWebUrl.split("#");
//        String fullUrl;
//        //user/booking targeted URLs need to be split at # to insert params like auth token
//        if(tokens.length == 2)
//        {
//            fullUrl = (baseUrl + tokens[0] + separatorCharacter + getAuthToken(data) + WEB_PARAM_DISABLE_MOBILE_SPLASH + "#" + tokens[1]);
//        }
//        else
//        {
//            fullUrl = (baseUrl + data.nodeContentWebUrl + separatorCharacter + getAuthToken(data) + WEB_PARAM_DISABLE_MOBILE_SPLASH);
//        }
//
//        return fullUrl;
//    }
//
//    private String getAuthToken(CTANavigationData data)
//    {
//        String authToken = "";
//        if(data.loginToken != null && !data.loginToken.isEmpty())
//        {
//            authToken = WEB_AUTH_TOKEN + data.loginToken;
//        }
//        else if(userManager.getCurrentUser() != null)
//        {
//            authToken = WEB_AUTH_TOKEN + userManager.getCurrentUser().getAuthToken();
//        }
//        return authToken;
//    }
//
//    //Open external web page
//    private void navigateToWeb(String webUrl)
//    {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
//        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(browserIntent);
//    }
//
//    private Boolean validateUrl (String webUrl)
//    {
//        if(webUrl == null || webUrl.isEmpty())
//        {
//            return false;
//        }
//        return true;
//    }
//
//    ///////
//    //Deeplinks
//    ///////
//
//    private String actionIdToDeepLinkId(String actionId)
//    {
//        if(ACTION_ID_TO_DEEP_LINK_ID.containsKey(actionId))
//        {
//            return ACTION_ID_TO_DEEP_LINK_ID.get(actionId);
//        }
//        return "";
//    }
//
//    private Boolean validateDeepLink (String deepLinkId)
//    {
//        if(deepLinkId == null || deepLinkId.isEmpty())
//        {
//            return false;
//        }
//        return true;
//    }
//
//    private void navigateToDeepLink(String deepLinkId, Map<String, String> params)
//    {
//        switch (deepLinkId)
//        {
//            case DEEP_LINK_ID_PROFILE: {openActivity(ProfileActivity.class, true);}break;
//            case DEEP_LINK_ID_BOOKINGS:{openActivity(BookingsActivity.class, true);}break;
//            case DEEP_LINK_ID_SERVICES:{openServiceCategoriesActivity();}break;
//            case DEEP_LINK_ID_PROMOTIONS:{openActivity(PromosActivity.class, true);}break;
//
//            case DEEP_LINK_ID_BOOKINGS_RESCHEDULE:
//            {
//                String bookingId = params.get(PARAM_BOOKING_ID);
//                openBookingRequiredActivity(bookingId, BookingDateActivity.class);
//            }
//            break;
//
//            case DEEP_LINK_ID_BOOKINGS_CANCEL:
//            {
//                String bookingId = params.get(PARAM_BOOKING_ID);
//                openBookingRequiredActivity(bookingId, BookingCancelOptionsActivity.class);
//            }
//            break;
//
//            default:{openServiceCategoriesActivity();}
//        }
//    }
//
//    private void startActivity(final Intent intent)
//    {
//        //Don't clear the stack, currently designed to return to entry point on back
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.context.startActivity(intent);
//    }
//
//    //Open deep links
//    private void openActivity(final Class<? extends Activity> targetClass, final boolean requiresUser) {
//        if (requiresUser && userManager.getCurrentUser() == null) {
//            openServiceCategoriesActivity();
//        }
//        startActivity(new Intent(this.context, targetClass));
//    }
//
//    private void openActivity(final Class<? extends Activity> targetClass, final HashMap<String, String> params) {
//        Intent navIntent = new Intent(this.context, targetClass);
//
//        startActivity(new Intent(this.context, targetClass));
//    }
//
//    private void openServiceCategoriesActivity() {
//        startActivity(new Intent(this.context, ServiceCategoriesActivity.class));
//    }
//
//    private void openBookingRequiredActivity(final String bookingId, final Class targetClass) {
//        User user = userManager.getCurrentUser();
//        final Context intentContext = this.context;
//        dataManager.getBooking(bookingId, user != null ? user.getAuthToken() : null,
//                new DataManager.Callback<Booking>()
//                {
//                    @Override
//                    public void onSuccess(final Booking booking)
//                    {
//                        onBookingRetrieved(booking, context, targetClass);
//                    }
//
//                    @Override
//                    public void onError(final DataManager.DataManagerError error)
//                    {
//                        onBookingDataError(error, intentContext);
//                    }
//                });
//    }
//
//    //Not necessary yet, but we could implement deeplink classes that implement their onBookingRetrieved instead of adding if statements
//    private void onBookingRetrieved(final Booking booking, final Context intentContext, Class targetClass)
//    {
//        if (targetClass == BookingCancelOptionsActivity.class)
//        {
//            dataManager.getPreCancelationInfo(booking.getId(),
//                    new DataManager.Callback<Pair<String, List<String>>>() {
//                        @Override
//                        public void onSuccess(final Pair<String, List<String>> result)
//                        {
//                            final Intent intent = new Intent(context, BookingCancelOptionsActivity.class);
//                            intent.putExtra(BookingCancelOptionsActivity.EXTRA_OPTIONS,
//                                    new ArrayList<>(result.second));
//                            intent.putExtra(BookingCancelOptionsActivity.EXTRA_NOTICE, result.first);
//                            intent.putExtra(BookingCancelOptionsActivity.EXTRA_BOOKING, booking);
//                            startActivity(intent);
//                        }
//
//                        @Override
//                        public void onError(final DataManager.DataManagerError error)
//                        {
//                            onBookingDataError(error, intentContext);
//                        }
//                    });
//        }
//        else if (targetClass == BookingDateActivity.class)
//        {
//            dataManager.getPreRescheduleInfo(booking.getId(),
//                    new DataManager.Callback<String>() {
//                        @Override
//                        public void onSuccess(final String notice) {
//                            final Intent intent = new Intent(intentContext, BookingDateActivity.class);
//                            intent.putExtra(BookingDateActivity.EXTRA_RESCHEDULE_BOOKING, booking);
//                            intent.putExtra(BookingDateActivity.EXTRA_RESCHEDULE_NOTICE, notice);
//                            startActivity(intent);
//                        }
//
//                        @Override
//                        public void onError(final DataManager.DataManagerError error) {
//                            onBookingDataError(error, intentContext);
//                        }
//                    }
//            );
//        }
//    }
//
//    private void onBookingDataError(final DataManager.DataManagerError error, final Context intentContext)
//    {
//        dataManagerErrorHandler.handleError(intentContext, error);
//        openServiceCategoriesActivity();
//    }
}