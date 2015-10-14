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
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SelectPaymentMethodFragment extends ActionBarFragment
{
    @InjectView(R.id.bank_account_details)
    TextView bankAccountDetails;

    @InjectView(R.id.debit_card_details)
    TextView debitCardDetails;

    @InjectView(R.id.payment_method_container)
    ViewGroup paymentMethodContainer;

    @OnClick(R.id.debit_card_option)
    public void onDebitCardOptionClicked()
    {
        launchUpdatePaymentMethodFragment();
    }

    @OnClick(R.id.bank_account_option)
    public void onBankAccountOptionClicked()
    {
        launchUpdatePaymentMethodFragment();
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
        switch(item.getItemId())
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
        bus.post(new PaymentEvents.RequestPaymentFlow());
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_payments_info, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    private void launchUpdatePaymentMethodFragment()
    {
        UIUtils.launchFragmentInMainActivityOnBackStack(getActivity(), new UpdatePaymentFragment(), TransitionStyle.NATIVE_TO_NATIVE);
    }

    @Subscribe
    public void onGetPaymentFlowSuccess(PaymentEvents.ReceivePaymentFlowSuccess event)
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
            }
        }
        paymentMethodContainer.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onGetPaymentFlowError(PaymentEvents.ReceivePaymentFlowError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.payment_flow_error);
    }
}
