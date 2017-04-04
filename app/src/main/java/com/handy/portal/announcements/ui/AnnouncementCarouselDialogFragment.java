package com.handy.portal.announcements.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.handy.portal.R;
import com.handy.portal.announcements.AnnouncementsManager;
import com.handy.portal.announcements.model.Announcement;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AnnouncementsLog;
import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * a dialog fragment that displays a carousel of announcements
 */
public class AnnouncementCarouselDialogFragment extends InjectedDialogFragment implements AnnouncementView.OnAnnouncementActionButtonClickedListener {

    public static final String TAG = AnnouncementCarouselDialogFragment.class.getName();
    private static final String BUNDLE_KEY_ANNOUNCEMENT_BUNDLE
            = "BUNDLE_KEY_ANNOUNCEMENT_BUNDLE";
    @BindView(R.id.fragment_announcement_carousel_viewpager)
    ViewPager mViewPager;

    @BindView(R.id.fragment_announcement_carousel_page_indicator)
    CirclePageIndicator mCirclePageIndicatorView;

    @BindView(R.id.fragment_announcement_carousel_dismiss_button)
    ImageView mDismissButton;

    @Inject
    PageNavigationManager mPageNavigationManager;

    @Inject
    AnnouncementsManager mAnnouncementsManager;

    @Inject
    EventBus mEventBus;

    /**
     * epoch timestamp representing when the current announcement was shown
     * for logging purposes only
     */
    private long mCurrentTrackedAnnouncementTimestampShownMs;
    /**
     * refers to the current announcement in view (only one can be shown at a time)
     * for tracking/logging purposes only
     */
    private Announcement mCurrentTrackedAnnouncement;

    public static AnnouncementCarouselDialogFragment newInstance(
            @NonNull List<Announcement> announcements) {

        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_ANNOUNCEMENT_BUNDLE, (Serializable) announcements);
        AnnouncementCarouselDialogFragment fragment = new AnnouncementCarouselDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation_slide_down_up_from_top);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_announcement_carousel, container);
    }


    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        List<Announcement> announcementsWrapper =
                (List<Announcement>) getArguments().getSerializable(BUNDLE_KEY_ANNOUNCEMENT_BUNDLE);

        final PagerAdapter pagerAdapter =
                new PagerAdapter(announcementsWrapper
                );
        mViewPager.setAdapter(pagerAdapter);
        onPageShown(mViewPager.getCurrentItem());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                onPageShown(position);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
        if (mViewPager.getAdapter().getCount() <= 1) {
            mCirclePageIndicatorView.setVisibility(View.GONE); //still want the space
        }
        else {
            mCirclePageIndicatorView.setVisibility(View.VISIBLE);
            mCirclePageIndicatorView.setViewPager(mViewPager);
        }
    }

    private Announcement getAnnouncementAtPosition(int position) {
        return ((PagerAdapter) mViewPager.getAdapter()).getModelAtPosition(position);
    }

    private void onPageShown(final int position) {

        if(mCurrentTrackedAnnouncement != null)
        {
            /*
            if there is a ref to mCurrentTrackedAnnouncement, that means that
            at this point, mCurrentTrackedAnnouncement was dismissed
            (swiped away or "next" button pressed)
             */
            final long timeElapsedSinceTrackedAnnouncementShownMs =
                    System.currentTimeMillis() - mCurrentTrackedAnnouncementTimestampShownMs;

            mEventBus.post(new LogEvent.AddLogEvent(new AnnouncementsLog.Dismissed(
                    mCurrentTrackedAnnouncement.getId(),
                    timeElapsedSinceTrackedAnnouncementShownMs
            )));
        }


        final Announcement announcement = getAnnouncementAtPosition(position);

        //only show dismiss button if we are on last item
        if (position == mViewPager.getAdapter().getCount() - 1) {
            mDismissButton.setVisibility(View.VISIBLE);
        }
        else {
            mDismissButton.setVisibility(View.GONE);
        }

        mAnnouncementsManager.markAnnouncementAsShown(announcement);
        mEventBus.post(new LogEvent.AddLogEvent(new AnnouncementsLog.Shown(
                announcement.getId()
        )));

        //update the current tracked announcement reference for logging purposes
        mCurrentTrackedAnnouncement = announcement;
        //update the current tracked announcement shown timestamp for logging purposes
        mCurrentTrackedAnnouncementTimestampShownMs = System.currentTimeMillis();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        //our current announcements set is no longer valid
        mAnnouncementsManager.invalidateCachedAnnouncements();
        //prefetch announcements so we can quickly show them next time
        mAnnouncementsManager.updateCachedAnnouncements(null);
        super.onDismiss(dialog);
    }

    @OnClick(R.id.fragment_announcement_carousel_dismiss_button)
    public void onDismissButtonClicked() {
        dismiss();
    }

    @Override
    public void onAnnouncementActionButtonClicked(@NonNull final Announcement announcement) {

        if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1) {
            //just dismiss the carousel if we're on the last item
            dismiss();
        }
        else {
            //otherwise just navigate to the next item

            //this triggers the onPageChangeListener and eventually the onPageShown() method
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        }
    }

    private final class PagerAdapter extends android.support.v4.view.PagerAdapter {

        private final AnnouncementView[] mAnnouncementViews;
        private final Announcement[] mAnnouncements;

        PagerAdapter(@NonNull List<Announcement> announcements) {
            this(announcements.toArray(new Announcement[announcements.size()]));
        }

        PagerAdapter(@NonNull Announcement[] announcements) {
            super();
            mAnnouncements = announcements;
            mAnnouncementViews = new AnnouncementView[announcements.length];
        }

        @Override
        public final int getCount() {
            return mAnnouncementViews.length;
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            if (mAnnouncementViews[position] == null) {
                AnnouncementView announcementView = new AnnouncementView(getContext());

                //server doesn't send the action text back to us
                String actionButtonText = getResources().getString(
                        position == (getCount() - 1) ?
                                R.string.announcements_dismiss_button : R.string.announcements_next_button);
                announcementView.update(
                        getModelAtPosition(position),
                        AnnouncementCarouselDialogFragment.this,
                        actionButtonText
                );
                mAnnouncementViews[position] = announcementView;
            }
            container.addView(mAnnouncementViews[position]);
            return mAnnouncementViews[position];
        }

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
            mAnnouncementViews[position] = null;
        }

        public Announcement getModelAtPosition(int position) {
            return mAnnouncements[position];
        }
    }
}
