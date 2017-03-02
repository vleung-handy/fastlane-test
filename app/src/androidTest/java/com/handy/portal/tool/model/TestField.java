package com.handy.portal.tool.model;

/**
 * associates a view resource id with a value
 * <p>
 * used for updating fields
 * and for asserting their values
 */
public class TestField {
    private int mViewResourceId;
    private String mValue;

    public TestField(int resourceId, String value) {
        mViewResourceId = resourceId;
        mValue = value;
    }

    public int getViewResourceId() {
        return mViewResourceId;
    }

    public String getValue() {
        return mValue;
    }
}

