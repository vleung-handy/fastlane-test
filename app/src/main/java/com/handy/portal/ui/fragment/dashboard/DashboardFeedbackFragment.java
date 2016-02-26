package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.ui.element.dashboard.DashboardFeedbackView;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DashboardFeedbackFragment extends ActionBarFragment
{
    @Bind(R.id.layout_dashboard_feedback)
    LinearLayout mFeedbackLayout;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD_REVIEWS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.feedback);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_dashboard_feedback, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        ProviderEvaluation evaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.EVALUATION);

        if (evaluation == null || evaluation.getProviderFeedback() == null) { return; }

        for (ProviderFeedback feedback : evaluation.getProviderFeedback())
        {
            mFeedbackLayout.addView(new DashboardFeedbackView(getContext(), feedback));
        }
    }
}
