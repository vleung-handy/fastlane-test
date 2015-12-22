package com.handy.portal.model.payments;


public class YearSectionHeader extends PaymentBatch
{
    private int mYear;

    public YearSectionHeader(int mYear)
    {
        this.mYear = mYear;
    }

    public int getYear() {
        return mYear;
    }
}
