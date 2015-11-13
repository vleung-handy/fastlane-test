package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.PaymentEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.payments.PaymentFlow;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SelectPaymentMethodFragment extends ActionBarFragment
{
    @Inject
    ProviderManager providerManager;

    @InjectView(R.id.bank_account_details)
    TextView bankAccountDetails;

    @InjectView(R.id.debit_card_details)
    TextView debitCardDetails;

    @InjectView(R.id.payment_method_container)
    ViewGroup paymentMethodContainer;

    @InjectView(R.id.debit_card_option)
    ViewGroup debitCardOption;

    @InjectView(R.id.verified_indicator)
    View verifiedIndicator;

    @InjectView(R.id.failed_indicator)
    View failedIndicator;

    @InjectView(R.id.pending_indicator)
    View pendingIndicator;

    @OnClick(R.id.debit_card_option)
    public void onDebitCardOptionClicked()
    {
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.UPDATE_DEBIT_CARD, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE));
    }

    @OnClick(R.id.bank_account_option)
    public void onBankAccountOptionClicked()
    {
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.UPDATE_BANK_ACCOUNT, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE));
    }

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.PAYMENTS;
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setOptionsMenuEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.select_payment_method, false);
        paymentMethodContainer.setVisibility(View.GONE);
        bus.post(new PaymentEvent.RequestPaymentFlow());
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_select_payment_method, container, false);
        ButterKnife.inject(this, view);

        if (providerManager.getCachedActiveProvider() != null &&
                !providerManager.getCachedActiveProvider().isUS()
                )
        {
            debitCardOption.setVisibility(View.GONE);
        }

        return view;
    }

    @Subscribe
    public void onGetPaymentFlowSuccess(PaymentEvent.ReceivePaymentFlowSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        String accountDetails = event.paymentFlow.getAccountDetails();
        if (accountDetails != null)
        {
            if (event.paymentFlow.isDebitCard())
            {
                debitCardDetails.setText(accountDetails);
            }
            else if (event.paymentFlow.isBankAccount())
            {
                bankAccountDetails.setText(accountDetails);
                showBankAccountStatus(event.paymentFlow.getStatus());
            }
        }
        paymentMethodContainer.setVisibility(View.VISIBLE);
    }

    private void showBankAccountStatus(String status)
    {
        if (status != null)
        {
            switch (status)
            {
                case PaymentFlow.STATUS_NEW:
                    pendingIndicator.setVisibility(View.VISIBLE);
                    break;
                case PaymentFlow.STATUS_VALIDATED:
                case PaymentFlow.STATUS_VERIFIED:
                    verifiedIndicator.setVisibility(View.VISIBLE);
                    break;
                case PaymentFlow.STATUS_ERRORED:
                    failedIndicator.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    @Subscribe
    public void onGetPaymentFlowError(PaymentEvent.ReceivePaymentFlowError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.payment_flow_error);
    }

}
