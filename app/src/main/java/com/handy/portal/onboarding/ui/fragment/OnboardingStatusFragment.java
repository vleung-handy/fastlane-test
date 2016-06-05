package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.PendingBookingElementView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.library.ui.view.CollapsibleContentLayout;
import com.handy.portal.library.ui.view.LabelAndValueView;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.model.Address;
import com.handy.portal.model.Designation;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.onboarding.SuppliesInfo;
import com.handy.portal.onboarding.model.status.ApplicationStatus;
import com.handy.portal.onboarding.model.status.LearningLink;
import com.handy.portal.onboarding.model.status.LearningLinkDetails;
import com.handy.portal.onboarding.model.status.StatusButton;
import com.handy.portal.onboarding.model.subflow.OnboardingHeader;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;

public class OnboardingStatusFragment extends OnboardingSubflowFragment
{
    @Bind(R.id.jobs_collapsible)
    CollapsibleContentLayout mJobsCollapsible;
    @Bind(R.id.supplies_collapsible)
    CollapsibleContentLayout mSuppliesCollapsible;
    @Bind(R.id.shipping_view)
    SimpleContentLayout mShippingView;
    @Bind(R.id.payment_view)
    LabelAndValueView mPaymentView;
    @Bind(R.id.order_total_view)
    LabelAndValueView mOrderTotalView;
    @Bind(R.id.links_title)
    TextView mLinksTitle;
    @Bind(R.id.links_container)
    ViewGroup mLinksContainer;

    private SubflowData mStatusData;
    private ProviderPersonalInfo mProviderPersonalInfo;

