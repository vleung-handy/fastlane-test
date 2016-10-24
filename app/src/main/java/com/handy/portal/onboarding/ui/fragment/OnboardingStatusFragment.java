package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.model.Address;
import com.handy.portal.model.Designation;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.onboarding.model.status.LearningLink;
import com.handy.portal.onboarding.model.status.LearningLinkDetails;
import com.handy.portal.onboarding.model.status.StatusButton;
import com.handy.portal.onboarding.model.subflow.StatusHeader;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.supplies.SuppliesInfo;
import com.handy.portal.onboarding.ui.activity.FirstDayActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class OnboardingStatusFragment extends OnboardingSubflowUIFragment
{
    @BindView(R.id.jobs_collapsible)
    CollapsibleContentLayout mJobsCollapsible;
    @BindView(R.id.supplies_collapsible)
    CollapsibleContentLayout mSuppliesCollapsible;
    @BindView(R.id.shipping_view)
    SimpleContentLayout mShippingView;
    @BindView(R.id.payment_view)
    LabelAndValueView mPaymentView;
    @BindView(R.id.order_total_view)
    LabelAndValueView mOrderTotalView;
    @BindView(R.id.links_title)
    TextView mLinksTitle;
    @BindView(R.id.links_container)
    ViewGroup mLinksContainer;
    @BindView(R.id.tips_card)
    RelativeLayout mTipsCard;

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
        initButtonColor();
    }

    private void initButtonColor()
    {
        final StatusButton button = mStatusData.getButton();
        if (button != null)
        {
            if (button.getType() == StatusButton.Type.ERROR)
            {
                final int red = ContextCompat.getColor(getContext(), R.color.error_red);
                mGroupPrimaryButton.setBackgroundColor(red);
                mSingleActionButton.setBackgroundColor(red);
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        bus.post(new LogEvent.AddLogEvent(
                new NativeOnboardingLog.StatusPageShown(mStatusData.getApplicationStatus())));
        if (mProviderPersonalInfo == null)
        {
            requestProviderProfile();
        }
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(
            final ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        hideLoadingOverlay();
        mProviderPersonalInfo = event.providerProfile.getProviderPersonalInfo();
        initJobsView();
        initSuppliesView();
        initTipsView();
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
                }, false);
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
                    DateTimeUtils.formatDayOfWeekMonthDate(claims.get(0).getStartDate());
            mJobsCollapsible.setHeader(R.drawable.ic_onboarding_schedule,
                    getResources().getQuantityString(R.plurals.number_of_jobs_formatted,
                            claims.size(), claims.size()),
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

    /**
     * This view is a link to a "tips" page. Only show this if the "status" is completed.
     */
    private void initTipsView()
    {
        if (mStatusData.isFirstJobContentEnabled())
        {
            mTipsCard.setVisibility(View.VISIBLE);
        }
        else
        {
            mTipsCard.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tips_card)
    public void launchTips()
    {
        startActivity(new Intent(getActivity(), FirstDayActivity.class));
    }

    private void initSuppliesView()
    {
        final SuppliesInfo suppliesInfo = mStatusData.getSuppliesInfo();
        if (suppliesInfo != null
                && suppliesInfo.getDesignation() != null
                && suppliesInfo.getDesignation() != Designation.UNDECIDED)
        {
            final String title = getString(R.string.supply_kit);
            switch (suppliesInfo.getDesignation())
            {
                case YES:
                    mSuppliesCollapsible.setHeader(R.drawable.ic_onboarding_supplies, title,
                            getString(R.string.order_details));
                    break;
                case NO:
                    mSuppliesCollapsible.setHeader(R.drawable.ic_onboarding_supplies, title,
                            getString(R.string.not_requested));
                    mSuppliesCollapsible.freeze();
                    break;
                default:
                    break;
            }

            // Shipping
            final Address address = mProviderPersonalInfo.getAddress();
            mShippingView.setContent(getString(R.string.ship_to),
                    mProviderPersonalInfo.getFullName() + "\n" + address.getShippingAddress());

            // Order/Fee Total
            final String orderTotalTitle = getString(providerHasPaymentMethod() ?
                    R.string.order_total : R.string.supplies_fee);
            mOrderTotalView.setContent(orderTotalTitle, suppliesInfo.getCost());

            // Payment
            if (providerHasPaymentMethod())
            {
                mPaymentView.setContent(getString(R.string.payment),
                        getString(R.string.card_info_formatted, getString(R.string.card),
                                mProviderPersonalInfo.getCardLast4()));
            }
            else
            {
                mPaymentView.setVisibility(View.GONE);
            }
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

    private boolean providerHasPaymentMethod()
    {
        final String cardLast4 = mProviderPersonalInfo.getCardLast4();
        return !TextUtils.isNullOrEmpty(cardLast4);
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
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.HelpLinkSelected(url)));
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
        if (statusButton != null
                && (!mIsSingleStepMode || !TextUtils.isNullOrEmpty(statusButton.getUrl()))
                && !TextUtils.isNullOrEmpty(statusButton.getTitle()))
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
        if (mStatusData.getHeader() != null)
        {
            final StatusHeader.ImageType imageType = mStatusData.getHeader().getImageType();
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
        }
        return HeaderImageTypes.NONE;
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        if (mStatusData.getHeader() != null)
        {
            return mStatusData.getHeader().getTitle();
        }
        return null;
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        if (mStatusData.getHeader() != null)
        {
            return mStatusData.getHeader().getDescription();
        }
        return null;
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
            bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.StatusPageSubmitted(
                    mStatusData.getApplicationStatus())));
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

    @Override
    protected void cancel(@NonNull final Intent data)
    {
        data.putExtra(BundleKeys.FORCE_FINISH, true);
        super.cancel(data);
    }
}
