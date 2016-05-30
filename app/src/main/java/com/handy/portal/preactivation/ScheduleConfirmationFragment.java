package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.view.LabelAndValueView;
import com.handy.portal.library.ui.view.SimpleContentLayout;

import butterknife.Bind;

public class ScheduleConfirmationFragment extends PreActivationFlowFragment
{
    @Bind(R.id.jobs_container)
    ViewGroup mJobsContainer;
    @Bind(R.id.shipping_view)
    SimpleContentLayout mShippingView;
    @Bind(R.id.payment_view)
    LabelAndValueView mPaymentView;
    @Bind(R.id.order_total_view)
    LabelAndValueView mOrderTotalView;

    public static ScheduleConfirmationFragment newInstance()
    {
        return new ScheduleConfirmationFragment();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mShippingView.setContent("Ship To", "123 Penny Lane\nBrooklyn, NY 11321");
        mPaymentView.setContent("Payment", "Card ending in 1234");
        mOrderTotalView.setContent("Order Total", "$50");
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_schedule_confirmation;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.confirmation);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return getString(R.string.ready_to_commit);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return getString(R.string.about_to_claim_and_order);
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.finish_building_schedule);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        // FIXME: Claim jobs here
        getActivity().finish();
    }
}
