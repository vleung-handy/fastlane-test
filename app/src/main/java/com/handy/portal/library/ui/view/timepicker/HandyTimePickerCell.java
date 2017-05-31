package com.handy.portal.library.ui.view.timepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.availability.viewmodel.TimePickerViewModel.SelectionType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class HandyTimePickerCell extends FrameLayout {
    private static final SimpleDateFormat HOUR_FORMAT =
            new SimpleDateFormat("HH", Locale.getDefault());

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_HIGHLIGHTED = 1;
    private static final int STATE_SELECTED = 2;

    @BindView(R.id.time_text)
    TextView mTimeText;

    private final int mHour;
    private final TimeClickListener mTimeClickListener;
    private int mState;

    public HandyTimePickerCell(final Context context,
                               final int hour,
                               @NonNull final TimeClickListener timeClickListener) {
        super(context);
        mHour = hour;
        mTimeClickListener = timeClickListener;
        mState = STATE_DEFAULT;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_time_picker_cell, this);
        ButterKnife.bind(this);
        final Date date = DateTimeUtils.parseDateString(String.valueOf(mHour), HOUR_FORMAT);
        mTimeText.setText(DateTimeUtils.formatDateToHour(date));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                mTimeClickListener.onHourClicked(getHour());
            }
        });
    }

    public void reset() {
        mTimeText.setBackground(null);
        mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mState = STATE_DEFAULT;

    }

    public void highlight() {
        mTimeText.setBackgroundResource(R.drawable.circle_inactive_gray);
        mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mState = STATE_HIGHLIGHTED;
    }

    public void select() {
        mTimeText.setBackgroundResource(R.drawable.circle_tertiary_gray);
        mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mState = STATE_SELECTED;
    }

    public void freeze() {
        setClickable(false);
        
        if (mState != STATE_SELECTED) {
            mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.black_pressed));
        }
    }

    public void unfreeze() {
        setClickable(true);
        switch (mState) {
            case STATE_DEFAULT:
            case STATE_HIGHLIGHTED:
                mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                break;
            case STATE_SELECTED:
                mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }

    public void taunt(final SelectionType selectionType) {
        if (mState != STATE_DEFAULT) { return; }
        if (selectionType == SelectionType.START_TIME) {
            mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_darkened_blue));
        }
        if (selectionType == SelectionType.END_TIME) {
            mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.painter_purple));
        }
    }

    public int getHour() {
        return mHour;
    }

    interface TimeClickListener {
        void onHourClicked(int time);
    }
}