    public static OnboardingStatusFragment newInstance()
    {
        return new OnboardingStatusFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mStatusData = (SubflowData) getArguments().getSerializable(BundleKeys.SUBFLOW_DATA);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mMainContentContainer.setVisibility(View.GONE);
        final ApplicationStatus status = mStatusData.getApplicationStatus();
        if (status == ApplicationStatus.REJECTED || status == ApplicationStatus.UNVERIFIED)
        {
            final int red = getResources().getColor(R.color.error_red);
            mGroupPrimaryButton.setBackgroundColor(red);
            mSingleActionButton.setBackgroundColor(red);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mProviderPersonalInfo == null)
        {
            requestProviderProfile();
        }
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(
            final ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        hideLoadingOverlay();
        mProviderPersonalInfo = event.providerProfile.getProviderPersonalInfo();
        initJobsView();
        initSuppliesView();
        initLearningLinksView();
        mMainContentContainer.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onReceiveProviderProfileError(
            final ProfileEvent.ReceiveProviderProfileError event)
    {
        hideLoadingOverlay();
        showError(event.error.getMessage(), getString(R.string.retry),
                new ErrorActionOnClickListener()
                {
                    @Override
                    public void onClick(final Snackbar snackbar)
                    {
                        requestProviderProfile();
                    }
                });
    }

    private void requestProviderProfile()
    {
        showLoadingOverlay();
        bus.post(new ProfileEvent.RequestProviderProfile(true));
    }

    private void initJobsView()
    {
        final ArrayList<Booking> claims = mStatusData.getClaims();
        if (claims != null && !claims.isEmpty())
        {
            final String firstBookingDateFormatted =
                    DateTimeUtils.formatDayOfWeekMonthDateYear(claims.get(0).getStartDate());
            mJobsCollapsible.setHeader(R.drawable.ic_onboarding_schedule,
                    getString(R.string.number_of_jobs_formatted, claims.size()),
                    firstBookingDateFormatted);

            final ViewGroup container = mJobsCollapsible.getContentViewContainer();
            container.removeAllViews();
            for (final Booking booking : claims)
            {
                final PendingBookingElementView elementView = new PendingBookingElementView();
                elementView.initView(getActivity(), booking, null, container);
                container.addView(elementView.getAssociatedView());
            }
        }
        else
        {
            mJobsCollapsible.setVisibility(View.GONE);
        }
    }

    private void initSuppliesView()
    {
        final SuppliesInfo suppliesInfo = mStatusData.getSuppliesInfo();
        if (suppliesInfo != null
                && suppliesInfo.getDesignation() != null
                && suppliesInfo.getDesignation() != Designation.UNDECIDED)
        {
            final String title = getString(R.string.supply_starter_kit);
            switch (suppliesInfo.getDesignation())
            {
                case YES:
                    mSuppliesCollapsible.setHeader(R.drawable.ic_onboarding_supplies, title, null);
                    break;
                case NO:
                    mSuppliesCollapsible.setHeader(R.drawable.ic_onboarding_supplies, title,
                            getString(R.string.not_requested));
                    mSuppliesCollapsible.freeze();
                    break;
                default:
                    break;
            }

            final Address address = mProviderPersonalInfo.getAddress();
            mShippingView.setContent(getString(R.string.ship_to),
                    mProviderPersonalInfo.getFullName() + "\n" + address.getShippingAddress());
            mPaymentView.setContent(getString(R.string.payment),
                    getString(R.string.card_info_formatted, getString(R.string.card),
                            mProviderPersonalInfo.getCardLast4()));
            mOrderTotalView.setContent(getString(R.string.order_total), suppliesInfo.getCost());
        }
        else
        {
            mSuppliesCollapsible.setVisibility(View.GONE);
        }
    }

    private void initLearningLinksView()
    {
        final LearningLinkDetails learningLinkDetails = mStatusData.getLearningLinkDetails();
        if (learningLinkDetails != null
                && learningLinkDetails.getLearningLinks() != null
                && !learningLinkDetails.getLearningLinks().isEmpty())
        {
            mLinksTitle.setText(learningLinkDetails.getTitle());
            for (final LearningLink learningLink : learningLinkDetails.getLearningLinks())
            {
                addLinkTextView(learningLink.getTitle(), learningLink.getUrl());
            }
        }
        else
        {
            mLinksTitle.setVisibility(View.GONE);
            mLinksContainer.setVisibility(View.GONE);
        }
    }

    private void addLinkTextView(final String text, @NonNull final String url)
    {
        final TextView view = (TextView) LayoutInflater.from(getActivity())
                .inflate(R.layout.view_link_text, mLinksContainer, false);
        view.setText(text);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Utils.safeLaunchIntent(intent, getActivity());
            }
        });
        mLinksContainer.addView(view);
    }

    @Override
    protected int getButtonType()
    {
        final StatusButton statusButton = mStatusData.getButton();
        if (statusButton != null && !TextUtils.isNullOrEmpty(statusButton.getTitle()))
        {
            return ButtonTypes.SINGLE_FIXED;
        }
        else
        {
            return ButtonTypes.NONE;
        }
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_onboarding_status;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.welcome);
    }

    @Override
    protected int getHeaderImageType()
    {
        final OnboardingHeader.ImageType imageType = mStatusData.getHeader().getImageType();
        if (imageType != null)
        {
            switch (imageType)
            {
                case WELCOME:
                    return HeaderImageTypes.WELCOME;
                case COMPLETE:
                    return HeaderImageTypes.COMPLETE;
                case ERROR:
                    return HeaderImageTypes.ERROR;
                default:
                    return HeaderImageTypes.NONE;
            }
        }
        return HeaderImageTypes.NONE;
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return mStatusData.getHeader().getTitle();
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return mStatusData.getHeader().getDescription();
    }

    @Override
    protected String getPrimaryButtonText()
    {
        final StatusButton statusButton = mStatusData.getButton();
        if (statusButton != null)
        {
            return statusButton.getTitle();
        }
        else
        {
            return super.getPrimaryButtonText();
        }
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        final StatusButton statusButton = mStatusData.getButton();
        if (statusButton != null)
        {
            final String url = statusButton.getUrl();
            if (!TextUtils.isNullOrEmpty(url))
            {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Utils.safeLaunchIntent(intent, getActivity());
            }
            else
            {
                terminate(new Intent());
            }
        }
    }
}
