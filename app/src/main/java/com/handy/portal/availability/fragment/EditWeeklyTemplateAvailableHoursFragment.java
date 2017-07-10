package com.handy.portal.availability.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.availability.AvailabilityEvent;
import com.handy.portal.availability.manager.AvailabilityManager;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.availability.view.AvailableHoursWithDateView;
import com.handy.portal.availability.view.WeeklyAvailableHoursView;
import com.handy.portal.availability.viewmodel.AvailableHoursViewModel;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProAvailabilityLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class EditWeeklyTemplateAvailableHoursFragment extends EditWeeklyAvailableHoursFragment {
    @Inject
    AvailabilityManager mAvailabilityManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.available_hours_content)
    ViewGroup mAvailableHoursContent;

    private WeeklyAvailableHoursView mWeeklyAvailableHoursView;
    private String mFlowContext;
    private Availability.Wrapper.TemplateTimelines mTemplateTimelinesWrapper;

    @Override
    protected int getContentResId() {
        return 0;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        mFlowContext = getArguments().getString(BundleKeys.FLOW_CONTEXT);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWeeklyAvailableHoursView == null) {
            requestAvailableHours();
        }
        else if (mAvailableHoursContent.getChildCount() == 0) {
            mScrollView.setVisibility(View.VISIBLE);
            ((ViewGroup) mWeeklyAvailableHoursView.getParent())
                    .removeView(mWeeklyAvailableHoursView);
            mAvailableHoursContent.addView(mWeeklyAvailableHoursView);
        }
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void requestAvailableHours() {
        mRefreshLayout.setRefreshing(true);
        mAvailabilityManager.getAvailabilityTemplate(
                new FragmentSafeCallback<Availability.Wrapper.TemplateTimelines>(this) {
                    @Override
                    public void onCallbackSuccess(
                            final Availability.Wrapper.TemplateTimelines templateTimelinesWrapper
                    ) {
                        mRefreshLayout.setRefreshing(false);
                        mFetchErrorView.setVisibility(View.GONE);
                        mScrollView.setVisibility(View.VISIBLE);
                        mTemplateTimelinesWrapper = templateTimelinesWrapper;
                        displayAvailableHours();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        mRefreshLayout.setRefreshing(false);
                        mFetchErrorView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void displayAvailableHours() {
        mAvailableHoursContent.removeAllViews();
        mWeeklyAvailableHoursView = new WeeklyAvailableHoursView(
                getActivity(),
                getViewModelsFromTemplateTimelines(),
                new WeeklyAvailableHoursView.CellClickListener() {
                    @Override
                    public void onCellClicked(final AvailableHoursViewModel viewModel) {
                        navigateToEditAvailableHours(viewModel);
                    }
                }
        );
        mAvailableHoursContent.addView(mWeeklyAvailableHoursView);
    }

    private void navigateToEditAvailableHours(final AvailableHoursViewModel viewModel) {
        final Availability.TemplateTimeline.Day day =
                (Availability.TemplateTimeline.Day) viewModel.getIdentifier();
        bus.post(new LogEvent.AddLogEvent(
                new ProAvailabilityLog.SetTemplateDayAvailabilitySelected(mFlowContext, day)
        ));
        final Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.FLOW_CONTEXT, mFlowContext);
        bundle.putSerializable(BundleKeys.MODE, EditAvailableHoursFragment.Mode.TEMPLATE);
        bundle.putSerializable(BundleKeys.DAY, day);
        bundle.putSerializable(BundleKeys.TIMELINE,
                mTemplateTimelinesWrapper.findTemplateTimelineForDay(day));
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.EDIT_AVAILABLE_HOURS, bundle, null, true);
    }

    private List<AvailableHoursViewModel> getViewModelsFromTemplateTimelines() {
        final List<AvailableHoursViewModel> viewModels = new ArrayList<>();
        for (final Availability.TemplateTimeline.Day day :
                Availability.TemplateTimeline.Day.values()) {
            final Availability.TemplateTimeline templateTimeline =
                    mTemplateTimelinesWrapper.findTemplateTimelineForDay(day);
            viewModels.add(getAvailableHoursViewModel(day, templateTimeline));
        }
        return viewModels;
    }

    @NonNull
    private AvailableHoursViewModel getAvailableHoursViewModel(
            final Availability.TemplateTimeline.Day day,
            final Availability.TemplateTimeline templateTimeline
    ) {
        return new AvailableHoursViewModel(
                getString(day.getDisplayStringResId()),
                templateTimeline != null ? templateTimeline.getIntervals() : null,
                true,
                day
        );
    }

    @Subscribe
    public void onAvailabilityTemplateTimelineUpdated(
            final AvailabilityEvent.TemplateTimelineUpdated event
    ) {
        final Availability.TemplateTimeline.Day day = event.getTimeline().getDay();
        final AvailableHoursWithDateView view =
                mWeeklyAvailableHoursView.getViewWithIdentifier(day);
        view.update(getAvailableHoursViewModel(day, event.getTimeline()));
    }
}
