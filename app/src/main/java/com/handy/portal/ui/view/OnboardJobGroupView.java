package com.handy.portal.ui.view;

import android.content.Context;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.onboarding.BookingViewModel;
import com.handy.portal.model.onboarding.BookingsWrapperViewModel;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.FontUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a custom view that holds a collection of HandyJobView. It has a title to label
 * the group
 * <p/>
 */
public class OnboardJobGroupView extends LinearLayout implements CompoundButton.OnCheckedChangeListener
{

    private TextView mTitle;
    private int mMargin;
    private int mMarginHalf;
    private BookingsWrapperViewModel mViewModel;
    private OnJobChangeListener mOnJobChangeListener;

    public OnboardJobGroupView(Context context)
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
        mTitle.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
        addView(mTitle);

        setPadding(0, 0, 0, mMargin);
    }


    public void bind(BookingsWrapperViewModel model)
    {
        mViewModel = model;

        mTitle.setText(Html.fromHtml(DateTimeUtils.getHtmlFormattedDateString(mViewModel.getSanitizedDate())));

        for (BookingViewModel bookingViewModel : mViewModel.mBookingViewModels)
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(mMargin, mMarginHalf, mMargin, 0);

            OnboardJobView view = new OnboardJobView(getContext());
            view.bind(bookingViewModel);
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

    /**
     * Returns a copy of all the jobs that are selected
     *
     * @return
     */
    public List<BookingViewModel> getSelectedJobs()
    {
        List<BookingViewModel> rval = new ArrayList<>(mViewModel.mBookingViewModels);

        for (BookingViewModel j : rval)
        {
            if (!j.selected)
            {
                rval.remove(j);
            }
        }

        return rval;
    }

    public interface OnJobChangeListener
    {
        void onPriceChanged();
    }
}
