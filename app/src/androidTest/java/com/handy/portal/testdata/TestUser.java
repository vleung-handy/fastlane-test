package com.handy.portal.testdata;

public class TestUser
{
    //disable pin request by setting disable_pin_request=true in override.properties
    public static TestUser FIRST_TIME_PROVIDER_NY = new TestUser(
            "6466466464",
            "123456"
    );

    //strings just in case we want them to be empty or have weird characters
    private final String mPhoneNumber;
    private final String mPinCode;

    public TestUser(final String email, final String pinCode)
    {
        mPhoneNumber = email;
        mPinCode = pinCode;
    }

    public String getPhoneNumber()
    {
        return mPhoneNumber;
    }

    public String getPinCode()
    {
        return mPinCode;
    }
}
