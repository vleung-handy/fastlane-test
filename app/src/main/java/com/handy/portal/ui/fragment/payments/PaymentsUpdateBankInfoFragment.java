package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.event.StripeEvents;
import com.handy.portal.model.payments.BankAccountInfo;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;
import java.util.List;

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
        setInputFilters();
        accountNumberText.setText("000123456789");
        taxIdText.setText("000000000");
        routingNumberText.setText("110000000");
    }

    public boolean isInputValid()
    {
        //TODO: implement
        return true;
    }

    private void setInputFilters()
    {
        //TODO: implement. below is for test only
        List<InputFilter> filters = new LinkedList<>();
        InputFilter lengthFilter = new InputFilter.LengthFilter(9);
        filters.add(lengthFilter);
        taxIdText.setInputType(InputType.TYPE_CLASS_NUMBER);
        taxIdText.setFilters(filters.toArray(new InputFilter[]{}));

        routingNumberText.setInputType(InputType.TYPE_CLASS_NUMBER);
        routingNumberText.setFilters(filters.toArray(new InputFilter[]{}));

        accountNumberText.setInputType(InputType.TYPE_CLASS_NUMBER);
        accountNumberText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
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
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        }
        else
        {
            //TODO: show error text
        }

    }

    //TODO: This fragment isn't paused when tab switched so this event is received even when not in view
    //we may move away from using tab layout so this may not have to be handled
    @Subscribe
    public void onReceiveStripeTokenSuccess(StripeEvents.ReceiveStripeTokenSuccess event)
    {
        String token = event.getToken();
        System.out.println("Received Stripe token: " + token);//TODO: test only, remove later

        //TODO: need to do validation first
        String taxIdString = taxIdText.getText().toString();
        String accountNumberString = accountNumberText.getText().toString();
        String accountNumberLast4Digits = accountNumberString.substring(accountNumberString.length() - 4);
        bus.post(new PaymentEvents.RequestCreateBankAccount(token, taxIdString, accountNumberLast4Digits));
    }

    @Subscribe
    public void onReceiveCreateBankAccountSuccess(PaymentEvents.ReceiveCreateBankAccountSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), event.successfullyCreated ? "Successfully created bank account" : "Failed to create bank account", Toast.LENGTH_LONG).show();

    }

    @Subscribe
    public void onReceiveCreateBankAccountError(PaymentEvents.ReceiveCreateBankAccountError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), "Failed to create bank account", Toast.LENGTH_LONG).show();

    }
}
