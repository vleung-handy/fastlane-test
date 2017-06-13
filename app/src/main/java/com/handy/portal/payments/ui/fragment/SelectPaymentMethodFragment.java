package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.MainContentFragmentHolder;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.PaymentFlow;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectPaymentMethodFragment extends ActionBarFragment {
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

    public static SelectPaymentMethodFragment newInstance() {
        return new SelectPaymentMethodFragment();
    }

    /*
    - not using event bus for navigation because want to ensure that the right component handles the navigation
    - not putting fragment switching logic in here because this fragment should not know about the fragment container
     */
    @OnClick(R.id.debit_card_option)
    public void onDebitCardOptionClicked() {

        ((MainContentFragmentHolder) getActivity()).replaceMainContentFragment(PaymentsUpdateDebitCardFragment.newInstance(), true);
    }

    @OnClick(R.id.bank_account_option)
    public void onBankAccountOptionClicked() {
        ((MainContentFragmentHolder) getActivity()).replaceMainContentFragment(PaymentsUpdateBankAccountFragment.newInstance(), true);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOptionsMenuEnabled(true);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.select_payment_method, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackButtonEnabled(true);

        bus.register(this);

        paymentMethodContainer.setVisibility(View.GONE);
        bus.post(new PaymentEvent.RequestPaymentFlow());
        showProgressSpinner();
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_select_payment_method, container, false));

        ButterKnife.bind(this, view);

        final ProviderProfile providerProfile = providerManager.getCachedProviderProfile();
        if (providerProfile != null
                && providerProfile.getProviderPersonalInfo() != null
                && !providerProfile.getProviderPersonalInfo().isUS()) {
            debitCardOption.setVisibility(View.GONE);
        }

        return view;
    }

    @Subscribe
    public void onGetPaymentFlowSuccess(PaymentEvent.ReceivePaymentFlowSuccess event) {
        hideProgressSpinner();
        String accountDetails = event.paymentFlow.getAccountDetails();
        if (accountDetails != null) {
            if (event.paymentFlow.isDebitCard()) {
                debitCardDetails.setText(accountDetails);
            }
            else if (event.paymentFlow.isBankAccount()) {
                bankAccountDetails.setText(accountDetails);
                showBankAccountStatus(event.paymentFlow.getStatus());
            }
        }
        paymentMethodContainer.setVisibility(View.VISIBLE);
    }

    private void showBankAccountStatus(String status) {
        if (status != null) {
            switch (status) {
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
    public void onGetPaymentFlowError(PaymentEvent.ReceivePaymentFlowError event) {
        hideProgressSpinner();
        showToast(R.string.payment_flow_error);
    }

}
