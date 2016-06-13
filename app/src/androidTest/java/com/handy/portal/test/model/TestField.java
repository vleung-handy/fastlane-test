package com.handy.portal.test.model;

/**
 * associates a view resource id with a value
 *
 * used for updating fields
 * and for asserting their values
 */
public class TestField
{
    private int mViewResourceId;
    private String mValue;
    public TestField(int resourceId, String value)
    {
        mViewResourceId = resourceId;
        mValue = value;
    }

    public int getViewResourceId()
    {
        return mViewResourceId;
    }

    public String getValue()
    {
        return mValue;
    }
}

