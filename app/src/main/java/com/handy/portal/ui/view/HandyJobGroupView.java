package com.handy.portal.ui.view;

import android.content.Context;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.onboarding.Job;
import com.handy.portal.model.onboarding.JobGroup;
import com.handy.portal.util.DateTimeUtils;

/**
 * This is a custom view that holds a collection of HandyJobView. It has a title to label
 * the group
 * <p/>
 * Created by jtse on 4/18/16.
 */
public class HandyJobGroupView extends LinearLayout implements CompoundButton.OnCheckedChangeListener
{

    TextView mTitle;

    int mMargin;
    int mMarginHalf;
    JobGroup mJobs;

    OnJobChangeListener mOnJobChangeListener;

    public HandyJobGroupView(Context context)
    {
        super(context);
        init();
    }

    public void init()
    {

        mMargin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        mMarginHalf = getResources().getDimensionPixelSize(R.dimen.default_margin_half);

        setOrientation(LinearLayout.VERTICAL);

        mTitle = new TextView(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(mMargin, 0, mMargin, 0);
        mTitle.setLayoutParams(layoutParams);
        mTitle.setTextAppearance(getContext(), R.style.TextView_Small);


        addView(mTitle);

        setPadding(0, 0, 0, mMargin);
    }


    /**
     * Takes in a date of 2016-04-09 format and converted to
     * <p/>
     * SATURDAY, April 9, 2016
     * TODAY, April 9, 2016
     * TOMORROW, April 9, 2016
     * <p/>
     * format, with the first word bolded.
     *
     * @param date
     * @return
     */
    private String getHtmlFormattedDateString(String date)
    {
        String rval = DateTimeUtils.toJobViewDateString(date);
        int idx = rval.indexOf(" ");

        return "<b>" + rval.substring(0, idx) + "</b>" + rval.substring(idx, rval.length());
    }

    public void bind(JobGroup group)
    {
        mJobs = group;
        mTitle.setText(Html.fromHtml(getHtmlFormattedDateString(group.sanitizedDate)));

        for (Job job : group.jobs)
        {

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(mMargin, mMarginHalf, mMargin, 0);

            OnboardJobView view = new OnboardJobView(getContext());
            view.bind(job);
            view.setLayoutParams(layoutParams);
            view.setOnCheckedChangeListener(this);
            addView(view);
        }
    }

    public void setOnJobChangeListener(final OnJobChangeListener onJobChangeListener)
    {
        mOnJobChangeListener = onJobChangeListener;
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
    {
        if (mOnJobChangeListener != null)
        {
            mOnJobChangeListener.onPriceChanged();
        }
    }

    public interface OnJobChangeListener
    {
        void onPriceChanged();
    }
}
