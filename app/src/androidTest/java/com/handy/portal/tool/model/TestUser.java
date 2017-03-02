package com.handy.portal.tool.model;

/**
 * represents a set of login credentials
 */
public class TestUser {
    //strings just in case we want them to be empty or have weird characters
    private final String mPhoneNumber;
    private final String mPinCode;
    private final String mPersistenceToken;

    public TestUser(final String phoneNumber, final String pinCode, final String persistenceToken) {
        mPhoneNumber = phoneNumber;
        mPinCode = pinCode;
        mPersistenceToken = persistenceToken;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getPinCode() {
        return mPinCode;
    }

    public String getPersistenceToken() {
        return mPersistenceToken;
    }
}
