package com.handy.portal.bookings.ui.element;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewDateButton extends LinearLayout {
    @BindView(R.id.month_text)
    TextView mMonthText;
    @BindView(R.id.day_of_month_holder)
    ViewGroup mDayOfMonthHolder;
    @BindView(R.id.day_of_month_text)
    TextView mDayOfMonthText;
    @BindView(R.id.schedule_indicator)
    ImageView mScheduleIndicator;
    @BindColor(R.color.handy_darkened_blue)
    int mDarkBlue;
    @BindColor(R.color.handy_blue)
    int mBlue;
    @BindColor(R.color.black)
    int mBlack;
    @BindColor(R.color.white)
    int mWhite;
    @BindColor(R.color.disabled)
    int mGray;

    private final Date mDate;
    private final int mDayOfMonth;
    private boolean mIsSelected = false;
    private boolean mIsEnabled;
    private SelectionChangedListener mSelectionChangedListener;
    private OnLayoutChangeListener mLayoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(final View view, final int left, final int top,
                                   final int right, final int bottom, final int oldLeft,
                                   final int oldTop, final int oldRight, final int oldBottom) {
            if (mDayOfMonthHolder.getHeight() < mDayOfMonthHolder.getWidth()) {
                // hacky way to make sure the background comes out as a circle
                mDayOfMonthHolder.getLayoutParams().width = mDayOfMonthHolder.getHeight();
                mDayOfMonthHolder.requestLayout();
            }
        }
    };

    public NewDateButton(final Context context, final Date date) {
        super(context);
        mDate = date;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_date_button_new, this);
        final LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        setLayoutParams(layoutParams);
        setBackgroundResource(R.color.white);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);
        setLayoutTransition(new LayoutTransition());

        ButterKnife.bind(this);

        mDayOfMonthText.setText(String.valueOf(mDayOfMonth));
        final boolean isFirstOfTheMonth = mDayOfMonth == 1;
        mMonthText.setVisibility(isFirstOfTheMonth ? VISIBLE : INVISIBLE);
        if (isFirstOfTheMonth) {
            mMonthText.setText(DateTimeUtils.getMonthShortName(mDate));
        }
        refreshState();
        if (DateTimeUtils.isDaysPast(mDate)) {
            mMonthText.setTextColor(mGray);
            mDayOfMonthText.setTextColor(mGray);
            mIsEnabled = false;
        }
        else {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    setSelected(true);
                }
            });
            mIsEnabled = true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnLayoutChangeListener(mLayoutChangeListener);
    }

    private void refreshState() {
        if (mIsSelected) {
            mMonthText.setVisibility(INVISIBLE);
            mDayOfMonthText.setTextColor(mWhite);
            mScheduleIndicator.setImageResource(R.drawable.circle_white);
            mDayOfMonthHolder.setBackgroundResource(R.drawable.circle_handy_blue);
        }
        else {
            mMonthText.setVisibility(mDayOfMonth == 1 ? VISIBLE : INVISIBLE);
            mDayOfMonthText.setTextColor(DateTimeUtils.isToday(mDate) ? mDarkBlue : mBlack);
            mScheduleIndicator.setImageResource(R.drawable.circle_handy_blue);
            mDayOfMonthHolder.setBackground(null);
        }
    }

    public Date getDate() {
        return mDate;
    }

    public void select() {
        setSelected(true);
    }

    public void setSelected(final boolean isSelected) {
        if (isSelected() == isSelected || !isEnabled()) {
            return;
        }
        mIsSelected = isSelected;
        refreshState();
        if (mSelectionChangedListener != null) {
            mSelectionChangedListener.onSelectionChanged(this);
        }
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void setSelectionChangedListener(final SelectionChangedListener selectionChangedListener) {
        mSelectionChangedListener = selectionChangedListener;
    }

    public void showClaimIndicator() {
        mScheduleIndicator.setVisibility(VISIBLE);
    }

    public interface SelectionChangedListener {
        void onSelectionChanged(NewDateButton targetButton);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeOnLayoutChangeListener(mLayoutChangeListener);
        super.onDetachedFromWindow();
    }
}
