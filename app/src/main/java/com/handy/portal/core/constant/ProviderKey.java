package com.handy.portal.core.constant;

public enum ProviderKey {
    LATITUDE, LONGITUDE, ACCURACY, ACTIVE, EMAIL, PHONE, ADDRESS1, ADDRESS2, CITY, STATE, ZIPCODE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
