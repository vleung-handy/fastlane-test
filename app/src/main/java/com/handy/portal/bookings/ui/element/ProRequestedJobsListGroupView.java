package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.library.util.DateTimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProRequestedJobsListGroupView extends FrameLayout
{
    @BindView(R.id.pro_requested_jobs_list_group_title_text)
    protected TextView titleText;

    public ProRequestedJobsListGroupView(Context context)
    {
        super(context);
        init();
    }

    public ProRequestedJobsListGroupView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init();
    }

    private void init()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.element_pro_requested_jobs_list_group_header,
                this, false);
        addView(view);
        setOnClickListener(null); //don't want this view to be clickable
        ButterKnife.bind(this);
    }

    public void updateDisplay(@NonNull BookingsWrapper bookingsWrapper,
                              @NonNull Context context)
    {
        String formattedDate =
                DateTimeUtils.getTodayTomorrowStringByStartDate(
                        bookingsWrapper.getDate(),
                        context)
                + DateTimeUtils.formatDayOfWeekMonthDate(bookingsWrapper.getDate());
        titleText.setText(formattedDate);
    }

}

