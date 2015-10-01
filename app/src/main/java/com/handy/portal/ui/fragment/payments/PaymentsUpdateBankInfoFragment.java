package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.event.StripeEvents;
import com.handy.portal.model.payments.BankAccountInfo;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentsUpdateBankInfoFragment extends InjectedFragment
{

    @InjectView(R.id.payments_update_info_routing_number_text)
    TextView routingNumberText;

    @InjectView(R.id.payments_update_info_account_number_text)
    TextView accountNumberText;

    @InjectView(R.id.payments_update_info_tax_id_text)
    TextView taxIdText;

    @InjectView(R.id.payments_update_info_submit_button)
    Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_payments_update_bank_account, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSubmitForm();
            }
        });
    }

    public boolean isInputValid()
    {
        //TODO: implement
        return true;
    }

    public void onSubmitForm()
    {
        if(isInputValid())
        {
            String routingNumber = routingNumberText.getText().toString();
            String accountNumber = accountNumberText.getText().toString();
            String taxId = taxIdText.getText().toString();
            BankAccountInfo bankAccountInfo = new BankAccountInfo();
            bankAccountInfo.setAccountNumber(accountNumber);
            bankAccountInfo.setRoutingNumber(routingNumber);
            bankAccountInfo.setCurrency("usd"); //TODO: test only. investigate how we can get the user's actual currency and country codes
            bankAccountInfo.setCountry("US");
            bus.post(new StripeEvents.RequestStripeToken(bankAccountInfo));
        }
        else
        {
            //TODO: show error text
        }

    }

    @Subscribe
    public void onReceiveStripeTokenSuccess(StripeEvents.ReceiveStripeTokenSuccess event)
    {
        String token = event.getToken();
        System.out.println("Received Stripe token: " + token);
    }
}
