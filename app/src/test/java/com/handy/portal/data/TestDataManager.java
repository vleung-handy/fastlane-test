package com.handy.portal.data;

import android.text.format.DateUtils;

import com.handy.portal.bookings.constant.BookingProgress;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.core.model.LoginDetails;
import com.handy.portal.core.model.SuccessWrapper;
import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.model.ProviderFeedback;
import com.handy.portal.dashboard.model.ProviderRating;
import com.handy.portal.retrofit.DynamicEndpoint;
import com.handy.portal.retrofit.DynamicEndpointService;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;
import com.handy.portal.setup.SetupData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TestDataManager extends DataManager
{
    public static final String BOOKING_ERROR_ID = "111";
    public static final String BOOKING_UNCLAIMED_ID = "222";
    public static final String BOOKING_IN_PROGRESS_ID = "333";

    public TestDataManager(final HandyRetrofitService service,
                           final HandyRetrofitEndpoint endpoint,
                           final StripeRetrofitService stripeService,
                           final DynamicEndpoint dynamicEndpoint,
                           final DynamicEndpointService dynamicEndpointService)
    {
        super(service, endpoint, stripeService, dynamicEndpoint, dynamicEndpointService);
    }

    public void getSetupData(final Callback<SetupData> cb)
    {
        cb.onSuccess(new SetupData());
    }

    @Override
    public void getConfiguration(final Callback<ConfigurationResponse> cb)
    {
        ConfigurationResponse configurationResponse = spy(new ConfigurationResponse());
        cb.onSuccess(configurationResponse);
    }

    @Override
    public void getProviderEvaluation(final String providerId, final Callback<ProviderEvaluation> cb)
    {
        ProviderEvaluation evaluation = createProviderEvaluation();
        cb.onSuccess(evaluation);
    }

    @Override
    public void getBookingDetails(final String bookingId, final Booking.BookingType type, final Callback<Booking> cb)
    {
        switch (bookingId)
        {
            case BOOKING_ERROR_ID:
            {
                cb.onError(new DataManagerError(DataManagerError.Type.OTHER, "error"));
                break;
            }
            case BOOKING_UNCLAIMED_ID:
            {
                cb.onSuccess(createMockBooking(BOOKING_UNCLAIMED_ID, BookingProgress.READY_FOR_CLAIM));
                break;
            }
            case BOOKING_IN_PROGRESS_ID:
            {
                cb.onSuccess(createMockBooking(BOOKING_IN_PROGRESS_ID, BookingProgress.READY_FOR_CHECK_OUT));
                break;
            }
        }
    }

    @Override
    public void requestSlt(final String phoneNumber, final Callback<SuccessWrapper> cb)
    {
        cb.onSuccess(new SuccessWrapper(true));
    }

    @Override
    public void requestLoginWithSlt(final String n, final String sig, final String slt, final Callback<LoginDetails> cb)
    {
        cb.onSuccess(new LoginDetails(true, "x", "x"));
    }

    // Factory Methods
    public static ProviderEvaluation createProviderEvaluation()
    {
        ProviderEvaluation.Rating lifeRating = new ProviderEvaluation.Rating(40, 44, 42, 30, 3.6,
                "positive", "", new Date(System.currentTimeMillis() - DateUtils.YEAR_IN_MILLIS), new Date());
        ProviderEvaluation.Rating rollingRating = new ProviderEvaluation.Rating(16, 17, 15, 10, 4.0,
                "positive", "", new Date(System.currentTimeMillis() - DateUtils.WEEK_IN_MILLIS * 4), new Date());
        ProviderEvaluation.Rating weekRating = new ProviderEvaluation.Rating(3, 5, 5, 3, 5.0,
                "positive", "", new Date(System.currentTimeMillis() - DateUtils.WEEK_IN_MILLIS), new Date());
        ProviderEvaluation.PayRates payRates = createPayRates();

        List<ProviderFeedback> feedback = new ArrayList<>();
        feedback.add(createProviderFeedback());

        List<ProviderRating> ratings = new ArrayList<>();
        ratings.add(new ProviderRating(2, 123, 5, 123, new Date(), "Mobile", "Nice job"));
        ratings.add(new ProviderRating(3, 1234, 5, 1234, new Date(), "Web", "Great job"));

        return spy(new ProviderEvaluation(rollingRating, lifeRating, weekRating, payRates, 4.2, ratings, feedback, ratings));
    }

    private static ProviderFeedback createProviderFeedback()
    {
        ProviderFeedback.FeedbackTip textTip = new ProviderFeedback.FeedbackTip(ProviderFeedback.FeedbackTip.DATA_TYPE_TEXT, "Do a better job next time :P");
        ProviderFeedback.FeedbackTip videoTip = new ProviderFeedback.FeedbackTip(ProviderFeedback.FeedbackTip.DATA_TYPE_VIDEO_ID, "fake_id");
        List<ProviderFeedback.FeedbackTip> tips = new ArrayList<>();
        tips.add(textTip);
        tips.add(videoTip);
        return new ProviderFeedback("Feedback", "Tips", tips);
    }

    private static ProviderEvaluation.PayRates createPayRates()
    {
        List<ProviderEvaluation.Incentive> incentives = new ArrayList<>();
        incentives.add(new ProviderEvaluation.Incentive(
                "Region Name", "Cleaning", new ArrayList<ProviderEvaluation.Tier>(), 0, 1, "$",
                ProviderEvaluation.Incentive.TIERED_TYPE));
        List<ProviderEvaluation.TiersServiceDescription> descriptions = new ArrayList<>();
        descriptions.add(new ProviderEvaluation.TiersServiceDescription("Title", "Body"));

        return new ProviderEvaluation.PayRates(incentives, descriptions);
    }

    private static Booking createMockBooking(String bookingId, @BookingProgress.Progress int bookingProgress)
    {
        Booking booking = mock(Booking.class);
        when(booking.getId()).thenReturn(bookingId);
        when(booking.getStartDate()).thenReturn(new Date(System.currentTimeMillis()));
        when(booking.getEndDate()).thenReturn(new Date(System.currentTimeMillis()));
        when(booking.getServiceInfo()).thenReturn(new Booking.ServiceInfo("home_cleaning", "Home Cleaning"));
        when(booking.getBookingProgress()).thenReturn(bookingProgress);
        when(booking.getCustomerPreferences()).thenReturn(createCustomerPreferences());

        return booking;
    }

    private static List<Booking.BookingInstructionUpdateRequest> createCustomerPreferences()
    {
        List<Booking.BookingInstructionUpdateRequest> customerPreferences = new ArrayList<>();

        customerPreferences.add(new Booking.BookingInstructionUpdateRequest());
        customerPreferences.add(new Booking.BookingInstructionUpdateRequest());

        return customerPreferences;
    }
}

