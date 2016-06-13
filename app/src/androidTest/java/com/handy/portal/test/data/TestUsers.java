package com.handy.portal.test.data;

import com.handy.portal.test.model.TestUser;

public class TestUsers
{
    //disable pin request by setting disable_pin_request=true in override.properties
    public static TestUser FIRST_TIME_NY_PROVIDER = new TestUser(
            "6463339879",
            "123456",
            null
    );

    public static TestUser NY_PROVIDER = new TestUser(
            "6465559879",
            "123456",
            "test_persistence_token"
    );
}
