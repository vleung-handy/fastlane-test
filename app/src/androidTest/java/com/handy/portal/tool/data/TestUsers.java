package com.handy.portal.tool.data;

import com.handy.portal.tool.model.TestUser;

public class TestUsers {
    //disable pin request by setting disable_pin_request=true in override.properties
    public static TestUser FIRST_TIME_NY_PROVIDER = new TestUser(
            "6463339879",
            "123456",
            null
    );

    public static TestUser BOOKINGS_NY_PROVIDER = new TestUser(
            "6465559879",
            "123456",
            "test_persistence_token"
    );

    public static TestUser ONBOARDING_TEST_PROVIDER = new TestUser(
            "6460000002",
            "123456",
            "test_persistence_token_onboarding"
    );

    public static TestUser CHECK_OUT_TEST_PROVIDER = new TestUser(
            "6466669879",
            "123456",
            "test_persistence_token_two"
    );

    public static TestUser CANCEL_BOOKING_TEST_PROVIDER = new TestUser(
            "6460000001",
            "123456",
            "test_persistence_token_1"
    );
}
