package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentBlockingFragment extends ActionBarFragment
{
    @BindView(R.id.fetch_error_view)
    View fetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView errorText;
    @BindView(R.id.try_again_button)
    Button errorCTAButton;

    public static final String FRAGMENT_TAG = "fragment_payment_blocking";

    protected MainViewPage getAppPage()
    {
        return MainViewPage.AVAILABLE_JOBS;
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
        if (!MainActivity.clearingBackStack)
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
                bus.post(new NavigationEvent.NavigateToPage(MainViewPage.SELECT_PAYMENT_METHOD, new Bundle(), TransitionStyle.REFRESH_PAGE, true));
            }
        });
    }
}
