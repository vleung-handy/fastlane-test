package com.handy.portal.model.payments;


public class YearSectionHeader extends PaymentBatch
{
    private int mYear;

    public YearSectionHeader(int year)
    {
        mYear = year;
    }

    public int getYear()
    {
        return mYear;
    }
}
