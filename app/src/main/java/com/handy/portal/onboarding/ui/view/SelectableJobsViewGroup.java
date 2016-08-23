package com.handy.portal.onboarding.ui.view;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;
import com.handy.portal.onboarding.viewmodel.BookingsWrapperViewModel;

public class SelectableJobsViewGroup extends LinearLayout
        implements CompoundButton.OnCheckedChangeListener
{
    private TextView mTitle;
    private int mMargin;
    private int mMarginHalf;
    private OnJobCheckedChangedListener mOnJobCheckedChangedListener;

    public SelectableJobsViewGroup(Context context)
    {
        super(context);
        init();
    }

    @SuppressWarnings("deprecation")
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
        if (Build.VERSION.SDK_INT < 23)
        {
            mTitle.setTextAppearance(getContext(), R.style.TextView_Small);
        }
        else
        {
            mTitle.setTextAppearance(R.style.TextView_Small);
        }
        mTitle.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
        addView(mTitle);

        setPadding(0, 0, 0, mMargin);
    }

    public void bind(final BookingsWrapperViewModel model)
    {
        final String sanitizedDate = model.getSanitizedDate();
        if (sanitizedDate != null)
        {
            mTitle.setText(Html.fromHtml(
                    DateTimeUtils.getHtmlFormattedDateString(sanitizedDate)));
        }
        else
        {
            mTitle.setVisibility(GONE);
        }

        for (final BookingViewModel bookingViewModel : model.getBookingViewModels())
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(mMargin, mMarginHalf, mMargin, 0);

            SelectableJobView view = new SelectableJobView(getContext());
            view.bind(bookingViewModel);
            view.setLayoutParams(layoutParams);
            view.setOnCheckedChangeListener(this);
            addView(view);
        }
    }

    public void setOnJobCheckedChangedListener(
            final OnJobCheckedChangedListener onJobCheckedChangedListener)
    {
        mOnJobCheckedChangedListener = onJobCheckedChangedListener;
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
    {
        if (mOnJobCheckedChangedListener != null)
        {
            mOnJobCheckedChangedListener.onJobCheckedChanged();
        }
    }

    public interface OnJobCheckedChangedListener
    {
        void onJobCheckedChanged();
    }
}
