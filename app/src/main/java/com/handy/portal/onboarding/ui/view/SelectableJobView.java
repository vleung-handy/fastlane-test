package com.handy.portal.onboarding.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectableJobView extends FrameLayout implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.check_box)
    CheckBox mCheckBox;
    @BindView(R.id.job_container)
    ViewGroup mJobContainer;

    private Drawable mCheckedDrawable;
    private Drawable mUncheckedDrawable;

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    private BookingViewModel mBookingViewModel;

    public SelectableJobView(final Context context) {
        super(context);
        init();
    }

    public SelectableJobView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectableJobView(final Context context, final AttributeSet attrs,
                             final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.onboarding_job_check_box, this);
        ButterKnife.bind(this);

        mUncheckedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.border_gray_bg_white);
        mCheckedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.border_green_bg_white);

        setBackground(mUncheckedDrawable);
        mCheckBox.setOnCheckedChangeListener(this);
        setClickable(true);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckBox.setChecked(!mBookingViewModel.isSelected());
            }
        });
    }

    public void setOnCheckedChangeListener(final CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void bind(final BookingViewModel bookingViewModel,
                     final Class<? extends BookingElementView> viewClass) {
        mBookingViewModel = bookingViewModel;

        mJobContainer.removeAllViews();
        final BookingElementView elementView;
        try {
            elementView = viewClass.newInstance();
        }
        catch (Exception e) {
            // This should never happen!
            Crashlytics.logException(e);
            return;
        }
        elementView.initView(getContext(), bookingViewModel.getBooking(), null, mJobContainer);
        final View view = elementView.getAssociatedView();
        mJobContainer.addView(view);
        mCheckBox.setChecked(bookingViewModel.isSelected());
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        mBookingViewModel.setSelected(isChecked);
        if (isChecked) {
            this.setBackground(mCheckedDrawable);
        }
        else {
            this.setBackground(mUncheckedDrawable);
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
        }
    }
}
