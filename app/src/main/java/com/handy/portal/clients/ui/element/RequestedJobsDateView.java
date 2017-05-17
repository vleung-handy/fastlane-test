package com.handy.portal.clients.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestedJobsDateView extends FrameLayout {
    @BindView(R.id.pro_requested_jobs_list_group_title_text)
    protected TextView titleText;

    public RequestedJobsDateView(Context context) {
        super(context);
        init();
    }

    public RequestedJobsDateView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.element_pro_requested_jobs_list_group_header,
                this, false);
        addView(view);
        setOnClickListener(null); //don't want this view to be clickable
        ButterKnife.bind(this);
    }

    public void updateDisplay(@NonNull Date date, @NonNull Context context) {
        String formattedDate = DateTimeUtils.getTodayTomorrowStringByStartDate(date, context)
                + DateTimeUtils.formatDayOfWeekMonthDate(date);
        titleText.setText(formattedDate);
    }

}

