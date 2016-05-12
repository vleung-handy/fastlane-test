package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentBlockingFragment extends ActionBarFragment
{
    @Bind(R.id.fetch_error_view)
    View fetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView errorText;
    @Bind(R.id.try_again_button)
    Button errorCTAButton;

    public static final String FRAGMENT_TAG = "fragment_payment_blocking";

    protected MainViewTab getTab()
    {
        return MainViewTab.AVAILABLE_JOBS;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_payment_blocking, container, false);
        ButterKnife.bind(this, view);
        setActionBarTitle(R.string.payment_blocking_title);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!MainActivityFragment.clearingBackStack)
        {
            displayPaymentInformationError();
        }
    }

    //Show an error indicating that payment information is incorrect/invalid and add a CTA directing them to the update payment screen
    //Should prevent user from claiming any jobs until their information is corrected but should not block them from leaving the tab
    protected void displayPaymentInformationError()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        errorText.setText(R.string.payments_need_updates_jobs_title);
        fetchErrorView.setVisibility(View.VISIBLE);
        errorCTAButton.setText(R.string.add_direct_deposit);
        errorCTAButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                bus.post(new NavigationEvent.NavigateToTab(MainViewTab.SELECT_PAYMENT_METHOD, new Bundle(), TransitionStyle.REFRESH_TAB, true));
            }
        });
    }
}
