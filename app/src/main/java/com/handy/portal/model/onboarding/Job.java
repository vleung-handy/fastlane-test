package com.handy.portal.model.onboarding;

/**
 * Created by jtse on 4/18/16.
 * <p/>
 * TODO: JIA: make this class reflect the actual JSON coming from the server
 */
public class Job
{
    public String title;
    public String subTitle;
    public float amount;
    public String symbol;
    public boolean selected;

    public Job(String title, String subTitle, float amount, boolean selected)
    {
        this.title = title;
        this.subTitle = subTitle;
        this.amount = amount;
        this.selected = selected;
        symbol = "$";
    }

    public String getFormattedPrice()
    {
        return symbol + String.valueOf(amount);
    }
}
