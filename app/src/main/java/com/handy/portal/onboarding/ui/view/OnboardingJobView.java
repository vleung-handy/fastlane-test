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

import com.handy.portal.R;
import com.handy.portal.bookings.ui.element.AvailableBookingElementView;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingJobView extends FrameLayout implements CompoundButton.OnCheckedChangeListener
{
    @BindView(R.id.check_box)
    CheckBox mCheckBox;
    @BindView(R.id.job_container)
    ViewGroup mJobContainer;

    private Drawable mCheckedDrawable;
    private Drawable mUncheckedDrawable;

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    private BookingViewModel mBookingViewModel;

    public OnboardingJobView(final Context context)
    {
        super(context);
        init();
    }

    public OnboardingJobView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public OnboardingJobView(final Context context, final AttributeSet attrs,
                             final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.onboarding_job_check_box, this);
        ButterKnife.bind(this);

        mUncheckedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.border_gray_bg_white);
        mCheckedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.border_green_bg_white);

        setBackground(mUncheckedDrawable);
        mCheckBox.setOnCheckedChangeListener(this);
        setClickable(true);

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCheckBox.setChecked(!mBookingViewModel.isSelected());
            }
        });
    }

    public void setOnCheckedChangeListener(final CompoundButton.OnCheckedChangeListener onCheckedChangeListener)
    {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void bind(final BookingViewModel bookingViewModel)
    {
        mBookingViewModel = bookingViewModel;

        mJobContainer.removeAllViews();
        final AvailableBookingElementView elementView = new AvailableBookingElementView();
        elementView.initView(getContext(), bookingViewModel.getBooking(), null, mJobContainer);
        final View view = elementView.getAssociatedView();
        hideServiceText(view);
        view.setBackground(null);
        mJobContainer.addView(view);

        mCheckBox.setChecked(bookingViewModel.isSelected());
    }

    private void hideServiceText(final View bookingView)
    {
        final View view = bookingView.findViewById(R.id.booking_entry_service_text);
        if (view != null)
        {
            view.setVisibility(GONE);
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
    {
        mBookingViewModel.setSelected(isChecked);
        if (isChecked)
        {
            this.setBackground(mCheckedDrawable);
        }
        else
        {
            this.setBackground(mUncheckedDrawable);
        }
        if (mOnCheckedChangeListener != null)
        {
            mOnCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
        }
    }
}
