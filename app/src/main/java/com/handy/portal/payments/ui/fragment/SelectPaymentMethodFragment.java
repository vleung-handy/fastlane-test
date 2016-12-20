package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.PaymentFlow;
import com.handy.portal.ui.fragment.ActionBarFragment;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectPaymentMethodFragment extends ActionBarFragment
{
    @Inject
    ProviderManager providerManager;

    @BindView(R.id.bank_account_details)
    TextView bankAccountDetails;

    @BindView(R.id.debit_card_details)
    TextView debitCardDetails;

    @BindView(R.id.payment_method_container)
    ViewGroup paymentMethodContainer;

    @BindView(R.id.debit_card_option)
    ViewGroup debitCardOption;

    @BindView(R.id.verified_indicator)
    View verifiedIndicator;

    @BindView(R.id.failed_indicator)
    View failedIndicator;

    @BindView(R.id.pending_indicator)
    View pendingIndicator;

    @OnClick(R.id.debit_card_option)
    public void onDebitCardOptionClicked()
    {
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.UPDATE_DEBIT_CARD, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, true));
    }

    @OnClick(R.id.bank_account_option)
    public void onBankAccountOptionClicked()
    {
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.UPDATE_BANK_ACCOUNT, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, true));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setOptionsMenuEnabled(true);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.select_payment_method, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackButtonEnabled(true);

        bus.register(this);

        paymentMethodContainer.setVisibility(View.GONE);
        bus.post(new PaymentEvent.RequestPaymentFlow());
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_select_payment_method, container, false);
        ButterKnife.bind(this, view);

        final ProviderProfile providerProfile = providerManager.getCachedProviderProfile();
        if (providerProfile != null
                && providerProfile.getProviderPersonalInfo() != null
                && !providerProfile.getProviderPersonalInfo().isUS())
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
