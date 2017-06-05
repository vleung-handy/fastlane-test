package com.handy.portal.availability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.availability.viewmodel.AvailableHoursViewModel;

import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;

public class WeeklyAvailableHoursView extends LinearLayout {
    private final CellClickListener mCellClickListener;
    private final List<AvailableHoursViewModel> mViewModels;

    @BindDimen(R.dimen.default_padding)
    int mDefaultPadding;

    public WeeklyAvailableHoursView(
            final Context context,
            final List<AvailableHoursViewModel> viewModels,
            final CellClickListener cellClickListener
    ) {
        super(context);
        mViewModels = viewModels;
        mCellClickListener = cellClickListener;
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        for (final AvailableHoursViewModel viewModel : mViewModels) {
            final AvailableHoursWithDateView view = new AvailableHoursWithDateView(
                    getContext(), viewModel
            );
            view.setRowPadding(mDefaultPadding, mDefaultPadding);
            view.setBackgroundResource(R.drawable.border_gray_bottom);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mCellClickListener.onCellClicked(viewModel);
                }
            });
            addView(view);
        }
    }

    @Nullable
    public AvailableHoursWithDateView getViewWithIdentifier(final Object identifier) {
        for (int i = 0; i < getChildCount(); i++) {
            final AvailableHoursWithDateView view = (AvailableHoursWithDateView) getChildAt(i);
            if (view.getViewModel().getIdentifier().equals(identifier)) {
                return view;
            }
        }
        return null;
    }

    public interface CellClickListener {
        void onCellClicked(final AvailableHoursViewModel viewModel);
    }
}
