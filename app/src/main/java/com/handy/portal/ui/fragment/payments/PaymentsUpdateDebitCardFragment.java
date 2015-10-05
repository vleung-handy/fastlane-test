package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
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
import com.handy.portal.model.payments.DebitCardInfo;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentsUpdateDebitCardFragment extends InjectedFragment
{

    @InjectView(R.id.payments_update_info_debit_card_number_text)
    TextView debitCardNumberText;

    @InjectView(R.id.payments_update_info_debit_card_expiration_month_text)
    TextView debitCardExpirationMonthText;

    @InjectView(R.id.payments_update_info_debit_card_expiration_year_text)
    TextView debitCardExpirationYearText;

    @InjectView(R.id.payments_update_info_debit_card_security_code_text)
    TextView debitCardSecurityCodeText;

    @InjectView(R.id.payments_update_info_debit_tax_id_text)
    TextView taxIdText;

    @InjectView(R.id.payments_update_info_debit_card_submit_button)
    Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_payments_update_debit_card, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        debitCardNumberText.setText("4000056655665556"); //TODO: remove. test only
        debitCardExpirationMonthText.setText("01");
        debitCardExpirationYearText.setText("2017");
        debitCardSecurityCodeText.setText("424");
        taxIdText.setText("000000000");
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
            DebitCardInfo debitCardInfo = new DebitCardInfo();
            debitCardInfo.setCardNumber(debitCardNumberText.getText().toString());
            debitCardInfo.setCvc(debitCardSecurityCodeText.getText().toString());
            debitCardInfo.setExpMonth(debitCardExpirationMonthText.getText().toString());
            debitCardInfo.setExpYear(debitCardExpirationYearText.getText().toString());
            bus.post(new StripeEvents.RequestStripeToken(debitCardInfo));
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
        System.out.println("Received Stripe token: " + token);

        //TODO: need to do validation first
        String taxIdString = taxIdText.getText().toString();
        String expMonthString = debitCardExpirationMonthText.getText().toString();
        String expYearString = debitCardExpirationYearText.getText().toString();
        String cardNumberString = debitCardNumberText.getText().toString();
        String cardNumberLast4Digits = cardNumberString.substring(cardNumberString.length() - 4);
        bus.post(new PaymentEvents.RequestCreateDebitCardRecipient(token, taxIdString, cardNumberLast4Digits, expMonthString, expYearString));
    }

    @Subscribe
    public void onReceiveStripeTokenError(StripeEvents.ReceiveStripeTokenError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Toast.makeText(this.getContext(), "Failed to get stripe token", Toast.LENGTH_LONG).show();

    }

    //TODO: clean this up
    @Subscribe
    public void onReceiveCreateDebitCardRecipientSuccess(PaymentEvents.ReceiveCreateDebitCardRecipientSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), event.successfullyCreated ? "Successfully created debit card" : "Failed to create debit card", Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onReceiveCreateDebitCardRecipientError(PaymentEvents.ReceiveCreateDebitCardRecipientError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), "Failed to create debit card", Toast.LENGTH_LONG).show();
    }
}
