package com.handy.portal.proavailability.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.model.ProviderAvailability;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.view.WeeklyAvailableHoursCardView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SendAvailableHoursFragment extends ActionBarFragment {

    @Inject
    ProviderManager mProviderManager;

    @BindView(R.id.header_title)
    TextView mHeaderTitle;
    @BindView(R.id.header_subtitle)
    TextView mHeaderSubtitle;
    @BindView(R.id.availability_pager)
    ViewPager mAvailabilityPager;
    @BindDimen(R.dimen.default_margin_half)
    int mGapMargin;
    @BindDimen(R.dimen.default_margin_double)
    int mCardMargin;

    private Booking mBooking;
    private ProviderAvailability mAvailability;
    private HashMap<Date, DailyAvailabilityTimeline> mUpdatedAvailabilityTimelines;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view =
                inflater.inflate(R.layout.fragment_send_available_hours, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.send_alternate_times, true);
        initHeader();
        initAvailabilityPager();
    }

    private void initHeader() {
        final Booking.RequestAttributes requestAttributes = mBooking.getRequestAttributes();
        if (requestAttributes != null) {
            mHeaderTitle.setText(requestAttributes.hasCustomer() ?
                    requestAttributes.getCustomerName() : requestAttributes.getDetailsTitle());
        }
        mHeaderSubtitle.setText(getString(R.string.original_time_formatted,
                DateTimeUtils.formatDateShortDayOfWeekShortMonthDay(mBooking.getStartDate()),
                DateTimeUtils.formatDateTo12HourClock(mBooking.getStartDate()),
                DateTimeUtils.formatDateTo12HourClock(mBooking.getEndDate())));
    }

    private void initAvailabilityPager() {
        if (mAvailability == null) {
            loadProviderAvailability();
        }
        else {
            mAvailabilityPager.setAdapter(
                    new WeeklyAvailableHoursPagerAdapter(getActivity(), mAvailability));
            mAvailabilityPager.setPageMargin(mGapMargin - (mCardMargin * 2));
        }
    }

    private void loadProviderAvailability() {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        dataManager.getProviderAvailability(mProviderManager.getLastProviderId(),
                new FragmentSafeCallback<ProviderAvailability>(this) {
                    @Override
                    public void onCallbackSuccess(final ProviderAvailability providerAvailability) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        mAvailability = providerAvailability;
                        mUpdatedAvailabilityTimelines = new HashMap<>();
                        initAvailabilityPager();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        // TODO: Implement
                    }
                });
    }

    private static class WeeklyAvailableHoursPagerAdapter extends PagerAdapter {
        public static final int[] WEEK_TITLE_RES_IDS = {R.string.current_week, R.string.next_week};
        public static final int POSITION_NOT_FOUND = -1;
        private List<WeeklyAvailableHoursCardView> mViews;

        WeeklyAvailableHoursPagerAdapter(
                final Context context,
                final ProviderAvailability availability
        ) {
            mViews = new ArrayList<>();
            ArrayList<WeeklyAvailabilityTimelinesWrapper> weeklyTimelinesList =
                    availability.getWeeklyAvailabilityTimelinesWrappers();
            for (int i = 0; i < weeklyTimelinesList.size(); i++) {
                final WeeklyAvailabilityTimelinesWrapper weeklyAvailability =
                        weeklyTimelinesList.get(i);
                final int weekTitleResId = i < WEEK_TITLE_RES_IDS.length ?
                        WEEK_TITLE_RES_IDS[i] : R.string.special_empty_string;
                mViews.add(new WeeklyAvailableHoursCardView(context, weekTitleResId,
                        weeklyAvailability, null));
                // TODO: Implement ability to edit
            }
        }

        @Override
        public int getItemPosition(final Object object) {
            int index = mViews.indexOf(object);
            if (index == POSITION_NOT_FOUND) {
                return POSITION_NONE;
            }
            else {
                return index;
            }
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final View view = getItemAt(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(getItemAt(position));
        }

        private View getItemAt(final int position) {
            return mViews.get(position);
        }
    }
}
